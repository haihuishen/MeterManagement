package com.zh.metermanagement.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shen.sweetdialog.SweetAlertDialog;
import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.AreaBean;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.utils.StringUtils;
import com.zh.metermanagement.view.AutoCompleteTextViewWithData1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/8/4.
 */

public class SendEmailActivity extends BaseActivity implements View.OnClickListener {

    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;

    /** 加载数据 -- 按钮 */
    Button mBtnSendEamil;


    /** 邮件收件人 */
    AutoCompleteTextViewWithData1 mAcTvRecipients;
    /** 邮件标题/主题 */
    AutoCompleteTextViewWithData1 mAcTvSubject;
    /** 邮件内容 */
    AutoCompleteTextViewWithData1 mAcTvText;

    String recipients = "";
    String subject = "";
    String text = "";

    long count = 0;

    @Override
    public int getContentLayout() {
        return R.layout.activity_send_email;
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

        mTvTitle.setText("发送邮件");
    }

    @Override
    public void initView() {

        mAcTvRecipients = (AutoCompleteTextViewWithData1) findViewById(R.id.actv_recipients);
        mAcTvSubject = (AutoCompleteTextViewWithData1) findViewById(R.id.actv_subject);
        mAcTvText = (AutoCompleteTextViewWithData1) findViewById(R.id.actv_text);

        mBtnSendEamil = (Button) findViewById(R.id.btn_sendEamil);
    }

    @Override
    public void initListener() {
        mBtnSendEamil.setOnClickListener(this);
    }

    @Override
    public void initData() {

        mAcTvRecipients.initData(AutoCompleteTextViewWithData1.DATA_Name_RECIPIENTS);
        mAcTvSubject.initData(AutoCompleteTextViewWithData1.DATA_Name_SUBJECT);
        mAcTvText.initData(AutoCompleteTextViewWithData1.DATA_Name_TEXT);

        mAcTvRecipients.setText("987775154@qq.com");

        AreaBean bean = MyApplication.getAreaBean();
        String s = bean.getPowerSupplyBureau()
                + bean.getCourts()
                + "(" + bean.getTheMeteringSection() + ")";

        mAcTvSubject.setText("标题:" + s);
        mAcTvText.setText("内容:" + s);
    }



    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_back_left:

                finish();
                break;

            case R.id.btn_menu_right:
                break;

            case R.id.btn_sendEamil:

                recipients = mAcTvRecipients.getText().toString().trim();
                subject = mAcTvSubject.getText().toString().trim();
                text = mAcTvText.getText().toString().trim();
                if(StringUtils.isEmpty(recipients)){
                    showToast("请输入收件人");
                    return;
                }
                if(StringUtils.isEmpty(subject)){
                    showToast("请输入主题");
                    return;
                }
                if(StringUtils.isEmpty(text)){
                    showToast("请输入内容");
                    return;
                }
                SweetAlertDialog mSweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("请确认收件人")
                        .setContentText(recipients)
                        .setConfirmText("确认")
                        .setCancelText("取消")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                startSendEmailIntent();
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });

                // mSweetAlertDialog.setCancelable(false);
                mSweetAlertDialog.show();

                break;
        }
    }

    private void startSendEmailIntent(){
        mAcTvRecipients.addNewRecord(AutoCompleteTextViewWithData1.DATA_Name_RECIPIENTS, recipients);
        mAcTvSubject.addNewRecord(AutoCompleteTextViewWithData1.DATA_Name_SUBJECT, subject);
        mAcTvText.addNewRecord(AutoCompleteTextViewWithData1.DATA_Name_TEXT, text);

        // ACTION_SENDTO            发送邮件,不带附件
        // ACTION_SEND              发送邮件,带单个附件
        // ACTION_SEND_MULTIPLE    发送邮件,带多附件
        //Intent intent =new Intent(Intent.ACTION_SEND);
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipients});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        //邮件发送类型：带附件的邮件
        intent.setType("application/octet-stream");


        //File file = new File("/storage/emulated/0/电表换装/无工单/0412411105/导出Excel/
        // 城郊供电所凌霄公变(0412411105)户表改造台账.xls");

        File file = new File(MyApplication.getNoWorkOrderPath().getAreaExportPath());
        if(file.exists()){
            File[] files = file.listFiles();
            ArrayList<Uri> fileUriList = new ArrayList<Uri>();
            if(files.length > 0) {
                for (File f : files) {
                    fileUriList.add(Uri.fromFile(f));
                }
                LogUtils.i(fileUriList.toString());
                //data.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUriList);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    showToast("请下载一个Email客户端");
                }
            }else {
                showToast("请导出文件");
            }
        }else {
            showToast("请导出文件");
        }
    }

}
