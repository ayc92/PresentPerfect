package com.radishugrads.presentperfect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecorderActivity extends MotherBrain {
	
	Context context;
	
	// views
	RelativeLayout recordView;
	ImageButton recordButton;
	ImageButton pauseButton;
	ImageButton settingsButton;
	TextView timeDisplay;
	TextView instructions;
	
	// params and default params
	Bundle params;
	private final boolean IS_TIMER = true;
	private final int TIME_LIMIT = 5;
	
	// time displays (default is stopwatch)
	Handler handler;
	boolean isTimer;
	int timeInSecs;
	int timeLimit;
	
	// current mic
	Handler micHandler;
	int currentMic;
	
	// animation
	TranslateAnimation slideLeft;
	TranslateAnimation slideRight;
	LayoutAnimationController animController;
	AnimationListener animListener;
	
	// flags
	boolean isRecording;
	boolean animEnabled;
	boolean isPaused;
	
	// recording variables
	static final int SAMPLE_RATE = 8000;
	byte[] buffer;
	AudioRecord recorder;
	int bufReadResult;
	AudioTrack audioTrack;
	RecordTask recordTask;
	
	File mFile = null;
	String filePath = null;
	private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
	
	// data variables
	HashMap<String, Integer> goodWordCounts;
	HashMap<String, Integer> badWordCounts;
	HashMap<String, Integer> allWordCounts;
	int wordsPerMin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recording);
		
		context = this;
		
		// format action bar
		formatActionBar("Recorder");
		
		// get params
		goodWordCounts = new HashMap<String, Integer>();
		badWordCounts = new HashMap<String, Integer>();
		allWordCounts = new HashMap<String, Integer>();
		
		params = getIntent().getExtras();
		if (params != null) {
			ArrayList<String> good_items = params.getStringArrayList("good_items");
			ArrayList<String> bad_items = params.getStringArrayList("bad_items");
			ArrayList<String> all_items = params.getStringArrayList("all_items");
			
			for (String str : good_items) {
				goodWordCounts.put(str, 0);
			}
			for (String str : bad_items) {
				badWordCounts.put(str, 0);
			}
			for (String str : all_items) {
				allWordCounts.put(str, 0);
			}
			isTimer = params.getBoolean("timer");
			if (isTimer) {
				timeInSecs = params.getInt("min") * 60;
				timeLimit = timeInSecs;
			} else {
				timeInSecs = 0;
				timeLimit = params.getInt("min") * 60;
			}
		} else {
			// initialize params first
			params = new Bundle();
			params.putStringArrayList("good_items", new ArrayList<String>());
			params.putStringArrayList("bad_items", new ArrayList<String>());
			params.putStringArrayList("all_items", new ArrayList<String>());
			params.putBoolean("timer", true);
			params.putInt("min", TIME_LIMIT);
			
			isTimer = IS_TIMER;
			timeInSecs = TIME_LIMIT * 60;
			timeLimit = timeInSecs;
		}
		
		
		// get needed views
		recordButton = (ImageButton) findViewById(R.id.begin_record);
		pauseButton = (ImageButton) findViewById(R.id.pause_record);
		settingsButton = (ImageButton) findViewById(R.id.settings_button);
		timeDisplay = (TextView) findViewById(R.id.time_display);
		instructions = (TextView) findViewById(R.id.instructions);
		
		timeDisplay.setText(String.format("%1$02d:%2$02d", timeInSecs / 60, timeInSecs % 60));
		
		// initialize settings button onclick		
		//last two args not used
		settingsButton.setOnTouchListener(new SettingsButtonOnTouchListener(R.drawable.settings,
				R.drawable.settings_down, 0, 0));
		
		// initialize button images to handle press and hold
		recordButton.setOnTouchListener(new RecordButtonOnTouchListener(R.drawable.new_record_button,
				R.drawable.new_record_button_pressed,
				R.drawable.new_record_button_active,
				R.drawable.new_record_button_active_pressed));
		pauseButton.setOnTouchListener(new PauseButtonOnTouchListener(R.drawable.new_pause_button,
				R.drawable.new_pause_button_pressed,
				R.drawable.new_play_button,
				R.drawable.new_play_button_pressed));
		
		// setup animations
		slideLeft = new TranslateAnimation (
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.5f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
				);
		slideLeft.setFillEnabled(true);
		slideLeft.setDuration(300);
		slideLeft.setAnimationListener(new SlideListenerWithView((View) recordButton, -1));
		
		slideRight = new TranslateAnimation (
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
				);
		slideRight.setDuration(300);
		slideRight.setAnimationListener(new SlideListenerWithView((View) pauseButton, 1));
		
		// set flags
		isRecording = false;
		animEnabled = true;
		isPaused = false;
		
		// setup timer
		handler = new Handler();
		
		// words per min
		wordsPerMin = 0;
		
		// setup current mic
		currentMic = R.drawable.new_record_button;
		
		// setup sound recorder and audio track
		int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
		        AudioFormat.ENCODING_PCM_16BIT);
		buffer = new byte[bufferSize];
		recorder = new AudioRecord(
				MediaRecorder.AudioSource.MIC,
				SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO,
		        AudioFormat.ENCODING_PCM_16BIT,
		        bufferSize);
		audioTrack = new AudioTrack(
        		AudioManager.STREAM_MUSIC,
        		SAMPLE_RATE,
        		AudioFormat.CHANNEL_OUT_MONO,
        		AudioFormat.ENCODING_PCM_16BIT,
        		bufferSize,
        		AudioTrack.MODE_STATIC);
		
		mFile = new File(getFilesDir(), "audiotest.3gp");
		filePath = mFile.getAbsolutePath();
	}

	@Override
	public void onBackPressed() {
		Intent recordIntent = new Intent(this, RecList.class);
		startActivity(recordIntent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mother_brain, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Intent recordIntent = new Intent(this, OptionsActivity.class);
	        	recordIntent.putExtras(params);
	    		startActivity(recordIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	// remove callbacks and create new intent
	private void sendFeedbackIntent() {
		handler.removeCallbacks(updateTime);
		handler.removeCallbacks(flashMic);

		Intent recordIntent = new Intent(context, Info.class);
		recordIntent.putExtra("recordPath", filePath);
		Bundle data = new Bundle();
		data.putBoolean("is_timer", isTimer);
		if (!isTimer) {
			data.putBoolean("over_time", timeInSecs > timeLimit);
			data.putInt("cur_time", timeInSecs);
		} else {
			data.putBoolean("over_time", timeInSecs <= 0);
			data.putInt("cur_time", timeLimit - timeInSecs);
		}
		data.putSerializable("good", goodWordCounts);
		data.putSerializable("bad", badWordCounts);
		data.putSerializable("all", allWordCounts);
		data.putSerializable("wpm", wordsPerMin);
		
		data.putInt("time_limit", timeLimit);
		recordIntent.putExtras(data);
		startActivity(recordIntent);
	}
	
	// run button slide animation
	private void slideButtons() {
		recordButton.startAnimation(slideLeft);
		pauseButton.startAnimation(slideRight);
	}

	// update button image and also set current button variable
	private void setRecordButtonImage(int resourceId) {
		recordButton.setImageResource(resourceId);
		currentMic = resourceId;
	}
	
	// runnable for updating time
	private Runnable updateTime = new Runnable() {
		public void run() {
			if (isTimer) {
				if (timeInSecs == 0) {
					setRecordButtonImage(R.drawable.new_record_button);
					sendFeedbackIntent();
					return;
				} else {
					timeInSecs -= 1;
				}
			} else {
				timeInSecs += 1;
			}
			timeDisplay.setText(String.format("%1$02d:%2$02d", timeInSecs / 60, timeInSecs % 60));
			handler.postDelayed(this, 1000);
		}
	};
	
	// runnable for indicating record mode (flashing)
	private Runnable flashMic = new Runnable() {
		public void run() {
			switch(currentMic) {
			case R.drawable.new_record_button:
				setRecordButtonImage(R.drawable.new_record_button_active);
				break;
			case R.drawable.new_record_button_active:
				setRecordButtonImage(R.drawable.new_record_button);
				break;
			}
			handler.postDelayed(this, 800);
		}
	};
	
	// class for handling stuff after animation
	private class SlideListenerWithView implements AnimationListener {
		View v;
		// right = 1, left = -1
		int direction;

		public SlideListenerWithView(View v, int direction) {
			this.v = v;
			this.direction = direction;
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			v.setX(v.getX() + direction * v.getWidth() * 0.5f);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}
		
	}
	
	// helper method for starting a new record task
	public void runNewRecordTask() {
		recordTask = new RecordTask();
		recordTask.execute();
	}
	
	// async task for recording in background thread
	private class RecordTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			while(isRecording) {
				bufReadResult = recorder.read(buffer, 0, buffer.length);
				// TODO: make this into a track that can be played back
								
			}
			recorder.stop();
			return null;
		}
	}
	
	private class RecordButtonOnTouchListener extends ButtonOnTouchListener {

		public RecordButtonOnTouchListener(int s, int ds, int e, int de) {
			super(s, ds, e, de);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				if (isRecording) {
					setRecordButtonImage(downEndResource);
				} else {
					setRecordButtonImage(downStartResource);
				}
			} else {
				if (isRecording || isPaused) {
					setRecordButtonImage(startResource);
					sendFeedbackIntent();
				} else {
					if (animEnabled) {
						slideButtons();
						animEnabled = false;
					}
					setRecordButtonImage(endResource);
					instructions.setText(R.string.instr_2);
					// start timer/stopwatch
					handler.postDelayed(updateTime, 800);
					handler.postDelayed(flashMic, 200);
					
					// TODO: speech recognition
				}
				isRecording = !isRecording;
			}
			return false;
		}
	}
	
	private class PauseButtonOnTouchListener extends ButtonOnTouchListener {

		public PauseButtonOnTouchListener(int s, int ds, int e, int de) {
			super(s, ds, e, de);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				if (isRecording) {
					pauseButton.setImageResource(downStartResource);
				} else {
					pauseButton.setImageResource(downEndResource);
				}
			} else {
				if (isRecording) {
					pauseButton.setImageResource(downEndResource);
					handler.removeCallbacks(updateTime);
					handler.removeCallbacks(flashMic);
				} else {
					pauseButton.setImageResource(downStartResource);
					handler.postDelayed(updateTime, 800);
					handler.postDelayed(flashMic, 200);
				}
				isPaused = !isPaused;
				isRecording = !isRecording;
			}
			return false;
		}
	}
	
	private class SettingsButtonOnTouchListener extends ButtonOnTouchListener {

		public SettingsButtonOnTouchListener(int s, int ds, int e, int de) {
			super(s, ds, e, de);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				settingsButton.setImageResource(downStartResource);
			} else {
				settingsButton.setImageResource(startResource);
				Intent recordIntent = new Intent(context, OptionsActivity.class);
	        	recordIntent.putExtras(params);
	    		startActivity(recordIntent);
			}
			return false;
		}
		
	}
}
