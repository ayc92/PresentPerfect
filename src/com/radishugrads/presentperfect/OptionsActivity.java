package com.radishugrads.presentperfect;


import java.util.ArrayList;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

public class OptionsActivity extends ListActivity {
	int hour;
	int min;
	EditText timeChosen;
	ImageButton buzzwords;
	boolean include;
	ArrayAdapter<String> adapter;
	Activity main;
	private ArrayList<String> items= new ArrayList<String>();
	ArrayList<String> good_items = new ArrayList<String>();
	ArrayList<String> bad_items = new ArrayList<String>();
	boolean timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		main = this;
		hour = 0;
		min = 5;
		timer = true;
		timeChosen = (EditText) findViewById(R.id.editText1);
		buzzwords = (ImageButton) findViewById(R.id.wordslist);
		include = true;
		adapter = new ArrayAdapter<String>(getApplicationContext(), 
			    android.R.layout.simple_list_item_1, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Log.d("WHHAAT", position+"");
			    View view = super.getView(position, convertView, parent);
			    TextView text = (TextView) view.findViewById(android.R.id.text1);
			    text.setTextColor(Color.BLACK);
			    return view;
			  }
			};
//		setListAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                items));
			setListAdapter(adapter);
		registerForContextMenu(getListView());
		addButtonListener();
		addRadioListener();
	}
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.setHeaderTitle("Options");
	    menu.add(0, v.getId(), 0, "Delete word");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	            .getMenuInfo();
	   if (item.getTitle() == "Delete word") {
	        //items.remove(item);
	        return true;
	    }
	    return super.onContextItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	
	public void onListItemClick(ListView parent, View v, int position,
            long id) {
			//buzzwords.setText(items.get(position));
		good_items.remove(items.get(position));
		bad_items.remove(items.get(position));
		items.remove(position);
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
	}
	
	public void addButtonListener(){
		buzzwords.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View view) {
//				Log.d("WHHAAT", "WORKING 1??");
//				Log.d("WHHAAT", "WORKING 2??");
//				numItems++;
//				include = !include;
				//adapter.add("Clicked!");
//				Log.d("WHHAAT", "WORKING 3??");
		        //adapter.notifyDataSetChanged();
//				Log.d("WHHAAT", listv.getFirstVisiblePosition()+"");
//				View v = listv.getChildAt(1-listv.getFirstVisiblePosition());
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						main);
				LayoutInflater inflater = main.getLayoutInflater();

			    // Inflate and set the layout for the dialog
			    // Pass null as the parent view because its going in the dialog layout
				final View v = inflater.inflate(R.layout.buzzworddialog, null);
				alertDialogBuilder.setView(v);
					// set title
					alertDialogBuilder.setTitle("Add new word")
						.setCancelable(false)
						.setPositiveButton("Add",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, close
								// current activity
								//MainActivity.this.finish();
								final EditText addedWord = (EditText) v.findViewById(R.id.newWord);
								String word = addedWord.getText().toString();
								items.add(word);
								adapter.notifyDataSetChanged();
								final RadioGroup g = (RadioGroup) v.findViewById(R.id.wordType);
								int selected = g.getCheckedRadioButtonId();
								if (selected == 0){
									Log.d("OOOO", "WEEE");
									include = true;
								} else {
									include = false;
									Log.d("OOOO", "COOOLLL");
								}
								if(include){
									good_items.add(word);
								} else {
									bad_items.add(word);
								}
								include = true;
							}
						  })
						.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
		 
						// show it
						alertDialog.show();
				}
			});
	}
	
	public void addRadioListener(){
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.timingOp);        
	    radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	            // checkedId is the RadioButton selected
	        	if (checkedId == 0){
	        		timer = true;
	        		Log.d("OOOO", "TIMERRR");
	        	} else {
	        		timer = false;
	        		Log.d("OOOO", "STOPWATCHH");
	        	}
	        }
	    });
	}
	
	public void showTimeDialog(View v)
    {
    	showDialog(0);
    }
    protected Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
    	case 0:
    		return new TimePickerDialog(this, timeSetListener, 0, 0, true);
    	}
    	return null;
    }
    private TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			hour=hourOfDay;
			min=minute;
			if (hour != 0){
				timeChosen.setText(hour + " hr " + min + " min");
			} else {
				timeChosen.setText(min + " min");
			}
		}
	};
}
