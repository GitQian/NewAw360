package com.android.allwinner.newaw360.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.allwinner.newaw360.R;
import com.android.allwinner.newaw360.db.ItemBean;
import com.android.allwinner.newaw360.listener.FragmentEventListener;
import com.android.allwinner.newaw360.utils.StringUtils;

import java.util.List;

/**
 * 锁视频Adapter
 * Created by Administrator on 2016/10/18.
 */

public class LockVideoAdapter extends BaseAdapter {

    private Context mContext;
    private List<ItemBean> mPathList;

    private FragmentEventListener onShowItemClickListener;

    public LockVideoAdapter(List<ItemBean> pathList, Context context) {
        mPathList = pathList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mPathList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LockVideoAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filelist, null);

            viewHolder.img = (ImageView) convertView.findViewById(R.id.img_file_list);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_file_title);
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.listview_select_cb);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LockVideoAdapter.ViewHolder) convertView.getTag();
        }

        final ItemBean bean = mPathList.get(position);
        // 是否是多选状态
        if (bean.isShow()) {
            viewHolder.cb.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cb.setVisibility(View.GONE);
        }

        viewHolder.img.setBackgroundResource(R.drawable.icon_file_lockvideo);
        viewHolder.title.setText(StringUtils.getPathSubName(mPathList.get(position).getPatch()));

        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bean.setChecked(true);
                } else {
                    bean.setChecked(false);
                }
                // 回调方法，将Item加入已选
                onShowItemClickListener.onShowItemClick(bean);
            }
        });
        // 必须放在监听后面
        viewHolder.cb.setChecked(bean.isChecked());

        return convertView;
    }

    public final class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView path;
        public CheckBox cb;
    }

    public void setOnShowItemClickListener(FragmentEventListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }
}
