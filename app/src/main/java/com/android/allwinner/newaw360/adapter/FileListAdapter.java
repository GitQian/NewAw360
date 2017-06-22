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

import java.util.List;

/**
 * Created by admin on 2016/10/1.
 */
public class FileListAdapter extends BaseAdapter {

    private List<ItemBean> fileList;
    private Context context;

    private FragmentEventListener onShowItemClickListener;

    public FileListAdapter(List<ItemBean> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_filelist, null);

            holder.img = (ImageView) convertView.findViewById(R.id.img_file_list);
            holder.title = (TextView) convertView.findViewById(R.id.tv_file_title);
            holder.cb = (CheckBox) convertView.findViewById(R.id.listview_select_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ItemBean bean = fileList.get(position);
        // 是否是多选状态
        if (bean.isShow()) {
            holder.cb.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.GONE);
        }

        holder.img.setBackgroundResource((int) bean.getDrawable());
        holder.title.setText((String) bean.getTitle());

        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
        holder.cb.setChecked(bean.isChecked());
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
