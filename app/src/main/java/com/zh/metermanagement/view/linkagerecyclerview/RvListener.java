package com.zh.metermanagement.view.linkagerecyclerview;


/**
 * 接口 -- RecyclerView的item点击事件(将点击事件暴露给子类)
 */
public interface RvListener {

    /**
     * RecyclerView的item点击事件(将点击事件暴露给子类)
     *
     * @param id                被点击的控件的id
     * @param position          被点击的item的下标
     */
    void onItemClick(int id, int position);
}
