package com.zh.metermanagement.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.utils.ExcelUtil;
import com.zh.metermanagement.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/8/6.
 */

public class GenerateReportsActivity extends BaseActivity implements View.OnClickListener {

    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;

    Button mBtnGenerateReports;

    List<MeterBean> mMeterBeenList;
    List<AssetNumberBean> mAssetNumberBeenList;

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
        showLoadingDialog("", "正在加载数据...");
        taskPresenter.readDbToBean(readObserver);
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
                taskPresenter.generateReports(generateReportsObserver, this, mMeterBeenList, mAssetNumberBeenList);
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
            mAssetNumberBeenList = assetNumberBeen;
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
            MyApplication.setMeterBeanList(meterBeen);
            mMeterBeenList = meterBeen;

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


    /**
     * 生成excel
     *
     * rxjava -- 主线程
     */
    Observer generateReportsObserver = new Observer<String>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull String s) {
            showToast(s);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            closeDialog();
        }

        @Override
        public void onComplete() {
            closeDialog();
        }
    };

}
