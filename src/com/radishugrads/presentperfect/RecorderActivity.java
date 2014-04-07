package com.radishugrads.presentperfect;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
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
		
		// setup timer
		handler = new Handler();
		timeInSecs = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mother_brain, menu);
		return true;
	}
	
	private void slideButtons() {
		recordButton.startAnimation(slideLeft);
		pauseButton.startAnimation(slideRight);
	}
	
	public void onRecordPressed(View v) {
		int vid = v.getId();
		switch (vid) {
			case R.id.begin_record:
				if (animEnabled) {
					isRecording = !isRecording;
					if (isRecording) {
						System.out.println("in here");
						slideButtons();
						recordButton.setImageResource(R.drawable.ic_action_mic_active);
						// start timer/stopwatch
						handler.postDelayed(updateTime, 0);
						
						// TODO: begin recording and speech recognition
					} else {
						System.out.println("in here 2");
						recordButton.setImageResource(R.drawable.ic_action_mic);
						handler.removeCallbacks(updateTime);
						
						//after one start/stop record cycle, disable animation and recording capability
						animEnabled = false;
					}
				}
				break;
			case R.id.pause_record:
				if (isRecording) {
					pauseButton.setImageResource(R.drawable.ic_action_play);
					handler.removeCallbacks(updateTime);
				} else {
					pauseButton.setImageResource(R.drawable.ic_action_pause);
					handler.postDelayed(updateTime, 0);
				}
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
}
