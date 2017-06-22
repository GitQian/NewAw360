package com.android.allwinner.newaw360.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.android.allwinner.newaw360.AppConfig;
import com.android.allwinner.newaw360.CameraActivity;
import com.android.allwinner.newaw360.FastReverseChecker;
import com.android.allwinner.newaw360.FloatWindow;
import com.android.allwinner.newaw360.ICameraManager;
import com.android.allwinner.newaw360.R;
import com.android.allwinner.newaw360.Receiver.UsbCameraStateReceiver;
import com.android.allwinner.newaw360.listener.CameraStatusListener;
import com.android.allwinner.newaw360.model.CameraDev;
import com.android.allwinner.newaw360.utils.LogUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RecordService extends Service implements CameraStatusListener {
    private final String TAG = getClass().getName();

    private List<CameraDev> cameraDevList;

    private UsbCameraStateReceiver mCameraStateReceiver;


    private FloatWindow mFloatWindow;
    public static final int MSG_DAOCHE = 424;
    public static final int MSG_FUWEI = 425;

    // 主线程Handler
    private Handler mServiceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DAOCHE:
                    mFloatWindow.show();
                    break;
                case MSG_FUWEI:
                    mFloatWindow.hide();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 子线程Handler
    private TaskHandler mTaskHandler;

    @Override
    public void onPlugIn(int cameraId) {
        LogUtil.e(TAG, "onPlugIn() ---------->");
        if (cameraId == AppConfig.FRONT_CAMERA) {
            CameraDev cameraDev = getCameraDev(cameraId);
            if (cameraDev != null) {
                Message message = new Message();
                message.what = AppConfig.MSG_FRONT_CAMERA_IN;
                cameraDev.mHandler.sendMessage(message);
            }
            return;
        } else if (cameraId == AppConfig.BEHIND_CAMERA) {
            CameraDev cameraDev = getCameraDev(cameraId);
            if (cameraDev != null) {
                Message message = new Message();
                message.what = AppConfig.MSG_BEHIND_CAMERA_IN;
                cameraDev.mHandler.sendMessage(message);
            }
        }

    }

    @Override
    public void onPlugOut(int cameraId) {
        LogUtil.e(TAG, "onPlugOut() -------->");
        if (cameraId == AppConfig.FRONT_CAMERA) {
            CameraDev cameraDev = getCameraDev(cameraId);
            if (cameraDev != null) {
                Message message = new Message();
                message.what = AppConfig.MSG_FRONT_CAMERA_OUT;
                cameraDev.mHandler.sendMessage(message);

                if (getCameraDev(cameraId).isRecording()) {
                    System.exit(0);
                } else {
                    cameraDev.releaseCameraAndPreview();
                }
            }
        } else if (cameraId == AppConfig.BEHIND_CAMERA) {
            CameraDev cameraDev = getCameraDev(cameraId);
            if (cameraDev != null) {
                Message message = new Message();
                message.what = AppConfig.MSG_BEHIND_CAMERA_OUT;
                cameraDev.mHandler.sendMessage(message);

                if (getCameraDev(cameraId).isRecording()) {
                    System.exit(0);
                } else {
                    cameraDev.releaseCameraAndPreview();
                }
            }
        }
    }

//    private final IBinder mBinder = new LocalBinder();

    public class LoBinder extends ICameraManager.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void open(int cameraId) throws RemoteException {
            LogUtil.d("qiansheng", "Remote Open!!!");
            RecordService.this.open(cameraId);
        }

        @Override
        public void startPreView(int cameraId) throws RemoteException {
            LogUtil.d("qiansheng", "Remote StartPreView!!!");
            RecordService.this.startPreView(cameraId, null);
        }

        @Override
        public void stopPreView(int cameraId) throws RemoteException {
            LogUtil.d("qiansheng", "Remote StopPreView!!!");
            RecordService.this.stopPreView(cameraId);
        }

        @Override
        public boolean startRecord(int cameraId) throws RemoteException {
            LogUtil.d("qiansheng", "Remote StartRecord!!!");
            RecordService.this.startRecord(cameraId);
            return false;
        }

        @Override
        public void stopRecord(int cameraId) throws RemoteException {
            LogUtil.d("qiansheng", "Remote StopRecord!!!");
            RecordService.this.stopRecord(cameraId);
        }

        public RecordService getService() {
            return RecordService.this;
        }
    }

    private Binder mBinder = new LoBinder();


    Notification notification;

    public RecordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cameraDevList = new ArrayList<CameraDev>();
        cameraDevList.add(null);
        cameraDevList.add(null);
        cameraDevList.add(null);
        cameraDevList.add(null);
        cameraDevList.add(null);
        cameraDevList.add(null);
        cameraDevList.add(null);
        LogUtil.d(TAG, "RecordService onCreate --------->");

        mFloatWindow = new FloatWindow(this);
        mFloatWindow.startFloat();
        FastReverseChecker fastReverseChecker = new FastReverseChecker(this);
        fastReverseChecker.setFastReverseListener(new FastReverseChecker.IFastReverseListener() {
            @Override
            public void onFastReverseBoot(boolean isReversing) {
                if (isReversing) {
                    LogUtil.e("qiansheng", "倒车回调！！！");
                    Message message = new Message();
                    message.what = MSG_DAOCHE;
                    mServiceHandler.sendMessage(message);
                } else {
                    LogUtil.e("qiansheng", "复位回调！！！");
                    Message message = new Message();
                    message.what = MSG_FUWEI;
                    mServiceHandler.sendMessage(message);
                }
            }
        });
        fastReverseChecker.start();

