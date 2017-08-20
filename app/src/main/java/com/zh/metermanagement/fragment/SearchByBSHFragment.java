package com.zh.metermanagement.fragment;



import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.shen.sweetdialog.SweetAlertDialog;
import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.ReplaceMeterActivity1;
import com.zh.metermanagement.adapter.FinishedAdapter;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.CollectorNumberBean;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.fragment.base.BaseFragment;
import com.zh.metermanagement.trasks.TaskPresenterImpl1;
import com.zh.metermanagement.utils.BeepManager;
import com.zh.metermanagement.utils.FilesUtils;
import com.zh.metermanagement.utils.ImageFactory;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.utils.StringUtils;
import com.zh.metermanagement.view.ClearEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * Created by Administrator on 2017/5/18.
 */

public class SearchByBSHFragment extends BaseFragment implements View.OnClickListener {


//et_userName et_userNumber et_oldAssetsNumber et_newAssetsNumber
    /** 用户名称 -- 编辑框 */
    ClearEditText mCEtUserName;
    /** 用户编码 -- 编辑框 */
    ClearEditText mCEtUserNumber;
    /** 旧资产编码 -- 编辑框 */
    ClearEditText mCEtOldAssetsNumber;
    /** 新资产编码 -- 编辑框 */
    ClearEditText mCEtNewAssetsNumber;

    Button mBtnOldAssetsNumber;
    Button mBtnNewAssetsNumber;

    /** 收起和展开--查询条件 */
    ToggleButton mTbUpAndDowm;

    LinearLayout LlayoutSearch;


    /** 查询 -- 按钮 */
    Button mBtnSearch;

    /** 查询出的信息 -- 文本 */
    TextView mTvInfo;

    TaskPresenterImpl1 taskPresenter1;

    /** 扫描类 */
    private ScanInterface scanDecode;
    /** 当前二维扫描的按钮 */
    private int mCurrentScanBtnId = 1;

    public BeepManager mBeepManager;


    //--------------------------------------------------
    FinishedAdapter mFinishedAdapter;
    ListView mListView;

    ArrayList<MeterBean1> mMeterBean1List = new ArrayList<>();

    public static SearchByBSHFragment newInstance(){
        SearchByBSHFragment f = new SearchByBSHFragment();
        return f;

    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_search_by_bsh;
    }

    @Override
    public void initView(View view) {

        mCEtUserName = (ClearEditText) view.findViewById(R.id.et_userName);
        mCEtUserNumber = (ClearEditText) view.findViewById(R.id.et_userNumber);
        mCEtOldAssetsNumber = (ClearEditText) view.findViewById(R.id.et_oldAssetsNumber);
        mCEtNewAssetsNumber = (ClearEditText) view.findViewById(R.id.et_newAssetsNumber);

        mBtnOldAssetsNumber = (Button) view.findViewById(R.id.btn_oldAssetsNumber);
        mBtnNewAssetsNumber = (Button) view.findViewById(R.id.btn_newAssetsNumber);

        mTbUpAndDowm = (ToggleButton) view.findViewById(R.id.tb_upAndDown);
        LlayoutSearch = (LinearLayout) view.findViewById(R.id.llayout_search);

        mBtnSearch = (Button) view.findViewById(R.id.btn_search);

        mTvInfo = (TextView) view.findViewById(R.id.tv_info);


        //--------------------------------------------------
        mLlayoutParent = view.findViewById(R.id.parent);
        mIvBg = view.findViewById(R.id.iv_bg);
        mPvBgImg = (PhotoView) view.findViewById(R.id.pv_bg);

        mListView = (ListView) view.findViewById(R.id.lv_info);
    }

    @Override
    public void initListener() {
        mBtnSearch.setOnClickListener(this);
        mBtnOldAssetsNumber.setOnClickListener(this);
        mBtnNewAssetsNumber.setOnClickListener(this);
        mPvBgImg.setOnClickListener(this);

        mTbUpAndDowm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                mTbUpAndDowm.setChecked(isChecked);
                //使用三目运算符来响应按钮变换的事件
                mTbUpAndDowm.setBackgroundResource(isChecked ? R.mipmap.dowm : R.mipmap.up);

                if(isChecked)
                    LlayoutSearch.setVisibility(View.VISIBLE);
                else
                    LlayoutSearch.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void initData() {


        mTbUpAndDowm.setChecked(true);

        mBeepManager = new BeepManager(getContext(),true,false);

        taskPresenter1 = new TaskPresenterImpl1(getContext());

        //---------------------------------扫描--------------------------------------
        scanDecode = new ScanDecode(getContext());
        scanDecode.initService("true");//初始化扫描服务

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {

                mBeepManager.playSuccessful();
                final String scanData = data.trim();
                if(mCurrentScanBtnId == R.id.btn_oldAssetsNumber){              // 旧表资产编号(二维扫描)
                    mCEtOldAssetsNumber.setText(scanData);

                }else if(mCurrentScanBtnId == R.id.btn_newAssetsNumber){    // 新表资产编号(二维扫描)
                    mCEtNewAssetsNumber.setText(scanData);

                }

            }
        });

