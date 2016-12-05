package com.xinxin.dockingexpandablelistview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;

import com.xinxin.dockingexpandablelistview.controller.IDockingController;
import com.xinxin.dockingexpandablelistview.controller.IDockingHeaderUpdateListener;

/**
 * Created by qianxin on 2016/11/21.
 */
public class DockingExpandableListView extends ExpandableListView implements OnScrollListener {
    private View mDockingHeader;
    private int mDockingHeaderWidth;
    private int mDockingHeaderHeight;
    private boolean mDockingHeaderVisible;
    private int mDockingHeaderState = IDockingController.DOCKING_HEADER_HIDDEN;

    private IDockingHeaderUpdateListener mListener;

    public DockingExpandableListView(Context context) {
        this(context, null);
    }

    public DockingExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DockingExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollListener(this);
    }

    public void setDockingHeader(View header, IDockingHeaderUpdateListener listener) {
        mDockingHeader = header;
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDockingHeader != null) {
            measureChild(mDockingHeader, widthMeasureSpec, heightMeasureSpec);
            mDockingHeaderWidth = mDockingHeader.getMeasuredWidth();
            mDockingHeaderHeight = mDockingHeader.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mDockingHeader != null) {
            mDockingHeader.layout(0, 0, mDockingHeaderWidth, mDockingHeaderHeight);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDockingHeaderVisible) {
            // draw header view instead of adding into view hierarchy
            drawChild(canvas, mDockingHeader, getDrawingTime());
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        long packedPosition = getExpandableListPosition(firstVisibleItem);
        int groupPosition = getPackedPositionGroup(packedPosition);
        int childPosition = getPackedPositionChild(packedPosition);

        // update header view based on first visible item
        // IMPORTANT: refer to getPackedPositionChild():
        // If this group does not contain a child, returns -1. Need to handle this case in controller.
        updateDockingHeader(groupPosition, childPosition);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private void updateDockingHeader(int groupPosition, int childPosition) {
        if (getExpandableListAdapter() == null) {
            return;
        }

        if (getExpandableListAdapter() instanceof IDockingController) {
            IDockingController dockingController = (IDockingController)getExpandableListAdapter();
            mDockingHeaderState = dockingController.getDockingState(groupPosition, childPosition);
            switch (mDockingHeaderState) {
                case IDockingController.DOCKING_HEADER_HIDDEN:
                    mDockingHeaderVisible = false;
                    break;
                case IDockingController.DOCKING_HEADER_DOCKED:
                    if (mListener != null) {
                        mListener.onUpdate(mDockingHeader, groupPosition, isGroupExpanded(groupPosition));
                    }
                    // Header view might be "GONE" status at the beginning, so we might not be able
                    // to get its width and height during initial measure procedure.
                    // Do manual measure and layout operations here.
                    mDockingHeader.measure(
                            MeasureSpec.makeMeasureSpec(mDockingHeaderWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(mDockingHeaderHeight, MeasureSpec.AT_MOST));
                    mDockingHeader.layout(0, 0, mDockingHeaderWidth, mDockingHeaderHeight);
                    mDockingHeaderVisible = true;
                    break;
                case IDockingController.DOCKING_HEADER_DOCKING:
                    if (mListener != null) {
                        mListener.onUpdate(mDockingHeader, groupPosition, isGroupExpanded(groupPosition));
                    }

                    View firstVisibleView = getChildAt(0);
                    int yOffset;
                    if (firstVisibleView.getBottom() < mDockingHeaderHeight) {
                        yOffset = firstVisibleView.getBottom() - mDockingHeaderHeight;
                    } else {
                        yOffset = 0;
                    }

                    // The yOffset is always non-positive. When a new header view is "docking",
                    // previous header view need to be "scrolled over". Thus we need to draw the
                    // old header view based on last child's scroll amount.
                    mDockingHeader.measure(
                            MeasureSpec.makeMeasureSpec(mDockingHeaderWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(mDockingHeaderHeight, MeasureSpec.AT_MOST));
                    mDockingHeader.layout(0, yOffset, mDockingHeaderWidth, mDockingHeaderHeight + yOffset);
                    mDockingHeaderVisible = true;
                    break;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && mDockingHeaderVisible) {
            Rect rect = new Rect();
            mDockingHeader.getDrawingRect(rect);
            if (rect.contains((int)ev.getX(), (int)ev.getY())
                    && mDockingHeaderState == IDockingController.DOCKING_HEADER_DOCKED) {
                // Hit header view area, intercept the touch event
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    // Note: As header view is drawn to the canvas instead of adding into view hierarchy,
    // it's useless to set its touch or click event listener. Need to handle these input
    // events carefully by ourselves.
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDockingHeaderVisible) {
            Rect rect = new Rect();
            mDockingHeader.getDrawingRect(rect);

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (rect.contains((int)ev.getX(), (int)ev.getY())) {
                        // forbid event handling by list view's item
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    long flatPostion = getExpandableListPosition(getFirstVisiblePosition());
                    int groupPos = ExpandableListView.getPackedPositionGroup(flatPostion);
                    if (rect.contains((int)ev.getX(), (int)ev.getY()) &&
                            mDockingHeaderState == IDockingController.DOCKING_HEADER_DOCKED) {
                        // handle header view click event (do group expansion & collapse)
                        if (isGroupExpanded(groupPos)) {
                            collapseGroup(groupPos);
                        } else {
                            expandGroup(groupPos);
                        }
                        return true;
                    }
                    break;
            }
        }

        return super.onTouchEvent(ev);
    }
}
