package com.radishugrads.presentperfect;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

public class buzzlistAdapter extends BaseAdapter implements ListAdapter {
	private ArrayList<String> list; 
	private Context context; 



	public buzzlistAdapter(ArrayList<String> list, Context context) { 
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
	        view = inflater.inflate(R.layout.buzzlist, null);
	        Log.d("OOOO", "INNNN222");
	    TextView listItemText = (TextView)view.findViewById(R.id.list_item_string); 
	    listItemText.setText(list.get(position)); 
	    Log.d("OOOO", "IN 33333");
	    //Handle buttons and add onClickListeners
	    Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);

	    deleteBtn.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) { 
	            //do something
	            list.remove(position); //or some other task
	            notifyDataSetChanged();
	        }
	    });

	    return view; 
	} 
	}
