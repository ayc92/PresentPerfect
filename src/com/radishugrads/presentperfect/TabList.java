package com.radishugrads.presentperfect;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v4.app.ListFragment;

public class TabList extends Fragment {
	String[] notifications = {"Bob shared a recording with you", "Beyonce commented on Rich Ppl Pitch - Rec 1"};
	ListView listv;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        /** Creating an array adapter to store the list of countries **/
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, notifications);
        tabAdapter adapter = new tabAdapter(notifications, getActivity());
		View currentTabView = inflater.inflate(R.layout.activity_tab_list, container, false);
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
		        view = inflater.inflate(R.layout.buzzlist, null);
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
//		    TextView listItemText = (TextView)view.findViewById(R.id.list_item_string); 
//		    listItemText.setText(list.get(position)); 
		    return view; 
		} 
		}
}
