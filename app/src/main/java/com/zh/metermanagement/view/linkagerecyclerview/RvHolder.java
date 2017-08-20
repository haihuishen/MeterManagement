package com.zh.metermanagement.view.linkagerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * RecyclerView -- 循环使用的"控件组"类
 * @param <T>
 */
public abstract class RvHolder<T> extends RecyclerView.ViewHolder {

    protected RvListener mListener;         // RecyclerView的item点击事件

    /**
     * 构造函数
     *
     * @param itemView      被点击的 item
     * @param type          item 的类型
     * @param listener      监听的"接口"--子类实现
     */
    public RvHolder(View itemView, int type, RvListener listener) {
        super(itemView);

        this.mListener = listener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v.getId(), getAdapterPosition());     // 将数据暴露给"子类"
            }
        });
    }

    /**
     * 数据绑定 --  抽象方法 -- 子类实现
     *
     * @param t
     * @param position
     */
    public abstract void bindHolder(T t, int position);

}
