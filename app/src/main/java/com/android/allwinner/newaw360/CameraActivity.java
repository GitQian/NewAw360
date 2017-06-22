package com.android.allwinner.newaw360;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.allwinner.newaw360.Receiver.UsbCameraStateReceiver;
import com.android.allwinner.newaw360.Receiver.UsbStateReceiver;
import com.android.allwinner.newaw360.db.LockVideoDAL;
import com.android.allwinner.newaw360.model.CameraDev;
import com.android.allwinner.newaw360.model.CameraFactory;
import com.android.allwinner.newaw360.service.RecordService;
import com.android.allwinner.newaw360.utils.DateTimeUtil;
import com.android.allwinner.newaw360.utils.LogUtil;
import com.android.allwinner.newaw360.utils.SDCardUtils;
import com.android.allwinner.newaw360.utils.SPUtils;

import java.io.File;
import java.lang.ref.WeakReference;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    private TextureView mCameraFrontTtv;
    private TextureView mCameraBehindTtv;

    private ImageView mRecFrontImg;
    private AnimationDrawable mAnimFrontRec;
    private ImageView mRecBehindImg;
    private AnimationDrawable mAnimBehindRec;
    private TextView mTimeFrontTv;
    private TextView mTimeBehindTv;

    private RelativeLayout mContainerRll;
    private RelativeLayout mFrontRll;
    private RelativeLayout mBehindRll;

    private int mCurCameraId;

    private static final int ALL_CAMERA = 10086;

    private CameraDev mCurCameraDev;

    private LinearLayout mRecordCtrlLly;
    private Button mTakePhotoBtn;
    private Button mRecordSwitchBtn;
    private Button mVideoDirBtn;
    private Button mRecordCtrlBtn;
    private Button mRecordSettingBtn;
    private Button mRecordFileLockBtn;

    CameraFactory factory = new CameraFactory();

    private DvrSurfaceTextureListener dvrSurfaceTextureFrontListener;
//    private DvrSurfaceTextureListener dvrSurfaceTextureBehindListener;

    private boolean isFrontAuto;
    private boolean isBehindAuto;

    private UsbStateReceiver usbStateReceiver;
    private UsbCameraStateReceiver mCameraStateReceiver;

    Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<CameraActivity> mActivity;

        public MyHandler(CameraActivity activity) {
            mActivity = new WeakReference<CameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity.get() == null || mActivity.get().mAnimFrontRec == null || mActivity.get().mAnimBehindRec == null
                    || mActivity.get().mRecFrontImg == null || mActivity.get().mRecBehindImg == null || mActivity.get().mTimeFrontTv == null) {
                return;
            }
            switch (msg.what) {
                case AppConfig.FRONT_CAMERA:
                    if (msg.arg1 == AppConfig.MSG_RECODE_STOP) {
                        //TODO camera 0 stop
                        Toast.makeText(MyApplication.getContext(), "停止录制", Toast.LENGTH_LONG).show();

                        mActivity.get().mAnimFrontRec.stop();
                        mActivity.get().mRecFrontImg.setVisibility(View.GONE);
                        mActivity.get().mTimeFrontTv.setVisibility(View.GONE);
                        if (mActivity.get().mFrontRll.getVisibility() == View.VISIBLE) {
                            //如果还处于前置摄像头界面，则更新（否则通过点击具体摄像头界面更新）
                            mActivity.get().mRecordFileLockBtn.setBackgroundResource(R.drawable.btn_record_lock_off);
                            if (mActivity.get().mCurCameraId == AppConfig.FRONT_CAMERA) {
                                mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_closed);
                            }
//                            mActivity.get().updateRecordCtrlBtn(mActivity.get().mCurCameraDev);
                        }
//                        mRecordStartBtn.setClickable(true);
//                        mRecordStopBtn.setClickable(false);

                    } else if (msg.arg1 == AppConfig.MSG_RECODE_START) {
                        //TODO camera 1 start
//                        Toast.makeText(CameraActivity.this, "正在录制" , Toast.LENGTH_LONG).show();
                        mActivity.get().mRecFrontImg.setVisibility(View.VISIBLE);
                        mActivity.get().mTimeFrontTv.setVisibility(View.VISIBLE);
//                        mRecordStartBtn.setClickable(false);
//                        mRecordStopBtn.setClickable(true);
                        //TODO 这里是有问题的！多次启动动画（内存）
                        mActivity.get().mAnimFrontRec.start();

                        if (mActivity.get().mCurCameraId == AppConfig.FRONT_CAMERA) {
                            mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
                        }

                    } else if (msg.arg1 == AppConfig.MSG_RECODE_COUNT) {
                        if (mActivity.get().mCurCameraId == AppConfig.FRONT_CAMERA) {
                            mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
                        }
                        //TODO Time Update
                        mActivity.get().mTimeFrontTv.setText(DateTimeUtil.formatLongToTimeStr(msg.arg2 * 1000));
                    }
                    break;

                case AppConfig.BEHIND_CAMERA:
                    if (msg.arg1 == AppConfig.MSG_RECODE_STOP) {
                        //stop
                        Toast.makeText(MyApplication.getContext(), "behind停止录制", Toast.LENGTH_LONG).show();
                        mActivity.get().mAnimBehindRec.stop();
                        mActivity.get().mRecBehindImg.setVisibility(View.GONE);
                        mActivity.get().mTimeBehindTv.setVisibility(View.GONE);
                        if (mActivity.get().mBehindRll.getVisibility() == View.VISIBLE) {
                            //如果还处于前置摄像头界面，则更新（否则通过点击具体摄像头界面更新）
                            mActivity.get().mRecordFileLockBtn.setBackgroundResource(R.drawable.btn_record_lock_off);
                            if (mActivity.get().mCurCameraId == AppConfig.BEHIND_CAMERA) {
                                mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_closed);
                            }
                            mActivity.get().updateRecordCtrlBtn(mActivity.get().mCurCameraDev);
                        }
