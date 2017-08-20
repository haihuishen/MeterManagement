package com.zh.passworddialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/8/14.
 */

public class PasswordDialog extends AlertDialog {

    private SecurityCodeView editText;
    private TextView text;
    private InputCompleteListener mInputCompleteListener;

    public PasswordDialog(@NonNull Context context, InputCompleteListener inputCompleteListener) {
        super(context);
        mInputCompleteListener = inputCompleteListener;
    }

    protected PasswordDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected PasswordDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_password);     // 窗口的布局样式

        // dialog.setCancelable(false);
        // dialog弹出后会点击屏幕或物理返回键，dialog不消失
        // dialog.setCanceledOnTouchOutside(false);
        // dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
        setCanceledOnTouchOutside(false);

        initView();
        initListener();



    }

    private void initView() {
        editText = (SecurityCodeView) findViewById(R.id.scv_edittext);
        text = (TextView) findViewById(R.id.tv_text);


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

    private void initListener() {
        editText.setInputCompleteListener(new SecurityCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
//                Log.i("shen", "验证码是：" + editText.getEditContent());
//                if (!editText.getEditContent().equals("1234")) {
//                    text.setText("验证码输入错误");
//                    text.setTextColor(Color.RED);
//                }
                mInputCompleteListener.inputComplete(PasswordDialog.this, editText, text);
            }

            @Override
            public void deleteContent(boolean isDelete) {
//                if (isDelete){
//                    text.setText("输入验证码表示同意《用户协议》");
//                    text.setTextColor(Color.BLACK);
//                }
                mInputCompleteListener.deleteContent(isDelete, text);
            }
        });
    }

    public interface InputCompleteListener {

        /**
         * 输入全部的密码后会马上调用这个方法
         *
         * @param dialog
         * @param securityCodeView      密码编辑框
         * @param tvPromptContent         提示的文本
         */
        void inputComplete(PasswordDialog dialog, SecurityCodeView securityCodeView, TextView tvPromptContent);



        /**
         * 删除内容后会调用
         *
         * @param isDelete              有没有删除
         * @param tvPromptContent         提示的文本
         */
        void deleteContent(boolean isDelete, TextView tvPromptContent);
    }
}
