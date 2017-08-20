package com.zh.metermanagement.view.linkagerecyclerview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zh.metermanagement.R;


/**
 * 灵活的布局(线性布局) -- FlexibleLayout extends LinearLayout
 *
 *
 */
public abstract class FlexibleLayout extends LinearLayout {

    private View mLoadingView;
    private View mNetworkErrorView;
    private View mEmptyView;

    /** 控件组 */
    private ViewGroup mSuccessView;

    /** mLoadingView -- 中的 进度条 */
    private ProgressBar mLoadingProgress;
    /** mLoadingView -- 中的 进度文字 */
    private TextView mLoadingText;

    private View title;


    public FlexibleLayout(Context context) {
        super(context);

        setOrientation(VERTICAL);                      // 设置为"垂直"
        setClipToPadding(true);                        // clipToPadding就是说控件的绘制区域是否在padding里面的，
                                                        // true的情况下如果你设置了padding那么绘制的区域就往里 缩

        setFitsSystemWindows(true);                     // true : 系统会自动的调整显示区域来实现详情的控件不会被遮住。

        inflate(context, R.layout.layout_all, this);    // 将布局 填充到 -- 当前控件

        mSuccessView = initNormalView();
        // 我们可能在有得需求情况下要给view设置一个tag,
        // 然后根据这个tag获取这个对应的view对象,给一个view设置一个tag为 -- setTag()，
        // 根据这个tag获取这个"view对象"使用 -- findViewWithTag()方法,
        title = mSuccessView.findViewWithTag("title");

        // 设置布局参数 (水平--铺满, 垂直--铺满)
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mSuccessView, lp);  // 以这个布局参数添加到 -- 当前布局里面

        int childCount = mSuccessView.getChildCount();   // 获得"控件组"里面有多少个--子控件
        Log.d("count---", String.valueOf(childCount));
    }

    public abstract ViewGroup initNormalView();


    /**
     * 加载数据
     */
    public void loadData() {
        showPageWithState(State.Loading);           // 正在加载
        onLoad();
    }

    /**
     * 子类实现 -- 加载数据
     */
    public abstract void onLoad();

    /**
     * 设置"页面"当前状态
     *
     * @param status    "页面"当前状态
     */
    public void showPageWithState(State status) {

        if (status != State.Normal && title != null) {      // 状态 -- 不为空 , 标题控件 -- 不为空

            String tag = (String) getChildAt(0).getTag();   // 获取"第一个子控件"的 -- 标识
            if (TextUtils.equals(tag, "title")) {
                //已经有标题栏
            } else {                                        // 这个View的"标识" 不是 "title"
                mSuccessView.removeView(title);
                addView(title, 0);                          //  加在第一个子控件
            }

        }

        switch (status) {

            case Normal:                                                        // 状态 -- 正常的
                mSuccessView.setVisibility(VISIBLE);                            // 设置控件 -- 可见
                int childCount = getChildCount();                               // 当前布局有多少个 -- 子控件
                Log.d("count--->", String.valueOf(childCount));
                View childAt = mSuccessView.getChildAt(0); //有可能为空 -- 前面忽略了"异常"

                if (childAt != null) {
                    String tag = (String) childAt.getTag();
                    if (!TextUtils.equals(tag, "title") && null != title) {
                        removeView(title);
                        mSuccessView.addView(title, 0);
                    }
                }

                if (mLoadingView != null) {
                    mLoadingView.setVisibility(GONE);
                }

                if (mNetworkErrorView != null) {
                    mNetworkErrorView.setVisibility(GONE);
                }

                invalidate();       // 重画控件
                break;

            case Loading:                                                       // 状态 -- 加载
                mSuccessView.setVisibility(GONE);
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(GONE);
                }

                if (mNetworkErrorView != null) {
                    mNetworkErrorView.setVisibility(GONE);
                }

                if (mLoadingView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_loading);
                    mLoadingView = viewStub.inflate();
                    mLoadingProgress = (ProgressBar) mLoadingView.findViewById(R.id.loading_progress);
                    mLoadingText = (TextView) mLoadingView.findViewById(R.id.loading_text);
                } else {
                    mLoadingView.setVisibility(VISIBLE);
                }

                mLoadingProgress.setVisibility(View.VISIBLE);
                mLoadingText.setText("正在加载");
                break;

            case Empty:                                                         // 状态 -- 空
                mSuccessView.setVisibility(GONE);
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(GONE);
                }

                if (mNetworkErrorView != null) {
                    mNetworkErrorView.setVisibility(GONE);
                }

                if (mEmptyView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_end);
                    mEmptyView = viewStub.inflate();
                } else {
                    mEmptyView.setVisibility(VISIBLE);
                }
                break;

            case NetWorkError:                                                  // 状态 -- 网络错误
                mSuccessView.setVisibility(GONE);
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(GONE);
                }

                if (mEmptyView != null) {
                    mEmptyView.setVisibility(GONE);
                }

                if (mNetworkErrorView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_error);
                    mNetworkErrorView = viewStub.inflate();
                    View btnRetry = mNetworkErrorView.findViewById(R.id.btn_retry);
                    btnRetry.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onLoad();
                        }
                    });

                } else {
                    mNetworkErrorView.setVisibility(VISIBLE);
                }
                break;

            default:
                break;
        }
    }


    /**
     * 状态 <p>
     *
     * Normal  -- 正常的           <br>
     * Empty  -- 空               <br>
     * Loading  -- 加载           <br>
     * NetWorkError  -- 网络错误   <br>
     */
    public enum State {
        Normal, Empty, Loading, NetWorkError
    }

}