//                        mRecordStartBtn.setClickable(true);
//                        mRecordStopBtn.setClickable(false);

                    } else if (msg.arg1 == AppConfig.MSG_RECODE_START) {
                        //start
                        mActivity.get().mRecBehindImg.setVisibility(View.VISIBLE);
                        mActivity.get().mTimeBehindTv.setVisibility(View.VISIBLE);
//                        mRecordStartBtn.setClickable(false);
//                        mRecordStopBtn.setClickable(true);
                        mActivity.get().mAnimBehindRec.start();

                        if (mActivity.get().mCurCameraId == AppConfig.BEHIND_CAMERA) {
                            mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
                        }
                        mActivity.get().updateRecordCtrlBtn(mActivity.get().mCurCameraDev);
                    } else if (msg.arg1 == AppConfig.MSG_RECODE_COUNT) {
                        if (mActivity.get().mCurCameraId == AppConfig.BEHIND_CAMERA) {
                            mActivity.get().mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
                        }
                        //update time
                        mActivity.get().mTimeBehindTv.setText(DateTimeUtil.formatLongToTimeStr(msg.arg2 * 1000));
                    }
                    break;

                case AppConfig.MSG_FRONT_CAMERA_IN:
                    mActivity.get().mCameraFrontTtv.setVisibility(View.VISIBLE);
                    if (mActivity.get().mService.getCameraDev(AppConfig.FRONT_CAMERA) != null) {
                        mActivity.get().mService.getCameraDev(AppConfig.FRONT_CAMERA).open();
                    }
                    mActivity.get().dvrSurfaceTextureFrontListener.onSurfaceTextureDestroyed(mActivity.get().dvrSurfaceTextureFrontListener.surfaceTexture);
                    mActivity.get().dvrSurfaceTextureFrontListener.onSurfaceTextureAvailable(mActivity.get().mCameraFrontTtv.getSurfaceTexture(), mActivity.get().mCameraFrontTtv.getWidth(), mActivity.get().mCameraFrontTtv.getHeight());
                    break;
                case AppConfig.MSG_FRONT_CAMERA_OUT:
                    mActivity.get().mCameraFrontTtv.setVisibility(View.INVISIBLE);
                    break;
                case AppConfig.MSG_BEHIND_CAMERA_IN:
                    mActivity.get().mCameraBehindTtv.setVisibility(View.VISIBLE);
                    if (mActivity.get().mService.getCameraDev(AppConfig.BEHIND_CAMERA) != null) {
                        mActivity.get().mService.getCameraDev(AppConfig.BEHIND_CAMERA).open();
                    }
