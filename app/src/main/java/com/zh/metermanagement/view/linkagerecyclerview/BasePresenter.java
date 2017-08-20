package com.zh.metermanagement.view.linkagerecyclerview;


/**
 * 任务
 */
public abstract class BasePresenter {

    protected ViewCallBack mViewCallBack;

    /**
     * 获取 -- "ViewCallBack"
     * @param viewCallBack
     */
    void add(ViewCallBack viewCallBack) {
        this.mViewCallBack = viewCallBack;
    }

    /**
     * 将"ViewCallBack" 设为 "null"
     */
    void remove() {
        this.mViewCallBack = null;
    }


    protected abstract void getData();
}
