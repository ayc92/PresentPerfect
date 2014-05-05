package com.radishugrads.presentperfect;
import android.view.MotionEvent;
import android.view.View;

public abstract class ButtonOnTouchListener implements View.OnTouchListener {
	
	protected int startResource;
	protected int downStartResource;
	protected int endResource;
	protected int downEndResource;
	
	public ButtonOnTouchListener(int s, int ds, int e, int de) {
		startResource = s;
		downStartResource = ds;
		endResource = e;
		downEndResource = de;
	}
	
	@Override
	public abstract boolean onTouch(View v, MotionEvent event);

}