//                    mActivity.get().dvrSurfaceTextureBehindListener.onSurfaceTextureDestroyed(mActivity.get().dvrSurfaceTextureBehindListener.surfaceTexture);
//                    mActivity.get().dvrSurfaceTextureBehindListener.onSurfaceTextureAvailable(mActivity.get().mCameraBehindTtv.getSurfaceTexture(), mActivity.get().mCameraBehindTtv.getWidth(), mActivity.get().mCameraBehindTtv.getHeight());
                    break;
                case AppConfig.MSG_BEHIND_CAMERA_OUT:
                    mActivity.get().mCameraBehindTtv.setVisibility(View.INVISIBLE);
                    break;

                case AppConfig.MSG_ACTIVITY_FUWEI:
//                    mActivity.get().dvrSurfaceTextureBehindListener.onSurfaceTextureDestroyed(mActivity.get().dvrSurfaceTextureBehindListener.surfaceTexture);
//                    mActivity.get().dvrSurfaceTextureBehindListener.onSurfaceTextureAvailable(mActivity.get().mCameraBehindTtv.getSurfaceTexture(), mActivity.get().mCameraBehindTtv.getWidth(), mActivity.get().mCameraBehindTtv.getHeight());
                    break;

                default:
                    break;
            }
        }
    }

    public RecordService mService = null;
    private ServiceConnection myServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected ------->");
            RecordService.LoBinder manager = (RecordService.LoBinder) ICameraManager.Stub.asInterface(service);
            mService = manager.getService();
//            mService = ((RecordService.LocalBinder) service).getService();
            if (null == mService.getCameraDev(AppConfig.FRONT_CAMERA) && null == mService.getCameraDev(AppConfig.BEHIND_CAMERA)) {
                //TODO 先得到服务，则为制空，待界面起来置入实例；先得到界面，则为置入实例(各路多要判断)
                LogUtil.d(TAG, "onServiceConnected ---------> addCameraDev");
                mService.addCameraDev(AppConfig.FRONT_CAMERA, dvrSurfaceTextureFrontListener.cameraDev);
//                mService.addCameraDev(AppConfig.BEHIND_CAMERA, dvrSurfaceTextureBehindListener.cameraDev);
            }
            updateRecordCtrlBtn(mService.getCameraDev(mCurCameraId));
        }
    };
