package com.zh.metermanagement.view.linkagerecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zh.metermanagement.R;


/**
 * author pangchao
 * created on 2017/5/20
 * email fat_chao@163.com.
 */

public abstract class BaseFragment<T extends BasePresenter, V> extends Fragment implements View.OnClickListener, ViewCallBack<V> {

    /** public T presenter -- T extends BasePresenter */
    public T presenter;

    protected boolean isVisible;
    protected Context mContext;
    protected boolean isPrepared;
    protected FlexibleLayout mFlexibleLayout;
    protected TextView tvTitle;
    private LinearLayout mLinearLayout;
    protected TextView tvRight;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        View view = initView(inflater, container);

        initListener();

        mFlexibleLayout.loadData();      // 加载数据
        isPrepared = true;
        return view;
    }

    /**
     * 设置"页面"当前状态
     *
     * @param code      当前状态
     */
    public void showRightPage(int code) {
        switch (code) {
            case 0:
                mFlexibleLayout.showPageWithState(FlexibleLayout.State.Empty);
                break;

            case 1:
                mFlexibleLayout.showPageWithState(FlexibleLayout.State.Normal);
                break;

            case 2:
                mFlexibleLayout.showPageWithState(FlexibleLayout.State.NetWorkError);
                break;
        }
    }

    /**
     * 设置 -- 标题的内容
     * @param title
     */
    protected void setTitle(String title) {
        if (null != tvTitle)
            tvTitle.setText(title);
    }

    /**
     * 设置 -- 标题的背景颜色
     *
     * @param color
     */
    protected void setBackColor(int color) {
        if (null != mLinearLayout)
            mLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, color));
    }

    /**
     * 右键点击事件
     */
    protected void onRightClick() {

    }

    private ViewGroup initView(final LayoutInflater inflater, final ViewGroup parent) {

        mFlexibleLayout = new FlexibleLayout(mContext) {
            @Override
            public ViewGroup initNormalView() {
                return initViewGroup(inflater, parent);
            }


            @Override
            public void onLoad() {
                if (presenter == null)
                    presenter = initPresenter();
                else
                    getData();
            }
        };

        return mFlexibleLayout;
    }


    private ViewGroup initViewGroup(LayoutInflater inflater, ViewGroup parent) {

        // 加载标题
        ViewGroup view = (ViewGroup) inflater.inflate(getLayoutId(), parent, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvRight = (TextView) view.findViewById(R.id.tv_right);
        if (tvRight != null)
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightClick();
                }
            });

        mLinearLayout = (LinearLayout) view.findViewById(R.id.top_layout);      // 标题


        initCustomView(view);       // 初始化布局
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.add((ViewCallBack) this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.remove();

    }

    /** 获取布局 id */
    protected abstract int getLayoutId();

    /** 初始化界面 */
    protected abstract void initCustomView(View view);

    /** 初始化监听事件 */
    protected abstract void initListener();

    /** 初始化数据以及请求参数 */
    protected abstract T initPresenter();

    /** 获取数据 */
    protected abstract void getData();



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContext != null)
            mContext = null;
    }

}