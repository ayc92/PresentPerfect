package com.radishugrads.presentperfect;

import java.util.ArrayList;
import java.util.Collections;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.ListFragment;

public class TabList extends Fragment {
	//String[] notifications = {"Bob shared a recording with you", "Beyonce commented on VC Pitch - Rec 1"};
	//String[] contacts = {"Angel", "Beyonce", "Bob", "King Henry", "Mr. Clean", "Zoo"};
	ArrayList<String> notifications;
	ArrayList<String> contacts;
	ListView listv;
	String tab;
	tabAdapter adapter;
	boolean deleteMode;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //setUpView(); 
        String someTitle = getArguments().getString("someTitle", "");
        tab = someTitle;
        Log.d("WORKED: ", someTitle);
        notifications = new ArrayList<String>();
        contacts = new ArrayList<String>();
        notifications.add("Bob shared a recording with you");
        notifications.add("Beyonce commented on VC Pitch - Rec 1");
        contacts.add("Angel");
        contacts.add("Beyonce");
        contacts.add("Bob");
        contacts.add("King Henry");
        contacts.add("Mr. Clean");
        contacts.add("Zoo");
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
        } else {
        	adapter = new tabAdapter(contacts, getActivity());
        	Button addb = (Button) currentTabView.findViewById(R.id.addProject);
        	addb.setOnClickListener(new OnClickListener(){
		        @Override
		        public void onClick(View v) {
		        	Log.d("CLICKED ADD", "YA");
		        	createChooseNameDialog();
		        	
		        }
        	});
        	Button deleteb = (Button) currentTabView.findViewById(R.id.removeParent);
        	deleteb.setOnClickListener(new OnClickListener(){
		        @Override
		        public void onClick(View v) {
		        	Log.d("CLICKED REM", "YA");
		        	deleteMode = !deleteMode;
		        	adapter.notifyDataSetChanged();
		        	
		        }
        	});
        }
		listv = (ListView) currentTabView.findViewById(R.id.list);
        listv.setAdapter(adapter);

        /** Setting the list adapter for the ListFragment */
        //setListAdapter(adapter);
 
        return currentTabView;
    }
	
	public class tabAdapter extends BaseAdapter implements ListAdapter {
		private ArrayList<String> list; 
		private Context context; 



		public tabAdapter(ArrayList<String> list, Context context) { 
		    this.list = list; 
		    this.context = context; 
		} 

		@Override
		public int getCount() { 
		    return list.size(); 
		} 

		@Override
		public Object getItem(int pos) { 
		    return list.get(pos); 
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
		    ImageButton deleteBtn = (ImageButton) view.findViewById(R.id.delete);
		    if(deleteMode){
		    	deleteBtn.setVisibility(View.VISIBLE);
		    }
		    deleteBtn.setOnClickListener(new View.OnClickListener(){
		        @Override
		        public void onClick(View v) { 
		            //do something
		        	if (deleteMode){
		        		String deletedWord = list.get(position);
		        		contacts.remove(deletedWord);
		        		notifyDataSetChanged();
		        		
		        	}
		        	if (contacts.size() == 0){
		        		deleteMode = false;
		        	}
		        }
		    });
		    TextView listItemText = (TextView)view.findViewById(R.id.lblListHeader); 
		    listItemText.setText(list.get(position)); 
		    return view; 
		} 
		}
	
	private void createChooseNameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Add a new contact");
		final EditText input = new EditText(getActivity());
		alert.setView(input);
		alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newest_input = input.getText().toString();
				contacts.add(newest_input);
				Collections.sort(contacts);
				adapter.notifyDataSetChanged();
				}
			});
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
			}
			});
		
		AlertDialog newDialog = alert.create();
		newDialog.show();
	}
}
