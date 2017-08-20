package com.zh.metermanagement.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.AreaBean;
import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.bean.NoWorkOrderPathBean;
import com.zh.metermanagement.utils.FilesUtils;
import com.zh.metermanagement.utils.LogUtils;

import java.io.File;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/8/6.
 */

public class GenerateReportsActivity1 extends BaseActivity implements View.OnClickListener {

    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;

    Button mBtnGenerateReports;

    List<MeterBean1> mMeterBeenList;
    List<AssetNumberBean> mAssetNumberBeenList;

    /** .../../XX供电所XX台区(抄表区段)户表改造台账.xls */
    String excelPath1 = "";
    /** .../../xx供电所xx台区(抄表区段)集中器户表档案(生成-计量自动化系统).xls */
    String excelPath2 = "";
    /** .../../xx供电所xx台区(抄表区段)户表档案(生成-营销系统).xls */
    String excelPath3 = "";

    @Override
    public int getContentLayout() {
        return R.layout.activity_generate_reports;
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

        mTvTitle.setText("生成报表");
    }

    @Override
    public void initView() {
        mBtnGenerateReports = (Button) findViewById(R.id.btn_generateReports);
    }

    @Override
    public void initListener() {
        mBtnGenerateReports.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }



    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_back_left:

                finish();
                break;

            case R.id.btn_menu_right:
                break;

            case R.id.btn_generateReports:      // 生成报表

                showLoadingDialog("", "正在生成报表");
                taskPresenter1.readDbToBean(readObserver, MyApplication.getCurrentMeteringSection());

                break;
        }
    }



    /**
     * rxjava -- 主线程
     */
    Observer statisticsObserver = new Observer<List<AssetNumberBean>>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull List<AssetNumberBean> assetNumberBeen) {
            MyApplication.setAssetNumberBeanList(assetNumberBeen);
            mAssetNumberBeenList = assetNumberBeen;
        }

        @Override
        public void onError(@NonNull Throwable e) {
            closeDialog();
        }

        @Override
        public void onComplete() {

            NoWorkOrderPathBean orderPathbean = MyApplication.getNoWorkOrderPath();
            AreaBean areaBean = MyApplication.getAreaBean();
            /** .../../XX供电所XX台区（抄表区段）户表改造台账.xls */
            excelPath1 = orderPathbean.getAreaExportPath()
                    + areaBean.getPowerSupplyBureau()
                    + areaBean.getCourts()
                    + "(" + areaBean.getTheMeteringSection() + ")"
                    + "户表改造台账.xls";
            /** .../../xx供电所xx台区(抄表区段)集中器户表档案(生成-计量自动化系统).xls */
            excelPath2 = orderPathbean.getAreaExportPath()
                    + areaBean.getPowerSupplyBureau()
                    + areaBean.getCourts()
                    + "(" + areaBean.getTheMeteringSection() + ")"
                    + "集中器户表档案(生成-计量自动化系统).xls";
            /** .../../xx供电所xx台区(抄表区段)户表档案(生成-营销系统).xls */
            excelPath3 = orderPathbean.getAreaExportPath()
                    + areaBean.getPowerSupplyBureau()
                    + areaBean.getCourts()
                    + "(" + areaBean.getTheMeteringSection() + ")"
                    + "户表档案(生成-营销系统).xls";
            taskPresenter1.generateReports(generateReportsObserver, getContext(),
                    mMeterBeenList, mAssetNumberBeenList,
                    excelPath1, excelPath2, excelPath3);
            //closeDialog();
        }
    };


    /**
     * 将数据从数据库读取到内存中
     * rxjava -- 主线程
     */
    Observer readObserver = new Observer<List<MeterBean1>>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull List<MeterBean1> meterBeen) {
            MyApplication.setMeterBean1List(meterBeen);
            mMeterBeenList = meterBeen;


        }

        @Override
        public void onError(@NonNull Throwable e) {
            closeDialog();
        }

        @Override
        public void onComplete() {
            taskPresenter1.statisticsData(statisticsObserver);
        }
    };


    /**
     * 生成excel
     *
     * rxjava -- 主线程
     */
    Observer generateReportsObserver = new Observer<Boolean>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull Boolean b) {
            showToast(b?"生成文件成功" : "生成文件失败");
            FilesUtils.broadCreateFile(getContext(), excelPath1);
            FilesUtils.broadCreateFile(getContext(), excelPath2);
            FilesUtils.broadCreateFile(getContext(), excelPath3);
        }

        @Override
        public void onError(@NonNull Throwable e) {

            LogUtils.i("generateReportsObserver:e.getMessage()" + e.getMessage());
            closeDialog();
        }

        @Override
        public void onComplete() {
            closeDialog();
        }
    };

}
