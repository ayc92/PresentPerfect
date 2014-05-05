package com.radishugrads.presentperfect;

import java.util.ArrayList;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.ListFragment;

public class TabList extends Fragment {
	String[] notifications = {"Bob shared a recording with you", "Beyonce commented on Rich Ppl Pitch - Rec 1"};
	String[] contacts = {"Angel", "Beyonce", "Bob", "King Henry", "Mr. Clean", "Zoo"};
	ListView listv;
	String tab;
	tabAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //setUpView(); 
        String someTitle = getArguments().getString("someTitle", "");
        tab = someTitle;
        Log.d("WORKED: ", someTitle);
    }
    
    public static TabList newInstance(String someTitle) {
        TabList explf = new TabList();
        Bundle args = new Bundle();
        args.putString("someTitle", someTitle);
        explf.setArguments(args);
        return explf;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        /** Creating an array adapter to store the list of countries **/
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, notifications);
		View currentTabView = inflater.inflate(R.layout.activity_tab_list, container, false);
		if (tab.equals("notifications")){
			LinearLayout bottom = (LinearLayout) currentTabView.findViewById(R.id.bottom_bar);
        	bottom.setVisibility(View.GONE);
        	adapter = new tabAdapter(notifications, getActivity());
        	Button addb = (Button) currentTabView.findViewById(R.id.addProject);
        	addb.setOnClickListener(new View.OnClickListener(){
		        @Override
		        public void onClick(View v) {
		        	createChooseNameDialog();
		        	Log.d("CLICKED ADD", "YA");
		        }
        	});
        } else {
        	adapter = new tabAdapter(contacts, getActivity());
        }
		listv = (ListView) currentTabView.findViewById(R.id.list);
        listv.setAdapter(adapter);

        /** Setting the list adapter for the ListFragment */
        //setListAdapter(adapter);
 
        return currentTabView;
    }
	
	public class tabAdapter extends BaseAdapter implements ListAdapter {
		private String[] list; 
		private Context context; 



		public tabAdapter(String[] list, Context context) { 
		    this.list = list; 
		    this.context = context; 
		} 

		@Override
		public int getCount() { 
		    return list.length; 
		} 

		@Override
		public Object getItem(int pos) { 
		    return list[pos]; 
		} 

		@Override
		public long getItemId(int pos) { 
		    return 0;
		    //just return 0 if your list items do not have an Id variable.
		} 

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
		    View view;
		    Log.d("OOOO", "INNNNN");
		        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		        view = inflater.inflate(R.layout.tab_list, null);
		        Log.d("OOOO", "INNNN222");
		    Log.d("OOOO", "IN 33333");
		    //Handle buttons and add onClickListeners
//		    ImageButton deleteBtn = (ImageButton) view.findViewById(R.id.delete_btn);
//		    if(deleteMode){
//		    	deleteBtn.setVisibility(View.VISIBLE);
//		    }
//		    deleteBtn.setOnClickListener(new View.OnClickListener(){
//		        @Override
//		        public void onClick(View v) { 
//		            //do something
//		        	if (deleteMode){
//		        	String deletedWord = list.get(position);
//		            list.remove(position); //or some other task
//		            all_items.remove(deletedWord);
//		            if (good_items.contains(deletedWord)){
//		            	good_items.remove(deletedWord);
//		            } else if (bad_items.contains(deletedWord)){
//		            	bad_items.remove(deletedWord);
//		            }
//		            notifyDataSetChanged();
//		            update();
//		        	}
//		        	if (all_items.size() == 0){
//		        		placeholder.setVisibility(View.VISIBLE);
//		        		deleteMode = false;
//		        	}
//		        }
//		    });
		    TextView listItemText = (TextView)view.findViewById(R.id.lblListHeader); 
		    listItemText.setText(list[position]); 
		    return view; 
		} 
		}
	
	private AlertDialog createChooseNameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Add a new contact");
		//alert.setMessage("Input name of "+ other + ".");
		final EditText input = new EditText(getActivity());
		alert.setView(input);
		alert.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newest_input = input.getText().toString();
				
				}
			});
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
			}
			});
		
		AlertDialog newDialog = alert.create();
		return newDialog;
	}
}
