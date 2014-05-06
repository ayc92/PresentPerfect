package com.radishugrads.presentperfect;

import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class ListFragment extends TabFragment {

	@Override
	void setUpView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void setUp() {
		// TODO Auto-generated method stub
		addButton = (Button) currentTabView.findViewById(R.id.addProject);
		delButton = (Button) currentTabView.findViewById(R.id.removeParent);
//		if (tab.equals("shared")){
//			addButton.setVisibility(View.GONE);
//		}
		OnClickListener buttonListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.addProject:
					addClick(v);
					break;
				case R.id.removeParent:
					//startRemove();
					break;
				case R.id.addRec:
					//add(v, "child", groupPosition);
				}
			}
		};
		
		addButton.setOnClickListener(buttonListener);
		delButton.setOnClickListener(buttonListener);
	}

	private void addClick(View v) {
		//add(v, "group", -10);
	}
	

	@Override
	void prepareList() {
		// TODO Auto-generated method stub
		
	}

}
