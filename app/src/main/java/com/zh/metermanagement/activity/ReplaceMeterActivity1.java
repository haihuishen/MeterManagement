package com.zh.metermanagement.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.serialport.MeterController;
import android.serialport.Tools;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.shen.sweetdialog.SweetAlertDialog;
import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.adapter.PicAdapter;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.config.MeterAgreement;
import com.zh.metermanagement.utils.ElectricMeterParsUtils;
import com.zh.metermanagement.utils.ExcelUtil;
import com.zh.metermanagement.utils.ImageFactory;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.utils.StringUtils;
import com.zh.metermanagement.utils.TimeUtils;
import com.zh.metermanagement.view.ChangeDialog;
import com.zh.metermanagement.view.ClearEditText;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class ReplaceMeterActivity1 extends BaseActivity implements View.OnClickListener {


    /** 拍照获取图片*/
    public static final int TAKE_PHOTO = 2000;
    public static final int RESULT_OK = 1;


    private final int BROAD_READMETER_FAIL = 100;
    private final int BROAD_READMETER_SUCCESS = 200;

    private final String READMETER_AGREEMENT_TASK = "ReadMeterAgreementTask";
    private final String READMETER_TASK = "ReadMeterTask";


    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;





    //-------------------------------------
    /** 隐藏的,出现幽灵事件了 -- 编辑框*/
    private ClearEditText mCEtGone;


    /** 旧表资产编号 -- 编辑框*/
    private ClearEditText mCEtOldAssetNumbers;
    /** 旧表资产编号(扫描) -- 按钮*/
    private Button mBtnOldAssetNumbers;
    /** 序号 -- 文本*/
    private TextView mTvSequenceNumber;
    /** 用户名称 -- 文本*/
    private TextView mTvUserName;
    /** 用户编号 -- 文本*/
    private TextView mTvUserNumber;
    /** 用户地址 -- 文本*/
    private TextView mTvUserAddr;
    /** 用户电话 -- 文本*/
    private TextView mTvUserPhone;

    /** 旧电能表表地址 -- 编辑框*/
    private ClearEditText mCEtOldAddr;
    /** 旧电能表止码 -- 编辑框*/
    private ClearEditText mCEtOldElectricity;
    /** 旧电能表止码(扫描) -- 按钮*/
    private Button mBtnOldElectricity;
    /** 新电能表表地址 -- 编辑框*/
    private ClearEditText mCEtNewAddr;
    /** 新表资产编号 -- 编辑框*/
    private ClearEditText mCEtNewAssetNumbersScan;
    /** 新表资产编号(扫描) -- 按钮*/
    private Button mBtnNewAssetNumbersScan;
    /** 新电能表止码 -- 编辑框*/
    private ClearEditText mCEtNewElectricity;
    /** 新电能表止码(扫描) -- 按钮*/
    private Button mBtnNewElectricity;


    /** 保存按钮 -- 按钮*/
    private Button mBtnSave;

    /** 扫描类 */
    private ScanInterface scanDecode;
    /** 当前二维扫描的按钮 */
    private int mCurrentScanBtnId = 1;

    //--------------------------------------------------------

    /** 从驱动文件中(缓冲区)获取数据的类 -- 红外的！ */
    private MeterController mMeterController;

    /** 当前红外扫描的按钮 */
    private int mCurrentReadBtnId = 1;

    /** 数据标识 */
    private String bz;

    private final int Pro_Idle = 0;
    private final int Pro_One = 1;
    private final int Pro_Two = 2;
    private int currentPro = Pro_Idle;

    private byte[] sendBuffer;
    private byte[] one = null;

    private String mTasking = "";

    private String mReadMeter97Or07 = "";

    private String mAddr97Or07 = "";

    ReadMeterAgreementTask mReadMeterAgreementTask;

    private MeterBean1 mMeterBean = new MeterBean1();
    //--------------------------------------------------------

    /** 摄影 -- 按钮 */
    private Button mBtnCamera;
    /** 照片列表 -- 再生控件 */
    private RecyclerView mRvPic;


    /** 包裹预览图片的控件*/
    private LinearLayout mLayoutPv;
    /** 预览图片控件*/
    private PhotoView mPvCamaraPhoto;

    private Bitmap mBitmap;

    View mLlayoutParent;
    View mIvBg;
    /** 放大后存放图片的控件*/
    PhotoView mPvBgImg;
    Info mInfo;

    AlphaAnimation in;
    AlphaAnimation out;


    String mCurrentPicName = "";


    /** 保存数据是否成功 */
    boolean isSaveSuccess = true;
    /** 是否是正在保存数据 */
    boolean isSave = false;

    /**
     * 0 -- 旧表地址为空
     * 1 --
     */
    int mDialogType = 0;
    /** 旧表地址为空 -- 手工输入表地址 */
    static int DialogType_oldAddrIsEmpty = 0;
    /** 旧表地址不为空 -- 是否手工输入"电表止码" */
    static int DialogType_oldAddrIsNotEmpty = 1;

    /** 新表地址为空 -- 手工输入表地址 */
    static int DialogType_newAddrIsEmpty = 2;
    /** 新表地址不为空 -- 是否手工输入"电表止码" */
    static int DialogType_newAddrIsNotEmpty = 3;

    /** 当前的抄表区段 */
    String mMeteringSection = "";



    //---------------------------------图片-----------------------
    List<String> mPicPaths = new ArrayList<>();
    String[] mTemp;
    PicAdapter mPicAdapter;
    int mPhotoIndex = 0;

    //--------------------------------------------------------------

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BROAD_READMETER_FAIL: 												// 抄表失败
                    mBeepManager.playError();
                    //promptTone();
                    showToast("抄表失败");
                    String fallMessage = "";

                    if(mCurrentReadBtnId == R.id.btn_oldElectricity){
                        String oldAddr = mCEtOldAddr.getText().toString().trim();
                        String oldElectricity = mCEtOldElectricity.getText().toString().trim();
                        if(TextUtils.isEmpty(oldAddr)){
                            fallMessage = "\n是否要手工输入表地址";
                            mDialogType = DialogType_oldAddrIsEmpty;
                        }else {
                            fallMessage = "\n是否要手工输入电表止码";
                            mDialogType = DialogType_oldAddrIsNotEmpty;
                        }
                    }else if(mCurrentReadBtnId == R.id.btn_newElectricity){
                        String newAddr = mCEtNewAddr.getText().toString().trim();
                        String newElectricity = mCEtNewElectricity.getText().toString().trim();
                        if(TextUtils.isEmpty(newAddr)){
                            fallMessage = "\n是否要手工输入表地址";
                            mDialogType = DialogType_newAddrIsEmpty;
                        }else {
                            fallMessage = "\n是否要手工输入电表止码";
                            mDialogType = DialogType_newAddrIsNotEmpty;
                        }
                    }



                    SweetAlertDialog dialog = new SweetAlertDialog(ReplaceMeterActivity1.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("提示")
                            .setContentText("抄表失败" + fallMessage)
                            .setConfirmText("是")
                            .setCancelText("否")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    ChangeDialog myDialog = new ChangeDialog(ReplaceMeterActivity1.this) {  // 注意这个上下文，用父的，还是自己的，全局的


                                        @Override
                                        public void confirm(ChangeDialog changeDialog, String addr, String electricity) {

                                            if(mDialogType == DialogType_oldAddrIsEmpty){
                                                mCEtOldAddr.setText(addr);
                                                mCEtOldElectricity.setText("");
                                            }else if(mDialogType == DialogType_oldAddrIsNotEmpty){
                                                mCEtOldElectricity.setText(electricity);

                                            }else if(mDialogType == DialogType_newAddrIsEmpty){
                                                mCEtNewAddr.setText(addr);
                                                mCEtNewElectricity.setText("");
                                            }else if(mDialogType == DialogType_newAddrIsNotEmpty){
                                                mCEtNewElectricity.setText(electricity);
                                            }

                                            changeDialog.dismiss();
                                        }

                                        @Override
                                        public void cancel(ChangeDialog changeDialog) {

                                            changeDialog.dismiss();
                                        }
                                    };

                                    myDialog.setTitle("手动输入");

                                    if(mDialogType == DialogType_oldAddrIsEmpty
                                            || mDialogType == DialogType_newAddrIsEmpty) {
                                        myDialog.setEtAddrVisibility(View.VISIBLE);
                                        myDialog.setEtElectricityVisibility(View.GONE);

                                    }else if(mDialogType == DialogType_oldAddrIsNotEmpty
                                            || mDialogType == DialogType_newAddrIsNotEmpty){
                                        myDialog.setEtAddrVisibility(View.GONE);
                                        myDialog.setEtElectricityVisibility(View.VISIBLE);
                                    }
                                    myDialog.show();

                                    sweetAlertDialog.dismiss();
                                }
                            }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Log.i("shen", "mDialogType:" + mDialogType);
                                    if(mDialogType == DialogType_oldAddrIsNotEmpty){
                                        mCEtOldAddr.setText("");
                                    }else if(mDialogType == DialogType_newAddrIsNotEmpty){
                                        mCEtNewAddr.setText("");
                                    }
                                    sweetAlertDialog.dismiss();
                                }
                            });


                    dialog.setCancelable(false);
                    dialog.show();
                    break;

                case BROAD_READMETER_SUCCESS: 											// 抄表成功
                    mBeepManager.playSuccessful();
                    //promptTone();                                            // 发出声音 -- 提示音
                    String[] datas = (String[]) msg.obj;					 // 刚刚抄表得到的数据

                    if(mTasking.equals(READMETER_AGREEMENT_TASK)) {

                        if(datas[1].length() == 4)
                            mReadMeter97Or07 = "97";
                        else if(datas[1].length() == 8)
                            mReadMeter97Or07 = "07";

                        if(mReadMeterAgreementTask!=null
                                && !mReadMeterAgreementTask.isCancelled()
                                && mReadMeterAgreementTask.getStatus() == AsyncTask.Status.RUNNING){

                            mReadMeterAgreementTask.setStop();
                        }

//                        mTvResult.setText("广播抄表结果 -- \n"
//                                + "表地址:" + datas[0] + "\n"
//                                + "数据标识:" + datas[1] + " : " + datas[2]);

                        if(mCurrentReadBtnId == R.id.btn_oldElectricity){
                            mCEtOldAddr.setText(datas[0]);
                            mCEtOldElectricity.setText(datas[2]);

                        }else if(mCurrentReadBtnId == R.id.btn_newElectricity){
                            mCEtNewAddr.setText(datas[0]);
                            mCEtNewElectricity.setText(datas[2]);
                        }

                    }
                    break;

                default:
                    break;
            }

        }
    };


    @Override
    public int getContentLayout() {
        return R.layout.activity_replace_meter1;
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

        mTvTitle.setText("换表");
    }

    @Override
    public void initView() {

        mCEtGone = (ClearEditText) findViewById(R.id.cet_gone);

        mCEtOldAssetNumbers = (ClearEditText) findViewById(R.id.cet_oldAssetNumbers);
        mBtnOldAssetNumbers = (Button) findViewById(R.id.btn_oldAssetNumbers);
        mTvSequenceNumber = (TextView) findViewById(R.id.tv_sequenceNumber);
        mTvUserName = (TextView) findViewById(R.id.tv_userName);
        mTvUserNumber = (TextView) findViewById(R.id.tv_userNumber);
        mTvUserAddr = (TextView) findViewById(R.id.tv_userAddr);
        mTvUserPhone = (TextView) findViewById(R.id.tv_userPhone);

        mCEtOldAddr = (ClearEditText) findViewById(R.id.cet_oldAddr);
        mCEtOldElectricity = (ClearEditText) findViewById(R.id.cet_oldElectricity);
        mBtnOldElectricity = (Button) findViewById(R.id.btn_oldElectricity);
        mCEtNewAddr = (ClearEditText) findViewById(R.id.cet_newAddr);
        mCEtNewAssetNumbersScan = (ClearEditText) findViewById(R.id.cet_newAssetNumbersScan);
        mBtnNewAssetNumbersScan = (Button) findViewById(R.id.btn_newAssetNumbersScan);
        mCEtNewElectricity = (ClearEditText) findViewById(R.id.cet_newElectricity);
        mBtnNewElectricity = (Button) findViewById(R.id.btn_newElectricity);

        mBtnCamera = (Button) findViewById(R.id.btn_camera);

        mRvPic = (RecyclerView) findViewById(R.id.rv_pic);
        mBtnSave = (Button) findViewById(R.id.btn_save);



        mLayoutPv = (LinearLayout) findViewById(R.id.linearLayout_pv);
        mPvCamaraPhoto = (PhotoView) findViewById(R.id.pv_image);

        mLlayoutParent = findViewById(R.id.parent);
        mIvBg = findViewById(R.id.bg);
        mPvBgImg = (PhotoView) findViewById(R.id.img);
    }

    @Override
    public void initListener() {

        mBtnOldAssetNumbers.setOnClickListener(this);
        mBtnOldElectricity.setOnClickListener(this);
        mBtnNewAssetNumbersScan.setOnClickListener(this);
        mBtnNewElectricity.setOnClickListener(this);

        mBtnCamera.setOnClickListener(this);
        mLayoutPv.setOnClickListener(this);
        mPvBgImg.setOnClickListener(this);

        mBtnSave.setOnClickListener(this);
    }

    @Override
    public void initData() {

        mBtnNewElectricity.setEnabled(false);

        mMeteringSection = MyApplication.getCurrentMeteringSection();

        //---------------------------------图片---------------------------------------
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);         // 水平
        mRvPic.setLayoutManager(manager);

        mPicAdapter = new PicAdapter(this, mPicPaths, new PicAdapter.PicListener() {
            @Override
            public void onDelete(int index, final String path) {                      // 删除资源

                SweetAlertDialog dialog = new SweetAlertDialog(ReplaceMeterActivity1.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("提示")
                        .setContentText("是否删除该图片")
                        .setConfirmText("是")
                        .setCancelText("否")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mMeterBean.setPicPath(deleteSubStr(mMeterBean.getPicPath(), path));
                                File file = new File(path);
                                if(file.exists()){
                                    file.delete();
                                }
                                ExcelUtil.broadCreateFile(ReplaceMeterActivity1.this, new File(path));

                                mPicAdapter.setPathList(mMeterBean.getPicPath());
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });


                dialog.show();

            }

            @Override
            public void onPreView(int index, String path, Info info){                      // 预览图片

                mInfo = info;                   // 拿到pv_camaraPhoto的信息(如：位置)，用于动画

                mBitmap = ImageFactory.getBitmap(path);
                mPvBgImg.setImageBitmap(mBitmap);
                mIvBg.startAnimation(in);             // 执行动画
                mIvBg.setVisibility(View.VISIBLE);
                mLlayoutParent.setVisibility(View.VISIBLE);
                mPvBgImg.animaFrom(mInfo);
                //ToastUtil.show("点击了预览图片");
                setTitleIsShow(View.GONE);

            }
        });

        mRvPic.setAdapter(mPicAdapter);

        //---------------------------------扫描--------------------------------------
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {

                final String scanData = data.trim();
                mCEtGone.setText("");
                if(mCurrentScanBtnId == R.id.btn_oldAssetNumbers){              // 旧表资产编号(二维扫描)
                    mCEtOldAssetNumbers.setText(scanData);

                    boolean isFind = false;

                    for(MeterBean1 bean : MyApplication.getMeterBean1List()){
                        LogUtils.i("bean.toString()" + bean.toString());
                        LogUtils.i("data" + scanData);

                        if(bean.getOldAssetNumbers().equals(scanData)){

                            mMeterBean = bean;

                            mTvUserName.setText(bean.getUserName());
                            mTvUserNumber.setText(bean.getUserNumber());
                            mTvUserAddr.setText(bean.getUserAddr());
                            mTvUserPhone.setText(bean.getUserPhone());

                            mCEtOldAddr.setText(bean.getOldAddr());
                            mCEtOldElectricity.setText(bean.getOldElectricity());

                            mCEtNewAddr.setText(bean.getNewAddr());
                            mCEtNewElectricity.setText(bean.getNewElectricity());
                            mCEtNewAssetNumbersScan.setText(bean.getNewAssetNumbersScan());

                            if(StringUtils.isEmpty(mMeterBean.getPicPath())){
                                mPhotoIndex = 1;
                            }else {
                                String path = mMeterBean.getPicPath();
                                for(String tempPath : path.split(",")){
                                    if(!(new File(tempPath).exists())){
                                        path = deleteSubStr(path, tempPath);
                                    }
                                }
                                mMeterBean.setPicPath(path);
                                if(StringUtils.isNotEmpty(mMeterBean.getPicPath())) {
                                    try {
                                        mPhotoIndex = Integer.parseInt(path.substring(path.lastIndexOf("_") + 1, path.lastIndexOf("."))) + 1;
                                    } catch (Exception e) {
                                    }
                                }
                                mPicAdapter.setPathList(mMeterBean.getPicPath());
                            }


//                            if (StringUtils.isNotEmpty(bean.getPicPath())) {                        // 图片
//                                mTemp = bean.getPicPath().split(",");
//                                for (String path : mTemp) {
//                                    mPicPaths.add(path);
//                                }
//                            }

                            isFind = true;
                            mBeepManager.playSuccessful();
                            break;
                        }
                    }

                    if(!isFind){
                        mBeepManager.playError();
                        SweetAlertDialog dialog = new SweetAlertDialog(ReplaceMeterActivity1.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("提示")
                                .setContentText(scanData + "\n该电表资产编码无匹配的用户，\n请通知供电所相关人员")
                                .setConfirmText("确认")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        ContentValues values = new ContentValues();
                                        values.put("assetNumbers", scanData);
                                        // 只有 14位的资产编码 或是 24位的资产编码才记录
                                        if(scanData.length() == 14 || scanData.length() == 24) {
                                            taskPresenter.addMismatchingAssetNumbers(addObserver, values);
                                        }
                                        mCEtOldAssetNumbers.setText("");
                                        sweetAlertDialog.dismiss();
                                    }
                                });


                        dialog.setCancelable(false);
                        dialog.show();
                    }

                }else if(mCurrentScanBtnId == R.id.btn_newAssetNumbersScan){    // 新表资产编号(二维扫描)

                    mBeepManager.playSuccessful();
                    if(scanData.length() != 24) {

                        SweetAlertDialog dialog = new SweetAlertDialog(ReplaceMeterActivity1.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("提示")
                                .setContentText("该资产编码不是24位的是否输入")
                                .setConfirmText("是")
                                .setCancelText("否")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        mCEtNewAssetNumbersScan.setText(scanData);
                                        mCEtNewAddr.setText(parseAddr(scanData));
                                        mCEtNewElectricity.setText("0");
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });


                        dialog.setCancelable(false);
                        dialog.show();


                    }

                }

            }
        });


        //------------------------- 红外 ----------------------------------

        mMeterController = MeterController.getInstance();
        mMeterController.Meter_Open(portData, this);


        //--------------------------加载数据--------------------------------
        showLoadingDialog("","正在加载数据...");

        LogUtils.i("mMeteringSection:" + mMeteringSection);
        // 从数据库中加载数据
        taskPresenter1.readDbToBean(readObserver, mMeteringSection);


        mCEtOldAssetNumbers.setEnabled(false);

        mTvUserName.setText("");
        mTvUserNumber.setText("");
        mTvUserAddr.setText("");
        mTvUserPhone.setText("");

        mCEtOldAddr.setEnabled(false);
        mCEtOldElectricity.setEnabled(false);

        mCEtNewAddr.setEnabled(false);
        mCEtNewElectricity.setEnabled(false);
        mCEtNewAssetNumbersScan.setEnabled(false);


        //------------------------- 拍照 ----------------------------------



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

