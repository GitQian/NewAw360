package com.android.allwinner.newaw360.media;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.android.allwinner.newaw360.utils.CamParaUtil;
import com.android.allwinner.newaw360.utils.DisplayUtil;
import com.android.allwinner.newaw360.utils.LogUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by xiasj on 17-6-14.
 */

public class AW360MediaManager {
    private static final String TAG = AW360MediaManager.class.getSimpleName();
    private Camera mCamera;
    private boolean isPreviewing = false;
    private Context mContext;

    private AW360MediaManager() {
    }

    private static class SingletHolder {
        static final AW360MediaManager sInstance = new AW360MediaManager();
    }

    public static AW360MediaManager getInstance() {
        return SingletHolder.sInstance;
    }


    public boolean init(Context context) {
        mContext = context;
        return openCamera();
    }


    private boolean openCamera() {
        boolean result = true;
        try {
            Class<?> camera = Class.forName("android.hardware.Camera");
            Method open360 = camera.getMethod("open360");
            mCamera = (Camera) open360.invoke(null);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "openCamera  ClassNotFoundException");
            result = false;
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "openCamera  NoSuchMethodException");
            result = false;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "openCamera  IllegalAccessException");
            result = false;
        } catch (InvocationTargetException e) {
            Log.e(TAG, "openCamera  InvocationTargetException");
            result = false;
        } catch (RuntimeException e) {
            Log.e(TAG, "openCamera  RuntimeException");
            result = false;
        }

        if (mCamera == null) {
            result = false;
        }

        return result;
    }

    public boolean startPreview(SurfaceHolder surfaceHolder) {
        LogUtil.i(TAG, "-----------startPreview");
        if (isPreviewing) {
            return true;
        }
        if (mCamera == null) {
            return false;
        }

        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            LogUtil.e(TAG, "----------startPreview----" + e.getMessage());
            return false;
        }


        Camera.Parameters mParams = mCamera.getParameters();
        mParams.setPictureFormat(PixelFormat.JPEG);// �������պ�洢��ͼƬ��ʽ

        Camera.Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                mParams.getSupportedPreviewSizes(), DisplayUtil.getScreenRate(mContext), 800);
        mParams.setPreviewSize(previewSize.width, previewSize.height);

        mCamera.setDisplayOrientation(90);

        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes.contains("continuous-video")) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(mParams);

        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            LogUtil.e(TAG, "startPreview  ---" + e.getMessage());
            return false;
        }

        mCamera.startPreview();
        isPreviewing = true;
        return isPreviewing;
    }

    public boolean startPreview(SurfaceTexture surface) {
        LogUtil.i(TAG, "----------startPreview----");
        if (isPreviewing) {
            return true;
        }

        if (mCamera == null) {
            return false;
        }

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            LogUtil.i(TAG, "----------mCamera startPreview----");
        } catch (IOException e) {
            Log.e(TAG, "----startPreview---" + e.getMessage());
            return false;
        }
        isPreviewing = true;
        return true;
    }


    public void stopPreview() {
        if (mCamera != null) {
            return;
        }
        mCamera.stopPreview();
    }


    public void release() {
        if (mCamera != null) {
            mCamera.release();
        }
    }

}
