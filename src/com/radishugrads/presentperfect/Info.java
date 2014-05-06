package com.radishugrads.presentperfect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Info extends MotherBrain implements Handler.Callback, MediaPlayer.OnCompletionListener {
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
	boolean isPlaying = false;
	boolean changeImage = false;
	Handler myHandler;
	ImageView arrow1;
	ImageView arrow2;
	ImageView arrow3;
	ImageView mediaButton;
	int currentImage;
	ArrayList<String> words;
	ArrayList<Integer> counts;
	ArrayList<String> chosen_contacts;
	wordCountAdapter adapter;
	String contacts[] = {"Angel", "Beyonce", "Bob", "King Henry", "Mr. Clean", "Zoo"};
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
	boolean is_timer;
	boolean over_time;
	Bundle data;
	
	private MediaPlayer mPlayer = null;
	String filePath = null;
	Handler handler = new Handler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		myHandler = new Handler();
		data = getIntent().getExtras();
		// format action bar
		formatActionBar("Feedback for " + data.getString("rec_name"));
		is_timer = data.getBoolean("is_timer");
		actual_min = data.getInt("cur_time") / 60;
		actual_sec = data.getInt("cur_time") % 60;
		target_min = data.getInt("time_limit") / 60;
		target_sec = data.getInt("time_limit") % 60;
		over_time = data.getBoolean("over_time");
		wpm = data.getInt("wpm");
		good_items = new ArrayList<String>(((HashMap<String, Integer>) data.getSerializable("good")).keySet());
		good_counts = new ArrayList<Integer>(((HashMap<String, Integer>) data.getSerializable("good")).values());
		
		bad_items = new ArrayList<String>(((HashMap<String, Integer>) data.getSerializable("bad")).keySet());
		bad_counts = new ArrayList<Integer>(((HashMap<String, Integer>) data.getSerializable("bad")).values());
		
		all_items = new ArrayList<String>(((HashMap<String, Integer>) data.getSerializable("all")).keySet());
		all_counts = new ArrayList<Integer>(((HashMap<String, Integer>) data.getSerializable("all")).values());
		
		words = new ArrayList<String>();
		words.addAll(all_items);
		counts = new ArrayList<Integer>();
		counts.addAll(all_counts);
		updateList();
		
		mediaButton = (ImageView) findViewById(R.id.playrec);

		time_f = (TextView) findViewById(R.id.speechtime);
		speed_f = (TextView) findViewById(R.id.wordspermin);
		if (over_time){
			time_f.setBackgroundColor(Color.parseColor("#D22027"));
			if (is_timer) {
				time_f.setText(String.format("%1$02d:%2$02d is up!", target_min, target_sec));
			} else {
				time_f.setText(String.format("Speech time: %1$02d:%2$02d", actual_min, actual_sec));
			}
		}
		if (!over_time) {
			time_f.setText(String.format("Speech time: %1$02d:%2$02d", actual_min, actual_sec));
		}
		if (wpm > 150 || wpm < 90){
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
		File dir = new File(Environment.getExternalStorageDirectory().getPath(), "PresentPerfect");
		filePath = dir + "/" + getIntent().getStringExtra("rec_name") + ".wav";
		speechToText();
		mediaButton.setOnTouchListener(new playbackButtonOnTouchListener(R.drawable.playb,  
				R.drawable.playb_pressed, 
				R.drawable.pauseb,
				R.drawable.pauseb_pressed));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_share:
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);

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
					break;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		Intent recordIntent = new Intent(this, RecList.class);
		startActivity(recordIntent);
		finish();
	}
	
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

	public void playbackStart() {
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(this);
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
	
	public void playbackStop() {
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}
	
	public void updateList(){
		Log.d("asdf", "starting updateList()");
		LinearLayout countList = (LinearLayout) findViewById(R.id.countList);
		countList.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < counts.size(); i++){
			View view;
//		    Log.d("OOOO", "INNNNN"); 
		    view = inflater.inflate(R.layout.wordcountlist, null);
//		    Log.d("OOOO", "INNNN222");
		    TextView listNum = (TextView)view.findViewById(R.id.numbercount); 
		    listNum.setText("" + counts.get(i));
//		    Log.d("OOOO", "IN 33333");
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
//		    Log.d("OOOO", "INNNNN"); 
		    view = inflater.inflate(R.layout.commentslist, null);
//		    Log.d("OOOO", "INNNN222");
		    TextView listUser = (TextView)view.findViewById(R.id.commentuser); 
		    listUser.setText("Bob says"); 
//		    Log.d("OOOO", "IN 33333");
		    TextView listComment = (TextView)view.findViewById(R.id.commenttext); 
		    listComment.setText(comments[i]);
		    comment_view.addView(view);
		}
		  
	}
	
	private void speechToText() {
		File f = new File(filePath);
		if(!f.exists()){
			Log.d("asdf", "speechToText on nonexistant file");
			return;
		}
		RecognitionV2 r = new RecognitionV2(handler, filePath);
		new Thread(r, "Speech2Text thread").start();
	}
	
	public boolean handleMessage(Message msg) {
		String transcription = (String) msg.obj;
		Log.d("asdf", "Transcription: " + transcription);
		String[] wordArray = transcription.split(" ");
		int numWds = wordArray.length;
		wpm = (int) (numWds / (actual_min + (actual_sec / 60.0)));
		TextView wpmView = ((TextView) findViewById(R.id.wordspermin));
		wpmView.setText("Speed: "+wpm+" wpm");
		if (wpm > 150 || wpm < 60){
			wpmView.setBackgroundColor(Color.parseColor("#D22027"));
		}
		wpmView.invalidate();
		// buzzword counts
		int[] goodCount = new int[good_items.size()];
		int[] badCount = new int[bad_items.size()];
		for(String word : wordArray) {
			for(int i = 0; i < good_items.size(); i++) {
				if( word.toLowerCase().equals(good_items.get(i).toLowerCase()) ) {
					goodCount[i] ++;
				}
			}
			for(int i = 0; i < bad_items.size(); i++) {
				if( word.toLowerCase().equals(bad_items.get(i).toLowerCase()) ) {
					badCount[i] ++;
				}
			}
		}
		good_counts = new ArrayList<Integer>();
		for(int c : goodCount) {
			good_counts.add(c);
		}
		bad_counts = new ArrayList<Integer>();
		for(int c : badCount) {
			bad_counts.add(c);
		}
		all_items = new ArrayList<String>();
		all_items.addAll(good_items);
		all_items.addAll(bad_items);
		all_counts = new ArrayList<Integer>();
		all_counts.addAll(good_counts);
		all_counts.addAll(bad_counts);
		updateList();
		return true;
	}
	
	public void onCompletion(MediaPlayer mp) {
		((ImageButton) findViewById(R.id.playrec)).performClick();
		playbackStop();
	}

		
	private Runnable finishPush = new Runnable() {
		public void run() {
			switch (currentImage) {
				case R.drawable.playb_pressed:
					mediaButton.setImageResource(R.drawable.pauseb);
					if (mPlayer == null) {
						playbackStart();
					} else {
						mPlayer.start();
					}
					break;
				case R.drawable.pauseb_pressed:
					mediaButton.setImageResource(R.drawable.playb);
					if (mPlayer != null && mPlayer.isPlaying()) {
						mPlayer.stop();
					}
					break;
					
			}
		}
		
	};
	
	private void changeImage(int ID) {
		mediaButton.setImageResource(ID);
		currentImage = ID;
	}
	
	private class playbackButtonOnTouchListener extends ButtonOnTouchListener {
		
		public playbackButtonOnTouchListener(int s, int ds, int e, int de) {
			super(s, ds, e, de);
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				if (isPlaying) { //Pause Button Showing
					changeImage(downEndResource);
				} else { //Play Button Showing
					changeImage(downStartResource);
				}
				changeImage = true;
				
			}
			else {
				if (changeImage) {
					myHandler.postDelayed(finishPush, 200);
					changeImage = false;
					isPlaying = !isPlaying;
				}
			} 
			return true;
		}
	}

}
