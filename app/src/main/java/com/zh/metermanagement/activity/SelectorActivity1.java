package com.zh.metermanagement.activity;


import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.utils.VibratorUtil;
import com.zh.passworddialog.SecurityCodeView;
import com.zh.passworddialog.PasswordDialog;

import java.util.ArrayList;

/**
 * 选择业务类型
 */
public class SelectorActivity1 extends BaseActivity implements OnClickListener {

	private long exitTime;



	/** 标题 */
	TextView mTvTitle;
	/** 返回按钮 -- 按钮 */
	Button mBtnBack;
	/** 菜单按钮 -- 按钮 */
	Button mBtnMenu;


	/** 按键ID数组 */
	private int[] mLayoutIds = {
			R.id.layout_loadData, 						// 导入数据
			R.id.layout_noWorkOrder, 					// 无工单换表
			R.id.layout_haveWorkOrder,					// 有工单换表
			R.id.layout_scatteredReplaceMeter,			// 零散换表
			R.id.layout_batchNewMeter,					// 批量新装电表
			R.id.layout_scatteredNewMeter,				// 零散新装电表
			R.id.layout_acceptance						// 数据验收

	};

	/** 显示当前软件版本号 */
	private TextView mTvAppVersion;

	private int mCurrIndex = -1;

	/** Layout 集合 */
	private ArrayList<LinearLayout> mLayoutList;


	@Override
	public int getContentLayout() {
		return R.layout.activity_selector1;
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
		mBtnMenu.setVisibility(View.GONE);

		mTvTitle.setText("业务类型");
	}

	/**
	 * 初始化控件
	 */
	@Override
	public void initView(){

	}

	@Override
	public void initListener() {

	}

	@Override
	public void initData() {

		mLayoutList = new ArrayList<LinearLayout>();
		LinearLayout layout = null ;

		for(int i = 0; i < mLayoutIds.length ; i++){			// 获取九个区域的控件
			layout = findView(mLayoutIds[i]);
			mLayoutList.add(layout);
			layout.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		scaleAnimation(v);
	}








	/**
	 * 控件的伸缩
	 * @param view
	 */
	public void scaleAnimation(final View view){

		/*
		 * 第一个参数 fromX x轴起始大小(这个大小指倍数，它内部会用这个倍数去乘实际像素)
		 * 第二参数 toX 轴截止大小(若起始大小=截止大小就是指x轴不伸缩)
 		 * 第三个参数fromY Y轴的起始大小
		 * 第四个参数toY 轴的截止大小
		 * 第五个参数pivotXType X轴的原点的类型（相对于自己而言还是相对于父容器而言）
		 * 第六个参数pivotXValue 开始伸缩时的X轴的原点(例:0.5就是指以图片宽度的二分之一的位置作为X轴的原点)
		 * 第七个参数pivotYType Y轴的原点的类型
		 * 第八个参数pivotYValue 开始伸缩时的Y轴的原点
		 */
		Animation sa = new ScaleAnimation(1f,0.5f, 1f, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(200);
		//sa.setRepeatCount(1);
		sa.setRepeatMode(Animation.RESTART);	//设置重复方式 Animation.RESTART代表重新开始播放
		sa.setFillBefore(true); 				//设置是否保持动画结束后的状态
		//sa.setFillAfter(true);					//动画执行完毕后是否停在结束时的状态
		//sa.setFillEnabled(true);

		//绑定动画事件
		sa.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// 动画开始播放
				//Log.d("shen","动画开始播放");
				for(LinearLayout layout : mLayoutList){
					layout.setEnabled(false);
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				// 动画重复中
				//Log.d("shen","动画重复中");
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束
				//Log.d("shen","动画结束");
				for(LinearLayout layout : mLayoutList){
					layout.setEnabled(true);
				}

				Intent intent;

				switch (view.getId()) {
					case R.id.layout_loadData:                              // 加载数据
						//showToast("无工单！");

						//showToast("加载数据！");
						new PasswordDialog(getContext(), new PasswordDialog.InputCompleteListener() {

							@Override
							public void inputComplete(PasswordDialog dialog, SecurityCodeView securityCodeView, TextView tvPromptContent) {
								Log.i("shen", "密码是：" + securityCodeView.getEditContent());
								if (!securityCodeView.getEditContent().equals("1234")) {
									tvPromptContent.setText("密码输入错误");
									tvPromptContent.setTextColor(Color.RED);
									VibratorUtil.Vibrate(SelectorActivity1.this, new long[]{100,100,100,100}, false);   //震动100ms

									Animation anim = AnimationUtils.loadAnimation(SelectorActivity1.this, R.anim.myanim);
									tvPromptContent.startAnimation(anim);

								}else {
									showToast("密码正确");
									dialog.dismiss();
									Intent intent = new Intent(SelectorActivity1.this, SortLoadDataActivity.class);
									startActivity(intent);
								}
							}

							@Override
							public void deleteContent(boolean isDelete, TextView tvPromptContent) {
								if (isDelete){
									tvPromptContent.setText("");
									tvPromptContent.setTextColor(Color.BLACK);
								}
							}
						}).show();


						break;

					case R.id.layout_noWorkOrder: 							// 无工单
						//showToast("无工单！");
						intent = new Intent(SelectorActivity1.this, SortActivity.class);
						startActivity(intent);
						break;

					case R.id.layout_haveWorkOrder:							// 有工单
						showToast("有工单！");

						break;

					case R.id.layout_scatteredReplaceMeter:					// 零散换表
						showToast("零散换表！");

						break;

					case R.id.layout_batchNewMeter:							// 批量新装电表
						showToast("批量新装电表！");

						break;

					case R.id.layout_scatteredNewMeter:						// 零散新装电表
						showToast("零散新装电表！");

						break;

					case R.id.layout_acceptance:							// 数据验收
						showToast("数据验收！");

						break;

					default:
						break;
				}
			}
		});

		view.startAnimation(sa);
	}


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
