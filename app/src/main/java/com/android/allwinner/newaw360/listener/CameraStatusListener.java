package com.android.allwinner.newaw360.listener;

/**
 * Created by Administrator on 2016/9/28.
 */
public interface CameraStatusListener {
//    public void onStartPreview();
//    public void onStartRecord();
//    public void onStopPreview();
//    public void onStopRecord();
    public void onPlugIn(int cameraId);
    public void onPlugOut(int cameraId);
}
