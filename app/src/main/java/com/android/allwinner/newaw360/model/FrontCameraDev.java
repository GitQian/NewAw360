package com.android.allwinner.newaw360.model;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;

import com.android.allwinner.newaw360.AppConfig;
import com.android.allwinner.newaw360.MyApplication;
import com.android.allwinner.newaw360.utils.ACache;
import com.android.allwinner.newaw360.utils.DateTimeUtil;
import com.android.allwinner.newaw360.utils.LogUtil;
import com.android.allwinner.newaw360.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/28.
 */
public class FrontCameraDev extends CameraDev {

    public int previewHeight;
    public String solution;

    public FrontCameraDev(int cameraIndexId) {
        this.cameraIndexId = cameraIndexId;
        this.cameraId = AppConfig.FRONT_CAMERA;
    }

    @Override
    public Camera.Parameters initRreviewParameters(Camera.Parameters parameters) {
        //TODO 获取并存储摄像头支持的分辨率
        ACache aCache = ACache.get(MyApplication.getContext());
        ArrayList<String> sizeList = new ArrayList<String>();
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            sizeList.add(size.width + "x" + size.height);
        }
        aCache.put("FrontSolution", sizeList);

        //设置Picture大小
        //TODO: 6.0设置了PictureSize后，拍照时会卡住！！！
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            parameters.setPictureSize(parameters.getSupportedPictureSizes().get(0).width, parameters.getSupportedPictureSizes().get(0).height);
//        }

        //TODO 设置已选Preview分辨率
        int soluWhere = (Integer) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_FRONT_SOLUTION_WHERE, Integer.valueOf(0));
        solution = sizeList.get(soluWhere);
        String str = sizeList.get(soluWhere);
        int width = Integer.valueOf(str.split("x")[0]);
        int height = Integer.valueOf(str.split("x")[1]);

        previewHeight = height;
        this.mDesiredPreviewHeight = height;

        if (soluWhere >= parameters.getSupportedPreviewSizes().size()) {
            soluWhere = 0;
            SPUtils.put(MyApplication.getContext(), AppConfig.KEY_FRONT_SOLUTION_WHERE, soluWhere);
        }
        //TODO: 6.0设置了PreviewSize后，使用第二个节点录制时会卡住！！！
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            parameters.setPictureSize(parameters.getSupportedPictureSizes().get(soluWhere).width, parameters.getSupportedPictureSizes().get(soluWhere).height);
            parameters.setPreviewSize(parameters.getSupportedPictureSizes().get(soluWhere).width, parameters.getSupportedPictureSizes().get(soluWhere).height);   //后视镜分辨率1600*480，如果设为1920*1080会绿屏！
