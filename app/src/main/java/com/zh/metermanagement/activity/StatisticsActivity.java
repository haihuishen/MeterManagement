package com.zh.metermanagement.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.utils.LogUtils;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/8/3.
 */

public class StatisticsActivity extends BaseActivity implements View.OnClickListener {

    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;

    /** 台区总数 -- 文本 */
    private TextView mTvAreaCount;
    /** 台区总数 -- 查看详情 -- 文本 */
    private TextView mTvArea;
    /** 已换表 -- 文本 */
    private TextView mTvFinishCount;
    /** 已换表 -- 查看详情 -- 文本 */
    private TextView mTvFinish;
    /** 未换表 -- 文本 */
    private TextView mTvUnfinishedCount;
    /** 未换表 -- 查看详情 -- 文本 */
    private TextView mTvUnfinished;
    /** 资产编码无匹配 -- 文本 */
    private TextView mTvAssetsNumberMismatchesCount;
    /** 资产编码无匹配 -- 查看详情 -- 文本 */
    private TextView mTvAssetsNumberMismatches;
    /** 旧表地址\n资产编码\n无匹配 -- 文本 */
    private TextView mTvOldAddrAndassetsNumberMismatchesCount;
    /** 旧表地址\n资产编码\n无匹配 -- 查看详情 -- 文本 */
    private TextView mTvOldAddrAndassetsNumberMismatches;
    /** 新表地址\n资产编码\n无匹配 -- 文本 */
    private TextView mTvNewAddrAndassetsNumberMismatchesCount;
    /** 新表地址\n资产编码\n无匹配 -- 查看详情 -- 文本 */
    private TextView mTvNewAddrAndassetsNumberMismatches;



    @Override
    public int getContentLayout() {
        return R.layout.activity_statistics;
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

        mTvTitle.setText("查询");
    }

    @Override
    public void initView() {

        mTvAreaCount = (TextView) findViewById(R.id.tv_areaCount);
        mTvArea = (TextView) findViewById(R.id.tv_area);
        mTvFinishCount = (TextView) findViewById(R.id.tv_finishCount);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        mTvUnfinishedCount = (TextView) findViewById(R.id.tv_unfinishedCount);
        mTvUnfinished = (TextView) findViewById(R.id.tv_unfinished);
        mTvAssetsNumberMismatchesCount = (TextView) findViewById(R.id.tv_assetsNumber_MismatchesCount);
        mTvAssetsNumberMismatches = (TextView) findViewById(R.id.tv_assetsNumber_Mismatches);
        mTvOldAddrAndassetsNumberMismatchesCount = (TextView) findViewById(R.id.tv_old_addrAndassetsNumber_MismatchesCount);
        mTvOldAddrAndassetsNumberMismatches = (TextView) findViewById(R.id.tv_old_addrAndassetsNumber_Mismatches);
        mTvNewAddrAndassetsNumberMismatchesCount = (TextView) findViewById(R.id.tv_new_addrAndassetsNumber_MismatchesCount);
        mTvNewAddrAndassetsNumberMismatches = (TextView) findViewById(R.id.tv_new_addrAndassetsNumber_Mismatches);
    }

    @Override
    public void initListener() {
        mTvArea.setOnClickListener(this);
        mTvFinish.setOnClickListener(this);
        mTvUnfinished.setOnClickListener(this);
        mTvAssetsNumberMismatches.setOnClickListener(this);
        mTvOldAddrAndassetsNumberMismatches.setOnClickListener(this);
        mTvNewAddrAndassetsNumberMismatches.setOnClickListener(this);
    }

    @Override
    public void initData() {


        showLoadingDialog("","正在加载数据...");
        // 从数据库中加载数据
        taskPresenter.readDbToBean(readObserver);


    }



    @Override
    public void onClick(View v) {

        Intent intent;
            switch (v.getId()) {
                case R.id.btn_back_left:

                    finish();
                    break;

                case R.id.btn_menu_right:
                    break;

                case R.id.tv_area:              // 台区总数
                    intent = new Intent(StatisticsActivity.this, StatisticsDetailsActivity.class);
                    intent.putExtra("type", Constant.all);
                    startActivity(intent);
                    break;

                case R.id.tv_finish:            // 已换表
                    intent = new Intent(StatisticsActivity.this, StatisticsDetailsActivity.class);
                    intent.putExtra("type", Constant.finish);
                    startActivity(intent);
                    break;

                case R.id.tv_unfinished:        // 未换表
                    intent = new Intent(StatisticsActivity.this, StatisticsDetailsActivity.class);
                    intent.putExtra("type", Constant.unfinished);
                    startActivity(intent);
                    break;

                case R.id.tv_assetsNumber_Mismatches:
                    intent = new Intent(StatisticsActivity.this, StatisticsDetailsActivity.class);
                    intent.putExtra("type", Constant.assetsNumber_Mismatches);
                    startActivity(intent);
                    break;
            }

    }



    /**
     * rxjava -- 主线程
     */
    Observer statisticsObserver = new Observer<List<AssetNumberBean>>() {

        @Override
        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull List<AssetNumberBean> assetNumberBeen) {
            MyApplication.setAssetNumberBeanList(assetNumberBeen);

            if(assetNumberBeen.size() == 0)
                mTvAssetsNumberMismatches.setEnabled(false);


            mTvAssetsNumberMismatchesCount.setText(assetNumberBeen.size() + "户");
        }

        @Override
        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
            closeDialog();
        }

        @Override
        public void onComplete() {
            closeDialog();
        }
    };


    /**
     * 将数据从数据库读取到内存中
     * rxjava -- 主线程
     */
    Observer readObserver = new Observer<List<MeterBean>>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull List<MeterBean> meterBeen) {


            int allCount = 0;
            int finishCount = 0;
            int unFinishedCount = 0;

            int oldAddrAndassetsNumberMismatchesCount = 0;
            int newAddrAndassetsNumberMismatchesCount = 0;

            LogUtils.i("meterBeen.size()" + meterBeen.size());

            MyApplication.setMeterBeanList(meterBeen);

            for(MeterBean bean : meterBeen){
                allCount++;
                if(bean.isFinish()) {
                    finishCount++;

                    if(!bean.isOldAddrAndAsset())
                        oldAddrAndassetsNumberMismatchesCount++;

                    if(!bean.isNewAddrAndAsset())
                        newAddrAndassetsNumberMismatchesCount++;

                } else
                    unFinishedCount++;

            }
            mTvAreaCount.setText(allCount + "户");
            mTvFinishCount.setText(finishCount + "户");
            mTvUnfinishedCount.setText(unFinishedCount + "户");

            mTvOldAddrAndassetsNumberMismatchesCount.setText(oldAddrAndassetsNumberMismatchesCount + "户");
            mTvNewAddrAndassetsNumberMismatchesCount.setText(newAddrAndassetsNumberMismatchesCount + "户");

            if(allCount == 0) {
                mTvArea.setEnabled(false);
            }
            if(finishCount == 0){
                mTvFinish.setEnabled(false);
            }
            if(unFinishedCount == 0){
                mTvUnfinished.setEnabled(false);
            }
            if(oldAddrAndassetsNumberMismatchesCount == 0){
                mTvOldAddrAndassetsNumberMismatches.setEnabled(false);
            }
            if(newAddrAndassetsNumberMismatchesCount == 0){
                mTvNewAddrAndassetsNumberMismatches.setEnabled(false);
            }


            taskPresenter.statisticsData(statisticsObserver);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            closeDialog();
        }

        @Override
        public void onComplete() {
        }
    };

}
