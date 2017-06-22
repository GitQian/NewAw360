package com.android.allwinner.newaw360.model;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.allwinner.newaw360.AppConfig;
import com.android.allwinner.newaw360.MyApplication;
import com.android.allwinner.newaw360.asynctask.DeleteFileTask;
import com.android.allwinner.newaw360.asynctask.SavePictureTask;
import com.android.allwinner.newaw360.db.LockVideoDAL;
import com.android.allwinner.newaw360.utils.FileOrderUtils;
import com.android.allwinner.newaw360.utils.LogUtil;
import com.android.allwinner.newaw360.utils.SDCardUtils;
import com.android.allwinner.newaw360.utils.SPUtils;
import com.xinzhihui.mydvr.FileUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/28.
 */
public abstract class CameraDev {
    private final String TAG = getClass().getName();

    public int mVideoDuration;   //录制时长
    public String mTimeStr;
    public int mDesiredPreviewHeight; //当前录制分辨率
    public static final String SUBFIX_1_MIN = "_1min";
    public static final String SUBFIX_2_MIN = "_2min";
    public static final String SUBFIX_3_MIN = "_3min";

    private static String mCanReplayFrontFile = null;
    private static String mCanRelayBehindfile = null;
    //fallocate begin
    public static final long PRESIZE_1296P_1MIN = 150 * 1024 * 1024;
    public static final long PRESIZE_1080P_1MIN = 120 * 1024 * 1024;
    public static final long PRESIZE_720P_1MIN = 110 * 1024 * 1024;
    public static final long PRESIZE_576P_1MIN = 60 * 1024 * 1024;
    public static final long PRESIZE_480P_1MIN = 50 * 1024 * 1024;
    private static FileUtil mFileUtil = new FileUtil();
    //fallocate end

    public static long mRecordNeedSpace = 800 * 1024 * 1024;// 800M
    public static long mRecordSwitchSpace = 500 * 1024 * 1024;// 600M

    public int cameraIndexId;
    public int cameraId;

    public boolean isCanUse = false;  //设备是否可用

    public Camera camera = null;  //预览Camera
    public Camera mRecordCamera = null;  //录制Camera
    public MediaRecorder mediaRecorder;

//    public File mVideoFile;
    public String mVideoPath;
    public boolean isLocked = false;

    private boolean isPreviewing = false;
    private boolean isRecording = false;

    public Handler mHandler = null;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private int mTimeCount = 0;


    /**
     * 打开摄像头
     *
     * @return Camera对象
     */
    public Camera open() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        File file = new File("/dev/video" + cameraIndexId);
        if (file.exists()) {
            LogUtil.e(TAG, "have video dev:" + cameraIndexId);
        } else {
            LogUtil.e(TAG, "Not have this video dev!!!" + cameraIndexId);
            camera = null;
            return camera;
        }
        try {
//            camera = Camera.open(cameraIndexId);
            boolean result = openCamera();
            LogUtil.d(TAG, "open -------> cameraId:" + cameraIndexId);
            LogUtil.d(TAG, "open -------> result:" + result);
        } catch (Exception e) {
            camera = null;
            LogUtil.e(TAG, "open -------> cameraId:" + cameraIndexId + "error!!!!");
            e.printStackTrace();
        }
        if (camera != null && cameraIndexId >= 4 && cameraIndexId <= 7) {
//            camera.setAnalogInputColor(67, 50, 100);
            Class<?> c = camera.getClass();
            try {
                Method setAnalogInputColor = c.getMethod("setAnalogInputColor", int.class, int.class, int.class);
                setAnalogInputColor.invoke(camera, 77, 80, 200);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "Camera setAnalogInputColor error!!!");
            }
        }
        return camera;
    }

    public abstract Camera.Parameters initRreviewParameters(Camera.Parameters parameters);

    /**
     * 开始预览
     *
     * @param surface
     */
    public boolean startPreview(SurfaceTexture surface) {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();

                parameters = initRreviewParameters(parameters);

                camera.setParameters(parameters);

                if (surface != null) {
                    //TODO 无预览录制，无需设置setPreviewTexture()
                    camera.setPreviewTexture(surface);
                } else {

                }

                camera.startPreview();
                LogUtil.d(TAG, "startPreview -------> have started");

                //TODO: 水印
                if (cameraIndexId == AppConfig.FRONT_CAMERA_INDEX) {
                    if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_WATER, true)) {
                        setWaterMark(camera);
                        setPreviewing(true);
                    }
                } else if (cameraIndexId == AppConfig.BEHIND_CAMERA_INDEX) {
                    if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_WATER, true)) {
                        setWaterMark(camera);
                        setPreviewing(true);
                    }
                }
            } catch (Exception e) {
                setPreviewing(false);
                LogUtil.e(TAG, "startPreview -------> startPreview error!!!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 停止预览，释放资源
     */
    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;

            setPreviewing(false);
            LogUtil.d(TAG, "stopPreView ------->have stoped");
        } else {
            LogUtil.e(TAG, "stopPreView ----------->camera:" + cameraIndexId + "is null");
        }
    }

    public abstract File makeFile();
    public abstract String makeFilePath();

    public abstract MediaRecorder initRecorderParameters(Camera camera, MediaRecorder mediaRecorder, String viewPath);

    //根据分辨率、时长获取需要预分配的空间大小
    public abstract long getFallocateSize();
    //正在录制的文件（用来避免被删掉）
    public abstract void putVidingPath(String videoPath);

    /**
     * 开始录像
     *
     * @param
     */
    public boolean startRecord() {
        if (isRecording()) {
            return true;
        }

        if (!checkStorageSpace()) {
            return false;
        }
//        mVideoFile = makeFile();

        mVideoPath = makeFilePath();
//        if (mediaRecorder != null) {
//            mediaRecorder.release();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //TODO android 6.0
            Class<?> c = null;
            try {
                c = Class.forName("android.media.MediaRecorder");
                Constructor<?> con = c.getConstructor(int.class);
                mediaRecorder = (MediaRecorder) con.newInstance(1);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "new MediaRecorder(int type) error!!!");
            }

        } else {
            //android 4.4
            mediaRecorder = new MediaRecorder();
        }

