package com.android.allwinner.newaw360.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.allwinner.newaw360.R;
import com.android.allwinner.newaw360.VideoPlayerActivity;
import com.android.allwinner.newaw360.adapter.FileListAdapter;
import com.android.allwinner.newaw360.asynctask.DeleteSelectFileTask;
import com.android.allwinner.newaw360.db.ItemBean;
import com.android.allwinner.newaw360.listener.FragmentEventListener;
import com.android.allwinner.newaw360.utils.LogUtil;
import com.android.allwinner.newaw360.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采用分类形式显示文件Fragment
 */
public class VideoFileFragment extends Fragment implements FragmentEventListener {
    private static final String ARG_FILE_TYPE = "FileType";
    private static final String ARG_DIR_URL = "DirUrl";
    private String mFileType;
    private String mDirUrl;

    private ListView mFileListView;
    private List<ItemBean> mFileList;
    private List<ItemBean> selectList;
    private ArrayList<String> mVideopathList;  //视屏播放器需要VideoList
    private FileListAdapter mFileListAdapter;
    private String mRootDirPath = null;
    private String mCurDirPath = null;

    private OnFragmentInteractionListener mListener;

    private LinearLayout lay;   //操作线性布局
    private TextView tvBack;
    private TextView tvDelete;
    private TextView tvSelect;
    private TextView tvInvertSelect;

    private static boolean isShow; // 是否显示CheckBox标识

    public VideoFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fileType Parameter 1.
     * @param dirUrl   Parameter 2.
     * @return A new instance of fragment VideoFileFragment.
     */
    public static VideoFileFragment newInstance(String fileType, String dirUrl) {
        VideoFileFragment fragment = new VideoFileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_TYPE, fileType);
        args.putString(ARG_DIR_URL, dirUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileType = getArguments().getString(ARG_FILE_TYPE);
            mDirUrl = getArguments().getString(ARG_DIR_URL);
            mRootDirPath = mDirUrl;
            mCurDirPath = mDirUrl;
        }
        LogUtil.d("qiansheng", mFileType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_file, container, false);
        findView(view);

        selectList = new ArrayList<ItemBean>();
        mFileList = getData();

        if (mFileList != null && mFileList.size() != 0) {
            view.findViewById(R.id.tv_filelist_emty).setVisibility(View.GONE);

            mFileListAdapter = new FileListAdapter(mFileList, getActivity());
            mFileListAdapter.setOnShowItemClickListener(this);
            mFileListView.setAdapter(mFileListAdapter);

            mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if ((int) mFileList.get(position).getDrawable() == R.drawable.icon_file) {
                        //目录
                        mCurDirPath = (String) mFileList.get(position).getPatch();
                        mFileList = getData();
                        mFileListAdapter = new FileListAdapter(mFileList, getActivity());
                        mFileListView.setAdapter(mFileListAdapter);
                    } else if ((int) mFileList.get(position).getDrawable() == R.drawable.icon_file_normalvideo) {
                        //TODO 视屏文件
                        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                        intent.putStringArrayListExtra("extra_play_list", mVideopathList);
                        mFileList.get(position).getPatch();
                        if (mVideopathList.contains((String) mFileList.get(position).getPatch())) {
                            int index = mVideopathList.indexOf((String) mFileList.get(position).getPatch());
                            intent.putExtra("extra_play_index", index);
                            startActivity(intent);
                        }

                    } else {
                        //其他普通文件
                        //TODO 图片文件使用图片浏览器
                        File file = new File((String) mFileList.get(position).getPatch());
                        Intent picIntent = new Intent(Intent.ACTION_VIEW);
                        picIntent.setDataAndType(Uri.fromFile(file), "image/*");
                        try {
                            startActivity(picIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "找不到应用程序打开该文件", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
            mFileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                    if (isShow) {
                        LogUtil.e("qiansheng", "isShow true");
                        return false;
                    } else {
                        LogUtil.e("qiansheng", "isShow false");
                        isShow = true;
                        for (ItemBean bean : mFileList) {
                            bean.setShow(true);
                        }
                        mFileListAdapter.notifyDataSetChanged();
                        showOpervate();
                        mFileListView.setLongClickable(false);
                    }
                    return true;
                }
            });

            return view;
        } else {
            view.findViewById(R.id.tv_filelist_emty).setVisibility(View.VISIBLE);
            mFileListView.setVisibility(View.GONE);
            return view;
        }
    }

    private void findView(View view) {
        mFileListView = (ListView) view.findViewById(R.id.listview_file);
        lay= (LinearLayout) view.findViewById(R.id.lay);
        tvBack =(TextView) view.findViewById(R.id.operate_back);
        tvDelete = (TextView) view.findViewById(R.id.operate_delete);
        tvSelect = (TextView) view.findViewById(R.id.operate_select);
        tvInvertSelect = (TextView) view.findViewById(R.id.invert_select);
    }

    private List<ItemBean> getData() {
        List<ItemBean> list = new ArrayList<ItemBean>();
        mVideopathList = new ArrayList<String>();
        Map<String, Object> map = null;
        File f = new File(mCurDirPath);
        File[] files = f.listFiles();

        if (!mCurDirPath.equals(mRootDirPath)) {
            //不在根目录下，加...
            map = new HashMap<String, Object>();
            map.put("title", "Back to ../");
            map.put("path", f.getParent()); //父目录
            map.put("img", R.drawable.icon_file);
//            list.add(map);
        }
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                map = new HashMap<String, Object>();
                ItemBean itemBean = new ItemBean();
                itemBean.setTitle(files[i].getName());
                itemBean.setPatch(files[i].getPath());
                map.put("title", files[i].getName());
                map.put("path", files[i].getPath());  //path
                if (files[i].isDirectory()) {
                    map.put("img", R.drawable.icon_file);
                    itemBean.setDrawable(R.drawable.icon_file);
                } else if (files[i].isFile() & ".ts".equalsIgnoreCase(StringUtils.getPathSuffix(files[i].getName())) || ".mp4".equalsIgnoreCase(StringUtils.getPathSuffix(files[i].getName()))) {
                    //TODO 视屏文件
                    map.put("img", R.drawable.icon_file_normalvideo);
                    itemBean.setDrawable(R.drawable.icon_file_normalvideo);

                    mVideopathList.add(files[i].getPath());
                } else {
                    //普通文件
//                    map.put("img", R.drawable.icon_doc);
                    map.put("img", R.drawable.icon_picture);
                    itemBean.setDrawable(R.drawable.icon_picture);
                }
//                list.add(map);
                list.add(itemBean);
            }
        }
        return list;
    }


    /**
     * 显示操作界面
     */
    private void showOpervate() {
        mListener.onFragmentInteraction(true);
        lay.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.operate_in);
        lay.setAnimation(anim);