        mFinishedAdapter = new FinishedAdapter(getContext(), mMeterBean1List, new ArrayList<CollectorNumberBean>(),
                new FinishedAdapter.FinishPhotoListener() {
                    @Override
                    public void onPreView(int index, String path, Info info) {
                        mInfo = info;

                        mBitmap = ImageFactory.getBitmap(path);
                        mPvBgImg.setImageBitmap(mBitmap);
                        mIvBg.startAnimation(in);             // 执行动画
                        mIvBg.setVisibility(View.VISIBLE);
                        mLlayoutParent.setVisibility(View.VISIBLE);
                        mPvBgImg.animaFrom(mInfo);

                        //setTitleIsShow(View.GONE);
                    }
                });
        mListView.setAdapter(mFinishedAdapter);

        //------------------------------------------------------------------

        // 预览图片的动画
        in = new AlphaAnimation(0, 1);
        out = new AlphaAnimation(1, 0);

        in.setDuration(300);
        out.setDuration(300);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mIvBg.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mBitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "no_preview_picture.png");

        mPvBgImg.setImageBitmap(mBitmap);
        mPvBgImg.enable();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scanDecode != null)
            scanDecode.stopScan();  // 停止
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scanDecode != null)
            scanDecode.onDestroy(); // 回复初始状态
    }



    /**
     * 查询
     */
    private void search() {

//        /** 用户编号 */
//        public final static String TABLE_METERINFO1_STR_userNumber = "userNumber";
//        /** 用户名称 */
//        public final static String TABLE_METERINFO1_STR_userName = "userName";
//        /** 旧表资产编号(导入的) */
//        public final static String TABLE_METERINFO1_STR_oldAssetNumbers = "oldAssetNumbers";
//        /** 新表资产编号(需扫描) */
//        public final static String TABLE_METERINFO1_STR_newAssetNumbersScan = "newAssetNumbersScan";

        HashMap<String, String> conditionMap = new HashMap<>();

        String userName = mCEtUserName.getText().toString().trim();
        String userNumber = mCEtUserNumber.getText().toString().trim();
        String oldAssetsNumber = mCEtOldAssetsNumber.getText().toString().trim();
        String newAssetsNumber = mCEtNewAssetsNumber.getText().toString().trim();

        boolean isEmpty = true;
        if(StringUtils.isNotEmpty(userName)){
            conditionMap.put("userName", userName);
            isEmpty = false;
        }
        if(StringUtils.isNotEmpty(userNumber)){
            conditionMap.put("userNumber", userNumber);
            isEmpty = false;
        }
        if(StringUtils.isNotEmpty(oldAssetsNumber)){
            conditionMap.put("oldAssetNumbers", oldAssetsNumber);
            isEmpty = false;
        }
        if(StringUtils.isNotEmpty(newAssetsNumber)){
            conditionMap.put("newAssetNumbersScan", newAssetsNumber);
            isEmpty = false;
        }

        if(isEmpty){
            SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("提示")
                    .setContentText("查询条件不能为空")
                    .setConfirmText("确认")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismiss();
                        }
                    });

            dialog.show();
        }else {
            taskPresenter1.searchMeterInfo(searchMeterInfoObserver,
                    MyApplication.getCurrentMeteringSection(),
                    conditionMap);
        }
    }


    /**
     * 查询表信息
     *
     * rxjava -- 主线程
     */
    Observer searchMeterInfoObserver = new Observer<List<MeterBean1>>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull List<MeterBean1> meterBeen) {
            if(meterBeen != null && meterBeen.size() > 0) {
                mTbUpAndDowm.setChecked(false);
//            for(MeterBean1 bean : meterBeen){
//                LogUtils.i("searchMeterInfoObserver :" + bean.toString());
//            }

                mMeterBean1List.clear();
                mMeterBean1List.addAll(meterBeen);
                mFinishedAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            LogUtils.i("searchMeterInfoObserver -- e.getMessage()" + e.getMessage());
        }

        @Override
        public void onComplete(){


        }
    };

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btn_search:
                search();
                break;

            case R.id.btn_oldAssetsNumber:
                mCurrentScanBtnId = R.id.btn_oldAssetsNumber;

                Timer timer1 = new Timer();
                timer1.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mCurrentScanBtnId == R.id.btn_oldAssetsNumber)
                            scanDecode.stopScan();
                    };
                }, 8000);
                scanDecode.stopScan();
                scanDecode.starScan();                              //启动扫描
                break;

            case R.id.btn_newAssetsNumber:
                mCurrentScanBtnId = R.id.btn_newAssetsNumber;

                Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mCurrentScanBtnId == R.id.btn_newAssetsNumber)
                            scanDecode.stopScan();
                    };
                }, 8000);
                scanDecode.stopScan();
                scanDecode.starScan();
                break;

            case R.id.pv_bg:                                          // 点击"放大后的预览图片的控件"，缩小、隐藏那个预览布局
                mIvBg.startAnimation(out);
                //setTitleIsShow(View.VISIBLE);
                mPvBgImg.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mLlayoutParent.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }

}
