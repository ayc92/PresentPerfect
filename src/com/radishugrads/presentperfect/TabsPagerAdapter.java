package com.radishugrads.presentperfect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		System.out.println("called this");
		// only the notifications tab is not an expandable list view
		if (index == 0) {
			return new ListFragment();
		} else {
			return new ExpandableListFragment();
		}
	}

	@Override
	public int getCount() {
		return 4;
	}
	
}
