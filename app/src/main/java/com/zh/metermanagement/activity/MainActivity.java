package com.zh.metermanagement.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.serialport.MeterController;
import android.serialport.Tools;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.shen.sweetdialog.SweetAlertDialog;
import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.config.MeterAgreement;
import com.zh.metermanagement.utils.ElectricMeterParsUtils;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.utils.TimeUtils;
import com.zh.metermanagement.view.ClearEditText;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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


    private long exitTime;

//    ListView mLvMeterInfo;
//
//    private MyAdapter myAdapter;
//    private List<MeterBean> mMeterBeanList;



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
    /** 采集器资产编号 -- 编辑框*/
    private ClearEditText mCEtCollectorAssetNumbersScan;
    /** 采集器资产编号(扫描) -- 按钮*/
    private Button mBtnCollectorAssetNumbersScan;

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

    ReadMeterAgreementTask mReadMeterAgreementTask;



    private MeterBean mMeterBean = new MeterBean();
    //--------------------------------------------------------

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BROAD_READMETER_FAIL: 												// 抄表失败
                    //mBeepManager.play();
                    promptTone();
                    showToast("抄表失败");
                    break;

                case BROAD_READMETER_SUCCESS: 											// 抄表成功

                    promptTone();                                            // 发出声音 -- 提示音
                    //resultData = (String) msg.obj;
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
        return R.layout.dialog_password;
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
        mBtnBack.setVisibility(View.GONE);
        mBtnMenu.setVisibility(View.VISIBLE);

        mTvTitle.setText("主界面");
    }

    @Override
    public void initView() {
        //mLvMeterInfo = (ListView) findViewById(R.id.lv_meterInfo);


        //---------------------
        mCEtGone = (ClearEditText) findViewById(R.id.cet_gone);

        mCEtOldAssetNumbers = (ClearEditText) findViewById(R.id.cet_oldAssetNumbers);
        mBtnOldAssetNumbers = (Button) findViewById(R.id.btn_oldAssetNumbers);
        mTvSequenceNumber = (TextView) findViewById(R.id.tv_sequenceNumber);
        mTvUserName = (TextView) findViewById(R.id.tv_userName);
        mTvUserNumber = (TextView) findViewById(R.id.tv_userNumber);
        mTvUserAddr = (TextView) findViewById(R.id.tv_userAddr);

        mCEtOldAddr = (ClearEditText) findViewById(R.id.cet_oldAddr);
        mCEtOldElectricity = (ClearEditText) findViewById(R.id.cet_oldElectricity);
        mBtnOldElectricity = (Button) findViewById(R.id.btn_oldElectricity);
        mCEtNewAddr = (ClearEditText) findViewById(R.id.cet_newAddr);
        mCEtNewAssetNumbersScan = (ClearEditText) findViewById(R.id.cet_newAssetNumbersScan);
        mBtnNewAssetNumbersScan = (Button) findViewById(R.id.btn_newAssetNumbersScan);
        mCEtNewElectricity = (ClearEditText) findViewById(R.id.cet_newElectricity);
        mBtnNewElectricity = (Button) findViewById(R.id.btn_newElectricity);
        mCEtCollectorAssetNumbersScan = (ClearEditText) findViewById(R.id.cet_collectorAssetNumbersScan);
        mBtnCollectorAssetNumbersScan = (Button) findViewById(R.id.btn_collectorAssetNumbersScan);

        mBtnSave = (Button) findViewById(R.id.btn_save);
    }

    @Override
    public void initListener() {

        mBtnOldAssetNumbers.setOnClickListener(this);
        mBtnOldElectricity.setOnClickListener(this);
        mBtnNewAssetNumbersScan.setOnClickListener(this);
        mBtnNewElectricity.setOnClickListener(this);
        mBtnCollectorAssetNumbersScan.setOnClickListener(this);

        mBtnSave.setOnClickListener(this);

    }

    @Override
    public void initData() {

        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {

                mCEtGone.setText("");
                if(mCurrentScanBtnId == R.id.btn_oldAssetNumbers){              // 旧表资产编号(二维扫描)
                    mCEtOldAssetNumbers.setText(data);

                    LogUtils.i("MyApplication.getMeterBeanList().size()" + MyApplication.getMeterBeanList().size());
                    boolean isFind = false;
                    for(MeterBean bean : MyApplication.getMeterBeanList()){

//                        Log.i("shen", "bean.getOldAssetNumbers():"+bean.getOldAssetNumbers());
//                        Log.i("shen", "data:"+data);
//                        Log.i("shen", "---------------------------------------------------------");

                        if(bean.getOldAssetNumbers().equals(data)){
                            mTvUserName.setText(bean.getUserName());
                            mTvUserNumber.setText(bean.getUserNumber());
                            mTvUserAddr.setText(bean.getUserAddr());

                            mMeterBean = bean;

                            isFind = true;
                            break;
                        }
                    }

                    if(!isFind){

                        SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("提示")
                                .setContentText("该电表资产编码无匹配的用户，\n请通知供电所相关人员")
                                .setConfirmText("确认")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });


                        dialog.setCancelable(false);
                        dialog.show();
                    }

                }else if(mCurrentScanBtnId == R.id.btn_newAssetNumbersScan){    // 新表资产编号(二维扫描)
                    mCEtNewAssetNumbersScan.setText(data);

                }else if(mCurrentScanBtnId == R.id.btn_collectorAssetNumbersScan){    // 采集器资产编号(二维扫描)
                    mCEtCollectorAssetNumbersScan.setText(data);
                }

            }
        });


        //------------------------- 红外 ----------------------------------

        mMeterController = MeterController.getInstance();
        mMeterController.Meter_Open(portData, this);


        showLoadingDialog("","正在加载数据...");
        // 从数据库中加载数据
        taskPresenter.readDbToBean(readObserver);


        mCEtOldAssetNumbers.setEnabled(false);

        mTvUserName.setText("");
        mTvUserNumber.setText("");
        mTvUserAddr.setText("");

        mCEtOldAddr.setEnabled(false);
        mCEtOldElectricity.setEnabled(false);

        mCEtNewAddr.setEnabled(false);
        mCEtNewElectricity.setEnabled(false);
        mCEtNewAssetNumbersScan.setEnabled(false);

        mCEtCollectorAssetNumbersScan.setText("");


