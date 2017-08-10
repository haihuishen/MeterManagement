package com.zh.metermanagement.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zh.metermanagement.R;


/**
 * Created by shen on 10/30 0030.
 */
public abstract class ChangeDialog extends AlertDialog implements View.OnClickListener{

    public static final int EDIT_NUMBER = 1;
    public static final int EDIT_TEXT = 2;

    private TextView mTvTitle;
    private EditText mEtAddr;
    private EditText mEtElectricity;

    private Button mBtnConfirm;
    private Button mBtnCancel;

    private String mTitle = "";                   // 标题
    private int mInputType = 1;

    private int mEtAddrVisibility = View.VISIBLE;
    private int mEtElectricityVisibility = View.VISIBLE;

    protected ChangeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change);

        // 安卓弹出ProgressDialog进度框之后触摸屏幕就消失了的解决方法
        setCanceledOnTouchOutside(false);

        initView();
        initListener();
        initData();
    }

    private void initView(){

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mEtAddr = (EditText) findViewById(R.id.et_addr);
        mEtElectricity = (EditText) findViewById(R.id.et_electricity);

        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);


    }

    private void initListener(){
        mBtnConfirm.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    private void initData(){
        mTvTitle.setText(mTitle);

        mEtAddr.setVisibility(mEtAddrVisibility);
        mEtElectricity.setVisibility(mEtElectricityVisibility);

        mEtAddr.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        mEtElectricity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

//        if(mInputType == EDIT_NUMBER) {
//            mEtAddr.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//            mEtElectricity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//        }else if(mInputType == EDIT_TEXT) {
//            mEtAddr.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
//            mEtElectricity.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
//        }

        // 设置此窗口的设置
        Window window = this.getWindow();
        // window.setContentView(R.layout.dialog_change);   // 最好不要弄这个，反正是不成功的
        WindowManager.LayoutParams params = window.getAttributes();
        // params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        // params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        // params.dimAmount=0.5f;//设置对话框的透明程度背景(非布局的透明度)

        // params.flags = 0x01810100; 这个是能使用"edittext"和"button"
        // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN  ==>0x00000100 ==> 设置了这个;弹出输入法时，不会将"对话框"顶上去
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH |
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        System.out.println("params.flags:"+ Integer.toHexString(params.flags));
        // params.alpha = (float) 0.5;  //透明度(背景)     （0.0-1.0）
        window.setAttributes(params);

    }

    /**
     * 设置"标题"
     * @param title 标题
     */
    public void setTitle(String title){
        if(mTvTitle != null)                // 在子类中，new 了窗口，马上设置这个,会是"空指针";因为 .show() 之后才 onCreate
            mTvTitle.setText(title);
        else
            mTitle = title;
    }

//    /**
//     * 设置"编辑框，输入类型"
//     * number : InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL
//     * text   : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL
//     * @param inputType    编辑框，输入类型
//     */
//    public void setEditInputType(int inputType){
//        if(inputType == EDIT_NUMBER) {
//            if (mEtAddr != null && mEtElectricity != null) {                // 在子类中，new 了窗口，马上设置这个,会是"空指针";因为 .show() 之后才 onCreate
//                mEtAddr.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                mEtElectricity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//            }else
//                mInputType = inputType;
//        }else if(inputType == EDIT_TEXT) {
//            if (mEtAddr != null && mEtElectricity != null) {               // 在子类中，new 了窗口，马上设置这个,会是"空指针";因为 .show() 之后才 onCreate
//                mEtAddr.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
//                mEtElectricity.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
//            }
//            else
//                mInputType = inputType;
//        }
//    }

    /**
     * 设置电表止码输入框是否可见
     * @param isVisibility
     */
    public void setEtElectricityVisibility(int isVisibility){
        mEtElectricityVisibility = isVisibility;
    }

    /**
     * 设置地址输入框是否可见
     * @param isVisibility
     */
    public void setEtAddrVisibility(int isVisibility){
        mEtAddrVisibility = isVisibility;
    }

    /**
     * 获取"地址输入框"是否可见
     * @return
     */
    public int getmEtAddrVisibility() {
        return mEtAddrVisibility;
    }

    /**
     * 获取"电表止码"是否可见
     * @return
     */
    public int getmEtElectricityVisibility() {
        return mEtElectricityVisibility;
    }

    /**
     * 点击"确定按钮"干的事情
     * @param addr                  地址
     * @param electricity           电表止码
     */
    public abstract void confirm(ChangeDialog changeDialog, String addr, String electricity);
    /**
     * 点击"取消按钮"干的事情
     * @return View
     */
    public abstract void cancel(ChangeDialog changeDialog);


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_confirm:
                confirm(this, mEtAddr.getText().toString(), mEtElectricity.getText().toString());
                break;
            case R.id.btn_cancel:
                cancel(this);
                break;
            default:
                break;
        }
    }
}
