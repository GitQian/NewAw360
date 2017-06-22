package com.android.allwinner.newaw360.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.allwinner.newaw360.AppConfig;
import com.android.allwinner.newaw360.ICameraManager;
import com.android.allwinner.newaw360.MainActivity;
import com.android.allwinner.newaw360.MyApplication;
import com.android.allwinner.newaw360.model.CameraDev;
import com.android.allwinner.newaw360.model.CameraFactory;
import com.android.allwinner.newaw360.service.RecordService;
import com.android.allwinner.newaw360.utils.LogUtil;
import com.android.allwinner.newaw360.utils.SPUtils;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getName();
    public RecordService mService = null;
    CameraFactory factory = new CameraFactory();
    public CameraDev cameraDev;

    static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    public BootBroadcastReceiver() {
    }

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

            if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_AUTO, false)) {
                if (mService.getCameraDev(AppConfig.FRONT_CAMERA) == null) {
                    cameraDev = factory.createCameraDev(AppConfig.FRONT_CAMERA);
                    mService.addCameraDev(AppConfig.FRONT_CAMERA, cameraDev);
                }
                if (!mService.isRecording(AppConfig.FRONT_CAMERA)) {
                    if (mService.getCameraDev(AppConfig.FRONT_CAMERA).camera == null) {
                        mService.open(AppConfig.FRONT_CAMERA);
                    }
                    mService.startPreView(AppConfig.FRONT_CAMERA, null);
                    if (!mService.startRecord(AppConfig.FRONT_CAMERA)) {
                        mService.getCameraDev(AppConfig.FRONT_CAMERA).killRecord();
                        mService.getCameraDev(AppConfig.FRONT_CAMERA).releaseCameraAndPreview();
                        mService.addCameraDev(AppConfig.FRONT_CAMERA, null);
                    }
                }
            }

            if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_AUTO, false)) {
                if (mService.getCameraDev(AppConfig.BEHIND_CAMERA) == null) {
                    cameraDev = factory.createCameraDev(AppConfig.BEHIND_CAMERA);
                    mService.addCameraDev(AppConfig.BEHIND_CAMERA, cameraDev);
                }
                if (!mService.isRecording(AppConfig.BEHIND_CAMERA)) {
                    if (mService.getCameraDev(AppConfig.BEHIND_CAMERA).camera == null) {
                        mService.open(AppConfig.BEHIND_CAMERA);
                    }
                    mService.startPreView(AppConfig.BEHIND_CAMERA, null);
                    if (!mService.startRecord(AppConfig.BEHIND_CAMERA)) {
                        mService.getCameraDev(AppConfig.BEHIND_CAMERA).killRecord();
                        mService.getCameraDev(AppConfig.BEHIND_CAMERA).releaseCameraAndPreview();
                        mService.addCameraDev(AppConfig.BEHIND_CAMERA, null);
                    }
                }
            }
        }
    };


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)) {
            boolean isAuto = (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_APP_AUTO_RUN, true);
            if (isAuto) {
                Intent startIntent = new Intent(context, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startIntent);
            }

            Intent intent1 = new Intent(context, RecordService.class);
            context.startService(intent1);
            if ((Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_FRONT_AUTO, false)
                    || (Boolean) SPUtils.get(MyApplication.getContext(), AppConfig.KEY_IS_BEHIND_AUTO, false)) {

                MyApplication.getContext().bindService(intent1, myServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }

    }

}
