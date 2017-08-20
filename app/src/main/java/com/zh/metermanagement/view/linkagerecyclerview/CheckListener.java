package com.zh.metermanagement.view.linkagerecyclerview;

/**
 * recycler控件的滑动 事件 -- 监听
 *
 */

public interface CheckListener {

    /**
     *
     * @param position  recycler控件的item
     * @param isScroll  recycler控件 的滑动状态
     */
    void check(int position, boolean isScroll);
}
