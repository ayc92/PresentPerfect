package com.radishugrads.presentperfect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
public class TabsPagerAdapter extends FragmentPagerAdapter {
	ArrayList<String> listDataHeader;
	 HashMap<String, List<String>>listDataChild;
	 
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		System.out.println("called this");
		// only the notifications tab is not an expandable list view
		if (index == 0) {
			return new TabList();
		} else if (index == 2){
			ExpandableListFragment exp = ExpandableListFragment.newInstance(5, "shared");
			return exp;
		} else {
			ExpandableListFragment exp = ExpandableListFragment.newInstance(5, "recordings");
			return exp;
		}
	}

	@Override
	public int getCount() {
		return 4;
	}
	
	void prepareList() {
		listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        // Adding projects/headers
        listDataHeader.add("Google");
        listDataHeader.add("Angel");
        listDataHeader.add("Microsoft");
        listDataHeader.add("Qualcomm");
        listDataHeader.add("VC");
	
        //Adding recordings/children
        // Adding child data
        List<String>  one = new ArrayList<String>();
        one.add("Rec 1 - 04/01/14");
        one.add("Rec 2 - 04/01/14");
        one.add("Rec 3 - 04/02/14");
        one.add("Rec 4 - 04/02/14");
        one.add("Rec 5 - 04/02/14");
        one.add("Rec 6 - 04/03/14");
        one.add("Rec 7 - 04/07/14");
        
        List<String>  two = new ArrayList<String>();
        two.add("Rec 1 - 04/01/14");
        two.add("Rec 2 - 04/01/14");
        two.add("Rec 3 - 04/02/14");
        two.add("Rec 4 - 04/02/14");
        two.add("Rec 5 - 04/02/14");
        two.add("Rec 6 - 04/03/14");
        two.add("Rec 7 - 04/07/14");
        
        List<String>  three = new ArrayList<String>();
        three.add("Rec 1 - 04/01/14");
        three.add("Rec 2 - 04/01/14");
        three.add("Rec 3 - 04/02/14");
        three.add("Rec 4 - 04/02/14");
        three.add("Rec 5 - 04/02/14");
        three.add("Rec 6 - 04/03/14");
        three.add("Rec 7 - 04/07/14");
        
        List<String>  four = new ArrayList<String>();
        four.add("Rec 1 - 04/01/14");
        four.add("Rec 2 - 04/01/14");
        four.add("Rec 3 - 04/02/14");
        four.add("Rec 4 - 04/02/14");
        four.add("Rec 5 - 04/02/14");
        four.add("Rec 6 - 04/03/14");
        four.add("Rec 7 - 04/07/14");
	
        List<String>  five = new ArrayList<String>();
        five.add("Rec 1 - 04/01/14");
        five.add("Rec 2 - 04/01/14");
        five.add("Rec 3 - 04/02/14");
        five.add("Rec 4 - 04/02/14");
        five.add("Rec 5 - 04/02/14");
        five.add("Rec 6 - 04/03/14");
        five.add("Rec 7 - 04/07/14");
	
        listDataChild.put(listDataHeader.get(0), one);
        listDataChild.put(listDataHeader.get(1), two);
        listDataChild.put(listDataHeader.get(2), three);
        listDataChild.put(listDataHeader.get(3), four);
        listDataChild.put(listDataHeader.get(4), five);
	}
	
}
