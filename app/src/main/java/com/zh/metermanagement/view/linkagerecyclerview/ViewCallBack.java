package com.zh.metermanagement.view.linkagerecyclerview;

/**
 * 接口 ： 数据的回调 -- 有一个方法(数据的刷新)
 *
 * @param <V>
 */
public interface ViewCallBack<V> {

    /**
     * 刷新数据
     *
     * @param code code -- 0:有数据;   1：数据为空;    2:加载失败
     * @param data 定义好的数据类型
     */
    void refreshView(int code, V data);
}