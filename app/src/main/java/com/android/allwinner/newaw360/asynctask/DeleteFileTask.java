package com.android.allwinner.newaw360.asynctask;

import android.os.AsyncTask;

import com.android.allwinner.newaw360.AppConfig;
import com.android.allwinner.newaw360.MyApplication;
import com.android.allwinner.newaw360.db.LockVideoDAL;
import com.android.allwinner.newaw360.model.CameraDev;
import com.android.allwinner.newaw360.utils.FileOrderUtils;
import com.android.allwinner.newaw360.utils.SDCardUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */

public class DeleteFileTask extends AsyncTask<String[], Integer, String> {

    @Override
    protected String doInBackground(String[]... params) {
        List<File> listFile = FileOrderUtils.orderByDate(params[0]);

        LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
        List<String> listPath = lockVideoDAL.getAllLockVideo();

        int index = 0;
        //TODO 在DVR根目录，对文件进行排序
        while (SDCardUtils.getFreeBytes(AppConfig.DVR_PATH) < CameraDev.mRecordSwitchSpace) {
            while (listPath.contains(listFile.get(index).getAbsolutePath())) {
                //跳过锁定文件，表删！
                index++;
            }
            //删除掉最久一个
            if (index < listFile.size()) {
                listFile.get(index).delete();
                index ++;
                continue;
            }

//            for (File file : listFile) {
//                Log.d("qiansheng", file.getAbsolutePath());
//            }
        }
        return null;
    }
}