//        }

        int where = (Integer) SPUtils.get(MyApplication.getContext(), "FrontTimeSize", 0);
        switch (where) {
            case 0:
                mVideoDuration = 0;
                this.mTimeStr = SUBFIX_1_MIN;
                break;
            case 1:
                mVideoDuration = 1;
                this.mTimeStr = SUBFIX_2_MIN;
                break;
            case 2:
                mVideoDuration = 2;
                this.mTimeStr = SUBFIX_3_MIN;
                break;
            default:
                break;
        }

        return parameters;
    }

    @Override
    public File makeFile() {
        File dir = new File(AppConfig.FRONT_VIDEO_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            file = new File(AppConfig.FRONT_VIDEO_PATH + "Front_" + DateTimeUtil.getCurrentDateTimeReplaceSpace() + ".ts");
        } else {
            file = new File(AppConfig.FRONT_VIDEO_PATH + "Front_" + DateTimeUtil.getCurrentDateTimeReplaceSpace() + ".mp4");
        }

        return file;
    }

    @Override
    public String makeFilePath() {
        File dir = new File(AppConfig.FRONT_VIDEO_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            path = AppConfig.FRONT_VIDEO_PATH + "Front_" + DateTimeUtil.getCurrentDateTimeReplaceSpace() + "_" + solution + mTimeStr + ".ts";
        } else {
            path = AppConfig.FRONT_VIDEO_PATH + "Front_" + DateTimeUtil.getCurrentDateTimeReplaceSpace() + "_" + solution + mTimeStr + ".mp4";
        }
        return path;
    }

    @Override
    public MediaRecorder initRecorderParameters(Camera camera, MediaRecorder mediaRecorder, String viewPath) {

        mediaRecorder.reset();

        mediaRecorder.setCamera(camera);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //前置
        boolean isSound = false;
        isSound = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_SOUND, true);
        if (isSound) {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaRecorder.setOutputFormat(8); //先设置输出格式 MediaRecorder.OutputFormat.OUTPUT_FORMAT_MPEG2TS
        } else {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //先设置输出格式
        }
        mediaRecorder.setVideoFrameRate(30);

        //获取已选择的分辨率
        int soluWhere = (Integer) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_FRONT_SOLUTION_WHERE, Integer.valueOf(0));
        ACache aCache = ACache.get(MyApplication.getContext());
        ArrayList<String> sizeList = (ArrayList<String>) aCache.getAsObject("FrontSolution");
        if (soluWhere >= sizeList.size()) {
            soluWhere = 0;
            SPUtils.put(MyApplication.getContext(), AppConfig.KEY_FRONT_SOLUTION_WHERE, soluWhere);
        }
        String str = sizeList.get(soluWhere);
        int width = Integer.valueOf(str.split("x")[0]);
        int height = Integer.valueOf(str.split("x")[1]);
        previewHeight = height;
        this.mDesiredPreviewHeight = height;
        LogUtil.d("qiansheng", "recordWidth:" + width + " " + "recordHeight:" + height);
        //TODO 设置录制分辨率
        mediaRecorder.setVideoSize(width, height);

        mediaRecorder.setVideoEncodingBitRate(6000000);  //6M

        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); //后设置视频编码格式
        if (isSound) {
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }

        mediaRecorder.setOutputFile(viewPath);

        int where = (Integer) SPUtils.get(MyApplication.getContext(), "FrontTimeSize", 0);
        int duration = AppConfig.DEFAULT_MAX_DURATION;
        switch (where) {
            case 0:
                mVideoDuration = 0;
                duration = AppConfig.DEFAULT_MAX_DURATION;
                break;
            case 1:
                mVideoDuration = 1;
                duration = AppConfig.THREE_MINUTE_DURATION;
                break;
            case 2:
                mVideoDuration = 2;
                duration = AppConfig.FIVE_MINUTE_DURATION;
                break;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mediaRecorder.setMaxDuration(duration);
        }
        return mediaRecorder;
    }

    @Override
    public long getFallocateSize() {
        long size = 0;
        if (previewHeight <= 480) {
            size = PRESIZE_480P_1MIN;
        } else if (previewHeight > 480 && previewHeight <= 576) {
            size = PRESIZE_576P_1MIN;
        } else if (previewHeight > 576 && previewHeight <= 720) {
            size = PRESIZE_720P_1MIN;
        } else if (previewHeight > 720 && previewHeight <= 1080) {
            size = PRESIZE_1080P_1MIN;
        } else if (previewHeight > 1080 && previewHeight <= 1296) {
            size = PRESIZE_1296P_1MIN;
        } else {
            size = PRESIZE_1296P_1MIN / 1296 * previewHeight;
        }
        LogUtil.d("qiansheng", "=== getFallocateSize size=  " + size);
        if (mVideoDuration >= 0 && mVideoDuration <= 2) {
            size *= (mVideoDuration + 1);
        } else {
            size *= (mVideoDuration + 1);
            LogUtil.d("qiansheng", "getFallocateSize unknow mVideoDuration=  " + mVideoDuration);
        }
        LogUtil.d("qiansheng", "=== getFallocateSize size*mVideoDuration=  " + size);
        return size;
    }

    @Override
    public void putVidingPath(String videoPath) {
        SPUtils.put(MyApplication.getContext(), "FrontVideoPath", videoPath);
    }
}
