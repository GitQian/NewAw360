package com.android.allwinner.newaw360;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.android.allwinner.newaw360.service.RecordService;
import com.android.allwinner.newaw360.utils.LogUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/3/16.
 */

public class FloatWindow implements SurfaceHolder.Callback {

    private RecordService mService;

    private WindowManager mWindowManager;

    private RelativeLayout mFloatWindow = null;
    private WindowManager.LayoutParams mWmParams;

    private SurfaceView mReverseSurfaceView;
    public static SurfaceHolder mReverseHolder;

    private Camera camera;

    public FloatWindow(RecordService service) {
        mService = service;
        mWindowManager = (WindowManager) mService.getApplication().getSystemService(Context.WINDOW_SERVICE);
    }

    public void startFloat() {

        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);

        mFloatWindow = createFloatWindow(R.layout.activity_tvd_2, Gravity.LEFT | Gravity.TOP, 0, 0, dm.widthPixels, dm.heightPixels);
        mReverseSurfaceView = (SurfaceView) mFloatWindow.findViewById(R.id.reverse_preview);
        mReverseHolder = mReverseSurfaceView.getHolder();
        mReverseHolder.addCallback(this);

        mWindowManager.addView(mFloatWindow, mWmParams);

        hide();

    }

    private RelativeLayout createFloatWindow(int id, int gravity, int x, int y, int w, int h) {
//        Log.i(TAG, "createFloatWindow");
        RelativeLayout window = (RelativeLayout) getView(id);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;// LayoutParams.TYPE_TOAST;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        params.gravity = gravity;
        params.x = x;
        params.y = y;
        params.width = w;
        params.height = h;
        params.windowAnimations = android.R.style.Animation_Activity;
        mWmParams = params;
        return window;
    }

    public View getView(int id) {
        View vv;
        LayoutInflater inflater = LayoutInflater.from(mService.getApplication());
        vv = (RelativeLayout) inflater.inflate(id, null);
        vv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return vv;
    }

    public void show() {
        mFloatWindow.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mFloatWindow.setVisibility(View.GONE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mReverseHolder = holder;
        try {
            if (mService.getCameraDev(1) == null || mService.getCameraDev(1).camera == null) {
                LogUtil.e("qiansheng", "nulllllllllllllll");
                camera = Camera.open(AppConfig.BEHIND_CAMERA_INDEX);
                camera.setPreviewDisplay(mReverseHolder);
                camera.startPreview();
            } else {
                if (mService.getCameraDev(1).isRecording()) {
                    startRender(mService.getCameraDev(1).camera);
                }
                mService.getCameraDev(1).camera.setPreviewDisplay(mReverseHolder);

                //TODO 需要检查是否已经startPreview
//                mService.getCameraDev(1).camera.startPreview();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mReverseHolder = null;
        mReverseSurfaceView = null;

        if (mService != null && mService.getCameraDev(1) != null && mService.getCameraDev(1).camera != null && mService.getCameraDev(1).mHandler != null) {
            Message msg = new Message();
            msg.what = AppConfig.MSG_ACTIVITY_FUWEI;
            mService.getCameraDev(1).mHandler.sendMessage(msg);
            if (mService.getCameraDev(1).isRecording()) {
                stopRender(mService.getCameraDev(1).camera);
            }
        }

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void startRender(Camera camera) {
        try {
            //TODO 反射调用startRender
            Class<?> c = camera.getClass();
            Method startRender = c.getMethod("startRender");
            startRender.invoke(camera);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void stopRender(Camera camera) {
        //TODO 反射调用stopRender
        Class<?> c = camera.getClass();
        Method startRender = null;
        try {
            startRender = c.getMethod("stopRender");
            startRender.invoke(camera);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