//
//    @Override
//    public void onPlugIn(int cameraIndex) {
//        LogUtil.e(TAG, "onPlugIn() ---------->");
//        if (cameraIndex == AppConfig.FRONT_CAMERA) {
//            mCameraFrontTtv.setVisibility(View.VISIBLE);
//            if (mService != null && mService.getCameraDev(cameraIndex) != null) {
//                if (!mService.getCameraDev(cameraIndex).isRecording() && mCameraFrontTtv.isAvailable()) {
//                    LogUtil.e(TAG, "onPlugIn() -----------> isAvailable");
//                    dvrSurfaceTextureFrontListener.onSurfaceTextureDestroyed(dvrSurfaceTextureFrontListener.surfaceTexture);
//                    dvrSurfaceTextureFrontListener.onSurfaceTextureAvailable(mCameraFrontTtv.getSurfaceTexture(), mCameraFrontTtv.getWidth(), mCameraFrontTtv.getHeight());
//                }
//            }
//            return;
//        } else if (cameraIndex == AppConfig.BEHIND_CAMERA) {
//            mCameraBehindTtv.setVisibility(View.VISIBLE);
//            if (mService != null && mService.getCameraDev(cameraIndex) != null) {
//                if (!mService.getCameraDev(cameraIndex).isRecording() && mCameraBehindTtv.isAvailable()) {
//                    LogUtil.e(TAG, "onPlugIn() ----------> isAvailable");
//                    dvrSurfaceTextureBehindListener.onSurfaceTextureDestroyed(dvrSurfaceTextureBehindListener.surfaceTexture);
//                    dvrSurfaceTextureBehindListener.onSurfaceTextureAvailable(mCameraBehindTtv.getSurfaceTexture(), mCameraBehindTtv.getWidth(), mCameraBehindTtv.getHeight());
////            mCameraFrontTtv.setSurfaceTextureListener(dvrSurfaceTextureFrontListener);
//                }
//            }
//        }
//
//    }
//
//    @Override
//    public void onPlugOut(int cameraId) {
//        LogUtil.e(TAG, "onPlugOut() -------->");
//        if (cameraId == AppConfig.FRONT_CAMERA) {
//            mCameraFrontTtv.setVisibility(View.INVISIBLE);
//            if (mService != null) {
//                if (mService.getCameraDev(cameraId) != null) {
//                    if (mService.getCameraDev(cameraId).isRecording()) {
//                        mService.getCameraDev(cameraId).setRecording(false);
//                        System.exit(0);
//                    } else {
//                        mService.getCameraDev(cameraId).releaseCameraAndPreview();
//                    }
//                }
//            }
//        } else if (cameraId == AppConfig.BEHIND_CAMERA) {
//            mCameraBehindTtv.setVisibility(View.INVISIBLE);
//            if (mService != null) {
//                if (mService.getCameraDev(cameraId) != null) {
//                    if (mService.getCameraDev(cameraId).isRecording()) {
//                        mService.getCameraDev(cameraId).setRecording(false);
//                        System.exit(0);
//                    } else {
//                        mService.getCameraDev(cameraId).releaseCameraAndPreview();
//                    }
//                }
//            }
//        }
//        updateRecordCtrlBtn(mCurCameraDev);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();

        isFrontAuto = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_AUTO, true);
        isBehindAuto = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_AUTO, false);

        dvrSurfaceTextureFrontListener = new DvrSurfaceTextureListener(AppConfig.FRONT_CAMERA);

//        dvrSurfaceTextureBehindListener = new DvrSurfaceTextureListener(AppConfig.BEHIND_CAMERA);

        mCameraFrontTtv.setSurfaceTextureListener(dvrSurfaceTextureFrontListener);
//        mCameraBehindTtv.setSurfaceTextureListener(dvrSurfaceTextureBehindListener);

        mFrontRll.setOnClickListener(this);
        mBehindRll.setOnClickListener(this);

//        mRecordCtrlLly.setVisibility(View.GONE);
        mTakePhotoBtn.setOnClickListener(this);
        mRecordSwitchBtn.setOnClickListener(this);
        mVideoDirBtn.setOnClickListener(this);
        mRecordCtrlBtn.setOnClickListener(this);
        mRecordSettingBtn.setOnClickListener(this);
        mRecordFileLockBtn.setOnClickListener(this);

        //先startService再bindService;
        Intent intent = new Intent(CameraActivity.this, RecordService.class);
        startService(intent);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);

        mCurCameraDev = dvrSurfaceTextureFrontListener.cameraDev;  //当做初始化
        mCurCameraId = AppConfig.FRONT_CAMERA;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        usbStateReceiver = new UsbStateReceiver();
//        registerReceiver(usbStateReceiver, filter);

