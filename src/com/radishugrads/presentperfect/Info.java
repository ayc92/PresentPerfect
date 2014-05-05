package com.radishugrads.presentperfect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Info extends MotherBrain {
	int actual_min;
	int actual_sec;
	int target_min;
	int target_sec;
	int wpm;
	TextView time_f;
	TextView speed_f;
	Object recording;

	ListView listcount;
	LinearLayout comment_view;
	boolean wordVisib;
	boolean commentVisib;
	boolean notesVisib;
	ImageView arrow1;
	ImageView arrow2;
	ImageView arrow3;
	ArrayList<String> words;
	ArrayList<Integer> counts;
	ArrayList<String> chosen_contacts;
	wordCountAdapter adapter;
	String contacts[] = {"Marie Antoinette", "George Orwell", "Clifford", "my mom"};
	String comments[] = {"work a little more on your enthusiasm. good use of stories", "7/10 needs more oomph"};
	Spinner spinner1;
	LinearLayout firstPanel;
	LinearLayout notes;
	ArrayList<String> good_items;
	ArrayList<String> bad_items;
	ArrayList<String> all_items;
	ArrayList<Integer> good_counts;
	ArrayList<Integer> bad_counts;
	ArrayList<Integer> all_counts;
	Bundle data;

	private MediaPlayer mPlayer = null;
	String filePath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		// format action bar
		formatActionBar("Feedback");

		data = getIntent().getExtras();
		actual_min = data.getInt("cur_time") / 60;
		actual_sec = data.getInt("cur_time") % 60;
		target_min = data.getInt("time_limit") / 60;
		target_sec = data.getInt("time_limit") % 60;
		wpm = data.getInt("wpm");
		good_items = new ArrayList<String>(((HashMap<String, Integer>) data.getSerializable("good")).keySet());
		good_counts = new ArrayList<Integer>(((HashMap<String, Integer>) data.getSerializable("good")).values());

		bad_items = new ArrayList<String>(((HashMap<String, Integer>) data.getSerializable("bad")).keySet());
		bad_counts = new ArrayList<Integer>(((HashMap<String, Integer>) data.getSerializable("bad")).values());

		all_items = new ArrayList<String>(((HashMap<String, Integer>) data.getSerializable("all")).keySet());
		all_counts = new ArrayList<Integer>(((HashMap<String, Integer>) data.getSerializable("all")).values());

		words = all_items;
		counts = all_counts;

		time_f = (TextView) findViewById(R.id.speechtime);
		speed_f = (TextView) findViewById(R.id.wordspermin);
		if (actual_min > target_min || (actual_min == target_min && actual_sec > target_sec)){
			time_f.setBackgroundColor(Color.parseColor("#D22027"));
		}
		time_f.setText("Speech time: " + actual_min + ":" + actual_sec);
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
		comment_view = (LinearLayout) findViewById(R.id.commentlist);
		arrow1 = (ImageView) findViewById(R.id.imageView2);
		arrow2 = (ImageView) findViewById(R.id.imageView3);
		arrow3 = (ImageView) findViewById(R.id.imageView4);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(new SpinnerActivity1());
		updateList_comments();
		filePath = getFilesDir() + "/audiotest.3gp";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mother_brain, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent recordIntent = new Intent(this, RecList.class);
		startActivity(recordIntent);
		finish();
	}

//	public void tempHardcode(){
//		actual_min = 0;
//		actual_sec = 14;
//		target_min = 0;
//		target_sec = 13;
//		wpm = 120;
//		counts = new ArrayList<String>();
//		counts.add("like");
//		counts.add("um");
//		counts.add("user generated content");
//		all_items = new ArrayList<String>();
//		all_items.add("like");
//		all_items.add("um");
//		all_items.add("user generated content");
//	}

	public void toggle_contents(View v){
		if (wordVisib){
			firstPanel.setVisibility(View.GONE);
			arrow1.setImageResource(R.drawable.arrowdownblue);
		} else {
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
		    listNum.setText(position+""); 
		    Log.d("OOOO", "IN 33333");
		    TextView listWord = (TextView)view.findViewById(R.id.wordofnum); 
		    listWord.setText(list.get(position)); 
		    return view; 
		} 
		}


public class SpinnerActivity1 extends Activity implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	    	String selected = parent.getItemAtPosition(pos).toString();
	    	words.clear();
	    	counts.clear();
	    	if (selected.equals("All")){
				words.addAll(all_items);
				counts.addAll(all_counts);
				updateList();
	    	} else if (selected.equals("Avoid")) {
				words.addAll(bad_items);
				counts.addAll(bad_counts);
				updateList();
	    	} else if (selected.equals("Incorporate")) {
				words.addAll(good_items);
				counts.addAll(good_counts);
				updateList();
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

	public void playback(View v) {
		mPlayer = new MediaPlayer();
        try {
			File mFile = new File(filePath);
	    	FileInputStream inputStream = new FileInputStream(mFile);
	    	mPlayer.setDataSource(inputStream.getFD());
	    	inputStream.close();
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("", "prepare() failed");
        }
	}

	public void updateList(){
		LinearLayout countList = (LinearLayout) findViewById(R.id.countList);
		countList.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < counts.size(); i++){
			View view;
		    Log.d("OOOO", "INNNNN"); 
		    view = inflater.inflate(R.layout.wordcountlist, null);
		    Log.d("OOOO", "INNNN222");
		    TextView listNum = (TextView)view.findViewById(R.id.numbercount); 
		    listNum.setText("" + counts.get(i));
		    Log.d("OOOO", "IN 33333");
		    TextView listWord = (TextView)view.findViewById(R.id.wordofnum); 
		    listWord.setText(words.get(i));
		    countList.addView(view);
		}

	}
	public void updateList_comments(){
		comment_view.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < comments.length; i++){
			View view;
		    Log.d("OOOO", "INNNNN"); 
		    view = inflater.inflate(R.layout.commentslist, null);
		    Log.d("OOOO", "INNNN222");
		    TextView listUser = (TextView)view.findViewById(R.id.commentuser); 
		    listUser.setText("Bob says"); 
		    Log.d("OOOO", "IN 33333");
		    TextView listComment = (TextView)view.findViewById(R.id.commenttext); 
		    listComment.setText(comments[i]);
		    comment_view.addView(view);
		}

	}

}