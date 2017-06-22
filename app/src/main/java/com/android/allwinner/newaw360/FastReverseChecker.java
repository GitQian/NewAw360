package com.android.allwinner.newaw360;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.android.allwinner.newaw360.utils.LogUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/16.
 */

public class FastReverseChecker {

    private IFastReverseListener mFastReverseListener = null;

    private long mStartTime = 0;
    private boolean mIsRunning = true;

    private Context mContext;

    private boolean mIsNeedExit = true;  //应用倒车开关

    public static final int CHECK_DELAY = 500;
    public static final int MSG_ON_FASTBOOT = 100;

    private int mReverseCount = 0;
    private boolean mIsReversing = false;

    private static final int STATUS_TRUE = 1;
    private static final int STATUS_FALSE = 0;
    private static final int FLAG_NEED_EXIT = 1;
    private static final String FILE_STATUS = "/sys/class/switch/parking-switch/state";
    private static final String FILE_EXIT_HANDLE = "/sys/class/car_reverse/needexit";

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ON_FASTBOOT:
                    if (mFastReverseListener != null) {
                        boolean isReversing = (msg.arg1 == 0 ? true : false);
                        mFastReverseListener.onFastReverseBoot(isReversing);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public FastReverseChecker(Context context) {
        mContext = context;
    }

    private Thread mCheckerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            mStartTime = System.currentTimeMillis();
            Intent it = null;
            while (mIsRunning) {
                if (mIsNeedExit) {
                    if (readBootStatus(FILE_STATUS)) {
                        //TODO 倒车
                        LogUtil.e("qiansheng", "倒车！！！");
                        if (mFastReverseListener != null) {
                            if (mReverseCount < 1) {
                                mReverseCount++;
                            } else {
                                if (!mIsReversing) {
                                    //只回调一次
                                    mFastReverseListener.onFastReverseBoot(true);
                                }
                                mIsReversing = true;
                            }
                        }
                    } else {
                        //TODO 复位
                        if (mFastReverseListener != null) {
                            if (mIsReversing) {
                                //只回调一次
                                mFastReverseListener.onFastReverseBoot(false);
                            }
                            mIsReversing = false;
                            mReverseCount = 0;
                        }
                    }


                    try {
                        Thread.sleep(CHECK_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

//                mHandler = null;
//                mFastReverseListener = null;
            }
        }
    });

    public void start() {
        mCheckerThread.start();
    }


    public interface IFastReverseListener {
        void onFastReverseBoot(boolean isReversing);
    }

    public void setFastReverseListener(IFastReverseListener ls) {
        mFastReverseListener = ls;
    }

    /**
     * 读取倒车状态
     *
     * @param file
     * @return
     */
    private boolean readBootStatus(String file) {
        boolean isBoot = false;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] buf = new char[15];
            int nn = reader.read(buf);
            if (nn > 0) {
                isBoot = (STATUS_TRUE == Integer.parseInt(new String(buf, 0, nn - 1)));
            }
        } catch (IOException ex) {
            // Log.e(TAG, "Couldn't read state from " + file + ": " + ex);
        } catch (NumberFormatException ex) {
            // Log.w(TAG, "Couldn't read state from " + file + ": " + ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return isBoot;
    }

    /**
     * 切换内核倒车和应用倒车
     *
     * @param file
     * @param flag
     */
    private void writeExitFlag(String file, int flag) {
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
