package com.radishugrads.presentperfect;

import java.io.File;
import java.util.HashMap;

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
	
	// views
	RelativeLayout recordView;
	ImageButton recordButton;
	ImageButton pauseButton;
	TextView timeDisplay;
	
	// time displays (default is stopwatch)
	Handler handler;
	int timeInSecs;
	
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
	HashMap<String, Integer> wordCounts;
	int wordsPerMin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recording);
		
		// format action bar
		formatActionBar("Recorder");
		
		// get needed views
		recordButton = (ImageButton) findViewById(R.id.begin_record);
		pauseButton = (ImageButton) findViewById(R.id.pause_record);
		timeDisplay = (TextView) findViewById(R.id.time_display);
		
		// initialize data vars
		wordCounts = new HashMap<String, Integer>();
		wordsPerMin = 0;
		
		// initialize button images to handle press and hold
		recordButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
					if (isRecording) {
						recordButton.setImageResource(R.drawable.new_record_button_active_pressed);
					} else {
						recordButton.setImageResource(R.drawable.new_record_button_pressed);
					}
				} else {
					if (isRecording || isPaused) {
						recordButton.setImageResource(R.drawable.new_record_button);
						handler.removeCallbacks(updateTime);
						
						Intent recordIntent = new Intent(v.getContext(), Info.class);
						recordIntent.putExtra("recordPath", filePath);
						startActivity(recordIntent);
					} else {
						if (animEnabled) {
							slideButtons();
							animEnabled = false;
						}
						recordButton.setImageResource(R.drawable.new_record_button_active);
						// start timer/stopwatch
						handler.postDelayed(updateTime, 800);
						
						// TODO: speech recognition
					}
					isRecording = !isRecording;
				}
				return false;
			}
		});
		pauseButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
					if (isRecording) {
						pauseButton.setImageResource(R.drawable.new_pause_button_pressed);
					} else {
						pauseButton.setImageResource(R.drawable.new_play_button_pressed);
					}
				} else {
					if (isRecording) {
						pauseButton.setImageResource(R.drawable.new_play_button);
						handler.removeCallbacks(updateTime);
					} else {
						pauseButton.setImageResource(R.drawable.new_pause_button);
						handler.postDelayed(updateTime, 800);
					}
					isPaused = !isPaused;
					isRecording = !isRecording;
				}
				return false;
			}
		});
		
		// setup animations
		slideLeft = new TranslateAnimation (
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.5f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
				);
		slideLeft.setFillEnabled(true);
		slideLeft.setDuration(200);
		slideLeft.setAnimationListener(new SlideListenerWithView((View) recordButton, -1));
		
		slideRight = new TranslateAnimation (
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
				);
		slideRight.setDuration(200);
		slideRight.setAnimationListener(new SlideListenerWithView((View) pauseButton, 1));
		
		// set flags
		isRecording = false;
		animEnabled = true;
		isPaused = false;
		
		// setup timer
		handler = new Handler();
		timeInSecs = 0;
		
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
	    		startActivity(recordIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void slideButtons() {
		recordButton.startAnimation(slideLeft);
		pauseButton.startAnimation(slideRight);
	}
	
	// runnable for updating time
	private Runnable updateTime = new Runnable() {
		public void run() {
			timeInSecs += 1;
			timeDisplay.setText(String.format("%1$02d:%2$02d", timeInSecs / 60, timeInSecs % 60));
			handler.postDelayed(this, 1000);
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
				// System.out.println(audioTrack.write(buffer, 0, bufReadResult) + " bytes written to track.");
				// System.out.println(bufReadResult);				
			}
			recorder.stop();
			return null;
		}
	}
}
