package com.radishugrads.presentperfect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;


public class ExpandableListFragment extends TabFragment {

	ExpandableListView expListView;
	ExpandableListAdapter listAdapter;
	
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
    private boolean isExpanded = false;
    private int currExpanded = -1;
    private boolean deleteGroup;
    private int[] removeInfo = new int[2]; //index 0 is groupPosition, index 1 is childPosition 
    private boolean[] removeWhich = new boolean[2]; //index 0 is for (removeProjects?), index 1 is for (removeRecordings?)
	
	@Override
	void setUpView() {
		expListView = (ExpandableListView) currentTabView.findViewById(R.id.lvExp);
		prepareList();
		listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
		setUp();
	}
	
	@Override
	void setUp() {
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			//On group expand
			@Override
			public void onGroupExpand(int groupPosition) {
				if (deleteGroup) {
					expListView.collapseGroup(groupPosition);
					removeInfo[0] = groupPosition;
					AlertDialog.Builder alert = createParentDeleteDialog(groupPosition);
					alert.show();
					return;
				}
				currExpanded = groupPosition;
				isExpanded = true;
				if (lastExpanded != NONE_EXPANDED &&
						groupPosition != lastExpanded) {
					expListView.collapseGroup(lastExpanded);
				}
				lastExpanded = groupPosition;
			}
		});
		
		// Listview Group expanded listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
		 
		    @Override
		    public void onGroupCollapse(int groupPosition) {
		    	if (removeButtonPushed) {
		    		removeButtonPushed = false;
		    		listAdapter.setRemoveChild(false);
		    		listAdapter.changedLayout(true);
		    		listAdapter.notifyDataSetChanged();
		    		listAdapter.notifyDataSetInvalidated();
		    	} else {
		    		listAdapter.changedLayout(false);
		    	}
			    	if (currExpanded == groupPosition) {
			    		isExpanded = false;
			    	}

		    }
		});
		
		//On child click
		expListView.setOnChildClickListener(new OnChildClickListener() {
			 
	            @Override
	            public boolean onChildClick(ExpandableListView parent, View v,
	                    int groupPosition, int childPosition, long id) {
	            	if (removeButtonPushed && childPosition != ADD_CHILD) {
	            		removeInfo[0] = groupPosition;
	            		removeInfo[1] = childPosition;
	            		AlertDialog.Builder alert = createChildDeleteDialog(groupPosition, childPosition);
	            		alert.show();
	            	} else if (childPosition == ADD_CHILD) {
	            		add(v, "child", groupPosition);
	            	} else {
	            			Intent i = new Intent(context, Info.class);
	            			startActivity(i);
	            	}
	                return false;
	            }
	        });
	}
	
	@Override
	void prepareList() {
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
	
        listDataChild.put(listDataHeader.get(0), one);
        listDataChild.put(listDataHeader.get(1), two);
        listDataChild.put(listDataHeader.get(2), three);
        listDataChild.put(listDataHeader.get(3), four);
        listDataChild.put(listDataHeader.get(4), five);
	}
	
	private AlertDialog.Builder createParentDeleteDialog(int groupPos) {
		String parentName = listDataHeader.get(groupPos);
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Delete Project?");
		alert.setMessage("Delete project: " + parentName + "?");
		alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				removeParent(removeInfo[0]);
			}
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
			});
		return alert;
	}
	
	private AlertDialog.Builder createChildDeleteDialog(int groupPos, int childPos) {
		String childName = listDataChild.get(listDataHeader.get(groupPos)).get(childPos);
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Delete recording?");
		alert.setMessage("Delete recording: " + childName + "?");
		alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				removeChild();
			}
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
			});
		return alert;
	}	
	
	private AlertDialog.Builder createOptionDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Choose which to delete!")
			.setItems(R.array.removeOptions, new DialogInterface.OnClickListener() {
	               
				/*
				 * Which: 0 for removing projects, 1 for removing recordings.
				 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
				 */
				public void onClick(DialogInterface dialog, int which) {
	            	   for (int i=0; i < 2; i++) {
	            		   if (i == which) {
	            			   removeWhich[i] = true;
	            		   } else {
	            			   removeWhich[i] = false;
	            		   }
	            	   }
	            	   removeHelper();
	               }
			});
		return alert;
	}
	
	private void removeChild() {
		listDataChild.get(listDataHeader.get(removeInfo[0])).remove(removeInfo[1]);
		listAdapter.notifyDataSetChanged();
		listAdapter.notifyDataSetInvalidated();
	}
	
	private void removeParent(int groupPosition) {
		listDataHeader.remove(groupPosition);
		//Deactivate Remove Mode
		listAdapter.setRemoveParent(false);
		listAdapter.setRemoveChild(false);
		removeButtonPushed = false;
		deleteGroup = false;
		listAdapter.changedLayout(true);
		listAdapter.notifyDataSetChanged();
		listAdapter.notifyDataSetInvalidated();
	}
	
	public void addClick(View v) {
		add(v, "group", -10);
	}
	
	public void add(View v, String group_or_child, int id) {
		lastId = id;
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
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(other.toUpperCase(Locale.US));
		alert.setMessage("Input name of "+ other + ".");
		final EditText input = new EditText(context);
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
			if (!isGroup) {
				Intent i = new Intent(context, RecorderActivity.class);
				startActivity(i);
			}
	}

	private void startRemoveChild() {
		if (!isExpanded) {
			Toast.makeText(appContext, 
					"A project must be chosen to be able to delete recordings.",
					   Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(appContext, 
				"Press the delete button again to disable delete mode.",
				   Toast.LENGTH_LONG).show();
		if (!removeButtonPushed) { //Remove Mode Activated
			listAdapter.setRemoveChild(true);
			removeButtonPushed = true;
		} else { //Remove Mode Deactivated
			listAdapter.setRemoveChild(false);
			removeButtonPushed = false;
		}
		listAdapter.changedLayout(true);
		listAdapter.notifyDataSetChanged();
		listAdapter.notifyDataSetInvalidated();
	}
	
	private void startRemoveParent() {
		Toast.makeText(appContext, 
				"Press the delete button again to disable delete mode.",
				   Toast.LENGTH_LONG).show();
		if (!removeButtonPushed) { //Remove Mode Activated
			if (isExpanded) {
				expListView.collapseGroup(lastExpanded);
			}
			listAdapter.setRemoveParent(true);
			removeButtonPushed = true;
			deleteGroup = true;
		} else { //Remove Mode Deactivated
			listAdapter.setRemoveParent(false);
			removeButtonPushed = false;
			deleteGroup = false;
		}
		listAdapter.changedLayout(true);
		listAdapter.notifyDataSetChanged();
		listAdapter.notifyDataSetInvalidated();
	}
	
	public void startRemove(View v) {
		if (deleteGroup || removeButtonPushed) {
			listAdapter.setRemoveParent(false);
			listAdapter.setRemoveChild(false);
			removeButtonPushed = false;
			deleteGroup = false;
			listAdapter.changedLayout(true);
			listAdapter.notifyDataSetChanged();
			listAdapter.notifyDataSetInvalidated();
			return;
		}
		AlertDialog.Builder alert = createOptionDialog();
		alert.show();
	}
	
	public void removeHelper() {
		if (removeWhich[0]) { //User chose to remove a project.
			startRemoveParent();
		} else if (removeWhich[1]) { //user chose to remove recordings.
			startRemoveChild();
		}
	}
	
}
