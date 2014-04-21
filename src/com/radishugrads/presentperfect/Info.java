package com.radishugrads.presentperfect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Info extends Activity {
	
	private MediaPlayer   mPlayer = null;
	String filePath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		Intent intent = getIntent();
		filePath = intent.getExtras().getString("recordPath");
		
//		mPlayer = new MediaPlayer();
//		try{
//			File mFile = new File(filePath);
//	    	FileInputStream inputStream = new FileInputStream(mFile);
//	    	mPlayer.setDataSource(inputStream.getFD());
//	    	inputStream.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		TextView t = (TextView) findViewById(R.id.speechTime);
//		t.setText("Speech time: " + mPlayer.getDuration() );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
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

}