//        HandlerThread ht = new HandlerThread("RecordService Handle Thread");
//        ht.start(); //start()--开启looper，不断的从MessageQueue中取消息处理，当没有消息的时候会阻塞，有消息的到来的时候会唤醒。
//        mTaskHandler = new TaskHandler(ht.getLooper());


        // 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(this); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, CameraActivity.class);
        nfIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("摄像头正在录制...") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("触摸可显示录制界面") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        //注册摄像头Camera插拔广播
        IntentFilter filterCamera = new IntentFilter("android.hardware.usb.action.USB_CAMERA_PLUG_IN_OUT");
        mCameraStateReceiver = new UsbCameraStateReceiver(this);
        registerReceiver(mCameraStateReceiver, filterCamera);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "RecordService onStartCommand --------->");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        LogUtil.d(TAG, "RecordService onBind --------->");
        return mBinder;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "RecordService onUnbind --------->");
//        for (CameraDev cameraDev : cameraDevList) {
//            cameraDev.mHandler = null;
//        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "RecordService onDestroy --------->");
        for (CameraDev cameraDev : cameraDevList) {
            if (cameraDev != null && cameraDev.camera != null) {
                cameraDev.stopPreview();
                cameraDev.releaseCameraAndPreview();
                cameraDev.stopRecord();
            }
        }
    }


    public class LocalBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }


    public void open(int cameraId) {
        if (cameraDevList.get(cameraId) != null) {
            cameraDevList.get(cameraId).open();
        }
    }

    public void startPreView(int cameraId, SurfaceTexture surface) {
        if (cameraDevList.get(cameraId) != null) {
            cameraDevList.get(cameraId).startPreview(surface);
        }
    }

    public void stopPreView(int cameraId) {
        if (cameraDevList.get(cameraId) != null) {
            cameraDevList.get(cameraId).stopPreview();
        }
    }

    public boolean startRecord(int cameraId) {
        boolean isSuccess = false;
        if (cameraDevList.get(cameraId) != null) {
            isSuccess = cameraDevList.get(cameraId).startRecord();
            // 参数一：唯一的通知标识；参数二：通知消息。
            if (isSuccess) {
                startForeground(110, notification);// 开始前台服务
            }
        }
        return isSuccess;
    }

    public void stopRecord(int cameraId) {
        if (cameraDevList.get(cameraId) != null) {
            cameraDevList.get(cameraId).stopRecord();
        }
        if (!isRecording()) {
            stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        }
    }

    public void startRender(int cameraId, SurfaceTexture surface) {
        try {
            CameraDev cameraDev = cameraDevList.get(cameraId);
            cameraDev.camera.setPreviewTexture(surface);
            //TODO 反射调用startRender
            Class<?> c = cameraDev.camera.getClass();
            Method startRender = c.getMethod("startRender");
            startRender.invoke(cameraDev.camera);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, "No strartRender() method!!!");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void stopRender(int cameraId) {
        CameraDev cameraDev = cameraDevList.get(cameraId);
        //TODO 反射调用stopRender
        Class<?> c = cameraDev.camera.getClass();
        Method startRender = null;
        try {
            startRender = c.getMethod("stopRender");
            startRender.invoke(cameraDev.camera);
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, "No stopRender() method!!!");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void killRecord(int cameraId) {
        if (cameraDevList.get(cameraId) != null) {
            cameraDevList.get(cameraId).killRecord();
        }
    }

    public void takePhoto(int cameraId) {
        if (cameraDevList.get(cameraId) != null) {
            cameraDevList.get(cameraId).takePhoto();
        }
    }

    public void stopAllRecord() {
        for (CameraDev cameraDev : cameraDevList) {
            if (cameraDev != null) {
                if (cameraDev.isRecording()) {
                    cameraDev.stopRecord();
                }
            }
        }
//        if (notification.visibility == View.VISIBLE) {
//            stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
//        }
    }

    public CameraDev getCameraDev(int cameraId) {
        if (cameraDevList == null) {
            return null;
        }
        return cameraDevList.get(cameraId);
    }

    public void addCameraDev(int cameraId, CameraDev cameraDev) {
//        cameraDevList.add(cameraDev);
        cameraDevList.set(cameraId, cameraDev);
    }


    public boolean isPreView(int cameraId) {
        if (cameraDevList.get(cameraId) == null) {
            return false;
        } else {
            return cameraDevList.get(cameraId).isPreviewing();
        }
    }

    /**
     * 检查所有设备是否有设备正在录制
     *
     * @return
     */
    public boolean isRecording() {
        for (CameraDev cameraDev : cameraDevList) {
            if (cameraDev != null && cameraDev.isRecording()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个设备是否正在录制
     *
     * @param cameraId
     * @return
     */
    public boolean isRecording(int cameraId) {
        if (cameraDevList.get(cameraId) == null) {
            return false;
        } else {
            return cameraDevList.get(cameraId).isRecording();
        }
    }


    // 子线程Handler
    private class TaskHandler extends Handler {
        TaskHandler(Looper looper) {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //TODO 存储空间的检查可以放到这
                //TODO 倒车检测轮询也可以放到这

                default:
                    break;
            }
        }
    }


}
