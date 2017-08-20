package com.zh.metermanagement.view.linkagerecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.zh.metermanagement.R;

import java.util.List;


/**
 * RecyclerView 的 "分割线"
 */
public class ItemHeaderDecoration extends RecyclerView.ItemDecoration {

    /** 头部 -- "标题布局" 高度 */
    private int mTitleHeight;
    private List<RightBean> mDatas;
    private LayoutInflater mInflater;
    private CheckListener mCheckListener;

    /** 标记当前左侧选中的position，因为有可能选中的item，右侧不能置顶，所以强制替换掉当前的tag */
    public static String currentTag = "-1";

    /**
     * 左边RecyclerView的 滑动监听
     */
    void setCheckListener(CheckListener checkListener) {
        mCheckListener = checkListener;
    }


    ItemHeaderDecoration(Context context, List<RightBean> datas) {

        this.mDatas = datas;

        Paint paint = new Paint();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        int titleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        paint.setTextSize(titleFontSize);
        paint.setAntiAlias(true);              // 抗锯齿

        mInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据 -- 返回"ItemHeaderDecoration"
     * @param mDatas
     * @return
     */
    public ItemHeaderDecoration setData(List<RightBean> mDatas) {
        this.mDatas = mDatas;
        return this;
    }

    /**
     * 设置当前左侧选中的position
     * @param currentTag
     */
    public static void setCurrentTag(String currentTag) {
        ItemHeaderDecoration.currentTag = currentTag;
    }


    // onDraw是在itemview绘制之前，onDrawOver是在itemview绘制之后。
    @Override
    public void onDrawOver(Canvas canvas, final RecyclerView parent, RecyclerView.State state) {

        // 获取RecyclerView 中的 GridLayoutManager
        GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
        // 可以看作 -- 每个"item"中的"列的个数"的 集合
        GridLayoutManager.SpanSizeLookup spanSizeLookup = manager.getSpanSizeLookup();

        // 获取 -- 当前界面 第一个显示的item 的 id
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();

        int spanSize = spanSizeLookup.getSpanSize(pos);                     // 获取"item"的"列"

        if (spanSize == 1) {                                                // 如果只有 -- 1个列  -- 就是"标题"
            //body
            Log.d("pos--->", String.valueOf(pos));
            String tag = mDatas.get(pos).getTag();
            // 出现一个奇怪的bug，有时候child为空，
            // 所以将 child = parent.getChildAt(i)。-》 parent.findViewHolderForLayoutPosition(pos).itemView
            View child = parent.findViewHolderForLayoutPosition(pos).itemView;
            boolean flag = false;
            String mSuspensionTag = "";

            //判断最后一行的个数
            if (!TextUtils.equals(mDatas.get(pos).getTag(), mDatas.get(pos + 1).getTag())) {
                //最后一行只有一个item
                tag = mDatas.get(pos).getTag();
                mSuspensionTag = mDatas.get(pos + 1).getTag();

                if (child.getHeight() + child.getTop() < mTitleHeight) {
                    canvas.save();  // canvas.save();和canvas.restore();是两个相互匹配出现的，作用是用来保存画布的状态和取出保存的状态的。
                    flag = true;
                    // 把当前画布的原点移到(x,y),后面的操作都以(x,y)作为参照点，默认原点为(0,0)  -- 第一次为默认
                    canvas.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }

            } else if (!TextUtils.equals(mDatas.get(pos).getTag(), mDatas.get(pos + 2).getTag())) {
                //最后一行有两个item
                tag = mDatas.get(pos).getTag();
                mSuspensionTag = mDatas.get(pos + 2).getTag();
                if (child.getHeight() + child.getTop() < mTitleHeight) {
                    canvas.save();
                    flag = true;
                    canvas.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }

            } else if (!TextUtils.equals(mDatas.get(pos).getTag(), mDatas.get(pos + 3).getTag())) {
                //最后一行有3个item
                tag = mDatas.get(pos).getTag();
                mSuspensionTag = mDatas.get(pos + 3).getTag();
                if (child.getHeight() + child.getTop() < mTitleHeight) {
                    canvas.save();
                    flag = true;
                    canvas.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }
            }

            drawHeader(parent, pos, canvas);

            if (flag)
                canvas.restore();       // 把当前画布返回（调整）到上一个save()状态之前

            Log.d("check----->", tag + "VS" + currentTag);

            if (!TextUtils.equals(tag, currentTag)) {
                currentTag = tag;
                Integer integer = Integer.valueOf(currentTag);
                mCheckListener.check(integer, false);
            }
        }
    }

    /**
     * 画头布局
     *
     * @param parent
     * @param pos
     */
    private void drawHeader(RecyclerView parent, int pos, Canvas canvas) {

        View topTitleView = mInflater.inflate(R.layout.item_title, parent, false);  // 填充头部"标题布局"
        TextView tvTitle = (TextView) topTitleView.findViewById(R.id.tv_title);     // 设置头部"标题布局" 的 文本
        tvTitle.setText(mDatas.get(pos).getTitleName());

        //绘制title开始
        int toDrawWidthSpec;        //用于测量的widthMeasureSpec
        int toDrawHeightSpec;       //用于测量的heightMeasureSpec
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) topTitleView.getLayoutParams();

        if (lp == null) {
            lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//这里是根据复杂布局layout的width height，new一个Lp
            topTitleView.setLayoutParams(lp);
        }
        topTitleView.setLayoutParams(lp);


        //MeasureSpec.EXACTLY是精确尺寸，当我们将控件的layout_width或layout_height指定为具体数值时如andorid:layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
        //MeasureSpec.AT_MOST是最大尺寸，当控件的layout_width或layout_height指定为WRAP_CONTENT时，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
        //MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，通过measure方法传入的模式。

        if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            //如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(
                    parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);

        } else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(
                    parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.AT_MOST);

        } else {
            //否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY);
        }

        //高度同理
        if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(
                    parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.EXACTLY);
        } else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(
                    parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.AT_MOST);
        } else {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(mTitleHeight, View.MeasureSpec.EXACTLY);
        }

        //依次调用 measure,layout,draw方法，将复杂头部显示在屏幕上
        topTitleView.measure(toDrawWidthSpec, toDrawHeightSpec);
        topTitleView.layout(
                parent.getPaddingLeft(),
                parent.getPaddingTop(),
                parent.getPaddingLeft() + topTitleView.getMeasuredWidth(),
                parent.getPaddingTop() + topTitleView.getMeasuredHeight());

        topTitleView.draw(canvas);//Canvas默认在视图顶部，无需平移，直接绘制
        //绘制title结束

    }
}
