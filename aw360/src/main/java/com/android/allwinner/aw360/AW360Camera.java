package com.android.allwinner.aw360;

import android.hardware.Camera;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xiasj on 17-6-9.
 */

public class AW360Camera {
    private static final String TAG = AW360Camera.class.getSimpleName();
    private static final int SUCCESS = 0;


    Camera mCamera;

    protected void init(Camera camera) {
        mCamera = camera;
    }

    protected boolean setCameraSensorType(int type) {
        if ((type != AW360Command.SENSOR_TYPE_1089_OR_3089) && (type != AW360Command.SENSOR_TYPE_1099_OR_1058)) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_SENSOR_TYPE, type);

        return result == SUCCESS;
    }

    protected boolean setCameraLensType(int type) {
        if ((type != AW360Command.LENS_TYPE_HAILIN4052) && (type != AW360Command.LENS_TYPE_HK8077)) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_LENS_TYPE, type);

        return result == SUCCESS;
    }

    protected boolean setCameraViewMode(int mode) {
        if ((mode < AW360Command.AW_DISPLAY_BIRDVIEW_FRONT) || (mode > AW360Command.AW_DISPLAY_SIDEVIEW_ALL)) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_DISPLAY_VIEW_MODE, mode);

        return result == SUCCESS;
    }

    protected boolean setCameraStandard(int standard) {
        if ((standard != AW360Command.NTSC) && (standard != AW360Command.PAL)) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_SYSTEM, standard);

        return result == SUCCESS;
    }

    protected boolean setCarWidth(int width) {

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_CAR_WIDTH, width);

        return result == SUCCESS;
    }

    protected boolean setCarHeight(int height) {

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_CAR_HEIGHT, height);

        return result == SUCCESS;
    }


    protected boolean setCarSideOffset(int offset) {
        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_SIDE_OFFSET, offset);

        return result == SUCCESS;
    }

    protected boolean setFrontCameraMirror(int mirror) {
        if (mirror != AW360Command.CAMERA_NORMAL && mirror != AW360Command.CAMERA_MIRROR) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_FRONT_CAMERA_MIRROR, mirror);

        return result == SUCCESS;
    }

    protected boolean setRearCameraMirror(int mirror) {
        if (mirror != AW360Command.CAMERA_NORMAL && mirror != AW360Command.CAMERA_MIRROR) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_REAR_CAMERA_MIRROR, mirror);

        return result == SUCCESS;
    }

    protected boolean setLeftCameraMirror(int mirror) {
        if (mirror != AW360Command.CAMERA_NORMAL && mirror != AW360Command.CAMERA_MIRROR) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_LEFT_CAMERA_MIRROR, mirror);

        return result == SUCCESS;
    }

    protected boolean setRightCameraMirror(int mirror) {
        if (mirror != AW360Command.CAMERA_NORMAL && mirror != AW360Command.CAMERA_MIRROR) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_RIGHT_CAMERA_MIRROR, mirror);

        return result == SUCCESS;
    }

    /**
     * @param x -120-120
     * @return
     */
    protected boolean setFrontCameraOffsetX(int x) {
        if (x > 120 || x < -120) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_FRONT_CAMERA_OFFSETX, x);

        return result == SUCCESS;
    }

    protected boolean setFrontCameraOffsetY(int y) {
        if (y > 120 || y < -120) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_FRONT_CAMERA_OFFSETY, y);

        return result == SUCCESS;
    }

    protected boolean setRearCameraOffsetX(int x) {
        if (x > 120 || x < -120) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_REAR_CAMERA_OFFSETX, x);

        return result == SUCCESS;
    }

    protected boolean setRearCameraOffsetY(int y) {
        if (y > 120 || y < -120) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_REAR_CAMERA_OFFSETY, y);

        return result == SUCCESS;
    }

    protected boolean setLeftCameraOffsetX(int x) {
        if (x > 120 || x < -120) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_LEFT_CAMERA_OFFSETX, x);

        return result == SUCCESS;
    }

    protected boolean setLeftCameraOffsetY(int y) {
        if (y > 120 || y < -120) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_LEFT_CAMERA_OFFSETY, y);

        return result == SUCCESS;
    }

    protected boolean setRightCameraOffsetX(int x) {
        if (x > 120 || x < -120) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_RIGHT_CAMERA_OFFSETX, x);

        return result == SUCCESS;
    }

    protected boolean setRightCameraOffsetY(int y) {
        if (y > 120 || y < -120) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_RIGHT_CAMERA_OFFSETY, y);

        return result == SUCCESS;
    }

    protected boolean setRightCamerScale(int scale) {
        if (14 < scale || scale < 6) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_RIGHT_CAMERA_SCALE, scale);

        return result == SUCCESS;
    }

    protected boolean setRearCamerScale(int scale) {
        if (14 < scale || scale < 6) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_REAR_CAMERA_SCALE, scale);

        return result == SUCCESS;
    }

    protected boolean setFrontCamerScale(int scale) {
        if (14 < scale || scale < 6) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_FRONT_CAMERA_SCALE, scale);

        return result == SUCCESS;
    }

    protected boolean setLeftCamerScale(int scale) {
        if (14 < scale || scale < 6) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_LEFT_CAMERA_SCALE, scale);

        return result == SUCCESS;
    }

    protected boolean setReverseRule(int show) {
        if (AW360Command.REVERSE_RULE_SHOW != show && AW360Command.REVERSE_RULE_MISS != show) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_REVERSE_RULE, show);

        return result == SUCCESS;
    }

    protected boolean setCameraLuma(int luma) {
        if (0 > luma && luma > 255) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_CAMERA_LUMA, luma);

        return result == SUCCESS;
    }

    protected boolean setCameraHue(int hue) {
        if (0 > hue && hue > 255) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_CAMERA_HUE, hue);

        return result == SUCCESS;
    }

    protected boolean setCameraContrast(int contrast) {
        if (0 > contrast && contrast > 255) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_CAMERA_CONTRAST, contrast);

        return result == SUCCESS;
    }

    protected boolean setCameraSaturation(int saturation) {
        if (0 > saturation && saturation > 255) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }
        int result = setValue(AW360Command.AW_COMMAND_SET_CAMERA_SATURATION, saturation);

        return result == SUCCESS;
    }


    protected int getCameraSensorType() {

        if (mCamera == null) {
            return AW360Command.SENSOR_TYPE_1089_OR_3089;
        }

        int type = getValue(AW360Command.AW_COMMAND_GET_SENSOR_TYPE);
        return type == -1 ? AW360Command.SENSOR_TYPE_1089_OR_3089 : type;
    }

    protected int getCameraLensType() {
        if (mCamera == null) {
            return AW360Command.LENS_TYPE_HK8077;
        }

        int type = getValue(AW360Command.AW_COMMAND_GET_LENS_TYPE);
        return type == -1 ? AW360Command.LENS_TYPE_HK8077 : type;
    }

    protected int getCameraViewMode() {
        if (mCamera == null) {
            return AW360Command.AW_DISPLAY_BIRDVIEW_FRONT;
        }

        int type = getValue(AW360Command.AW_COMMAND_GET_DISPLAY_VIEW_MODE);
        return type == -1 ? AW360Command.AW_DISPLAY_BIRDVIEW_FRONT : type;
    }

    protected int getCameraStandard() {
        if (mCamera == null) {
            return AW360Command.NTSC;
        }

        int type = getValue(AW360Command.AW_COMMAND_GET_SYSTEM);
        return type == -1 ? AW360Command.NTSC : type;
    }

    protected int getCarWidth() {
        if (mCamera == null) {
            return 0;
        }

        int width = getValue(AW360Command.AW_COMMAND_GET_CAR_WIDTH);
        return width;
    }

    protected int getCarHeight() {
        if (mCamera == null) {
            return 0;
        }

        int height = getValue(AW360Command.AW_COMMAND_GET_CAR_HEIGHT);
        return height;
    }

    protected int getCarSideOffset() {
        if (mCamera == null) {
            return 0;
        }

        int offset = getValue(AW360Command.AW_COMMAND_GET_SIDE_OFFSET);
        return offset;
    }

    protected int getFrontCameraMirror() {
        if (mCamera == null) {
            return AW360Command.CAMERA_NORMAL;
        }

        int mirror = getValue(AW360Command.AW_COMMAND_GET_FRONT_CAMERA_MIRROR);
        return mirror;
    }

    protected int getRearCameraMirror() {
        if (mCamera == null) {
            return AW360Command.CAMERA_NORMAL;
        }

        int mirror = getValue(AW360Command.AW_COMMAND_GET_REAR_CAMERA_MIRROR);
        return mirror;
    }

    protected int getLeftCameraMirror() {
        if (mCamera == null) {
            return AW360Command.CAMERA_NORMAL;
        }

        int mirror = getValue(AW360Command.AW_COMMAND_GET_LEFT_CAMERA_MIRROR);
        return mirror;
    }

    protected int getRightCameraMirror() {
        if (mCamera == null) {
            return AW360Command.CAMERA_NORMAL;
        }

        int mirror = getValue(AW360Command.AW_COMMAND_GET_RIGHT_CAMERA_MIRROR);
        return mirror;
    }

    protected int getFrontCameraOffsetX() {
        if (mCamera == null) {
            return 0;
        }

        int x = getValue(AW360Command.AW_COMMAND_GET_FRONT_CAMERA_OFFSETX);
        return x;
    }

    protected int getFrontCameraOffsetY() {
        if (mCamera == null) {
            return 0;
        }

        int y = getValue(AW360Command.AW_COMMAND_GET_FRONT_CAMERA_OFFSETY);
        return y;
    }

    protected int getRearCameraOffsetX() {
        if (mCamera == null) {
            return 0;
        }

        int x = getValue(AW360Command.AW_COMMAND_GET_REAR_CAMERA_OFFSETX);
        return x;
    }

    protected int getRearCameraOffsetY() {
        if (mCamera == null) {
            return 0;
        }

        int y = getValue(AW360Command.AW_COMMAND_GET_REAR_CAMERA_OFFSETY);
        return y;
    }

    protected int getLeftCameraOffsetX() {
        if (mCamera == null) {
            return 0;
        }

        int x = getValue(AW360Command.AW_COMMAND_GET_LEFT_CAMERA_OFFSETX);
        return x;
    }

    protected int getLeftCameraOffsetY() {
        if (mCamera == null) {
            return 0;
        }

        int y = getValue(AW360Command.AW_COMMAND_GET_LEFT_CAMERA_OFFSETY);
        return y;
    }

    protected int getRightCameraOffsetX() {
        if (mCamera == null) {
            return 0;
        }

        int x = getValue(AW360Command.AW_COMMAND_GET_RIGHT_CAMERA_OFFSETX);
        return x;
    }

    protected int getRightCameraOffsetY() {
        if (mCamera == null) {
            return 0;
        }

        int y = getValue(AW360Command.AW_COMMAND_GET_RIGHT_CAMERA_OFFSETY);
        return y;
    }

    protected int getFrontCameraScale() {
        if (mCamera == null) {
            return 0;
        }

        int scale = getValue(AW360Command.AW_COMMAND_GET_FRONT_CAMERA_SCALE);
        return scale;
    }

    protected int getRearCameraScale() {
        if (mCamera == null) {
            return 0;
        }

        int scale = getValue(AW360Command.AW_COMMAND_GET_REAR_CAMERA_SCALE);
        return scale;
    }

    protected int getLeftCameraScale() {
        if (mCamera == null) {
            return 0;
        }

        int scale = getValue(AW360Command.AW_COMMAND_GET_LEFT_CAMERA_SCALE);
        return scale;
    }

    protected int getRightCameraScale() {
        if (mCamera == null) {
            return 0;
        }

        int scale = getValue(AW360Command.AW_COMMAND_GET_RIGHT_CAMERA_SCALE);
        return scale;
    }

    protected int getCameraLuma() {
        if (mCamera == null) {
            return 0;
        }

        int luma = getValue(AW360Command.AW_COMMAND_GET_CAMERA_LUMA);
        return luma;
    }

    protected int getCameraHue() {
        if (mCamera == null) {
            return 0;
        }

        int hue = getValue(AW360Command.AW_COMMAND_GET_CAMERA_HUE);
        return hue;
    }

    protected int getCameraContrast() {
        if (mCamera == null) {
            return 0;
        }

        int contrast = getValue(AW360Command.AW_COMMAND_GET_CAMERA_CONTRAST);
        return contrast;
    }

    protected int getCameraSaturation() {
        if (mCamera == null) {
            return 0;
        }

        int saturation = getValue(AW360Command.AW_COMMAND_GET_CAMERA_SATURATION);
        return saturation;
    }

    protected String getMixPosition() {
        if (mCamera == null) {
            return "";
        }
        return getString(AW360Command.AW_COMMAND_GET_MIX_OVERLAY_POS);
    }

    protected String getBirdViewPosition() {
        if (mCamera == null) {
            return "";
        }
        return getString(AW360Command.AW_COMMNAD_GET_BD_OVERLAY_POS);
    }

    protected void cameraAdjust() {
        if (mCamera == null) {
            return ;
        }
        setValue(AW360Command.AW_COMMAND_CAMERA_ADJUST, -1);
    }

    protected void birdViewAdjust() {
        if (mCamera == null) {
            return ;
        }
        setValue(AW360Command.AW_COMMAND_BIRDVIEW_ADJUST, -1);
    }

    private int setValue(int command, int value) {
        Class camera = mCamera.getClass();
        try {
            Method sendCmd = camera.getDeclaredMethod("bv360SetVal", camera);
            return (Integer) sendCmd.invoke(camera, command, value);

        } catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException bv360SetVal");
            return -1;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException bv360SetVal");
            return -1;
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException bv360SetVal");
            return -1;
        }
    }


    private int getValue(int command) {
        Class camera = mCamera.getClass();
        try {
            Method getVal = camera.getDeclaredMethod("bv360GetVal", camera);
            return (Integer) getVal.invoke(camera, command);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException bv360GetVal");
            return -1;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException bv360GetVal");
            return -1;
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException bv360GetVal");
            return -1;
        }
    }

    private String getString(int command) {
        Class camera = mCamera.getClass();
        try {
            Method sendCmd = camera.getDeclaredMethod("bv360GetStr", camera);
            return (String) sendCmd.invoke(sendCmd, command);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException bv360GetStr");
            return "";
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException bv360GetStr");
            return "";
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException bv360GetStr");
            return "";
        }
    }

    private int setString(int command, String parameter) {

        Class camera = mCamera.getClass();
        try {
            Method sendCmd = camera.getDeclaredMethod("bv360SetStr", camera);
            return (Integer) sendCmd.invoke(sendCmd, command, parameter);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException bv360SetStr");
            return -1;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException bv360SetStr");
            return -1;
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException bv360SetStr");
            return -1;
        }
    }
}
