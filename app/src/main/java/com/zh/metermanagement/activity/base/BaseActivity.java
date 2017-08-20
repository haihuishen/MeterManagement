package com.zh.metermanagement.activity.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shen.sweetdialog.SweetAlertDialog;
import com.zh.metermanagement.R;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.trasks.TaskPresenterImpl;
import com.zh.metermanagement.trasks.TaskPresenterImpl1;
import com.zh.metermanagement.utils.BeepManager;


import java.util.List;


public abstract class BaseActivity extends AppCompatActivity {

	/************************ 标题控件 ***************************/
	/** 标题 */
	TextView mTvBaseTitle;
	/** 返回按钮 -- 按钮 */
	Button mBtnBaseBack;
	/** 菜单按钮 -- 按钮 */
	Button mBtnBaseMenu;
	/** 将 布局填充成 "控件"， 将控件加载到"界面"中 */
	RelativeLayout mLayoutTitle;

	/** 将 布局填充成 "控件"， 将控件加载到"界面"中 */
	LinearLayout mLayoutContainer;


	private Toast mToast;
	private SweetAlertDialog mSweetAlertDialog = null;
	private Context context;
	public TaskPresenterImpl taskPresenter;
	public TaskPresenterImpl1 taskPresenter1;

	/** 将 布局填充成 "控件"， 将控件加载到"界面"中 */
	public abstract int getContentLayout();

	public abstract void initTitleListener(TextView tvTitle, Button btnBack, Button btnMenu);
	public abstract void initTitleData(TextView tvTitle, Button btnBack, Button btnMenu);

	public abstract void initView();
	public abstract void initListener();
	public abstract void initData();

	public BeepManager mBeepManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_title);

		MyApplication.listActivity.add(this);
		context = this;
		taskPresenter = new TaskPresenterImpl(this);
		taskPresenter1 = new TaskPresenterImpl1(this);

		initTitleView();
		initTitleListener(mTvBaseTitle, mBtnBaseBack, mBtnBaseMenu);
		initTitleData(mTvBaseTitle, mBtnBaseBack, mBtnBaseMenu);


		// 将 布局填充成 "控件"， 将控件加载到"界面"中
		View contentV = LayoutInflater.from(this).inflate(getContentLayout(), null);
		// 这里要设置铺满(不然默认为"包裹")
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		contentV.setLayoutParams(params);
		mLayoutContainer.addView(contentV);

		mBeepManager = new BeepManager(this,true,false);

		initView();
		initListener();
		initData();
	}


	/**
	 *
	 * @param isShow  View.VISIBLE / View.GONE / View.INVISIBLE
	 */
	public void setTitleIsShow(int isShow){
		if(mLayoutTitle != null)
			mLayoutTitle.setVisibility(isShow);
	}


	/**
	 * 初始化"标题"
	 */
	private void initTitleView() {
		mTvBaseTitle = (TextView) findViewById(R.id.tv_title);
		mBtnBaseBack = (Button) findViewById(R.id.btn_back_left);
		mBtnBaseMenu = (Button) findViewById(R.id.btn_menu_right);

		mLayoutTitle = (RelativeLayout) findViewById(R.id.layout_titlelayout_title);
		mLayoutContainer = (LinearLayout) findViewById(R.id.layout_container);

	}


	/**
	 * 如果 Toast对象存在"正在显示"      			<br>
	 * 就"不等其显示完"再"显示另一个Toast"			<br>
	 * 直接修改"toast内部文本"						<br>
	 * 
	 * @param text 待显示的文字
	 */
	public void showToast(String text) {
		// 判断程序是否在前台运行 如果程序是在后台运行 不显示toast
		if (!isTopActivity()) {
			return;
		}
		if (mToast != null) {
			mToast.setText(text);
		} else {
			mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		}
		//mToast.setGravity(Gravity.CENTER, 0, 0);	// 这里显示在中间 -- 可以去掉,就是默认位置
		mToast.show(); 								// 显示toast信息
	}



	/**
	 * 程序是否正在前台运行
	 * 
	 * @return
	 */
	public boolean isTopActivity() {
		/*
		 * System.out.println("**********************top packageName:" +
		 * getInstance().getPackageName());
		 */
		ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			/*
			 * System.out.println("*********************curr packageName:" +
			 * tasksInfo.get(0).topActivity.getPackageName());
			 */
			// 应用程序位于堆栈的顶层
			if (getApplication().getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int id){
		View view = super.findViewById(id);
		return (T) view ;
	}


	public void showLoadingDialog(String text, String content) {

		mSweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
				//.setTitleText("正在加载");
				.setTitleText(text)
				.setContentText(content);

		mSweetAlertDialog.setCancelable(false);
		mSweetAlertDialog.show();
	}

	public void updataLoadingDialog(String text, String content){
		if(mSweetAlertDialog != null && mSweetAlertDialog.isShowing()){
			mSweetAlertDialog.setTitleText(text)
					.setContentText(content);
		}
	}

	public void closeDialog(){

		if(mSweetAlertDialog != null && mSweetAlertDialog.isShowing()){
			mSweetAlertDialog.dismiss();
		}
	}

	public boolean isShowDialog(){

		if(mSweetAlertDialog != null) {
			return mSweetAlertDialog.isShowing();
		}else  {
			return false;
		}
	}
	
	public Context getContext(){
		return context;
	}




	// --------------------------点击"空白地方"隐藏输入法-----------------------------
	// 获取点击事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();                      // 获得焦点的控件
			if (isHideInput(view, ev)) {
				HideSoftInput(view.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	// 判定是否需要隐藏
	private boolean isHideInput(View v, MotionEvent ev) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	// 隐藏软键盘
	private void HideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);     // 隐藏输入法
		}
	}


}
