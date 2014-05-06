package com.radishugrads.presentperfect;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public abstract class TabFragment extends Fragment {
	
	View currentTabView;
	Button addButton;
	Button delButton;
	ImageButton addGroup;
	
	Context context;
	Context appContext;
	
	static List<String> pitchListDataHeader;
    static HashMap<String, List<String>> pitchListDataChild;
    static List<String> sharedListDataHeader;
    static HashMap<String, List<String>> sharedListDataChild;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		appContext = context.getApplicationContext();
		currentTabView = inflater.inflate(R.layout.activity_rec_list, container, false);
		setUpView();
		return currentTabView;
	}
	
	abstract void setUpView();
	abstract void setUp();
	abstract void prepareList();
}