//        mMeterBeanList = new ArrayList<MeterBean>();
//        myAdapter = new MyAdapter(MainActivity.this, mMeterBeanList);
//        mLvMeterInfo.setAdapter(myAdapter);
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

//        if(mMeterController != null)
//            mMeterController.Meter_Close();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back_left:

                finish();
                break;

            case R.id.btn_menu_right:


                // PopupMenu 菜单弹出类 -- 弹出的地方在 "rightBtn" 的位置
                final PopupMenu popupMenu = new PopupMenu(MainActivity.this, mBtnMenu);
                // 填充菜单
                popupMenu.getMenuInflater().inflate(R.menu.metermanagement_menu, popupMenu.getMenu());
                // 每个菜单项 设置 "点击事件"
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.menu_import) {     // "统计" 菜单
                            showLoadingDialog("","导入中...");
                            taskPresenter.importExcelToDb(importObserver);

                        }else if(item.getItemId() == R.id.menu_read){
                            showLoadingDialog("","加载中...");
                            taskPresenter.readDbToBean(readObserver);

                        }else if(item.getItemId() == R.id.menu_statistics){
                            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                            startActivity(intent);
                        }

                        popupMenu.dismiss();  // 点击菜单项过后隐藏
                        return true;
                    }
                });
                popupMenu.show();
                break;


            case R.id.btn_oldAssetNumbers:                         // 旧表资产编号(二维扫描)
                mCEtOldAssetNumbers.setText("");

                mTvUserName.setText("");
                mTvUserNumber.setText("");
                mTvUserAddr.setText("");

                mCEtOldAddr.setText("");
                mCEtOldElectricity.setText("");

                mCEtNewAddr.setText("");
                mCEtNewElectricity.setText("");
                mCEtNewAssetNumbersScan.setText("");

                mCEtCollectorAssetNumbersScan.setText("");


                mCurrentScanBtnId = R.id.btn_oldAssetNumbers;
                scanDecode.stopScan();
                scanDecode.starScan();                              //启动扫描
                break;

            case R.id.btn_oldElectricity:                         // 旧电能表止码(红外扫描)
                mCurrentReadBtnId = R.id.btn_oldElectricity;

                String oldAssetNumbers = mCEtOldAssetNumbers.getText().toString().trim();
                if(!TextUtils.isEmpty(oldAssetNumbers)) {
                    String addr = parseAddr(oldAssetNumbers);
                    startReadMeter(addr);
                }else{
                    showToast("请输入--旧表资产编号");
                }
                break;

            case R.id.btn_newAssetNumbersScan:                     // 新表资产编号(二维扫描)
                mCurrentScanBtnId = R.id.btn_newAssetNumbersScan;
                scanDecode.stopScan();
                scanDecode.starScan();                              //启动扫描
                break;

            case R.id.btn_newElectricity:                         // 新电能表止码(红外扫描)
                mCurrentReadBtnId = R.id.btn_newElectricity;

                String newAssetNumbers = mCEtNewAssetNumbersScan.getText().toString().trim();
                if(!TextUtils.isEmpty(newAssetNumbers)) {
                    String addr = parseAddr(newAssetNumbers);
                    startReadMeter(addr);
                }else{
                    showToast("请输入--新表资产编号");
                }

                break;

            case R.id.btn_collectorAssetNumbersScan:              // 采集器资产编号(二维扫描)
                mCurrentScanBtnId = R.id.btn_collectorAssetNumbersScan;
                scanDecode.stopScan();
                scanDecode.starScan();                              //启动扫描
                break;

            case R.id.btn_save:
                saveDate();

                break;

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

        if(str.length() == 14){
            addr = str.substring(7, 14);

        }else if(str.length() == 24){
            addr = str.substring(12, 12);
        }

        while (addr.length() < 12){
            str = "A" + str;
        }

        return addr;
    }

    /**
     * 保存数据
     *
     */
    private void saveDate() {


        mCEtOldAddr.setText("old");
        mCEtOldElectricity.setText("1");
        mCEtNewAddr.setText("new");
        mCEtNewElectricity.setText("2");

        String oldAssetNumbers = mCEtOldAssetNumbers.getText().toString().trim();           // 旧表资产编号
        String oldAddr = mCEtOldAddr.getText().toString().trim();                           // 旧电能表表地址
        String oldElectricity = mCEtOldElectricity.getText().toString().trim();             // 旧电能表止码
        String newAddr = mCEtNewAddr.getText().toString().trim();                           // 新电能表表地址
        String newAssetNumbersScan = mCEtNewAssetNumbersScan.getText().toString().trim();   // 新表资产编号
        String newElectricity = mCEtNewElectricity.getText().toString().trim();             // 新电能表止码
        String collectorAssetNumbersScan = mCEtCollectorAssetNumbersScan.getText().toString().trim(); // 采集器资产编号

        if(TextUtils.isEmpty(oldAssetNumbers)){
            showToast("请输入--旧表资产编号");
            return;
        }else if(TextUtils.isEmpty(oldAddr)){
            showToast("请输入--旧电能表表地址");
            return;
        }else if(TextUtils.isEmpty(oldElectricity)){
            showToast("请输入--旧电能表止码");
            return;
        }else if(TextUtils.isEmpty(newAddr)){
            showToast("请输入--新电能表表地址");
            return;
        }else if(TextUtils.isEmpty(newAssetNumbersScan)){
            showToast("请输入--新表资产编号");
            return;
        }else if(TextUtils.isEmpty(newElectricity)){
            showToast("请输入--新电能表止码");
            return;
        }else if(TextUtils.isEmpty(collectorAssetNumbersScan)){
            showToast("请输入--采集器资产编号");
            return;
        }



        mMeterBean.setOldAssetNumbersScan(oldAssetNumbers);
        mMeterBean.setOldAddr(oldAddr);
        mMeterBean.setOldAddrAndAsset(true);
        mMeterBean.setOldElectricity(oldElectricity);
        mMeterBean.setNewAddr(newAddr);
        mMeterBean.setNewAddrAndAsset(true);
        mMeterBean.setNewAssetNumbersScan(newAssetNumbersScan);
        mMeterBean.setNewElectricity(newElectricity);
        mMeterBean.setCollectorAssetNumbersScan(collectorAssetNumbersScan);
        mMeterBean.setTime(TimeUtils.getCurrentTimeRq());
        mMeterBean.setFinish(true);

        LogUtils.i("mMeterBean.toString():" + mMeterBean.toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put("oldAddr", mMeterBean.getOldAddr());
        contentValues.put("oldAddrAndAsset", mMeterBean.isOldAddrAndAsset());
        contentValues.put("oldAssetNumbersScan", mMeterBean.getOldAssetNumbersScan());
        contentValues.put("oldElectricity", mMeterBean.getOldElectricity());
        contentValues.put("newAddr", mMeterBean.getNewAddr());
        contentValues.put("newAddrAndAsset", mMeterBean.isNewAddrAndAsset());
        contentValues.put("newAssetNumbersScan", mMeterBean.getNewAssetNumbersScan());
        contentValues.put("newElectricity", mMeterBean.getNewElectricity());

        contentValues.put("collectorAssetNumbersScan", mMeterBean.getCollectorAssetNumbersScan());
        contentValues.put("time", mMeterBean.getTime());
        contentValues.put("isFinish", mMeterBean.isFinish());

        taskPresenter.saveData(saveObserver, contentValues,
                Constant.TABLE_METERINFO_STR_oldAssetNumbers + "=?", new String[]{oldAssetNumbers});

    }

    /**
     * 发出声音 -- 提示音
     */
    private void promptTone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

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


            mMeterController.openSerialPort("/dev/ttyMT2", 1200, 8, 1, 1);        // 打开串口
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
                mMeterController.openSerialPort("/dev/ttyMT2", 2400, 8, 1, 1);        // 打开串口
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


        // 发送数据 设置延迟10毫秒
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        Log.i("info", "sendBuffer === " + Tools.bytesToHexString(sendBuffer));

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
     * 将数据从excel中读取到db
     * rxjava -- 主线程
     */
    Observer importObserver = new Observer<Long>() {

        @Override
        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

        }

        @Override
        public void onNext(@io.reactivex.annotations.NonNull Long aLong) {
        }

        @Override
        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
            closeDialog();
        }

        @Override
        public void onComplete() {
            closeDialog();

            showLoadingDialog("","加载中...");
            taskPresenter.readDbToBean(readObserver);
        }
    };


    /**
     * 将数据从数据库读取到内存中
     * rxjava -- 主线程
     */
    Observer readObserver = new Observer<List<MeterBean>>() {

        @Override
        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull List<MeterBean> meterBeen) {
            LogUtils.i("meterBeen.size()" + meterBeen.size());
            MyApplication.setMeterBeanList(meterBeen);
            closeDialog();
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
     * 保存数据！
     * rxjava -- 主线程
     */
    Observer saveObserver = new Observer<Long>() {

        @Override
        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

        }

        @Override
        public void onNext(@NonNull Long aLong) {
            LogUtils.i("aLong:" + aLong);
            Log.i("shen", "保存情况：" + (aLong>0 ? "成功" : "失败"));
            showToast("保存" + (aLong>0 ? "成功" : "失败"));
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

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()- exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
                MyApplication.exitApp();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
