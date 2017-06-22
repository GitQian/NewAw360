package com.xinzhihui.mydvr;

import android.util.Log;

/**
 * Created by Administrator on 2017/5/27.
 */

public class FileUtil {
    private final String TAG = this.getClass().getName();

    static {
        System.loadLibrary("fileutil");
    }

    public int ftruncate(String fileName, long size) {
        Log.d(TAG, "ftruncate fileName=" + fileName);
        return _ftruncate(fileName, size);
    }

    public int fallocate(String fileName, long size) {
        Log.d(TAG, "fallocate fileName=" + fileName);
//        return _fallocate(fileName, size);
        synchronized (TAG) {
            return _fallocate(fileName, size);
        }
    }

    private native int _ftruncate(String fileName, long size);

    private native int _fallocate(String fileName, long size);

    public native String _test();
}
