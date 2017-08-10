package com.zh.metermanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.IDNA;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.shen.sweetdialog.SweetAlertDialog;
import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.adapter.MeterContentAdapter;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.utils.ImageFactory;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.view.ClearEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/8/4.
 */

public class NewCollectorActivity extends BaseActivity implements View.OnClickListener {


    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;

    /** 采集器编号 -- 编辑框 */
    ClearEditText mCEtCollectorNumbers;
    /** 采集器编号 -- 按钮 */
    Button mBtnCollectorNumbers;


    /** 扫描类 */
    private ScanInterface scanDecode;
    /** 当前二维扫描的按钮 */
    private int mCurrentScanBtnId = 1;

    /** 存放(电表资产编号) -- 列表 */
    private ListView mLvMeterContent;

    /** 扫描电表资产编号 -- 按钮*/
    Button mBtnScan;
    /** 保存 -- 按钮*/
    Button mBtnSave;

    MeterContentAdapter mMeterContentAdapter;

    ArrayList<String> mMeterNumberList;

    /** 用户/表主的信息 */
    private  List<MeterBean> mMeterBeanList;



    @Override
    public int getContentLayout() {
        return R.layout.activity_new_collector;
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

        mTvTitle.setText("新装采集器");
    }

    @Override
    public void initView() {
        mCEtCollectorNumbers = (ClearEditText) findViewById(R.id.cet_collectorNumbers);
        mBtnCollectorNumbers = (Button) findViewById(R.id.btn_collectorNumbers);

        mLvMeterContent = (ListView) findViewById(R.id.lv_meterContent);

        mBtnScan = (Button) findViewById(R.id.btn_scan);
        mBtnSave = (Button) findViewById(R.id.btn_save);


    }

    @Override
    public void initListener() {
        mBtnCollectorNumbers.setOnClickListener(this);

        mBtnScan.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
    }

    @Override
    public void initData() {

        showLoadingDialog("","正在加载数据...");
        // 从数据库中加载数据
        taskPresenter.readDbToBean(readObserver);

        mCEtCollectorNumbers.setEnabled(false);

        mMeterNumberList = new ArrayList<String>();

        mMeterContentAdapter = new MeterContentAdapter(this, mMeterNumberList, new MeterContentAdapter.AdapterDel(){


            @Override
            public void del(int position) {

                mMeterNumberList.remove(position);
                mMeterContentAdapter.notifyDataSetChanged();
            }
        });

        mLvMeterContent.setAdapter(mMeterContentAdapter);


        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {

                data = data.trim();

                if(mCurrentScanBtnId == R.id.btn_collectorNumbers){              // 采集器
                    mCEtCollectorNumbers.setText(data);

                }else if(mCurrentScanBtnId == R.id.btn_scan){                       // 扫描资产编号
                    boolean exist = false;
                    for(MeterBean bean : mMeterBeanList){
                        if(bean.getOldAssetNumbers().equals(data)){
                            exist = true;
                            mMeterNumberList.add(data);
                            mMeterContentAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    if(!exist){
                        SweetAlertDialog mSweetAlertDialog = new SweetAlertDialog(NewCollectorActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                //.setTitleText("正在加载");
                                .setTitleText("提示")
                                .setContentText("该资产编码无匹配的用户数据,\n请核实后再次操作")
                                .setConfirmText("确认")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                        mSweetAlertDialog.show();

                    }

                }

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scanDecode != null)
            scanDecode.stopScan();  // 停止
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

            case R.id.btn_collectorNumbers:             // 采集器
                mCurrentScanBtnId = R.id.btn_collectorNumbers;
                Timer timer1 = new Timer();
                timer1.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mCurrentScanBtnId == R.id.btn_collectorNumbers)
                            scanDecode.stopScan();
                    };
                }, 8000);
                scanDecode.stopScan();
                scanDecode.starScan();
                break;

            case R.id.btn_scan:                         // 扫描资产编号
                mCurrentScanBtnId = R.id.btn_scan;
                Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mCurrentScanBtnId == R.id.btn_scan)
                            scanDecode.stopScan();
                    };
                }, 8000);
                scanDecode.stopScan();
                scanDecode.starScan();
                break;

            case R.id.btn_save:                         // 保存
                String collectorNumbers = mCEtCollectorNumbers.getText().toString().trim();

                if(!TextUtils.isEmpty(collectorNumbers)) {
                    if(mMeterNumberList.size() != 0) {

                        String[] strArr = new String[mMeterNumberList.size()];
                        mMeterNumberList.toArray(strArr);
                        showLoadingDialog("","正在保存数据...");
                        taskPresenter.saveNewCollector(saveObserver, collectorNumbers, strArr);
                    }else{
                        SweetAlertDialog mSweetAlertDialog = new SweetAlertDialog(NewCollectorActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                //.setTitleText("正在加载");
                                .setTitleText("提示")
                                .setContentText("请输入电表的资产编码")
                                .setConfirmText("确认")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                        mSweetAlertDialog.show();
                    }
                } else {
                    SweetAlertDialog mSweetAlertDialog = new SweetAlertDialog(NewCollectorActivity.this, SweetAlertDialog.NORMAL_TYPE)
                            //.setTitleText("正在加载");
                            .setTitleText("提示")
                            .setContentText("请输入采集器编号")
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                    mSweetAlertDialog.show();
                }

                break;

        }
    }


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

            LogUtils.i("meterBeen.size()" + meterBeen.size());
            MyApplication.setMeterBeanList(meterBeen);

            mMeterBeanList = meterBeen;
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


    /**
     * 保存数据！
     * rxjava -- 主线程
     */
    Observer saveObserver = new Observer<Long>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull Long aLong) {
            LogUtils.i("aLong:" + aLong);
            Log.i("shen", "保存情况：" + (aLong>0 ? "成功" : "失败"));
            //showToast("保存" + (aLong>0 ? "成功" : "失败"));
        }

        @Override
        public void onError(@NonNull Throwable e) {
            closeDialog();
        }


        @Override
        public void onComplete() {
            showToast("保存成功");
            closeDialog();
        }
    };

}
