package com.android.allwinner.newaw360.asynctask;


import android.os.AsyncTask;

import com.android.allwinner.newaw360.db.ItemBean;
import com.android.allwinner.newaw360.utils.FileUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */

public class DeleteSelectFileTask extends AsyncTask<List<ItemBean>, Integer, String> {

    @Override
    protected String doInBackground(List<ItemBean>... params) {
        List<ItemBean> list = params[0];
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i).getPatch();
            FileUtils.deleteFile(path);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