//        if (cameraIndexId != isUVCCameraSonix(cameraIndexId)) {
//            LogUtil.d(TAG, "Realy RecordCameraIndex:" + isUVCCameraSonix(cameraIndexId));
//            if (mRecordCamera != null) {
//                mRecordCamera.release();
//                mRecordCamera = null;
//            }
//            mRecordCamera = Camera.open(isUVCCameraSonix(cameraIndexId));
////            Camera.Parameters parameters = mRecordCamera.getParameters();
////            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
////                LogUtil.e("qiansheng", "width:" + size.width +"height:" + size.height);
////            }
////            mRecordCamera = camera;
//            //TODO 第二个节点加不了水印，加上之后视频绿屏！！！
////            if (cameraIndexId == AppConfig.FRONT_CAMERA_INDEX) {
////                if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_WATER, true)) {
////                    setWaterMark(mRecordCamera);
////                }
////            } else if (cameraIndexId == AppConfig.BEHIND_CAMERA_INDEX) {
////                if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_WATER, true)) {
////                    setWaterMark(mRecordCamera);
////                }
////            }
//            mRecordCamera.unlock();
//        } else {
            camera.unlock();
            mRecordCamera = camera;
//        }

        if (AppConfig.IS_PREALLOCATE) {
            exeAllocateOrTruncate(mVideoPath);
        } else {

        }
        initRecorderParameters(mRecordCamera, mediaRecorder, mVideoPath);

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Class<?> c = mediaRecorder.getClass();
                                Method setNextSaveFile = null;
                                try {
                                    if (checkStorageSpace()) {
                                        setNextSaveFile = c.getMethod("setNextSaveFile", String.class);
//                                        mVideoFile = makeFile();
                                        mVideoPath = makeFilePath();
                                        if (AppConfig.IS_PREALLOCATE) {
                                            exeAllocateOrTruncate(mVideoPath);
                                        } else {

                                        }
                                        setNextSaveFile.invoke(mediaRecorder, mVideoPath);

                                        setRecording(true);
                                        setLocked(false);
                                        sendMessage(mHandler, cameraId, AppConfig.MSG_RECODE_STOP, 0);  //stop
                                        sendMessage(mHandler, cameraId, AppConfig.MSG_RECODE_START, 0);  //start
                                        mTimeCount = 1;
                                    } else {
                                        //录制时外部因素导致留给DVR的空间不足
                                        Toast.makeText(MyApplication.getContext(), "存储空间不足，停止录制！", Toast.LENGTH_LONG).show();
                                        stopRecord();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtil.e("qiansheng", "setNextSaveFile Error!!!");
                                }
                            }
                        }).start();