//        IntentFilter filterCamera = new IntentFilter("android.hardware.usb.action.USB_CAMERA_PLUG_IN_OUT");
//        mCameraStateReceiver = new UsbCameraStateReceiver(this);
//        registerReceiver(mCameraStateReceiver, filterCamera);
    }

    private void initView() {
        mCameraFrontTtv = (TextureView) findViewById(R.id.ttv_camera_front);
        mCameraBehindTtv = (TextureView) findViewById(R.id.ttv_camera_behind);
        mRecFrontImg = (ImageView) findViewById(R.id.img_front_rec);
        mAnimFrontRec = (AnimationDrawable) mRecFrontImg.getBackground();
        mRecBehindImg = (ImageView) findViewById(R.id.img_behind_rec);
        mAnimBehindRec = (AnimationDrawable) mRecBehindImg.getBackground();

        mContainerRll = (RelativeLayout) findViewById(R.id.rll_container);
        mFrontRll = (RelativeLayout) findViewById(R.id.rll_front);
        mBehindRll = (RelativeLayout) findViewById(R.id.rll_behind);

        mRecordCtrlLly = (LinearLayout) findViewById(R.id.lly_record_ctrl);
        mTakePhotoBtn = (Button) findViewById(R.id.btn_camera_takephoto);
        mRecordSwitchBtn = (Button) findViewById(R.id.btn_record_switch);
        mVideoDirBtn = (Button) findViewById(R.id.btn_video_dir);
        mRecordCtrlBtn = (Button) findViewById(R.id.btn_record_ctrl);
        mRecordSettingBtn = (Button) findViewById(R.id.btn_record_setting);
        mRecordFileLockBtn = (Button) findViewById(R.id.btn_record_lock);

        mTimeFrontTv = (TextView) findViewById(R.id.tv_front_time);
        mTimeBehindTv = (TextView) findViewById(R.id.tv_behind_time);

//        mCurCameraDev = dvrSurfaceTextureFrontListener.cameraDev;  //当做初始化
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_ctrl:
                if (mService.getCameraDev(mCurCameraId) == null) {
                    Toast.makeText(this, "无摄像设备！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!mService.getCameraDev(mCurCameraId).isCanUse) {
                    Toast.makeText(this, "摄像头不可用！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (mService.getCameraDev(mCurCameraId).isRecording()) {
                    //通过mService获取到的是已经更新过的，安全！
                    mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_closed);
//                    mCurCameraDev.stopRecord();
                    mService.stopRecord(mCurCameraId);
                } else {
                    //通过mService获取到的当前CameraDev是最新的（已更新过），mCurCameraDev没有及时更新
                    if (mService.startRecord(mCurCameraId)) {
                        mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
                    }
                }
                break;

            case R.id.btn_camera_takephoto:
                mService.takePhoto(mCurCameraId);
                break;


            case R.id.rll_front:
                if (mCurCameraId == AppConfig.FRONT_CAMERA) {
                    return;
                }
                mCurCameraId = AppConfig.FRONT_CAMERA;
                switchView();
                updateRecordCtrlBtn(mCurCameraDev);
                break;

            case R.id.rll_behind:
                if (mCurCameraId == AppConfig.BEHIND_CAMERA) {
                    return;
                }
                mCurCameraId = AppConfig.BEHIND_CAMERA;
                switchView();
                updateRecordCtrlBtn(mCurCameraDev);
                break;

            case R.id.btn_record_switch:
                mFrontRll.setVisibility(View.VISIBLE);
                mBehindRll.setVisibility(View.VISIBLE);
//                mRecordCtrlLly.setVisibility(View.GONE);
                mCurCameraId = ALL_CAMERA;
                break;

            case R.id.btn_record_lock:
                if (mService.getCameraDev(mCurCameraId) == null) {
                    Toast.makeText(this, "不在录制状态！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (mService.getCameraDev(mCurCameraId).isRecording()) {
                    String path = mService.getCameraDev(mCurCameraId).getmVideoFile();
                    if (mService.getCameraDev(mCurCameraId).isLocked()) {
                        //处于锁定状态
                        Toast.makeText(CameraActivity.this, "解锁", Toast.LENGTH_SHORT).show();
                        LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
                        lockVideoDAL.deleteLockVideo(path);
                        mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_off);
                        mService.getCameraDev(mCurCameraId).setLocked(false);
                    } else {
                        //处于未锁定状态
                        Toast.makeText(CameraActivity.this, "上锁", Toast.LENGTH_SHORT).show();
                        LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
                        lockVideoDAL.addLockVideo(path);
                        mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_on);
                        mService.getCameraDev(mCurCameraId).setLocked(true);
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "不在录制状态！", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_video_dir:
                //先停止录像
//                dvrSurfaceTextureFrontListener.cameraDev.stopRecord();
                if (!SDCardUtils.isPathEnable(AppConfig.DVR_PATH)) {
                    Toast.makeText(CameraActivity.this, "存储路径不存在！", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(CameraActivity.this, FileList2Activity.class);
                startActivity(intent);
                break;

            case R.id.btn_record_setting:
                Dialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("进入设置将会停止当前录像！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mService.stopAllRecord();
                                Intent settingIntent = new Intent(CameraActivity.this, Setting2Activity.class);
                                startActivity(settingIntent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                alertDialog.show();

                break;

            default:
                break;
        }
    }

    private void switchView() {
        if (mCurCameraId == AppConfig.FRONT_CAMERA) {
            mFrontRll.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mContainerRll.removeView(mBehindRll);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(240, 160);
            layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mBehindRll.setLayoutParams(layoutParams1);
            mContainerRll.addView(mBehindRll);

            mCurCameraDev = dvrSurfaceTextureFrontListener.cameraDev;
        } else if (mCurCameraId == AppConfig.BEHIND_CAMERA) {
            mBehindRll.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mContainerRll.removeView(mFrontRll);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(240, 160);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mFrontRll.setLayoutParams(layoutParams);
            mContainerRll.addView(mFrontRll);

            mRecordCtrlLly.setVisibility(View.VISIBLE);
//            mCurCameraDev = dvrSurfaceTextureBehindListener.cameraDev;
        }
    }

    private void updateRecordCtrlBtn(CameraDev cameraDev) {
        if (cameraDev != null && cameraDev.isRecording()) {
            mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_started);
            //TODO 判断该文件是否被标记 锁定
            if (cameraDev.isLocked()) {
                mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_on);
            } else {
                mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_off);
            }
        } else {
            mRecordCtrlBtn.setBackgroundResource(R.drawable.selector_record_closed);
            mRecordFileLockBtn.setBackgroundResource(R.drawable.selector_record_lock_off);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null && mService.getCameraDev(mCurCameraId) != null) {
            updateRecordCtrlBtn(mService.getCameraDev(mCurCameraId));
        }

        //再次bindService，得到service实例
        Intent intent = new Intent(CameraActivity.this, RecordService.class);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        isFrontAuto = false;  //避免其他界面返回时开始录像
        isBehindAuto = false;
        super.onPause();
        //解除绑定，服务仍在运行(停止服务必须先解除绑定！！！)
        unbindService(myServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "CameraActivity onDestroy ------>");
//        if (mService != null && !mService.isRecording()) {
//            //TODO 检查所有设备是否有正在录像的
//            Intent intent = new Intent(CameraActivity.this, RecordService.class);
//            stopService(intent);
//            mService = null;
//        }

//        unregisterReceiver(usbStateReceiver);
        mHandler.removeCallbacksAndMessages(null);

//        unregisterReceiver(mCameraStateReceiver);

    }

    /**
     * 内部类，实现SurfaceTextureListener
     */
    public class DvrSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        private final String TAG = this.getClass().getName();
        private int mCameraId;
        //        private CameraStatusListener cameraStatusListener;
        public CameraDev cameraDev;
        public SurfaceTexture surfaceTexture;

        public int updateCount = 0;

        public DvrSurfaceTextureListener(int cameraId) {
            mCameraId = cameraId;
//            this.cameraStatusListener = cameraStatusListener;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtil.d(TAG, "onSurfaceTextureAvailable ------->");
            surfaceTexture = surface;
            if (mService != null) {
                //服务还没绑定
                if (mService.isRecording(mCameraId)) {
                    //后台正在录制
                    LogUtil.d(TAG, "onSurfaceTextureAvailable --------> Cmera:" + mCameraId + " is Recording");
                    if (null == mService.getCameraDev(mCameraId)) {
                        LogUtil.e(TAG, "onSurfaceTextureAvailable ------> cameraDev is null!!!!");
                    }
                    mService.startRender(mCameraId, surface);
                    LogUtil.d(TAG, "onSurfaceTextureAvailable -------> Camera:" + mCameraId + " is recording to starRender");

                    cameraDev = mService.getCameraDev(mCameraId);
                    cameraDev.mHandler = mHandler;  //更新handler
                } else {
                    //后台没有录制（进入之后不录制...再次进入）（绑定服务在先，就会进入这）------第一次进入情况2（绑定在先）
                    LogUtil.d(TAG, "onSurfaceTextureAvailable --------> mService not Recording");
                    cameraDev = factory.createCameraDev(mCameraId);
                    cameraDev.mHandler = mHandler;   //更新设置handler
                    cameraDev.open();
                    cameraDev.startPreview(surface);

                    mService.addCameraDev(mCameraId, cameraDev);  //service和cameraDev关联
//                    if (mCameraId == AppConfig.FRONT_CAMERA && isFrontAuto) {
//                        cameraDev.startRecord();  //自启动录像
//                    } else if (mCameraId == AppConfig.BEHIND_CAMERA && isBehindAuto) {
//                        cameraDev.startRecord();
//                    }
                }
            } else {
                //第一次进入(与绑定服务有同步问题，可能会进入)----第一次进入情况1（绑定在后）
                LogUtil.d(TAG, "onSurfaceTextureAvailable --------> mService is null");
                cameraDev = factory.createCameraDev(mCameraId);
                cameraDev.open();
                cameraDev.startPreview(surface);

                cameraDev.mHandler = mHandler;     //设置handler
//                if (mCameraId == AppConfig.FRONT_CAMERA && isFrontAuto) {
//                    cameraDev.startRecord();  //自启动录像
//                } else if (mCameraId == AppConfig.BEHIND_CAMERA && isBehindAuto) {
//                    cameraDev.startRecord();
//                }
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtil.d(TAG, "onSurfaceTextureSizeChanged --------->");
            updateCount = 0;
            surfaceTexture = surface;
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtil.d(TAG, "onSurfaceTextureDestroyed --------->");
            if (mService == null) {
                //activity onDestroy先执行
                cameraDev.stopPreview();
                cameraDev.killRecord();
                cameraDev.releaseCameraAndPreview();
                return true;
            }

            if (mService.isRecording(mCameraId)) {
                LogUtil.d(TAG, "onSurfaceTextureDestroyed ---------> Camera:" + mCameraId + " is Recording to stopRender");
                mService.stopRender(mCameraId);
            } else {
                cameraDev.stopPreview();
                if (cameraDev.mediaRecorder != null) {
                    //处理back返回卡死问题
//                    cameraStatusListener.onStopRecord();
                    //stopRecord后 mediaRecorder 置空了
                    cameraDev.killRecord();
                }
                cameraDev.releaseCameraAndPreview();
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            LogUtil.d(TAG, "onSurfaceTextureUpdated --------->" + mCameraId);
            updateCount++;
            if (updateCount > 2) {
                cameraDev.isCanUse = true;
            }
            if (updateCount > 1000) {
                updateCount = 0;
            }
            surfaceTexture = surface;
        }
    }

    private boolean isStorageEnough() {
        long allSize = SDCardUtils.getFolderSize(new File(AppConfig.DVR_PATH)) + SDCardUtils.getFreeBytes(AppConfig.ROOT_DIR);
        //给DVR预留600M，否则不让录制
        if (allSize < CameraDev.mRecordNeedSpace) {
            LogUtil.d("qiansheng", "DVR can use size:" + allSize);
            return false;
        } else {
            LogUtil.d("qiansheng", "DVR can use size:" + allSize);
            return true;
        }
    }
}
