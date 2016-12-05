package com.xinxin.dockingexpandablelistview;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinxin.dockingexpandablelistview.adapter.IDockingAdapterDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by qianxin on 2016/11/21.
 */
public class DemoDockingAdapterDataSource implements IDockingAdapterDataSource {
    private Context mContext;

    private HashMap<Integer, String> mGroups = new HashMap<>();
    private SparseArray<List<String>> mGroupData = new SparseArray<>();
    private int mCurrentGroup = -1;

    public DemoDockingAdapterDataSource(Context context) {
        mContext = context;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (groupPosition < 0 || !mGroups.containsKey(groupPosition)) {
            return null;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_view_item, parent, false);
        }
        TextView titleView = (TextView)convertView.findViewById(R.id.group_view_title);
        titleView.setText(mGroups.get(groupPosition));
        return convertView;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        List<String> children = mGroupData.get(groupPosition);
        if (children == null || childPosition < 0 || childPosition > children.size()) {
            return null;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.child_view_item, parent, false);
        }
        TextView titleView = (TextView)convertView.findViewById(R.id.child_view_title);
        titleView.setText(children.get(childPosition));
        return convertView;
    }

    public int getGroupCount() {
        return mGroups.size();
    }

    public int getChildCount(int groupPosition) {
        if (mGroupData.get(groupPosition) != null) {
            return mGroupData.get(groupPosition).size();
        }

        return 0;
    }

    public List<String> getGroup(int groupPosition) {
        if (mGroupData.get(groupPosition) != null) {
            return mGroupData.get(groupPosition);
        }

        return null;
    }

    public String getChild(int groupPosition, int childPosition) {
        if (mGroupData.get(groupPosition) != null) {
            List<String> group = mGroupData.get(groupPosition);
            if (childPosition >=0 && childPosition < group.size()) {
                return group.get(childPosition);
            }
        }

        return null;
    }

    // Helper method to add group
    public DemoDockingAdapterDataSource addGroup(String group) {
        if (!mGroups.containsValue(group)) {
            mCurrentGroup++;
            mGroups.put(mCurrentGroup, group);
            mGroupData.put(mCurrentGroup, new ArrayList<String>());
        }

        return this;
    }

    // Helper method to add child into one group
    public DemoDockingAdapterDataSource addChild(String child) {
        if (mGroupData.get(mCurrentGroup) != null) {
            mGroupData.get(mCurrentGroup).add(child);
        }

        return this;
    }
}