//        mPvCamaraPhoto.disenable();// 把PhotoView当普通的控件，把触摸功能关掉
//        mPvCamaraPhoto.setImageBitmap(mBitmap);

        mPvBgImg.setImageBitmap(mBitmap);
        mPvBgImg.enable();

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (scanDecode != null)
            scanDecode.stopScan();  // 停止
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanDecode != null)
            scanDecode.onDestroy(); // 回复初始状态

        if(mMeterController != null)
            mMeterController.Meter_Close();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back_left:

                finish();
                break;

            case R.id.btn_menu_right:

                break;


            case R.id.btn_oldAssetNumbers:                         // 旧表资产编号(二维扫描)
                mCEtOldAssetNumbers.setText("");

                mTvUserName.setText("");
                mTvUserNumber.setText("");
                mTvUserAddr.setText("");
                mTvUserPhone.setText("");

                mCEtOldAddr.setText("");
                mCEtOldElectricity.setText("");

                mCEtNewAddr.setText("");
                mCEtNewElectricity.setText("");
                mCEtNewAssetNumbersScan.setText("");

                //------------------------------------------------
                mBitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "no_preview_picture.png");

//                mPvCamaraPhoto.disenable();// 把PhotoView当普通的控件，把触摸功能关掉
//                mPvCamaraPhoto.setImageBitmap(mBitmap);

                mPvBgImg.setImageBitmap(mBitmap);
                mPvBgImg.enable();

                mCurrentPicName = "";
                //------------------------------------------------

                mCurrentScanBtnId = R.id.btn_oldAssetNumbers;

                Timer timer1 = new Timer();
                timer1.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mCurrentScanBtnId == R.id.btn_oldAssetNumbers)
                            scanDecode.stopScan();
                    };
                }, 8000);
                scanDecode.stopScan();
                scanDecode.starScan();                              //启动扫描
                break;

            case R.id.btn_oldElectricity:                         // 旧电能表止码(红外扫描)
                mCurrentReadBtnId = R.id.btn_oldElectricity;

                //mCEtOldAddr.setText("");
                mCEtOldElectricity.setText("");

                String oldAssetNumbers = mCEtOldAssetNumbers.getText().toString().trim();
                if(!TextUtils.isEmpty(oldAssetNumbers)) {
                    String oldAddr = mCEtOldAddr.getText().toString().trim();
                    String addr = "";
                    if(TextUtils.isEmpty(oldAddr))
                        addr = parseAddr(oldAssetNumbers);
                    else
                        addr = parseAddr(oldAddr);
                    //String addr = parseAddr("123456789123456789123456");
                    if(!TextUtils.isEmpty(addr))
                        startReadMeter(addr);
                    else
                        closeDialog();
                }else{
                    showToast("请输入--旧表资产编号");
                }
                break;

            case R.id.btn_newAssetNumbersScan:                     // 新表资产编号(二维扫描)
                mCurrentScanBtnId = R.id.btn_newAssetNumbersScan;

                Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mCurrentScanBtnId == R.id.btn_newAssetNumbersScan)
                            scanDecode.stopScan();
                    };
                }, 8000);
                scanDecode.stopScan();
                scanDecode.starScan();                              //启动扫描
                break;

            case R.id.btn_newElectricity:                         // 新电能表止码(红外扫描)
                mCurrentReadBtnId = R.id.btn_newElectricity;

                //mCEtNewAddr.setText("");
                mCEtNewElectricity.setText("");

                String newAssetNumbers = mCEtNewAssetNumbersScan.getText().toString().trim();
                if(!TextUtils.isEmpty(newAssetNumbers)) {

                    String newAddr = mCEtNewAddr.getText().toString().trim();
                    String addr = "";
                    if(TextUtils.isEmpty(newAddr))
                        addr = parseAddr(newAssetNumbers);
                    else
                        addr = parseAddr(newAddr);

                    if(!TextUtils.isEmpty(addr)) {

                        startReadMeter(addr);
                    }
                    else
                        closeDialog();
                }else{
                    showToast("请输入--新表资产编号");
                }

                break;

            case R.id.btn_camera:

                String oldAssetNumbers1 = mCEtOldAssetNumbers.getText().toString().trim();
                if(!TextUtils.isEmpty(oldAssetNumbers1)) {

                    int size = 0;
                    if(StringUtils.isEmpty(mMeterBean.getPicPath())){
                        size = 0;
                    }else{
                        size = mMeterBean.getPicPath().split(",").length;
                    }
                    if(size > 5){
                        showToast("照片已超过6张");
                    }else {

                        String userName = mTvUserName.getText().toString().trim();
                        String userNumber = mTvUserNumber.getText().toString().trim();

                        //mCurrentPicName = Constant.IMAGE_PATH + userName + "_" + oldAssetNumbers1 + "_" + mPhotoIndex + ".jpg";
                        mCurrentPicName = MyApplication.getNoWorkOrderPath().getReplaceMeterPhotoPath()
                                + userName + "_" + oldAssetNumbers1 + "_" + mPhotoIndex + ".jpg";

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(mCurrentPicName);  // 携带图片存放路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(intent, TAKE_PHOTO);
                    }
                }else{
                    showToast("请输入--旧表资产编号");
                }


                break;

