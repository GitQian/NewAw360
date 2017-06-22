package com.android.allwinner.newaw360.utils;

import android.content.Context;
import android.os.storage.StorageManager;

import com.android.allwinner.newaw360.AppConfig;
import com.android.allwinner.newaw360.MyApplication;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/14.
 */

public class CommonUtils {

    /**
     * 反射获取内、外存储设备路径
     *
     * @param mContext
     * @param is_removale
     * @return
     */
    public static String[] getStoragePaths(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getState = storageVolumeClazz.getMethod("getState");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);

            ArrayList<String> pathList = new ArrayList<String>();

            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);

                String state = (String) getState.invoke(storageVolumeElement);
                if ("mounted".equals(state)) {
                    pathList.add(path);
                }
//                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
//                if (is_removale == removable) {
//                    return path;
//                }
            }
            if (pathList != null && pathList.size() > 0) {
                String paths[] = new String[pathList.size()];
                pathList.toArray(paths);
                return paths;
            }
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStoragePath() {
        String defaultPath = SDCardUtils.getSDCardPath();
        String path = (String) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_STORAGE_PATH, defaultPath);
        return path;
    }

    /**
     * 倒车开关 0:内核倒车 1:应用倒车
     *
     * @param file
     * @param flag
     */
    public static void writeExitFlag(String file, int flag) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(String.valueOf(flag));
        } catch (IOException ex) {
            // Log.e(TAG, "Couldn't write state to " + file + ": " + ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return;
    }

}
