package com.radishugrads.presentperfect;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Info extends Activity {
	int actual_hour;
	int actual_min;
	int target_hour;
	int target_min;
	int wpm;
	TextView time_f;
	TextView speed_f;
	Object recording;
	
	ListView listcount;
	ListView comment_view;
	boolean wordVisib;
	boolean commentVisib;
	boolean notesVisib;
	ImageView arrow1;
	ImageView arrow2;
	ImageView arrow3;
	ArrayList<String> counts;
	ArrayList<String> chosen_contacts;
	wordCountAdapter adapter;
	String contacts[] = {"Marie Antoinette", "George Orwell", "Clifford", "my mom"};
	String s2[] = {"work a little more on your enthusiasm. good use of stories", "7/10 needs more oomph"};
	Spinner spinner1;
	LinearLayout firstPanel;
	LinearLayout notes;
	ArrayList<String> good_items = new ArrayList<String>();
	ArrayList<String> bad_items = new ArrayList<String>();
	ArrayList<String> all_items = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		tempHardcode();
		time_f = (TextView) findViewById(R.id.speechtime);
		speed_f = (TextView) findViewById(R.id.wordspermin);
		if (actual_hour > target_hour || (actual_hour == target_hour && actual_min > target_min)){
			time_f.setBackgroundColor(Color.parseColor("#D22027"));
		}
		time_f.setText("Speech time: " + actual_hour + ":" + actual_min);
		if (wpm > 150 || wpm < 130){
			speed_f.setBackgroundColor(Color.parseColor("#D22027"));
		}
		speed_f.setText("Speed: " + wpm + " wpm");
		
		wordVisib = false;
		commentVisib = false;
		notesVisib = false;
		chosen_contacts = new ArrayList<String>();
		firstPanel = (LinearLayout) findViewById(R.id.firstPanel);
		notes = (LinearLayout) findViewById(R.id.thirdPanel);
		listcount = (ListView) findViewById(R.id.listcount);
		adapter = new wordCountAdapter(counts, this);
		listcount.setAdapter(adapter);
		comment_view = (ListView) findViewById(R.id.commentlist);
		comment_view.setAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, s2));
		arrow1 = (ImageView) findViewById(R.id.imageView2);
		arrow2 = (ImageView) findViewById(R.id.imageView3);
		arrow3 = (ImageView) findViewById(R.id.imageView4);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.word_choices, android.R.layout.simple_spinner_item);
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinner1.setAdapter(adapter);
		spinner1.setOnItemSelectedListener(new SpinnerActivity1());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}
	
	public void tempHardcode(){
		actual_hour = 0;
		actual_min = 14;
		target_hour = 0;
		target_min = 13;
		wpm = 120;
		counts = new ArrayList<String>();
		counts.add("like");
		counts.add("um");
		counts.add("user generated content");
		all_items = new ArrayList<String>();
		all_items.add("like");
		all_items.add("um");
		all_items.add("user generated content");
	}
	public void toggle_contents(View v){
		if (wordVisib){
//			listcount.setVisibility(View.GONE);
//			spinner1.setVisibility(View.GONE);
			firstPanel.setVisibility(View.GONE);
			arrow1.setImageResource(R.drawable.arrowdownblue);
		} else {
//	      listcount.setVisibility(View.VISIBLE);
//	      spinner1.setVisibility(View.VISIBLE);
			firstPanel.setVisibility(View.VISIBLE);
	      arrow1.setImageResource(R.drawable.arrowupblue);
		}
		wordVisib = !wordVisib;
	}
	
	public void toggle_contents2(View v){
		if (commentVisib){
			comment_view.setVisibility(View.GONE);
			arrow2.setImageResource(R.drawable.arrowdownblue);
		} else {
	      comment_view.setVisibility(View.VISIBLE);
	      arrow2.setImageResource(R.drawable.arrowupblue);
		}
		commentVisib = !commentVisib;
	}
	
	public void toggle_contents3(View v){
		if (notesVisib){
			notes.setVisibility(View.GONE);
			arrow3.setImageResource(R.drawable.arrowdownblue);
		} else {
	      notes.setVisibility(View.VISIBLE);
	      arrow3.setImageResource(R.drawable.arrowupblue);
		}
		notesVisib = !notesVisib;
	}
	
	public class wordCountAdapter extends BaseAdapter implements ListAdapter {
		private ArrayList<String> list; 
		private Context context; 



		public wordCountAdapter(ArrayList<String> list, Context context) { 
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
		        view = inflater.inflate(R.layout.wordcountlist, null);
		        Log.d("OOOO", "INNNN222");
		    TextView listNum = (TextView)view.findViewById(R.id.numbercount); 
		    listNum.setText(position+" "); 
		    Log.d("OOOO", "IN 33333");
		    TextView listWord = (TextView)view.findViewById(R.id.wordofnum); 
		    listWord.setText(list.get(position)); 
		    return view; 
		} 
		}
	
	public void hardcoded(){
		counts = new ArrayList<String>();
		counts.add("like");
		counts.add("um");
		counts.add("user generated content");
	}
	
public class SpinnerActivity1 extends Activity implements OnItemSelectedListener {
	    
	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	    	String selected = parent.getItemAtPosition(pos).toString();
	    	if (selected.equals("All")){
				counts.clear();
				counts.addAll(all_items);
				adapter.notifyDataSetChanged();
	    	} else if (selected.equals("Avoid")) {
				counts.clear();
				counts.addAll(bad_items);
				adapter.notifyDataSetChanged();
	    	} else if (selected.equals("Incorporate")) {
				counts.clear();
				counts.addAll(good_items);
				adapter.notifyDataSetChanged();
	    	}
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	    }
	}

public void shareAction(View v){
	
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			this);
	LayoutInflater inflater = getLayoutInflater();

    // Inflate and set the layout for the dialog
    // Pass null as the parent view because its going in the dialog layout
//	final View v = inflater.inflate(R.layout.buzzworddialog, null);
//	alertDialogBuilder.setView(v);
		// set title
		alertDialogBuilder.setTitle("Contacts to share with")
			.setCancelable(false)
			.setMultiChoiceItems(contacts, null,
                      new DialogInterface.OnMultiChoiceClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which,
                       boolean isChecked) {
                   if (isChecked) {
                       // If the user checked the item, add it to the selected items
                	   Log.d("ADDED: ", ""+contacts[which]);
                       chosen_contacts.add(contacts[which]);
                   } else if (chosen_contacts.contains(contacts[which])) {
                       // Else, if the item is already in the array, remove it 
                	   Log.d("REMOVED: ", ""+contacts[which]);
                       chosen_contacts.remove(contacts[which]);
                   }
               }
           })
			.setPositiveButton("Done",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					//MainActivity.this.finish();
					
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

}
