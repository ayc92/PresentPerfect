package com.radishugrads.presentperfect;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;

public abstract class MotherBrain extends FragmentActivity {
	ActionBar actionBar;

	void formatActionBar(String subtitle) {
		actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(0, 72, 108)));
        actionBar.setTitle("PresentPerfect");
        actionBar.setSubtitle(subtitle);
	}
}
