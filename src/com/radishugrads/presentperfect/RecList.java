package com.radishugrads.presentperfect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

/*
 * Code based on tutorial:
 * http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */
public class RecList extends FragmentActivity implements TabListener {
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private final int NONE_EXPANDED = -1;
    private int lastExpanded = NONE_EXPANDED;
    public final static String ADD_BUTTON = "[  Add New Recording  ]";
    private final int ADD_CHILD = 0;
    private String newest_input = "";
    private boolean changed = false;
    private boolean isGroup = false;
    private boolean removeButtonPushed = false; //Remove Mode Activated?
    private int lastId = -1;
    private final int ADDGROUP = -10;
    private final Context context = this;
    private boolean isExpanded = false;
    private int currExpanded = -1;
    private boolean deleteGroup;
    private int[] removeInfo = new int[2]; //index 0 is groupPosition, index 1 is childPosition 
    private boolean[] removeWhich = new boolean[2]; //index 0 is for (removeProjects?), index 1 is for (removeRecordings?)
    
    
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    
    // Tab titles
    private final String[] tabs = { "Notifications", "My Pitches", "Shared With Me", "Contacts" };
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_rec_list);
		setContentView(R.layout.tabs_view);
		// Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
         
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
         
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
		// get the listview
        /*
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        prepareList();
        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        setUp();
        */
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mother_brain, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}