package com.zh.metermanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.adapter.FinishedAdapter;
import com.zh.metermanagement.adapter.UnFinishedAdapter;
import com.zh.metermanagement.adapter.AssetsNumberMismatchesAdapter;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.config.Constant;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

/**
 * Created by Administrator on 2017/8/3.
 */

public class StatisticsDetailsActivity extends BaseActivity implements View.OnClickListener {

    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;


    /******************************全屏图片***********************************/
    View mParent;
    View mBg;
    PhotoView mPhotoView;
    Info mInfo;

    AlphaAnimation in = new AlphaAnimation(0, 1);
    AlphaAnimation out = new AlphaAnimation(1, 0);

    /** 包裹预览图片的控件*/
    private LinearLayout mLayoutPv;
    /** 预览图片控件*/
    private PhotoView mPvCamaraPhoto;

    ListView mListView;
    ListView mAssetsNumberListView;

    @Override
    public int getContentLayout() {
        return R.layout.activity_statistics_details;
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

        mTvTitle.setText("详情");
    }


    @Override
    public void initView() {
        mListView = (ListView) findViewById(R.id.lv_info);
        mAssetsNumberListView = (ListView) findViewById(R.id.lv_assetsNumber_Mismatches);

        mParent = findViewById(R.id.parent);
        mBg = findViewById(R.id.bg);
        mPhotoView = (PhotoView) findViewById(R.id.img);

    }

    @Override
    public void initListener() {

        // 全屏图片
        in.setDuration(300);
        out.setDuration(300);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBg.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mPhotoView.enable();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBg.startAnimation(out);
                setTitleIsShow(View.VISIBLE);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(GONE);

                    }
                });
            }
        });

    }

    @Override
    public void initData() {

        ArrayList<MeterBean> beanList = new ArrayList<MeterBean>();
        List<AssetNumberBean> assetNumberBeanList = new ArrayList<AssetNumberBean>();

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        if(type.equals(Constant.all)){
            beanList = (ArrayList<MeterBean>) MyApplication.getMeterBeanList();
            mListView.setVisibility(View.VISIBLE);
            mAssetsNumberListView.setVisibility(View.GONE);

            UnFinishedAdapter unFinishedAdapter = new UnFinishedAdapter(this, beanList);
            mListView.setAdapter(unFinishedAdapter);

        }else if(type.equals(Constant.finish)){
            for(MeterBean bean : MyApplication.getMeterBeanList()){
                if(bean.isFinish()){
                    beanList.add(bean);
                }
            }
            mListView.setVisibility(View.VISIBLE);
            mAssetsNumberListView.setVisibility(View.GONE);

            FinishedAdapter finishedAdapter = new FinishedAdapter(this, beanList,
                    mParent,
                    mBg,
                    mPhotoView,
                    mInfo,
                    in,
                    out,
                    new FinishedAdapter.PhotoViewInfo(){

                        @Override
                        public void whenOnClickSetPhotoViewInfo(Info info) {
                            mInfo = info;
                            setTitleIsShow(View.GONE);
                        }
                    }){

            };
            mListView.setAdapter(finishedAdapter);

        }else if(type.equals(Constant.unfinished)){
            for(MeterBean bean : MyApplication.getMeterBeanList()){
                if(!bean.isFinish()){
                    beanList.add(bean);
                }
            }
            mListView.setVisibility(View.VISIBLE);
            mAssetsNumberListView.setVisibility(View.GONE);

            UnFinishedAdapter unFinishedAdapter = new UnFinishedAdapter(this, beanList);
            mListView.setAdapter(unFinishedAdapter);

        }else if(type.equals(Constant.assetsNumber_Mismatches)){

            assetNumberBeanList = MyApplication.getAssetNumberBeanList();
            mListView.setVisibility(View.GONE);
            mAssetsNumberListView.setVisibility(View.VISIBLE);
        }

        mTvTitle.setText(type);




        AssetsNumberMismatchesAdapter assetsNumberMismatchesAdapter =
                new AssetsNumberMismatchesAdapter(StatisticsDetailsActivity.this, assetNumberBeanList);
        mAssetsNumberListView.setAdapter(assetsNumberMismatchesAdapter);


    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_back_left:

                finish();
                break;

            case R.id.btn_menu_right:
                break;


            case R.id.img:                                          // 点击"放大后的预览图片的控件"，缩小、隐藏那个预览布局
                mBg.startAnimation(out);
                setTitleIsShow(View.VISIBLE);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(View.GONE);

                    }
                });
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

            AssetsNumberMismatchesAdapter assetsNumberMismatchesAdapter =
                    new AssetsNumberMismatchesAdapter(StatisticsDetailsActivity.this, assetNumberBeen);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"

            if(mParent.getVisibility() == View.VISIBLE && mBg.getVisibility() == View.VISIBLE){   // 缩小、隐藏那个预览布局
                mBg.startAnimation(out);
                setTitleIsShow(View.VISIBLE);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(View.GONE);

                    }
                });
                return true;
            }

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
