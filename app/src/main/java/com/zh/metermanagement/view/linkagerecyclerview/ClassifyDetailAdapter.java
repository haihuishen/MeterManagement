package com.zh.metermanagement.view.linkagerecyclerview;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.zh.metermanagement.R;

import java.util.List;


/**
 * 右边数据 -- 适配器
 */
public class ClassifyDetailAdapter extends RvAdapter<RightBean> {


    public ClassifyDetailAdapter(Context context, List<RightBean> list, RvListener listener) {
        super(context, list, listener);
    }


    // 根据布局类型 -- 获取"布局id"
    @Override
    protected int getLayoutId(int viewType) {
        return viewType == 0 ? R.layout.item_title : R.layout.item_classify_detail;
    }

    // 获取布局中的"类型"
    @Override
    public int getItemViewType(int position) {
        return list.get(position).isTitle() ? 0 : 1;
    }


    // 控件初始化
    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new ClassifyHolder(view, viewType, listener);
    }


    public class ClassifyHolder extends RvHolder<RightBean> {
        TextView tvCity;

        ImageView avatar;
        TextView tvTitle;

        public ClassifyHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            switch (type) {
                case 0:                     // "标题布局"
                    tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                    break;

                case 1:                     // "内容布局"
                    tvCity = (TextView) itemView.findViewById(R.id.tvCity);
                    avatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
                    break;
            }

        }

        @Override
        public void bindHolder(RightBean sortBean, int position) {

            int itemViewType = ClassifyDetailAdapter.this.getItemViewType(position);    //
            switch (itemViewType) {
                case 0:
                    tvTitle.setText(sortBean.getName());
                    break;

                case 1:
                    tvCity.setText(sortBean.getName());
//                    avatar.setImageResource(R.mipmap.ic_launcher);
                    break;
            }

        }
    }
}
