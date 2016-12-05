package com.xinxin.dockingexpandablelistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xinxin.dockingexpandablelistview.view.DockingExpandableListView;
import com.xinxin.dockingexpandablelistview.adapter.DockingExpandableListViewAdapter;
import com.xinxin.dockingexpandablelistview.controller.IDockingHeaderUpdateListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DemoDockingAdapterDataSource listData = prepareData();
        DockingExpandableListView listView
                = (DockingExpandableListView) findViewById(R.id.docking_list_view);
        listView.setGroupIndicator(null);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listView.setAdapter(new DockingExpandableListViewAdapter(this, listView, listData));
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    parent.expandGroup(groupPosition);
                }

                return true;
            }
        });

        View headerView = getLayoutInflater().inflate(R.layout.group_view_item, listView, false);
        listView.setDockingHeader(headerView, new IDockingHeaderUpdateListener() {
            @Override
            public void onUpdate(View headerView, int groupPosition, boolean expanded) {
                String groupTitle = "Group #" + String.valueOf(groupPosition + 1);
                TextView titleView = (TextView) headerView.findViewById(R.id.group_view_title);
                titleView.setText(groupTitle);
            }
        });
    }

    private DemoDockingAdapterDataSource prepareData() {
        DemoDockingAdapterDataSource listData
                = new DemoDockingAdapterDataSource(this);
        listData.addGroup("Group #1")
                    .addChild("Dish #1")
                    .addChild("Dish #2")
                    .addChild("Dish #3")
                    .addChild("Dish #4")
                .addGroup("Group #2")
                    .addChild("Drink #1")
                    .addChild("Drink #2")
                    .addChild("Drink #3")
                    .addChild("Drink #4")
                    .addChild("Drink #5")
                    .addChild("Drink #6")
                    .addChild("Drink #7")
                    .addChild("Drink #8")
                    .addChild("Drink #9")
                .addGroup("Group #3")
                    .addChild("Dessert #1")
                    .addChild("Dessert #2")
                    .addChild("Dessert #3")
                    .addChild("Dessert #4")
                    .addChild("Dessert #5")
                    .addChild("Dessert #6")
                    .addChild("Dessert #7")
                    .addChild("Dessert #8");

        return listData;
    }
}
