package com.android.allwinner.newaw360.db;

/**
 * Created by Administrator on 2017/4/12.
 */

public class ItemBean {
    private String title;
    private String patch;
    private Object drawable;
    private boolean isShow; // 是否显示CheckBox
    private boolean isChecked; // 是否选中CheckBox


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPatch() {
        return patch;
    }

    public void setPatch(String mPatch) {
        this.patch = mPatch;
    }

    public Object getDrawable() {
        return drawable;
    }

    public void setDrawable(Object drawable) {
        this.drawable = drawable;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
