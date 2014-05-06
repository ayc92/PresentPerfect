package com.radishugrads.presentperfect;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
 
/*
 * Custom ListAdapater. Code based heavily on a tutorial:
 * http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private List<String> _listDataHeader; // header titles
    ExpandableListFragment myList;
    private View lastView;
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private boolean childRemoveOn = false;
    private boolean parentRemoveOn = false;
    private boolean changedLayout = false;
    private boolean shared;
 
    public ExpandableListAdapter(ExpandableListFragment list, Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData, boolean sharedV) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.myList = list;
        this.shared = sharedV;
    }
    
    public void setRemoveChild(boolean remove) {
    	childRemoveOn = remove;
    }
    
    public void setRemoveParent(boolean remove) {
    	parentRemoveOn = remove;
    }
    
    public void changedLayout(boolean b) {
    	changedLayout = b;
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final String childText = (String) getChild(groupPosition, childPosition);
 
        if (convertView == null || changedLayout) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (childRemoveOn) { //Remove option selected
                convertView = infalInflater.inflate(R.layout.list_item_rem, null);
            } else { //Remove option not selected
            	convertView = infalInflater.inflate(R.layout.list_item, null);
            }
        }
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null || changedLayout) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (parentRemoveOn) {
            	convertView = infalInflater.inflate(R.layout.list_group_rem, null);
            } else {
            convertView = infalInflater.inflate(R.layout.list_group, null);
            }
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        lastView = convertView;
        if (!(myList.viewTable.containsKey(convertView))) {
        	myList.viewTable.put(convertView, groupPosition);
        }
    	ImageButton removeProj = (ImageButton) convertView.findViewById(R.id.removeProject);
    	ImageButton addRec = (ImageButton) convertView.findViewById(R.id.addRec);
    	if(shared){
    		addRec.setVisibility(View.GONE);
    	}
        if (parentRemoveOn && removeProj != null && !(removeProj.callOnClick())) {
        	removeProj.setFocusable(false);
        	removeProj.setOnClickListener(new OnClickListener() {
        		View myView = lastView;
        		@Override
        		public void onClick(View v) {
        			myList.deleteGroup(myView);
        		}
        	});
        } else if (addRec != null) {
        	addRec.setFocusable(false);
        	addRec.setOnClickListener(new OnClickListener() {
	        	View myView = lastView;
	        	@Override
	        	public void onClick(View v) {
	        		myList.addRec(myView);
	        	}
	        });
        }
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}