//		ViewGroup view = (ViewGroup) findViewById(R.id.main_view);
        /*LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = (RelativeLayout) findViewById(R.id.main_activity);
        opreateView = (LinearLayout) inflater.inflate(R.layout.select, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM ,R.id.main_listview);
        // 操作界面动画
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.operate_in);
        opreateView.setAnimation(anim);
        rootView.addView(opreateView, params);*/
        // 返回、删除、全选和反选按钮初始化及点击监听
        /*tvSelect.setText("全选");*/
        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShow) {
                    selectList.clear();
                    for (ItemBean bean : mFileList) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    mFileListAdapter.notifyDataSetChanged();
                    isShow = false;
                    mFileListView.setLongClickable(true);
                    dismissOperate();
                }
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*if ("全选".equals(tvSelect.getText().toString())) {*/
                for (ItemBean bean : mFileList) {
                    if (!bean.isChecked()) {
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }
                }
                mFileListAdapter.notifyDataSetChanged();
                    /*tvSelect.setText("反选");*/
                /*} */
                /*else if ("反选".equals(tvSelect.getText().toString())) {
                    for (ItemBean bean : dataList) {
                        bean.setChecked(false);
                        if (!selectList.contains(bean)) {
                            selectList.remove(bean);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvSelect.setText("全选");
                }*/
            }
        });
        tvInvertSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ItemBean bean : mFileList){
                    if (!bean.isChecked()){
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }else {
                        bean.setChecked(false);
                        if (!selectList.contains(bean)) {
                            selectList.remove(bean);
                        }
                    }
                }
                mFileListAdapter.notifyDataSetChanged();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectList != null && selectList.size() > 0) {
                    LogUtil.e("qiansheng", "list size:" + selectList.size());
                    DeleteSelectFileTask task = new DeleteSelectFileTask();
                    //对象的 值传递！！！
                    List<ItemBean> list = new ArrayList<ItemBean>(selectList);
                    task.execute(list);
                    mFileList.removeAll(selectList);
                    mFileListAdapter.notifyDataSetChanged();
                    selectList.clear();
                } else {
                    Toast.makeText(getActivity(), "请选择条目", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 隐藏操作界面
     */
    private void dismissOperate() {
        mListener.onFragmentInteraction(false);
//        if (opreateView != null) {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.operate_out);
//            opreateView.setAnimation(anim);
//            rootView.removeView(opreateView);

        lay.setVisibility(View.GONE);
        lay.setAnimation(anim);
//        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            LogUtil.e("qiansheng", "hide true");
            if (isShow) {
                selectList.clear();
            for (ItemBean bean : mFileList) {
                bean.setChecked(false);
                bean.setShow(false);
            }
            mFileListAdapter.notifyDataSetChanged();
                isShow = false;
                mFileListView.setLongClickable(true);
                lay.setVisibility(View.GONE);
            }
        }else {
            LogUtil.e("qiansheng", "hide false");
        }
    }

    @Override
    public void onKeyDown() {
        if (isShow) {
            selectList.clear();
            for (ItemBean bean : mFileList) {
                bean.setChecked(false);
                bean.setShow(false);
            }
            mFileListAdapter.notifyDataSetChanged();
            isShow = false;
            mFileListView.setLongClickable(true);
            dismissOperate();
        }
    }

    @Override
    public void onShowItemClick(ItemBean bean) {
        if (bean.isChecked() && !selectList.contains(bean)) {
            selectList.add(bean);
        } else if (!bean.isChecked() && selectList.contains(bean)) {
            selectList.remove(bean);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(boolean isShow);
    }
}
