package com.android.allwinner.newaw360.fragment;

import android.content.Context;
import android.content.Intent;
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

import com.android.allwinner.newaw360.MyApplication;
import com.android.allwinner.newaw360.R;
import com.android.allwinner.newaw360.VideoPlayerActivity;
import com.android.allwinner.newaw360.adapter.LockVideoAdapter;
import com.android.allwinner.newaw360.db.ItemBean;
import com.android.allwinner.newaw360.db.LockVideoDAL;
import com.android.allwinner.newaw360.listener.FragmentEventListener;
import com.android.allwinner.newaw360.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 锁视频列表Fragment
 */
public class LockVideoFragment extends Fragment implements FragmentEventListener {

    private ListView mListView;
    private LockVideoAdapter mAdapter;
    private List<ItemBean> mPathList;
    private List<ItemBean> selectList;
    private List<String> mVideopathList;


    private OnFragmentInteractionListener mListener;

    private LinearLayout lay;   //操作线性布局
    private TextView tvBack;
    private TextView tvDelete;
    private TextView tvSelect;
    private TextView tvInvertSelect;

    private static boolean isShow; // 是否显示CheckBox标识

    public LockVideoFragment() {
        // Required empty public constructor
    }

    public static LockVideoFragment newInstance() {
        LockVideoFragment fragment = new LockVideoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lock_video, container, false);
        mListView = (ListView) view.findViewById(R.id.listview_file);
        lay= (LinearLayout) view.findViewById(R.id.lay);
        tvBack =(TextView) view.findViewById(R.id.operate_back);
        tvDelete = (TextView) view.findViewById(R.id.operate_delete);
        tvSelect = (TextView) view.findViewById(R.id.operate_select);
        tvInvertSelect = (TextView) view.findViewById(R.id.invert_select);

        selectList = new ArrayList<ItemBean>();
        mPathList = getData();
        if (mPathList != null && mPathList.size() != 0) {
            view.findViewById(R.id.tv_filelist_emty).setVisibility(View.GONE);

            mAdapter = new LockVideoAdapter(mPathList, getActivity());
            mAdapter.setOnShowItemClickListener(this);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    intent.putStringArrayListExtra("extra_play_list", (ArrayList<String>) mVideopathList);
                    intent.putExtra("extra_play_index", position);
                    startActivity(intent);
                }
            });

            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                    if (isShow) {
                        LogUtil.e("qiansheng", "isShow true");
                        return false;
                    } else {
                        LogUtil.e("qiansheng", "isShow false");
                        isShow = true;
                        for (ItemBean bean : mPathList) {
                            bean.setShow(true);
                        }
                        mAdapter.notifyDataSetChanged();
                        showOpervate();
                        mListView.setLongClickable(false);
                    }
                    return true;
                }
            });
            return view;
        } else {
            view.findViewById(R.id.tv_filelist_emty).setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            return view;
        }
    }

    private List<ItemBean> getData() {
        List<ItemBean> listBean = new ArrayList<ItemBean>();
        List<String> list;
        LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
        list = lockVideoDAL.getAllLockVideo();
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i));
            if (!file.exists()) {
                lockVideoDAL.deleteLockVideo(list.get(i));
                list.remove(i);
                i--;
            } else {
            }
        }
        mVideopathList = new ArrayList<String>();
        mVideopathList = list;
        for (int i = 0; i < list.size(); i++) {
            ItemBean bean = new ItemBean();
            bean.setPatch(list.get(i));
            listBean.add(bean);
        }
        return listBean;
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
                    for (ItemBean bean : mPathList) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    mAdapter.notifyDataSetChanged();
                    isShow = false;
                    mListView.setLongClickable(true);
                    dismissOperate();
                }
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*if ("全选".equals(tvSelect.getText().toString())) {*/
                for (ItemBean bean : mPathList) {
                    if (!bean.isChecked()) {
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
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
                for (ItemBean bean : mPathList){
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
                mAdapter.notifyDataSetChanged();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectList != null && selectList.size() > 0) {
                    LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
                    for (int i = 0; i < selectList.size(); i++) {
                        lockVideoDAL.deleteLockVideo(selectList.get(i).getPatch());
                    }

                    mPathList.removeAll(selectList);
                    mAdapter.notifyDataSetChanged();
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
        if (context instanceof LockVideoFragment.OnFragmentInteractionListener) {
            mListener = (LockVideoFragment.OnFragmentInteractionListener) context;
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
                for (ItemBean bean : mPathList) {
                    bean.setChecked(false);
                    bean.setShow(false);
                }
                mAdapter.notifyDataSetChanged();
                isShow = false;
                mListView.setLongClickable(true);
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
            for (ItemBean bean : mPathList) {
                bean.setChecked(false);
                bean.setShow(false);
            }
            mAdapter.notifyDataSetChanged();
            isShow = false;
            mListView.setLongClickable(true);
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
