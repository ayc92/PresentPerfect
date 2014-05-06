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
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

public class OptionsActivity extends MotherBrain {
	String recName;
	int min;
	EditText timeChosen;
	ImageButton buzzwords;
	boolean include;
	buzzlistAdapter adapter;
	Activity main;
	private ArrayList<String> items = new ArrayList<String>();
	ArrayList<String> good_items;
	ArrayList<String> bad_items;
	ArrayList<String> all_items;
	boolean timer;
	Button boot;
	boolean deleteMode;
	int currList; // 0 = all, 1 = good, 2 = bad
	Spinner spinner1; 
	TextView placeholder;
	LinearLayout buzzList;
	Bundle params;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		// format action bar
		formatActionBar("Recording Options");
		
		buzzList = (LinearLayout) findViewById(R.id.buzzList);
		Intent intent = getIntent();
		params = intent.getExtras();
		recName = params.getString("rec_name");
		main = this;
		min = params.getInt("min");
		timer = params.getBoolean("timer");
		good_items = params.getStringArrayList("good_items");
		bad_items = params.getStringArrayList("bad_items");
		all_items = params.getStringArrayList("all_items");
		
		deleteMode = false;
		currList = 0;
		buzzwords = (ImageButton) findViewById(R.id.wordslist);
		placeholder = (TextView) findViewById(R.id.placeholdr);
		if(all_items.size() > 0){
			placeholder.setVisibility(View.GONE);
		}
		include = true;
		adapter = new buzzlistAdapter(items, this);

		update();

		addButtonListener();
		addRadioListener();

		spinner1 = (Spinner) findViewById(R.id.spinner_op);
		spinner1.setOnItemSelectedListener(new SpinnerActivity2());
		NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker1);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setMaxValue(60);
		np.setMinValue(1);
		np.setValue(min);
		np.setOnValueChangedListener( new NumberPicker.
	            OnValueChangeListener() {
	            @Override
	            public void onValueChange(NumberPicker picker, int
	                oldVal, int newVal) {
	                min = newVal;
	            }
	        });
		
		if (!timer){
			RadioButton stopw = (RadioButton) findViewById(R.id.radioStopwatch);
			stopw.setChecked(true);
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent recordIntent = new Intent(this, RecorderActivity.class);
		recordIntent.putExtra("rec_name", recName);
		recordIntent.putStringArrayListExtra("all_items", all_items);
		recordIntent.putStringArrayListExtra("good_items", good_items);
		recordIntent.putStringArrayListExtra("bad_items", bad_items);
		recordIntent.putExtra("timer", timer);
		recordIntent.putExtra("min", min);
		startActivity(recordIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mother_brain, menu);
		return true;
	}
	
	public void addButtonListener(){
		buzzwords.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View view) {
				deleteMode = false;
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
								if (all_items.contains(word)){
									Toast.makeText(getApplicationContext(), "You already added this word!", Toast.LENGTH_SHORT).show();
									return;
								}
								all_items.add(word);
								if (currList == 0){
									items.add(word);
								}
								final RadioGroup g = (RadioGroup) v.findViewById(R.id.wordType);
								int selected = g.getCheckedRadioButtonId();
								if (selected == R.id.incorp){
									include = true;
								} else {
									include = false;
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
								update();
								placeholder.setVisibility(View.GONE);
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
		
		
		ImageButton trash = (ImageButton) findViewById(R.id.wordsdelete);
		trash.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View view) {
				deleteMode = !deleteMode;
				adapter.notifyDataSetChanged();
				update();
			}
		});
	}
	
	public void addRadioListener(){
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.timingOp);
	    radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	    		RadioButton rtimer = (RadioButton) findViewById(R.id.radioTimer);
	    		RadioButton rstop = (RadioButton) findViewById(R.id.radioStopwatch);
	        	if (rtimer.isChecked()){
	        		timer = true;
	        	} else if (rstop.isChecked()){
	        		timer = false;
	        	}
	        	
	        }
	    });
	}
	
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
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	        view = inflater.inflate(R.layout.buzzlist, null);		    
		    //Handle buttons and add onClickListeners
		    ImageButton deleteBtn = (ImageButton) view.findViewById(R.id.delete_btn);
		    if(deleteMode){
		    	deleteBtn.setVisibility(View.VISIBLE);
		    }
		    deleteBtn.setOnClickListener(new View.OnClickListener(){
		        @Override
		        public void onClick(View v) { 
		        	if (deleteMode){
		        	String deletedWord = list.get(position);
		            list.remove(position); 
		            all_items.remove(deletedWord);
		            if (good_items.contains(deletedWord)){
		            	good_items.remove(deletedWord);
		            } else if (bad_items.contains(deletedWord)){
		            	bad_items.remove(deletedWord);
		            }
		            notifyDataSetChanged();
		            update();
		        	}
		        	if (all_items.size() == 0){
		        		placeholder.setVisibility(View.VISIBLE);
		        		deleteMode = false;
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
				update();
				currList = 0;
	    	} else if (selected.equals("Avoid")) {
				items.clear();
				items.addAll(bad_items);
				adapter.notifyDataSetChanged();
				update();
				currList = 2;
	    	} else if (selected.equals("Incorporate")) {
				items.clear();
				items.addAll(good_items);
				adapter.notifyDataSetChanged();
				update();
				currList = 1;
	    	}
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	    }
	}
	public void update(){
		buzzList.removeAllViews();
		final int adapterCount = adapter.getCount();
		for (int i = 0; i < adapterCount; i++) {
			  View item = adapter.getView(i, null, null);
			  buzzList.addView(item);
			}
	}
	  
}

