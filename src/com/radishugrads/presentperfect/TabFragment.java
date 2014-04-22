package com.radishugrads.presentperfect;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TabFragment extends Fragment {
	
	View currentTabView;
	
	Context context;
	Context appContext;
	
	List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		appContext = context.getApplicationContext();
		currentTabView = inflater.inflate(R.layout.activity_rec_list, container, false);
//		expListView = (ExpandableListView) currentTabView.findViewById(R.id.lvExp);
//		prepareList();
//		listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
//		expListView.setAdapter(listAdapter);
//		setUp();
		setUpView();
		return currentTabView;
	}
	
	abstract void setUpView();
	abstract void setUp();
	abstract void prepareList();
}
