package com.zh.metermanagement.view.linkagerecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class RvAdapter<T> extends RecyclerView.Adapter<RvHolder> {

    /** 数据 */
    protected List<T> list;
    protected Context mContext;
    /** 接口 -- RecyclerView的item点击事件(将点击事件暴露给子类) */
    protected RvListener listener;
    /** 填充器 */
    protected LayoutInflater mInflater;
    private RecyclerView mRecyclerView;

    public RvAdapter(Context context, List<T> list, RvListener listener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.listener = listener;
    }

    @Override
    public RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(getLayoutId(viewType), parent, false);

        return getHolder(view, viewType);
    }

    /**
     * 获得填充给RecyclerView的做item的布局的id
     *
     * @param viewType RecyclerView的"item布局的id"
     * @return
     */
    protected abstract int getLayoutId(int viewType);


    // 数据绑定
    @Override
    public void onBindViewHolder(RvHolder holder, int position) {
        holder.bindHolder(list.get(position), position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    /** 获取布局的类型 -- 这里只有一种 */
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * 抽象方法 -- 子类实现
     * 绑定控件 -- 初始化控件
     *
     * @param view          已经填充完成的item
     * @param viewType      item布局的id
     * @return
     */
    protected abstract RvHolder getHolder(View view, int viewType);

}
