package com.zh.metermanagement.view.linkagerecyclerview;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.SelectorActivity2;
import com.zh.metermanagement.activity.SortActivity;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.AreaBean;
import com.zh.metermanagement.bean.NoWorkOrderPathBean;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.utils.FilesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SortDetailFragment extends BaseFragment<SortDetailPresenter, String> implements CheckListener {

    private RecyclerView mRv;
    private ClassifyDetailAdapter mAdapter;

    /** 布局管理 */
    private GridLayoutManager mManager;

    /** 右边布局中的数据 */
    private List<RightBean> mDatas = new ArrayList<>();

    /** RecyclerView 的 "分割线" */
    private ItemHeaderDecoration mDecoration;

    private boolean move = false;
    private int mIndex = 0;
    private CheckListener checkListener;

    @Override
    protected void getData() {          // BaseFragment 中的 抽象方法

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sort_detail;
    }

    //初始化布局
    @Override
    protected void initCustomView(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv);

    }

    @Override
    protected void initListener() {
        mRv.addOnScrollListener(new RecyclerViewListener());            // 添加滑动的监听
    }

    // 获取任务的实例
    @Override
    protected SortDetailPresenter initPresenter() {

        showRightPage(1);                                   // 设置"页面"当前状态
        mManager = new GridLayoutManager(mContext, 3);      // 布局设置 最大是 3 列
        //mManager = new GridLayoutManager(mContext, 2);      // 布局设置 最大是 3 列

        // setSpanSizeLookup可以让你根据position来设置 span size
        //通过isTitle的标志来判断是否是title
        mManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mDatas.get(position).isTitle() ? 3 : 1;      // 如果是 "标题" -- 设置为"1列"； 否则为"3列"
                //return mDatas.get(position).isTitle() ? 2 : 1;      // 如果是 "标题" -- 设置为"1列"； 否则为"3列"
            }
        });

        mRv.setLayoutManager(mManager);

        mAdapter = new ClassifyDetailAdapter(mContext, mDatas, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {

                String content = "";
                switch (id) {
                    case R.id.root:                         // 点击的是 -- 标题
                        content = "title";
                        break;

                    case R.id.content:                      // 点击的是 -- 内容
                        content = "content";

                        AreaBean areaBean = new AreaBean();
                        areaBean.setPowerSupplyBureau(mDatas.get(position).getTitleName());
                        areaBean.setCourts(mDatas.get(position).getCourts());
                        areaBean.setTheMeteringSection(mDatas.get(position).getMeteringSection());
                        MyApplication.setAreaBean(areaBean);

                        String path = Constant.NoWorkOrder_PATH
                                + mDatas.get(position).getTitleName()
                                + mDatas.get(position).getCourts()
                                + "(" + mDatas.get(position).getMeteringSection() + ")"+ File.separator;

                        FilesUtils.createFile(getContext(), path);
                        NoWorkOrderPathBean bean = new NoWorkOrderPathBean();
                        bean.setAreaPath(path);
                        bean.setAreaReplaceMeterPath(path + NoWorkOrderPathBean.ReplaceMeter);
                        bean.setAreaNewCollectorPath(path + NoWorkOrderPathBean.NewCollector);
                        bean.setAreaExportPath(path + NoWorkOrderPathBean.ExportExcel);
                        bean.setReplaceMeterPhotoPath(path + NoWorkOrderPathBean.ReplaceMeter + NoWorkOrderPathBean.Photo);
                        bean.setNewCollectorPhotoPath(path + NoWorkOrderPathBean.NewCollector + NoWorkOrderPathBean.Photo);

                        bean.onCreate(getContext());
                        MyApplication.setNoWorkOrderPath(bean);

                        MyApplication.setCurrentMeteringSection(mDatas.get(position).getMeteringSection());
                        //Toast.makeText(getContext(), MyApplication.getCurrentMeteringSection(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), SelectorActivity2.class);
                        intent.putExtra("台区", mDatas.get(position).getCourts());
                        intent.putExtra("抄表区段", mDatas.get(position).getMeteringSection());
                        startActivity(intent);

                        break;

                }



//                // 类似于吐司
//                Snackbar snackbar = Snackbar.make(mRv, "当前点击的是" + content + ":" + mDatas.get(position).getName(), Snackbar.LENGTH_SHORT);
//                View mView = snackbar.getView();
//                mView.setBackgroundColor(Color.BLUE);
//                TextView text = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
//                text.setTextColor(Color.WHITE);
//                text.setTextSize(25);
//                snackbar.show();
            }
        });

        mRv.setAdapter(mAdapter);
        mDecoration = new ItemHeaderDecoration(mContext, mDatas);
        mRv.addItemDecoration(mDecoration);
        mDecoration.setCheckListener(checkListener);

        initData();
        return new SortDetailPresenter();
    }


    private void initData() {
//        ArrayList<SortBean.CategoryOneArrayBean> rightList = getArguments().getParcelableArrayList("right");
//
//        for (int i = 0; i < rightList.size(); i++) {
//            RightBean head = new RightBean(rightList.get(i).getName());
//
//            //头部设置为true
//            head.setTitle(true);
//            head.setTitleName(rightList.get(i).getName());
//            head.setTag(String.valueOf(i));
//            mDatas.add(head);
//
//            List<SortBean.CategoryOneArrayBean.CategoryTwoArrayBean> categoryTwoArray = rightList.get(i).getCategoryTwoArray();
//            for (int j = 0; j < categoryTwoArray.size(); j++) {
//                RightBean body = new RightBean(categoryTwoArray.get(j).getName());
//                body.setTag(String.valueOf(i));
//                String name = rightList.get(i).getName();
//                body.setTitleName(name);
//                mDatas.add(body);
//            }
//        }

        ArrayList<ShenBean> rightList = getArguments().getParcelableArrayList("right");

        for (int i = 0; i < rightList.size(); i++) {
            RightBean head = new RightBean(rightList.get(i).getPowerSupplyBureau());

            //头部设置为true
            head.setTitle(true);
            head.setTitleName(rightList.get(i).getPowerSupplyBureau());
            head.setTag(String.valueOf(i));
            mDatas.add(head);

            ArrayList<ShenBean.SupplyBureau> supplyBureaus = rightList.get(i).getSupplyBureaus();
            for (int j = 0; j < supplyBureaus.size(); j++) {
                RightBean body = new RightBean(supplyBureaus.get(j).getCourts() +
                        "\n" + supplyBureaus.get(j).getTheMeteringSection());
                body.setTag(String.valueOf(i));
                String name = rightList.get(i).getPowerSupplyBureau();
                body.setTitleName(name);
                body.setMeteringSection(supplyBureaus.get(j).getTheMeteringSection());
                body.setCourts(supplyBureaus.get(j).getCourts());
                mDatas.add(body);
            }
        }

        mAdapter.notifyDataSetChanged();
        mDecoration.setData(mDatas);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void refreshView(int code, String data) {

    }

    public void setData(int n) {
        mIndex = n;
        mRv.stopScroll();
        smoothMoveToPosition(n);
    }

    public void setListener(CheckListener listener) {
        this.checkListener = listener;
    }


    private void smoothMoveToPosition(int n) {
        int firstItem = mManager.findFirstVisibleItemPosition();
        int lastItem = mManager.findLastVisibleItemPosition();

        Log.d("first--->", String.valueOf(firstItem));
        Log.d("last--->", String.valueOf(lastItem));

        if (n <= firstItem) {
            mRv.scrollToPosition(n);
        } else if (n <= lastItem) {
            Log.d("pos---->", String.valueOf(n) + "VS" + firstItem);
            int top = mRv.getChildAt(n - firstItem).getTop();
            Log.d("top---->", String.valueOf(top));
            mRv.scrollBy(0, top);
        } else {
            mRv.scrollToPosition(n);
            move = true;
        }
    }


    @Override
    public void check(int position, boolean isScroll) {
        checkListener.check(position, isScroll);

    }


    /**
     * RecyclerView滑动监听
     */
    private class RecyclerViewListener extends RecyclerView.OnScrollListener {

        // 正在滑动
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {       // 停止了滚动
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                Log.d("n---->", String.valueOf(n));
                if (0 <= n && n < mRv.getChildCount()) {
                    int top = mRv.getChildAt(n).getTop();
                    Log.d("top--->", String.valueOf(top));
                    mRv.smoothScrollBy(0, top);             // 动画滚动通过给定的像素沿轴。
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (move) {
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                if (0 <= n && n < mRv.getChildCount()) {
                    int top = mRv.getChildAt(n).getTop();
                    mRv.scrollBy(0, top);                   // 动画滚动通过给定的像素沿轴。
                }
            }
        }
    }


}
