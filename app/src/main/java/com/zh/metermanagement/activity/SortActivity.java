package com.zh.metermanagement.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.view.linkagerecyclerview.CheckListener;
import com.zh.metermanagement.view.linkagerecyclerview.ItemHeaderDecoration;
import com.zh.metermanagement.view.linkagerecyclerview.RvListener;
import com.zh.metermanagement.view.linkagerecyclerview.ShenBean;
import com.zh.metermanagement.view.linkagerecyclerview.SortAdapter;
import com.zh.metermanagement.view.linkagerecyclerview.SortDetailFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class SortActivity extends BaseActivity implements View.OnClickListener {

    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;

    private RecyclerView rvSort;
    private LinearLayoutManager mLinearLayoutManager;
    private SortAdapter mSortAdapter;

    private SortDetailFragment mSortDetailFragment;
    //private SortBean mSortBean;

    private boolean isMoved;
    private int targetPosition;//点击左边某一个具体的item的位置

    ArrayList<ShenBean> mShenBeanArrayList;


    @Override
    public int getContentLayout() {
        return R.layout.activity_sort;
    }

    @Override
    public void initTitleListener(TextView tvTitle, Button btnBack, Button btnMenu) {
        mTvTitle = tvTitle;
        mBtnBack = btnBack;
        mBtnMenu = btnMenu;

        mBtnBack.setOnClickListener(this);
        mBtnMenu.setOnClickListener(this);
    }

    @Override
    public void initTitleData(TextView tvTitle, Button btnBack, Button btnMenu) {
        mTvTitle.setVisibility(View.VISIBLE);
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnMenu.setVisibility(View.GONE);

        mTvTitle.setText("选择台区");
    }

    @Override
    public void initView() {
        rvSort = (RecyclerView) findViewById(R.id.rv_sort);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        rvSort.setLayoutManager(mLinearLayoutManager);
        // 控件的方向 -- 纵向 -- 这是分割线
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvSort.addItemDecoration(decoration);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

        showLoadingDialog("", "正在加载数据...");
        taskPresenter1.getMeteringSection(meteringSectionObserver);
//        List<String> list = new ArrayList<>();
//        //初始化左侧列表数据
//        for (int i = 0; i < 20; i++) {
//            list.add("供电所" + i);
//        }
//
//        mShenBeanArrayList = new ArrayList<>();
//        for(int i=0; i<20; i++) {
//            ShenBean shenBean = new ShenBean();
//            shenBean.setPowerSupplyBureau("供电所" + i);
//
//            ArrayList<ShenBean.SupplyBureau> supplyBureaus = new ArrayList<>();
//            for(int j=0; j<5; j++){
//                ShenBean.SupplyBureau supplyBureau = new ShenBean.SupplyBureau();
//                supplyBureau.setTheMeteringSection("抄表区段" + j);
//                supplyBureau.setCourts("台区" + j);
//
//                supplyBureaus.add(supplyBureau);
//            }
//            shenBean.setSupplyBureaus(supplyBureaus);
//
//            mShenBeanArrayList.add(shenBean);
//        }



    }

    /**
     * 创建右边的Fragment
     */
    public void createFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mSortDetailFragment = new SortDetailFragment();
        Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList("right", mSortBean.getCategoryOneArray());
        bundle.putParcelableArrayList("right", mShenBeanArrayList);

        mSortDetailFragment.setArguments(bundle);               // 将数据传给 SortDetailFragment
        mSortDetailFragment.setListener(new CheckListener() {
            @Override
            public void check(int position, boolean isScroll) {
                setChecked(position, isScroll);
            }
        });
        fragmentTransaction.add(R.id.lin_fragment, mSortDetailFragment);
        fragmentTransaction.commit();
    }


    /**
     * 根据 SortDetailFragment 来调整左边的 RecyclerView
     * @param position
     * @param isLeft
     */
    private void setChecked(int position, boolean isLeft) {
        Log.d("p-------->", String.valueOf(position));
        if (isLeft) {
            mSortAdapter.setCheckedPosition(position);
            //此处的位置需要根据每个分类的集合来进行计算
            int count = 0;
            for (int i = 0; i < position; i++) {
                //count += mSortBean.getCategoryOneArray().get(i).getCategoryTwoArray().size();
                count += mShenBeanArrayList.get(i).getSupplyBureaus().size();
            }
            count += position;
            mSortDetailFragment.setData(count);
        } else {
            if (isMoved) {
                isMoved = false;
            } else
                mSortAdapter.setCheckedPosition(position);
            ItemHeaderDecoration.setCurrentTag(String.valueOf(targetPosition));

        }
        moveToCenter(position);

    }

    //将当前选中的item居中
    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = rvSort.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        if (childAt != null) {
            int y = (childAt.getTop() - rvSort.getHeight() / 2);
            rvSort.smoothScrollBy(0, y);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back_left:

                finish();
                break;

            case R.id.btn_menu_right:
                break;
        }
    }


    /**
     * 将数据从数据库读取到内存中
     * rxjava -- 主线程
     */
    Observer meteringSectionObserver = new Observer<ArrayList<ShenBean>>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull ArrayList<ShenBean> shenBeen) {
            mShenBeanArrayList = shenBeen;
            LogUtils.i("shenBeen.size()" + shenBeen.size());
        }


        @Override
        public void onError(@NonNull Throwable e) {
            closeDialog();
            LogUtils.i("加载失败：" + e.getMessage());
            showToast("加载数据失败");
        }

        @Override
        public void onComplete() {
            closeDialog();

            List<String> list = new ArrayList<>();
            for(ShenBean shenBean : mShenBeanArrayList){
                list.add(shenBean.getPowerSupplyBureau());
            }

            if(list.size() != 0){
                mSortAdapter = new SortAdapter(getContext(), list, new RvListener() {
                    @Override
                    public void onItemClick(int id, int position) {
                        if (mSortDetailFragment != null) {
                            isMoved = true;
                            targetPosition = position;
                            setChecked(position, true);
                        }
                    }
                });
                rvSort.setAdapter(mSortAdapter);
                createFragment();
            }



            //showToast("加载数据成功");
        }
    };
}
