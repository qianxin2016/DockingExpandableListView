package com.xinxin.dockingexpandablelistview.controller;

/**
 * Created by qianxin on 2016/11/21.
 */
public interface IDockingController {
    int DOCKING_HEADER_HIDDEN = 1;
    int DOCKING_HEADER_DOCKING = 2;
    int DOCKING_HEADER_DOCKED = 3;

    int getDockingState(int firstVisibleGroup, int firstVisibleChild);
}
