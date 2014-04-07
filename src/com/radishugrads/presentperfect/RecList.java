package com.radishugrads.presentperfect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;


/*
 * Code based on tutorial:
 * http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */
public class RecList extends Activity {
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private AlertDialog.Builder alert;
    private final int NONE_EXPANDED = -1;
    private int lastExpanded = NONE_EXPANDED;
    private final String ADD_BUTTON = "[  Add New Recording  ]";
    private final int ADD_CHILD = 0;
    private String newest_input = "";
    private boolean isTyping = false;
    private boolean changed = false;
    private boolean isGroup = false;
    private int lastId = -1;
    private final int ADDGROUP = -10;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rec_list);
		 // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        prepareList();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        alert = new AlertDialog.Builder(this);
        setUp();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mother_brain, menu);
		return true;
	}
	
	private void setUp() {
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				if (lastExpanded != NONE_EXPANDED &&
						groupPosition != lastExpanded) {
					expListView.collapseGroup(lastExpanded);
				}
				lastExpanded = groupPosition;
			}
		});
		
		 expListView.setOnChildClickListener(new OnChildClickListener() {
			 
	            @Override
	            public boolean onChildClick(ExpandableListView parent, View v,
	                    int groupPosition, int childPosition, long id) {
	            	if (childPosition == ADD_CHILD) {
	            		add(v, "child", groupPosition);
	            	} else {
	            		//GO TO THE INFO SCREEN FOR THAT RECORDING
	            	}
	                return false;
	            }
	        });
	}
	public void addClick(View v) {
		add(v, "group", -10);
	}
	
	public void add(View v, String group_or_child, int id) {
		lastId = id;
		isTyping = true;
		changed = false;
		if (group_or_child == "group") {
			isGroup = true;
		} else if (group_or_child == "child") {
			isGroup = false;
		}
		String other = "new project";
		if (!(isGroup)) {
			other = "new recording";
		}
		alert.setTitle(other.toUpperCase());
		alert.setMessage("Input name of "+ other + ".");
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				newest_input = input.getText().toString();
				changed = true;
				if (newest_input.length() < 1) {
					changed = false;
				}
				update_list();
				}
			});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
				  changed = false;
			  }
			});

		alert.show();
	}
	
	private void update_list() {
		if (changed) {
		addGroup(newest_input, isGroup, lastId);
		}
	}
	
	public void addGroup(String name, boolean isGroup, int which){ //Which refers to id of group or id of child
		SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy");
		String timeStamp = s.format(new Date()); // Find todays date
		boolean empty = false;
		if (!(isGroup) && (which != ADDGROUP)) {
			empty = (listAdapter.getChildrenCount(which)-1 == 0);
			}
		if (!(isGroup)) {
			listDataChild.get(listDataHeader.get(which)).add(newest_input + " - " + timeStamp);
		} else if (!(empty) && !(isGroup)) {
			listDataChild.get(listDataHeader.get(which)).add(newest_input + " - " + timeStamp);
		} else if (isGroup) {
			int size = listAdapter.getGroupCount();
			listDataHeader.add(name);
			List<String>  new_group = new ArrayList<String>();
			new_group.add(ADD_BUTTON);
			listDataChild.put(listDataHeader.get(size), new_group);
		}
		listAdapter.notifyDataSetChanged();
	}
	
	
	/*
	 * Put dummy stuff in list for prototype.
	 */
	private void prepareList() {
		listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        // Adding projects/headers
        listDataHeader.add("Google Pitch");
        listDataHeader.add("Angel Pitch");
        listDataHeader.add("Rich People Pitch");
        listDataHeader.add("Tech Convention Speech");
        listDataHeader.add("VC Pitch");
	
        //Adding recordings/children
     // Adding child data
        List<String>  one = new ArrayList<String>();
        one.add(ADD_BUTTON);
        one.add("Rec 1 - 04/01/14");
        one.add("Rec 2 - 04/01/14");
        one.add("Rec 3 - 04/02/14");
        one.add("Rec 4 - 04/02/14");
        one.add("Rec 5 - 04/02/14");
        one.add("Rec 6 - 04/03/14");
        one.add("Rec 7 - 04/07/14");
        
        List<String>  two = new ArrayList<String>();
        two.add(ADD_BUTTON);
        two.add("Rec 1 - 04/01/14");
        two.add("Rec 2 - 04/01/14");
        two.add("Rec 3 - 04/02/14");
        two.add("Rec 4 - 04/02/14");
        two.add("Rec 5 - 04/02/14");
        two.add("Rec 6 - 04/03/14");
        two.add("Rec 7 - 04/07/14");
        
        List<String>  three = new ArrayList<String>();
        three.add(ADD_BUTTON);
        three.add("Rec 1 - 04/01/14");
        three.add("Rec 2 - 04/01/14");
        three.add("Rec 3 - 04/02/14");
        three.add("Rec 4 - 04/02/14");
        three.add("Rec 5 - 04/02/14");
        three.add("Rec 6 - 04/03/14");
        three.add("Rec 7 - 04/07/14");
        
        List<String>  four = new ArrayList<String>();
        four.add(ADD_BUTTON);
        four.add("Rec 1 - 04/01/14");
        four.add("Rec 2 - 04/01/14");
        four.add("Rec 3 - 04/02/14");
        four.add("Rec 4 - 04/02/14");
        four.add("Rec 5 - 04/02/14");
        four.add("Rec 6 - 04/03/14");
        four.add("Rec 7 - 04/07/14");
	
        List<String>  five = new ArrayList<String>();
        five.add(ADD_BUTTON);
        five.add("Rec 1 - 04/01/14");
        five.add("Rec 2 - 04/01/14");
        five.add("Rec 3 - 04/02/14");
        five.add("Rec 4 - 04/02/14");
        five.add("Rec 5 - 04/02/14");
        five.add("Rec 6 - 04/03/14");
        five.add("Rec 7 - 04/07/14");
	
        listDataChild.put(listDataHeader.get(0), one); // Header, Child data
        listDataChild.put(listDataHeader.get(1), two);
        listDataChild.put(listDataHeader.get(2), three);
        listDataChild.put(listDataHeader.get(3), four);
        listDataChild.put(listDataHeader.get(4), five);
	}
}
