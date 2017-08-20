package com.zh.metermanagement.view.linkagerecyclerview;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;


import com.zh.metermanagement.R;

import java.util.List;

public class SortAdapter extends RvAdapter<String> {

    /** 当前被点中的"item的id" */
    private int checkedPosition;

    /**
     * 设置被点中的item
     * @param checkedPosition   被点中的
     */
    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
    }

    /**
     * 构造函数
     *
     * @param context
     * @param list          数据
     * @param listener      接口 -- RecyclerView的item点击事件(将点击事件暴露给子类)
     */
    public SortAdapter(Context context, List<String> list, RvListener listener) {
        super(context, list, listener);
    }

    @Override
    protected int getLayoutId(int viewType) {       // 获得填充给RecyclerView的做item的布局的id
        return R.layout.item_sort_list;
    }

    /**
     *
     * @param view          已经填充完成的item
     * @param viewType      item布局的id
     * @return
     */
    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new SortHolder(view, viewType, listener);
    }


    /**
     * ecyclerView -- 循环使用的"控件组"类
     */
    private class SortHolder extends RvHolder<String> {

        private TextView tvName;
        private View mView;

        /**
         *
         * @param itemView      已经填充完成的item
         * @param type          item布局的id
         * @param listener      接口 -- RecyclerView的item点击事件(将点击事件暴露给子类)
         */
        SortHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            this.mView = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tv_sort);
        }

        @Override
        public void bindHolder(String string, int position) {
            tvName.setText(string);

            // 设置被点击的item -- 对应的里面的控件的颜色 ！
            if (position == checkedPosition) {                              // 被点击的颜色
                mView.setBackgroundColor(Color.parseColor("#f3f3f3"));
                tvName.setTextColor(Color.parseColor("#0068cf"));

            } else {
                mView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tvName.setTextColor(Color.parseColor("#1e1d1d"));
            }
        }

    }
}
