package com.radishugrads.presentperfect;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;


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
    private boolean isExpanded = false;
    private int currExpanded = -1;
    private boolean deleteGroup;
    private int[] removeInfo = new int[2]; //index 0 is groupPosition, index 1 is childPosition 
    private boolean[] removeWhich = new boolean[2]; //index 0 is for (removeProjects?), index 1 is for (removeRecordings?)
    public Map<View, Integer> viewTable = new HashMap<View, Integer>(); //A hash table which maps hashCodes to a unique ID.
	String tab;
//    public ExpandableListFragment(ArrayList<String> headers, HashMap<String, List<String>> listData) {
//        // Required empty public constructor
//		listDataHeader = headers;
//        listDataChild = listData;
//    }
    
    public static ExpandableListFragment newInstance(int someInt, String someTitle) {
        ExpandableListFragment explf = new ExpandableListFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", someInt);
        args.putString("someTitle", someTitle);
        explf.setArguments(args);
        return explf;
    }
    
    public ExpandableListFragment(){
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //setUpView();
        int SomeInt = getArguments().getInt("someInt", 0);   
        String someTitle = getArguments().getString("someTitle", "");
        tab = someTitle;
        Log.d("WORKED: ", someTitle + " " + SomeInt);
    }
	@Override
	void setUpView() {
		expListView = (ExpandableListView) currentTabView.findViewById(R.id.lvExp);
		prepareList();
		boolean shared = false;
		if (tab.equals("shared")){
			shared = true;
		}
		listAdapter = new ExpandableListAdapter(this, context, listDataHeader, listDataChild, shared);
		expListView.setAdapter(listAdapter);
		setUp();
	}
	
	@Override
	void setUp() {
		addButton = (Button) currentTabView.findViewById(R.id.addProject);
		delButton = (Button) currentTabView.findViewById(R.id.removeParent);
		if (tab.equals("shared")){
			addButton.setVisibility(View.GONE);
//			delButton.setVisibility(View.GONE);
		}
		OnClickListener buttonListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.addProject:
					addClick(v);
					break;
				case R.id.removeParent:
					startRemove();
					break;
				case R.id.addRec:
					//add(v, "child", groupPosition);
				}
			}
		};
		
		addButton.setOnClickListener(buttonListener);
		delButton.setOnClickListener(buttonListener);
		
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			//On group expand
			@Override
			public void onGroupExpand(int groupPosition) {
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
	            	} else {
	            			Intent i = new Intent(context, Info.class);
	            			// fake data here
	            			File mFile = new File(getActivity().getFilesDir(), "audiotest.3gp");
	            			String filePath = mFile.getAbsolutePath();
	            			Bundle data = new Bundle();
	            			i.putExtra("recordPath", filePath);
	            			data.putBoolean("is_timer", true);
	            			data.putBoolean("over_time", true);
	            			HashMap<String, Integer> goodWordCounts = new HashMap<String, Integer>();
	            			goodWordCounts.put("crowdsourced", 5);
	            			goodWordCounts.put("user-generated content", 3);
	            			
	            			HashMap<String, Integer> badWordCounts = new HashMap<String, Integer>();
	            			badWordCounts.put("um", 9);
	            			badWordCounts.put("like", 4);
	            			
	            			HashMap<String, Integer> allWordCounts = new HashMap<String, Integer>();
	            			allWordCounts.putAll(goodWordCounts);
	            			allWordCounts.putAll(badWordCounts);	            			
	            			
	            			data.putSerializable("good", goodWordCounts);
	            			data.putSerializable("bad", badWordCounts);
	            			data.putSerializable("all", allWordCounts);
	            			data.putSerializable("wpm", 120);
	            			data.putInt("cur_time", 310);
	            			data.putInt("time_limit", 300);
	            			
	            			i.putExtras(data);
	            			startActivity(i);
	            	}
	                return false;
	            }
	        });
	}
	
	public void deleteGroup(View v) {
		AlertDialog.Builder alert = createParentDeleteDialog(viewTable.get(v));
		alert.show();
		return;
	}
	
	public void addRec(View v) {
		add(v, "child", viewTable.get(v));
	}
	
	private void addClick(View v) {
		add(v, "group", -10);
	}
	
	private void add(View v, String group_or_child, int id) {
		lastId = id; //Stores ID for future use.
		changed = false; //default value, no change has happened.
		isGroup = false;
		String other = "new project";
		if (group_or_child.equals("group")) {
			isGroup = true;
		}
		if (!(isGroup)) {
			other = "new recording";
		}
		AlertDialog dialog = createChooseNameDialog(other);
		dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();
	}

	private AlertDialog createChooseNameDialog(String other) {
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
		
		AlertDialog newDialog = alert.create();
		return newDialog;
	}
	
	private void update_list() {
		if (changed) {
			addItem(newest_input, isGroup, lastId);
		}
	}
	
	private void addItem(String name, boolean isGroup, int which){ //Which refers to id of group or id of child
		SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy");
		String timeStamp = s.format(new Date()); // Find todays date
		if (!(isGroup)) { //Adding a recording.
			listDataChild.get(listDataHeader.get(which)).add(newest_input + " - " + timeStamp);
		} else if (isGroup) { //Adding a project.
			int size = listAdapter.getGroupCount();
			listDataHeader.add(name);
			List<String>  new_group = new ArrayList<String>();
			listDataChild.put(listDataHeader.get(size), new_group);
		}
		listAdapter.notifyDataSetChanged();
		listAdapter.notifyDataSetInvalidated();
		if (!isGroup) {
			Intent i = new Intent(context, RecorderActivity.class);
			startActivity(i);
		}
	}
	
	private void startRemove() {
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
		AlertDialog.Builder alert = createDeleteOptionDialog();
		alert.show();
	}	
	
	private AlertDialog.Builder createDeleteOptionDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Choose which to delete!")
			.setItems(R.array.removeOptions, new DialogInterface.OnClickListener() {
	               
				/*
				 * Which: 0 for removing projects, 1 for removing recordings.
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

	private void removeHelper() {
		if (removeWhich[0]) { //User chose to remove a project.
			startRemoveParent();
		} else if (removeWhich[1]) { //user chose to remove recordings.
			startRemoveChild();
		}
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
	
	private AlertDialog.Builder createParentDeleteDialog(int groupPos) {
		removeInfo[0] = groupPos;
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
	
	private void startRemoveChild() {
		if (!isExpanded) {
			Toast.makeText(appContext, 
					"A project must be chosen to be able to delete recordings.",
					   Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(appContext, 
				"Press the remove button again to disable remove mode.",
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
	
	private void removeChild() {
		listDataChild.get(listDataHeader.get(removeInfo[0])).remove(removeInfo[1]);
		listAdapter.notifyDataSetChanged();
		listAdapter.notifyDataSetInvalidated();
	}
		
	@Override
	void prepareList() {
		if (tab.equals("recordings")){
			recordingsView();
		} else if(tab.equals("shared")){
			sharedwithMeView();
		}
		else {
			//
		}
	}
	
	public void recordingsView(){
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
	public void sharedwithMeView(){
		listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        // Adding projects/headers
        listDataHeader.add("Bob H.'s recordings");
        listDataHeader.add("Angel's recordings");
        listDataHeader.add("King Henry's recordings");
        listDataHeader.add("Mr. Clean's recordings");
        listDataHeader.add("Zoo's recordings");
	
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
