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
import android.content.Context;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

public class OptionsActivity extends ListActivity {
	int hour;
	int min;
	EditText timeChosen;
	ImageButton buzzwords;
	boolean include;
	//ArrayAdapter<String> adapter;
	buzzlistAdapter adapter;
	ArrayAdapter<String> adapter2;
	Activity main;
	private ArrayList<String> items= new ArrayList<String>();
	ArrayList<String> good_items = new ArrayList<String>();
	ArrayList<String> bad_items = new ArrayList<String>();
	ArrayList<String> all_items = new ArrayList<String>();
	boolean timer;
	Button boot;
	boolean deleteMode;
	int currList; // 0 = all, 1 = good, 2 = bad
	Spinner spinner1; 
	TextView placeholder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		main = this;
		hour = 0;
		min = 5;
		timer = true;
		deleteMode = false;
		currList = 0;
		timeChosen = (EditText) findViewById(R.id.editText1);
		buzzwords = (ImageButton) findViewById(R.id.wordslist);
		placeholder = (TextView) findViewById(R.id.placeholdr);
		include = true;
//		adapter = new ArrayAdapter<String>(getApplicationContext(), 
//			    android.R.layout.simple_list_item_1, items) {
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				Log.d("WHHAAT", position+"");
//			    View view = super.getView(position, convertView, parent);
//			    TextView text = (TextView) view.findViewById(android.R.id.text1);
//			    text.setTextColor(Color.BLACK);
//			    return view;
//			  }
//			};
		adapter = new buzzlistAdapter(items, this);
//		setListAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                items));
			setListAdapter(adapter);
		//registerForContextMenu(getListView());
		addButtonListener();
		addRadioListener();
		//listv = (ListView) findViewById(R.id.list);
		spinner1 = (Spinner) findViewById(R.id.spinner_op);
		spinner1.setOnItemSelectedListener(new SpinnerActivity2());
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
	
//	public void onListItemClick(ListView parent, View v, int position,
//            long id) {
//			//buzzwords.setText(items.get(position));
//		good_items.remove(items.get(position));
//		bad_items.remove(items.get(position));
//		items.remove(position);
//        adapter.notifyDataSetChanged();
//        adapter.notifyDataSetInvalidated();
//	}
	
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
								String word = addedWord.getText().toString().toLowerCase();
								Log.d("OOOO", "PREADD");
								if (all_items.contains(word)){
									Toast.makeText(getApplicationContext(), "You already added this word!", Toast.LENGTH_SHORT).show();
									return;
								}
								all_items.add(word);
								if (currList == 0){
									items.add(word);
									Log.d("OOOO", "ADDED TO ITEMS");
								}
								Log.d("OOOO", "POST ADD PRE NOTIFY");
								//((ListView)findViewById(R.id.list)).getAdapter().notifyDataSetChanged();
								Log.d("OOOO", "POST NOTIFY");
								final RadioGroup g = (RadioGroup) v.findViewById(R.id.wordType);
								int selected = g.getCheckedRadioButtonId();
								if (selected == R.id.incorp){
									Log.d("OOOO", "WEEE");
									include = true;
								} else {
									include = false;
									Log.d("OOOO", "COOOLLL");
								}
								if(include){
									good_items.add(word);
									if (currList == 1){
										items.add(word);
									}
								} else {
									bad_items.add(word);
									if (currList == 2){
										items.add(word);
									}
								}
								include = true;
								adapter.notifyDataSetChanged();
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
							placeholder.setVisibility(View.GONE);

				}
			});
		
		
		ImageButton trash = (ImageButton) findViewById(R.id.wordsdelete);
		trash.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View view) {
				deleteMode = !deleteMode;
				adapter.notifyDataSetChanged();
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
		    Log.d("OOOO", "IN 33333");
		    //Handle buttons and add onClickListeners
		    ImageButton deleteBtn = (ImageButton) view.findViewById(R.id.delete_btn);
		    if(deleteMode){
		    	deleteBtn.setVisibility(View.VISIBLE);
		    }
		    deleteBtn.setOnClickListener(new View.OnClickListener(){
		        @Override
		        public void onClick(View v) { 
		            //do something
		        	if (deleteMode){
		        	String deletedWord = list.get(position);
		            list.remove(position); //or some other task
		            all_items.remove(deletedWord);
		            if (good_items.contains(deletedWord)){
		            	good_items.remove(deletedWord);
		            } else if (bad_items.contains(deletedWord)){
		            	bad_items.remove(deletedWord);
		            }
		            notifyDataSetChanged();
		        	}
		        	if (all_items.size() == 0){
		        		placeholder.setVisibility(View.VISIBLE);
		        	}
		        }
		    });
		    TextView listItemText = (TextView)view.findViewById(R.id.list_item_string); 
		    listItemText.setText(list.get(position)); 
		    return view; 
		} 
		}
public class SpinnerActivity2 extends Activity implements OnItemSelectedListener {
	    
	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	    	String selected = parent.getItemAtPosition(pos).toString();
	    	if (selected.equals("All")){
	    		// change list adapter to all
				items.clear();
				items.addAll(all_items);
				adapter.notifyDataSetChanged();
				currList = 0;
	    	} else if (selected.equals("Avoid")) {
				items.clear();
				items.addAll(bad_items);
				adapter.notifyDataSetChanged();
				currList = 2;
	    	} else if (selected.equals("Incorporate")) {
				items.clear();
				items.addAll(good_items);
				adapter.notifyDataSetChanged();
				currList = 1;
	    	}
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	    }
	}
}