////                        LogUtil.e("qiansheng", "OnInfo thread id:" + Thread.currentThread().getId());
//                        stopRecord();
//                        startRecord();
                        break;
                    default:
                        break;
                }
            }
        });

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            LogUtil.e(TAG, "startRecord ------> cameraDev:" + cameraIndexId + " " + "startRecord!!!");
        } catch (Exception e) {
            setRecording(false);
            LogUtil.e(TAG, "startRecord ------> cameraDev:" + cameraIndexId + " " + "startRecord error!!!");
            e.printStackTrace();
            return false;
        }


        setRecording(true);

        sendMessage(mHandler, cameraId, AppConfig.MSG_RECODE_START, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //TODO only android 6.0能在这有效调用setNextSaveFile，so 需要判断下api level
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //判断所选视频时长
                    int where = (Integer) SPUtils.get(MyApplication.getContext(), "FrontTimeSize", 0);
                    int duration = AppConfig.DEFAULT_MAX_DURATION;
                    switch (where) {
                        case 0:
                            duration = AppConfig.DEFAULT_MAX_DURATION;
                            break;
                        case 1:
                            duration = AppConfig.THREE_MINUTE_DURATION;
                            break;
                        case 2:
                            duration = AppConfig.FIVE_MINUTE_DURATION;
                            break;
                    }

                    if (mTimeCount >= (duration / 1000)) {
                        mTimeCount = 0;
                        sendMessage(mHandler, cameraId, AppConfig.MSG_RECODE_COUNT, mTimeCount);
                        LogUtil.e("qiansheng", "Runnable thread id:" + Thread.currentThread().getId());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Class<?> c = mediaRecorder.getClass();
                                Method setNextSaveFile = null;
                                try {
                                    if (checkStorageSpace()) {
                                        setNextSaveFile = c.getMethod("setNextSaveFile", String.class);
//                                        mVideoFile = makeFile();
                                        mVideoPath = makeFilePath();
                                        if (AppConfig.IS_PREALLOCATE) {
                                            exeAllocateOrTruncate(mVideoPath);
                                        } else {

                                        }
                                        setNextSaveFile.invoke(mediaRecorder, mVideoPath);
                                        setRecording(true);
                                        setLocked(false);
                                        sendMessage(mHandler, cameraId, AppConfig.MSG_RECODE_STOP, 0);  //stop
                                        sendMessage(mHandler, cameraId, AppConfig.MSG_RECODE_START, 0);  //start
                                    } else {
                                        //录制时外部因素导致留给DVR的空间不足
                                        Toast.makeText(MyApplication.getContext(), "存储空间不足，停止录制！", Toast.LENGTH_LONG).show();
                                        stopRecord();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtil.e("qiansheng", "setNextSaveFile Error!!!");
                                }
                            }
                        }).start();

                    } else {
                        sendMessage(mHandler, cameraId, AppConfig.MSG_RECODE_COUNT, mTimeCount);
                    }
                    mTimeCount++;

                }
            };
        } else {
            //android 4.4需要在onInfo()中setNextSaveFile
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    sendMessage(mHandler, cameraId, AppConfig.MSG_RECODE_COUNT, mTimeCount);
                    mTimeCount++;
                }
            };
        }
        mTimer.schedule(mTimerTask, 0, 1000);

        return true;
    }

    private void sendMessage(Handler handler, int msgWhat, int msgArg1, int msgArg2) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = msgWhat;
            msg.arg1 = msgArg1;
            msg.arg2 = msgArg2;
            handler.sendMessage(msg);
        }
    }

    /**
     * 停止录像
     *
     * @param
     */
    public void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.stop();
                mediaRecorder.release();

                mediaRecorder = null;

                setRecording(false);
                LogUtil.d(TAG, "stopRecord -------> have stoped");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "stopRecord -------->mediaRecorder stop failed!!!");
                mediaRecorder = null;
                setRecording(false);
                sendMessage(mHandler, cameraId, 0, 0);
            }

            setLocked(false);
            mTimerTask.cancel();
            mTimeCount = 0;
            sendMessage(mHandler, cameraId, 0, 0);
        } else {
            setRecording(false);
            LogUtil.i(TAG, "stopRecord --------->mediaRecorder is null!");
        }

    }

    /**
     * 释放所有Camera
     */
    public void releaseCameraAndPreview() {
//        myCameraPreview.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }
        if (mRecordCamera != null) {
            mRecordCamera.release();
            mRecordCamera = null;
        }
    }

    /**
     * 释放mediaRecorder
     */
    public void killRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }

    public void takePhoto() {
        checkStorageSpace();
        //TODO 可以设置一个参数，通过参数确定拍照完毕之后是否录像！
        if (camera != null) {
            camera.autoFocus(null);
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    new SavePictureTask().execute(data);
//                    camera.startPreview();
//                    startRecord();
                }
            });
        } else {
            LogUtil.e(TAG, "takePhoto -------> camera is null!!!");
        }
    }


    public boolean checkStorageSpace() {
        if (!SDCardUtils.isPathEnable(AppConfig.DVR_PATH)) {
            LogUtil.e(TAG, "checkStorageSpace ---------> this path not exist!!!");
            Toast.makeText(MyApplication.getContext(), "存储路径不存在！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!isStorageEnough()) {
                LogUtil.e(TAG, "checkStorageSpace -------> storage not enough!!!");
                Toast.makeText(MyApplication.getContext(), "存储空间不足，请及时清理！", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (camera == null) {
            Toast.makeText(MyApplication.getContext(), "设备不能使用", Toast.LENGTH_SHORT).show();
            LogUtil.e(TAG, "startRecord ------>camera is null!!!!!");
            return false;
        }

        //TODO 漏秒情况下，可统一在startRecord处检测存储空间是否充足---低于300M触发
        if (!AppConfig.IS_PREALLOCATE) {
            if (SDCardUtils.getFreeBytes(AppConfig.DVR_PATH) < mRecordSwitchSpace) {
                LogUtil.i(TAG, "checkStorageSpace -------> Run DeleteRileTask!");
                new DeleteFileTask().execute(new String[]{AppConfig.FRONT_VIDEO_PATH, AppConfig.BEHIND_VIDEO_PATH});
            } else {
                LogUtil.d(TAG, "checkStorageSpace -------> Free storge enough! Size byte:" + SDCardUtils.getFreeBytes(AppConfig.DVR_PATH));
            }
        }
        return true;
    }

    private boolean isStorageEnough() {
        long allSize = SDCardUtils.getFolderSize(new File(AppConfig.DVR_PATH)) + SDCardUtils.getFreeBytes(AppConfig.ROOT_DIR);
        //给DVR预留600M，否则不让录制
        if (allSize < mRecordNeedSpace) {
            LogUtil.d(TAG, "DVR can use size:" + allSize);
            return false;
        } else {
            LogUtil.d(TAG, "DVR can use size:" + allSize);
            return true;
        }
    }

    /**
     * 检查是否需要循环删除
     * @return
     */
    public boolean checkNeedSwitchNow() {
        if (SDCardUtils.getFreeBytes(AppConfig.DVR_PATH) < mRecordSwitchSpace) {
            LogUtil.i(TAG, "checkStorageSpace -------> Run DeleteRileTask!");
//            new DeleteFileTask().execute(new String[]{AppConfig.FRONT_VIDEO_PATH, AppConfig.BEHIND_VIDEO_PATH});
            return true;
        } else {
            LogUtil.d(TAG, "checkStorageSpace -------> Free storge enough! Size byte:" + SDCardUtils.getFreeBytes(AppConfig.DVR_PATH));
            return false;
        }
    }

    public void exeAllocateOrTruncate(String videoPath) {
        int result = -1;
        if (!checkNeedSwitchNow()) {
            //TODO 空间够 预分配fallocate
            result = mFileUtil.fallocate(videoPath, getFallocateSize());
            putVidingPath(videoPath);
            LogUtil.d(TAG, "fallocate result =" + result);
        } else {
            //TODO rename 重写ftruncate
            //TODO getCanRepalceFiles
            String old = getCanRepalceFiles();
            if (old == null) {
                //预分配
                result = mFileUtil.fallocate(videoPath, getFallocateSize());
                Log.d(TAG, "fallocate pre result =" + result);
                return;
            }

            if (new File(old).exists()){
                LogUtil.d(TAG, "Old File existes!");
                LogUtil.d(TAG, "Old File Size:" + new File(old).length());
            }else {
                LogUtil.e(TAG, "Old File not existes!");
                result = -1;
            }
            putVidingPath(videoPath);
            result = mFileUtil.ftruncate(old, 0);
            LogUtil.e(TAG, "ftruncate result =" + result);
            new File(old).renameTo(new File(videoPath));
            if (new File(videoPath).exists()) {
                LogUtil.d(TAG, "File existes!");
            }else {
                LogUtil.e(TAG, "File not existes!");
                result = -1;
            }

            if (result < 0) {
                result = mFileUtil.fallocate(videoPath, getFallocateSize());
                Log.d(TAG, "fallocate last result =" + result);
            }
        }
    }

    public synchronized String getCanRepalceFiles() {
        File f = null;
        int index = 0;
        int count = 0;
        List<File> listFile = FileOrderUtils.orderByDate(new String[]{AppConfig.FRONT_VIDEO_PATH, AppConfig.BEHIND_VIDEO_PATH});
        LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
        List<String> listPath = lockVideoDAL.getAllLockVideo();
        String frontVideoPath = (String) SPUtils.get(MyApplication.getContext(), "FrontVideoPath", "default");
        String behindVideoPath = (String) SPUtils.get(MyApplication.getContext(), "BehindVideoPath", "default");
        LogUtil.d(TAG, "getCanReplaceFiles FrontViding:" + frontVideoPath);
        LogUtil.d(TAG, "getCanReplaceFiles BehindViding:" + behindVideoPath);

        while (SDCardUtils.getFreeBytes(AppConfig.DVR_PATH) < mRecordSwitchSpace) {

            while (listPath.contains(listFile.get(index).getAbsolutePath()) || frontVideoPath.equals(listFile.get(index).getAbsolutePath()) || behindVideoPath.equals(listFile.get(index).getAbsolutePath())) {
                //跳过锁定文件、跳过当前文件，表删！
                //TODO 可能会删除掉另外一路当前录制的文件！！
                LogUtil.e(TAG, "index++");
                index++;
            }
            if (mCanReplayFrontFile != null) {
                //跳过待删除文件
                if (mCanReplayFrontFile.equals(listFile.get(index).getAbsolutePath())){
                    index ++;
                }
            }
            if (mCanRelayBehindfile !=null){
                //跳过待删除文件
                if (mCanRelayBehindfile.equals(listFile.get(index).getAbsolutePath())){
                    index ++;
                }
            }

            //删除掉最久一个（分辨率匹配 & size > 0）
            if (index < listFile.size()) {
                f = listFile.get(index);
                while (!f.exists()) {
                    index++;
                    f = listFile.get(index);
                    continue;
                }

                if (isMatchFile(f.getName())) {
                    if (f.length() > 0) {
                        if (f.getAbsolutePath().contains("Front")) {
                            mCanReplayFrontFile = f.getAbsolutePath();
                        } else if (f.getAbsolutePath().contains("Behind")) {
                            mCanRelayBehindfile = f.getAbsolutePath();
                        } else {
                            //脏文件（文件名_有分辨率_有时长--基本不可能）
                            f.delete();
                            index ++;
//                            f = listFile.get(index);
                            f = null;
                            continue;
                        }
                        break;
                    } else {
                        //size等于0的文件
                        f.delete();
                        f = null;
                        index ++;
                        continue;
                    }
                } else {
                    //不匹配
                    if (SDCardUtils.getFreeBytes(AppConfig.DVR_PATH) > mRecordSwitchSpace) {
                        //大于300M了，应该返回null，直接预分配fallocate空间！
//                        f = listFile.get(index);
                        f = null;
                        LogUtil.d("qiansheng", "> 500");
                        continue;
                    } else {
                        //不匹配，则删除！
                        LogUtil.d("qiansheng", "< 500");
                        if (cameraId == AppConfig.FRONT_CAMERA) {
                            if (f.getAbsolutePath().contains("Front")) {
                                f.delete();
                                f = null;
                                index ++;
                            } else if (f.getAbsolutePath().contains("Behind")) {
                                //不对应，扔掉再来
                                count ++;
                                if (count >= 2) {
                                    //连续找到两个异路文件，删掉，为了平衡
                                    f.delete();
                                    f = null;
                                }
                                index ++;
                            }
                        } else if (cameraId == AppConfig.BEHIND_CAMERA) {
                            if (f.getAbsolutePath().contains("Behind")) {
                                f.delete();
                                f = null;
                                index ++;
                            } else if (f.getAbsolutePath().contains("Front")) {
                                //不对应，扔掉再来
                                count ++;
                                if (count >= 2) {
                                    //连续找到两个异路文件
                                    f.delete();
                                    f = null;
                                }
                                index ++;
                            }
                        } else {
                            //完全不匹配，脏文件！！！
                            f.delete();
                            index ++;
                            f = null;
                        }
//                        f.delete();
//                        index++;
//                        f = listFile.get(index);
//                        f = null;
                        continue;
                    }
                }
            }

            for (File file : listFile) {
                Log.d("qiansheng", file.getAbsolutePath());
            }
        }

        listFile = null;
        if (f == null) {
            return null;
        } else {
            return f.getAbsolutePath();
        }
    }

    /**
     * 文件匹配 匹配规则：文件名中包含当前分辨率 & 录制时长
     * @param name
     * @return
     */
    public boolean isMatchFile(String name) {
        Log.d(TAG, "isMatchFile name:" + name);
        if (name == null) {
            return false;
        }
        Log.d(TAG, "isMatchFile h=" + mDesiredPreviewHeight);
        boolean isMatch = false;
        if (mDesiredPreviewHeight <= 480 && name.contains("x480")) {
            isMatch = true;
        } else if (mDesiredPreviewHeight > 480 && mDesiredPreviewHeight <= 576
                && name.contains("x" + mDesiredPreviewHeight)) {
            isMatch = true;
        } else if (mDesiredPreviewHeight > 576 && mDesiredPreviewHeight <= 720
                && name.contains("x" + mDesiredPreviewHeight)) {
            isMatch = true;
        } else if (mDesiredPreviewHeight > 720 && mDesiredPreviewHeight <= 1080
                && name.contains("x" + mDesiredPreviewHeight)) {
            isMatch = true;
        } else if (mDesiredPreviewHeight > 720 && mDesiredPreviewHeight <= 1296
                && name.contains("x" + mDesiredPreviewHeight)) {
            isMatch = true;
        }
        Log.d(TAG, "isMatchFile isMatch=" + isMatch);
        //TODO 还要对时间进行匹配
        Log.d(TAG, "isMatchFile mVideoDuration=" + mVideoDuration);
        if (isMatch && mVideoDuration == 0 && name.contains(SUBFIX_1_MIN)) {
            return true;
        } else if (isMatch && mVideoDuration == 1 && name.contains(SUBFIX_2_MIN)) {
            return true;
        } else if (isMatch && mVideoDuration == 2 && name.contains(SUBFIX_3_MIN)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     *
	 * 返回值和index一样表示 不是uvc
	 * 返回值和index不一样 表示是uvccamera
	 * 在camera open之后有效，表示状态不可用
	 */
    public int isUVCCameraSonix(int index) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //TODO: Only android 6.0 have the field of cameraInfo.is_uvc!!!
//            int recordIndex = index;
//            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//            Camera.getCameraInfo(index, cameraInfo);
//            //Log.d(TAG,"camera:video" + index +" is_uvc:" + cameraInfo.is_uvc);
//            Class<?> c = cameraInfo.getClass();
//            int is_uvc_data = 0;
//            try {
//                Field is_uvc = c.getField("is_uvc");
//                is_uvc_data = (int) is_uvc.get(cameraInfo);
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//                LogUtil.e(TAG, "Not have the field of cameraInfo.is_uvc!!!");
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//                LogUtil.e(TAG, "cameraInfo.is_uvc IllegalAccessException!!!");
//            }
//            int realyIndex = index;
//            if (is_uvc_data > 0) {
//                realyIndex = index + 1;
//            } else {
//                realyIndex = index;
//            }
//            //mCameraUVC = index + 1;
//            //直接返回原值
//            return realyIndex;
//        } else {
            //4.4平台
            if (index == 0) {
                File file = new File("/dev/video1");
                if (file.exists()) {
                    //打开0节点，如果1节点存在，在返回1录像
                    LogUtil.e(TAG, "have video dev:video1");
                    return 1;
                } else {
                    //只存在0，不存在1，则返回0
                    return 0;
                }
            } else {
                //打开其他节点，则返回原值；
                return index;
            }
//        }
    }

    /**
     * 设置水印-反射调用
     *
     * @param camera
     */
    public void setWaterMark(Camera camera) {
        Class<?> c = camera.getClass();
        Method startWaterMark = null;
        try {
            startWaterMark = c.getMethod("startWaterMark");
            startWaterMark.invoke(camera);
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, "setWaterMark -------> not have startWaterMark method!!!");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "setWaterMark -------> IllegalAccessException!!!");
            e.printStackTrace();
        }
    }

    private boolean openCamera() {
        boolean result = true;
        try {
            Class<?> c = Class.forName("android.hardware.Camera");
            Method open360 = c.getMethod("open360");
            camera = (Camera) open360.invoke(null);
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

        if (camera == null) {
            result = false;
        }

        return result;
    }

    public void setPreviewing(boolean previewing) {
        isPreviewing = previewing;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public boolean isPreviewing() {
        return isPreviewing;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getmVideoFile() {
        return mVideoPath;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

}