//            case R.id.linearLayout_pv:                              // 点击"包裹预览图片控件的布局"，放大、那个预览布局设为可见.
//                mInfo = mPvCamaraPhoto.getInfo();                   // 拿到pv_camaraPhoto的信息(如：位置)，用于动画
//
//                mPvBgImg.setImageBitmap(mBitmap);
//                mIvBg.startAnimation(in);             // 执行动画
//                mIvBg.setVisibility(View.VISIBLE);
//                mLlayoutParent.setVisibility(View.VISIBLE);
//                mPvBgImg.animaFrom(mInfo);
//                //ToastUtil.show("点击了预览图片");
//                setTitleIsShow(View.GONE);
//                break;

            case R.id.img:                                          // 点击"放大后的预览图片的控件"，缩小、隐藏那个预览布局
                mIvBg.startAnimation(out);
                setTitleIsShow(View.VISIBLE);
                mPvBgImg.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mLlayoutParent.setVisibility(View.GONE);
                    }
                });
                break;


            case R.id.btn_save:

                saveDate();

                break;

        }

    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_PHOTO){                       // 拍照获取图片
            if (resultCode == Activity.RESULT_OK) {


                if (mPicAdapter != null && mMeterBean != null) {
                    mPicAdapter.addPath(mCurrentPicName);
                    //LogUtils.i("前：" + mMeterBean.getPicPath());

                    if (StringUtils.isEmpty(mMeterBean.getPicPath())) {
                        mMeterBean.setPicPath(mCurrentPicName);
                    } else {
                        mMeterBean.setPicPath(mMeterBean.getPicPath() + "," + mCurrentPicName);
                    }
                    //LogUtils.i("后：" + mMeterBean.getPicPath());
                    ExcelUtil.broadCreateFile(ReplaceMeterActivity1.this, new File(mCurrentPicName));
                    mPhotoIndex++;

                }
            }

        }



    }

    /**
     * 解析地址
     *
     * @param str
     * @return
     */
    private String parseAddr(String str) {
        String addr = "AAAAAAAAAAAA";
        //String addr = "000000000000";

        if(str.length() == 14){
            addr = str.substring(8, 14);            // 因为其他原因取 后6位

        }else if(str.length() == 24){
            addr = str.substring(12, 24);
        }else {
//            SweetAlertDialog dialog = new SweetAlertDialog(ReplaceMeterActivity.this, SweetAlertDialog.NORMAL_TYPE)
//                    .setTitleText("提示")
//                    .setContentText(str + "\n该编码不符合南网电表编码规则\n请确认后再操作")
//                    .setConfirmText("确认")
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                            sweetAlertDialog.dismiss();
//                        }
//                    });
//
//
//            dialog.setCancelable(false);
//            dialog.show();
//
//            return "";
            if(str.length() >= 12){
                addr = str.substring(str.length() - 12, str.length());
            }else if(str.length() < 12){
                addr = str;
            }
        }



        LogUtils.i("addr:"+addr + "--addr.length():" + addr.length());

        return addr;
    }

    /**
     * 保存数据
     *
     */
    private void saveDate() {


//        mCEtOldAddr.setText("old");
//        mCEtOldElectricity.setText("1");
//        mCEtNewAddr.setText("new");
//        mCEtNewElectricity.setText("2");

        String oldAssetNumbers = mCEtOldAssetNumbers.getText().toString().trim();           // 旧表资产编号
        String oldAddr = mCEtOldAddr.getText().toString().trim();                           // 旧电能表表地址
        String oldElectricity = mCEtOldElectricity.getText().toString().trim();             // 旧电能表止码
        String newAddr = mCEtNewAddr.getText().toString().trim();                           // 新电能表表地址
        String newAssetNumbersScan = mCEtNewAssetNumbersScan.getText().toString().trim();   // 新表资产编号
        String newElectricity = mCEtNewElectricity.getText().toString().trim();             // 新电能表止码

        if(TextUtils.isEmpty(oldAssetNumbers)){
            showToast("请输入--旧表资产编号");
            return;
        }
//        else if(TextUtils.isEmpty(oldAddr)){
//            showToast("请输入--旧电能表表地址");
//            return;
//        }else if(TextUtils.isEmpty(oldElectricity)){
//            showToast("请输入--旧电能表止码");
//            return;
//        }else if(TextUtils.isEmpty(newAddr)){
//            showToast("请输入--新电能表表地址");
//            return;
//        }else if(TextUtils.isEmpty(newAssetNumbersScan)){
//            showToast("请输入--新表资产编号");
//            return;
//        }else if(TextUtils.isEmpty(newElectricity)){
//            showToast("请输入--新电能表止码");
//            return;
//        }

        boolean isFinish = true;

        if(TextUtils.isEmpty(oldAddr)){
            isFinish = false;
        }
        if(TextUtils.isEmpty(oldElectricity)){
            isFinish = false;
        }
        if(TextUtils.isEmpty(newAddr)){
            isFinish = false;
        }
        if(TextUtils.isEmpty(newAssetNumbersScan)){
            isFinish = false;
        }
        if(TextUtils.isEmpty(newElectricity)){
            isFinish = false;
        }

        LogUtils.i("isFinish:" + isFinish);
        LogUtils.i("false:" + false);

        mMeterBean.setOldAddr(oldAddr);
        mMeterBean.setOldAddrAndAsset(false);
        mMeterBean.setOldElectricity(oldElectricity);
        mMeterBean.setNewAddr(newAddr);
        mMeterBean.setNewAddrAndAsset(false);
        mMeterBean.setNewAssetNumbersScan(newAssetNumbersScan);
        mMeterBean.setNewElectricity(newElectricity);
        mMeterBean.setTime(TimeUtils.getCurrentTimeRq());
        mMeterBean.setRelaceOrAnd("0");
        mMeterBean.setFinish(isFinish);
        mMeterBean.setCollectorAssetNumbersScan("");

//        LogUtils.i("oldAddr.substring(oldAddr.length() - 5, oldAddr.length()" +
//                oldAddr.substring(oldAddr.length() - 5, oldAddr.length())+
//                "oldAssetNumbers.substring(oldAssetNumbers.length() - 5, oldAssetNumbers.length())"+
//                        oldAssetNumbers.substring(oldAssetNumbers.length() - 5, oldAssetNumbers.length()));

        if(oldAssetNumbers.length() > 5 && oldAddr.length()>5) {
            if (!oldAddr.substring(oldAddr.length() - 5, oldAddr.length()).equals(
                    oldAssetNumbers.substring(oldAssetNumbers.length() - 5, oldAssetNumbers.length()))) {
                mMeterBean.setOldAddrAndAsset(true);
                LogUtils.i("不同");
            }
        }

//        if(newAssetNumbersScan.length() > 5 && newAddr.length()>5) {
//            if (newAddr.substring(newAddr.length() - 5, newAddr.length()).equals(
//                    newAssetNumbersScan.substring(newAssetNumbersScan.length() - 5, newAssetNumbersScan.length()))) {
//                mMeterBean.setNewAddrAndAsset(true);
//            }
//        }

        LogUtils.i("mMeterBean.toString():" + mMeterBean.toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put("oldAddr", mMeterBean.getOldAddr());
        contentValues.put("oldAddrAndAsset", mMeterBean.isOldAddrAndAsset());
        contentValues.put("oldElectricity", mMeterBean.getOldElectricity());
        contentValues.put("newAddr", mMeterBean.getNewAddr());
        contentValues.put("newAddrAndAsset", mMeterBean.isNewAddrAndAsset());
        contentValues.put("newAssetNumbersScan", mMeterBean.getNewAssetNumbersScan());
        contentValues.put("newElectricity", mMeterBean.getNewElectricity());
        contentValues.put("collectorAssetNumbersScan", mMeterBean.getCollectorAssetNumbersScan());
        contentValues.put("time", mMeterBean.getTime());
        contentValues.put("picPath", mMeterBean.getPicPath());
        contentValues.put("relaceOrAnd", mMeterBean.getRelaceOrAnd());                      // 0:换表 1：加装采集器
        contentValues.put("isFinish", mMeterBean.isFinish());

        showLoadingDialog("", "正在保存数据...");

        taskPresenter1.saveData(saveObserver, contentValues,
                Constant.TABLE_METERINFO_STR_oldAssetNumbers + "=?", new String[]{oldAssetNumbers});

    }

//    /**
//     * 发出声音 -- 提示音
//     */
//    private void promptTone() {
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        r.play();
//    }

    /**
     * 开始抄表 -- 广播抄表
     */
    private void startReadMeter(String addr) {

        mReadMeterAgreementTask = new ReadMeterAgreementTask(addr);
        mReadMeterAgreementTask.execute();

    }

    /**
     * 异步 -- 广播抄表 -- 获取地址&正向有功电度
     */
    public class ReadMeterAgreementTask extends AsyncTask<String, String, String> {

        boolean stop = false;

        String mAddr = "AAAAAAAAAAAA";

        public ReadMeterAgreementTask(String addr){
            mAddr = addr;
        }


        public void setStop(){
            stop = true;
        }

        @Override
        protected void onPreExecute() {                                                 // 执行前
            super.onPreExecute();

            stop = false;
            mTasking = READMETER_AGREEMENT_TASK;
            mReadMeter97Or07 = "";
            showLoadingDialog("正在确定电表协议,请稍等", null);



        }


        @Override
        protected String doInBackground(String... strings) {            // 执行中

            byte[] command;


            mMeterController.openSerialPort("/dev/ttyMT2", 2400, 8, 1, 1);        // 打开串口
            mAddr97Or07 = "97";
            command = getBuffer(getMeterAddress(mAddr), MeterAgreement.Pro97.STR_9010);                      // 生成广播命令！
            publishProgress("广播抄表: 97协议\n"
                    + "\n数据标识: " + MeterAgreement.Pro97.STR_9010
                    + "\n命令: " + Tools.bytesToHexString(command));
            mMeterController.writeCommand(command);



            Timer timer97 = new Timer();
            timer97.schedule(new TimerTask() {
                @Override
                public void run() {
                    stop = true;
                };
            }, 3000);

            while (!stop) {
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            timer97.cancel();
            stop = false;

            if(!mReadMeter97Or07.equals("97")) {
                mMeterController.openSerialPort("/dev/ttyMT2", 1200, 8, 1, 1);        // 打开串口
                mAddr97Or07 = "07";
                command = getBuffer(getMeterAddress(mAddr), MeterAgreement.Pro07.STR_00010000);                      // 生成广播命令！
                publishProgress("广播抄表: 07协议\n"
                        + "\n数据标识: " + MeterAgreement.Pro07.STR_00010000
                        + "\n命令: " + Tools.bytesToHexString(command));
                mMeterController.writeCommand(command);

                Timer timer07 = new Timer();
                timer07.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stop = true;
                    };
                }, 3000);

                while (!stop) {
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                timer07.cancel();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String str) {           // 执行后
            super.onPostExecute(str);

            if(TextUtils.isEmpty(mReadMeter97Or07)) {                        	 // 没有收到数据
                Message message = Message.obtain();
                message.what = BROAD_READMETER_FAIL;
                mHandler.sendMessage(message);
            }

            closeDialog();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            updataLoadingDialog("正在确定电表协议,请稍等", values[0]);
        }
    }




    /**
     * 根据"表地址"和"数据标识"生成"命令"
     *
     * @param bdz		表地址
     * @param str		数据标识
     * @return
     */
    private byte[] getBuffer(byte[] bdz, String str) {
        int sendL = 0;
        int i = 0;

        this.bz = str;
        String strCortrol = "";
        String strLength = "";

        int bzLength = bz.length();
        if (bzLength == 4){ 			// 97协议
            sendL = 18;
            strCortrol = "01";			// 控制码
            strLength = "02";			// 数据域长度 -- (应为是请,没有附带有用的数据--只有数据标识)


        } else if (bzLength == 8){ 		// 07协议
            sendL = 20;
            strCortrol = "11";			// 控制码
            strLength = "04";			// 数据域长度 -- (应为是请,没有附带有用的数据--只有数据标识)
        } else {
            return null;
        }

        sendBuffer = new byte[sendL];

        sendBuffer[0] = (byte) 0xFE;
        sendBuffer[1] = (byte) 0xFE;
        sendBuffer[2] = (byte) 0xFE;
        sendBuffer[3] = (byte) 0xFE;
        sendBuffer[4] = 0x68;

        // 表地址						// 这个就要注意 -- 地址要从"低到高"
        sendBuffer[5] = bdz[5];
        sendBuffer[6] = bdz[4];
        sendBuffer[7] = bdz[3];
        sendBuffer[8] = bdz[2];
        sendBuffer[9] = bdz[1];
        sendBuffer[10] = bdz[0];

        sendBuffer[11] = 0x68;

        // 控制码
        sendBuffer[12] = Tools.hexString2Bytes(strCortrol)[0];

        //数据域长度
        sendBuffer[13] = Tools.hexString2Bytes(strLength)[0];

        // 数据域 -- 都要 + 0x33
        for (i = 1; i <= Integer.parseInt(strLength.substring(1, 2)); i++) {
            int j = 2 * (Integer.parseInt(strLength.substring(1, 2)) - i);
            sendBuffer[13 + i] = (byte) (Tools.hexString2Bytes(bz.substring(j, j + 2))[0] + 0x33);
        }

        // 校验位
        int sumMod = 0;
        for (i = 4; i <= sendL - 3; i++) {
            sumMod += sendBuffer[i];
        }
        sendBuffer[sendL - 2] = (byte) (sumMod % 256);

        // 结束符
        sendBuffer[sendL - 1] = 0x16;

        mMeterController.writeCommand("fefefefefefe".getBytes());
        // 发送数据 设置延迟10毫秒
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("shen", "sendBuffer === " + Tools.bytesToHexString(sendBuffer));

        setCurrentPro(Pro_One);

        return sendBuffer;
    }

    /**
     * 设置 currentPro
     *
     * @param pro    有三个：Pro_Idle = 0; Pro_One = 1; Pro_Two = 2;
     */
    private void setCurrentPro(int pro) {
        this.currentPro = pro;
    }


    /**
     * 获取表地址
     *
     * @return
     */
    private byte[] getMeterAddress(String addr) {

        if(mAddr97Or07.equals("97")){
            while (addr.length() < 12){
                addr = "A" + addr;
            }
        }else if (mAddr97Or07.equals("07")){
            while (addr.length() < 12){
                addr = "0" + addr;
            }
        }
        byte[] bdzByte = new byte[6];
        // 取表地址后六位 再在前面拼六个A
        String bdzString = addr;

        bdzByte[0] = Tools.hexString2Bytes(bdzString.substring(0, 2))[0];
        bdzByte[1] = Tools.hexString2Bytes(bdzString.substring(2, 4))[0];
        bdzByte[2] = Tools.hexString2Bytes(bdzString.substring(4, 6))[0];
        bdzByte[3] = Tools.hexString2Bytes(bdzString.substring(6, 8))[0];
        bdzByte[4] = Tools.hexString2Bytes(bdzString.substring(8, 10))[0];
        bdzByte[5] = Tools.hexString2Bytes(bdzString.substring(10, 12))[0];

        return bdzByte;
    }

    /**
     * 获取到数据后(红外抄表) -- 会调用这个方法！！！
     *
     * MeterController.MeterCallBack    <p>
     * MeterController类的回调方法！
     */
    private MeterController.MeterCallBack portData = new MeterController.MeterCallBack() {

        @Override
        public void Meter_Read(byte[] buffer, int size) {

            if (null != mMeterController) {
                // 将拿到(读取到)的数据封装到 SerialPortData类中
                SerialPortData serialPortData = new SerialPortData(buffer, size);
                intervalDoRead(serialPortData);						// 数据间有间隔
            }
        }

        @Override
        public void Meter_ChaoBiao(String result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void Meter_Adress(String result) {
            // TODO Auto-generated method stub

        }
    };

    /**
     * 串口数据类！！<p>
     * 两个参数：参数1：数据byte[],参数2：数据的长度
     */
    private class SerialPortData {

        /** 接收到的数据 */
        private byte[] dataByte;
        /** 接收到的数据的长度 */
        private int size;

        /**
         * 串口数据类
         * @param _dataByte 	接收到的数据
         * @param _size 		接收到的数据的长度
         */
        public SerialPortData(byte[] _dataByte, int _size) {
            this.setDataByte(_dataByte);
            this.setSize(_size);
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public byte[] getDataByte() {
            return dataByte;
        }

        public void setDataByte(byte[] dataByte) {
            this.dataByte = dataByte;
        }
    }

    /**
     * 根据接收到的数据"截取数据", 发送消息给Handler
     *
     * @param serialPortData 串口数据类
     */
    private void dealData(SerialPortData serialPortData) {

        byte[] b = serialPortData.getDataByte();
        //String result = getMsg(b, this).trim();   // 根据"数据标识"截取数据
        String result[] = ElectricMeterParsUtils.getMsg(b);


        if (result != null && result.length == 3 && result[2].length() > 0) {
            Message message = Message.obtain();
            message.obj = result;
            message.what = BROAD_READMETER_SUCCESS;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 好像是因为时间间隔所以要这样处理数据
     *
     * @param serialPortData  串口数据类！！
     */
    private void intervalDoRead(SerialPortData serialPortData) {
        if (serialPortData.getSize() > 0) {				// 从串口中获取到了数据

            switch (currentPro) {
                case Pro_Idle:
                    break;

                case Pro_One:
                    one = serialPortData.getDataByte();
                    dealData(serialPortData);
                    currentPro = Pro_Two;
                    break;

                case Pro_Two:
                    if (one != null) {
                        byte[] temp = new byte[one.length + serialPortData.getSize()];

                        if (temp.length - sendBuffer.length > 0) {

                            System.arraycopy(one, 0, temp, 0, one.length);
                            System.arraycopy(serialPortData.getDataByte(), 0, temp, one.length, serialPortData.getSize());
                            byte[] availableData = new byte[temp.length - sendBuffer.length];
                            System.arraycopy(temp, sendBuffer.length, availableData, 0, availableData.length);
                            System.out.println("avaliable_data:" + Tools.bytesToHexString(availableData));
                            SerialPortData data = new SerialPortData(availableData, availableData.length);

                            dealData(data);
                        }
                    }
                    currentPro = Pro_Idle;

                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 在"资源字符串"中，删除对应的"文件路径"
     *
     * @param parent        资源字符串
     * @param sub           要删除的"文件路径"
     * @return
     */
    private String deleteSubStr(String parent, String sub) {
        if (StringUtils.isNotEmpty(parent) && StringUtils.isNotEmpty(sub)) {

            if (parent.contains(sub + ",")) {                   // 如果是最后一个        包含
                return parent.replace(sub + ",", "");

            } else if (parent.contains("," + sub)) {            // 如果不是最后一个      包含
                return parent.replace("," + sub, "");

            } else if (parent.equals(sub.trim())){              // 如果只有一个
                return parent.replace(sub, "");
            }

        }
        return parent;
    }


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
            LogUtils.i("meterBeen.size()" + meterBeen.size());
            MyApplication.setMeterBean1List(meterBeen);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            closeDialog();
        }

        @Override
        public void onComplete() {
            if(isSave){
                showToast(isSaveSuccess?"保存成功" : "保存失败");
            }
            isSave = false;
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
            isSaveSuccess = (aLong>0 ? true : false);
            isSave = true;


            mCEtOldAssetNumbers.setText("");

            mTvUserName.setText("");
            mTvUserNumber.setText("");
            mTvUserAddr.setText("");
            mTvUserPhone.setText("");

            mCEtOldAddr.setText("");
            mCEtOldElectricity.setText("");

            mCEtNewAddr.setText("");
            mCEtNewElectricity.setText("");
            mCEtNewAssetNumbersScan.setText("");

            mMeterBean = new MeterBean1();
            mPicAdapter.clearPathList();

            //------------------------------------------------
            mBitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "no_preview_picture.png");

//            ColorDrawable drawable = new ColorDrawable(Color.parseColor("#AA0000"));
//            Bitmap bitmap = Bitmap.createBitmap(你需要的图片的宽度,你需要的图片的高度,Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            drawable.draw(canvas); //TODO  这样你就可以拿到这个bitmap了

//            mPvCamaraPhoto.disenable();// 把PhotoView当普通的控件，把触摸功能关掉
//            mPvCamaraPhoto.setImageBitmap(mBitmap);

            mPvBgImg.setImageBitmap(mBitmap);
            mPvBgImg.enable();

            //------------------------------------------------
        }

        @Override
        public void onError(@NonNull Throwable e) {
            closeDialog();
        }

        @Override
        public void onComplete() {

            // 从数据库中加载数据
            taskPresenter1.readDbToBean(readObserver, mMeteringSection);
        }
    };

    /**
     * 保存未匹配的资产编码数据
     *
     * rxjava -- 主线程
     */
    Observer addObserver = new Observer<Long>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull Long aLong) {
            LogUtils.i("aLong:" + aLong);
            Log.i("shen", "保存未匹配的资产编码数据情况：" + (aLong>0 ? "成功" : "失败"));
            //showToast("保存" + (aLong>0 ? "成功" : "失败"));
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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"

            if(mLlayoutParent.getVisibility() == View.VISIBLE && mIvBg.getVisibility() == View.VISIBLE){   // 缩小、隐藏那个预览布局
                mIvBg.startAnimation(out);
                setTitleIsShow(View.VISIBLE);
                mPvBgImg.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mLlayoutParent.setVisibility(View.GONE);

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
