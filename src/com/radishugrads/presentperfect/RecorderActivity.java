package com.radishugrads.presentperfect;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecorderActivity extends Activity {
	
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
		
		// get needed views
		recordButton = (ImageButton) findViewById(R.id.begin_record);
		pauseButton = (ImageButton) findViewById(R.id.pause_record);
		timeDisplay = (TextView) findViewById(R.id.time_display);
		
		// initialize data vars
		wordCounts = new HashMap<String, Integer>();
		wordsPerMin = 0;
		
		// setup animations
		slideLeft = new TranslateAnimation (
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.7f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
				);
		slideLeft.setFillEnabled(true);
		slideLeft.setDuration(500);
		slideLeft.setAnimationListener(new SlideListenerWithView((View) recordButton, -1));
		
		slideRight = new TranslateAnimation (
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.7f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
				);
		slideRight.setDuration(500);
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
	
	public void onRecordPressed(View v) {
		int vid = v.getId();
		switch (vid) {
			case R.id.begin_record:
				// if the user ever presses this button twice consecutively, goto feedback screen
				if (isRecording || isPaused) {
					System.out.println("in here");
					recordButton.setImageResource(R.drawable.ic_action_mic);
					handler.removeCallbacks(updateTime);
					
					mRecorder.stop();
			    	mRecorder.reset();
			        mRecorder.release();
			        mRecorder = null;
			        
					Intent recordIntent = new Intent(this, Info.class);
					recordIntent.putExtra("recordPath", filePath);
					startActivity(recordIntent);
				} else {
					System.out.println("in here 2");
					if (animEnabled) {
						slideButtons();
						animEnabled = false;
						
						// TODO: start feedback activity
					}
					recordButton.setImageResource(R.drawable.ic_action_mic_active);
					// start timer/stopwatch
					handler.postDelayed(updateTime, 0);
					
					// TODO: speech recognition
//					recorder.startRecording();
					runNewRecordTask();
					
					mRecorder = new MediaRecorder();
			        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			        mRecorder.setOutputFile(filePath);
			        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			        try {
			            mRecorder.prepare();
			        } catch (IOException e) {
			            Log.e("", "prepare() failed");
			        }

			        mRecorder.start();
				}
				isRecording = !isRecording;
				break;
			case R.id.pause_record:
				if (isRecording) {
					pauseButton.setImageResource(R.drawable.ic_action_play);
					handler.removeCallbacks(updateTime);
					mRecorder.stop();
				} else {
					mRecorder.start();
					pauseButton.setImageResource(R.drawable.ic_action_pause);
					handler.postDelayed(updateTime, 0);
					runNewRecordTask();
				}
				isPaused = !isPaused;
				isRecording = !isRecording;
				break;
		}
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
			v.setX(v.getX() + direction * v.getWidth() * 0.7f);
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
