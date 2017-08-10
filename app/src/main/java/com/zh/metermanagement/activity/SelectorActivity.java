package com.zh.metermanagement.activity;


import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.application.MyApplication;

import java.util.ArrayList;

/**
 * 功能选择界面
 */
public class SelectorActivity extends BaseActivity implements OnClickListener {

	private long exitTime;


	/** 标题 */
	TextView mTvTitle;
	/** 返回按钮 -- 按钮 */
	Button mBtnBack;
	/** 菜单按钮 -- 按钮 */
	Button mBtnMenu;


	/** 按键ID数组 */
	private int[] mLayoutIds = {
			R.id.layout_loadData, 				// 加载数据
			R.id.layout_newElectricMeter, 		// 新装电表
			R.id.layout_replaceMeter,			// 换表
			R.id.layout_newCollector, 			// 新装采集器
			R.id.layout_generateReports, 			// generate_reports
			R.id.layout_query,					// 查询
			R.id.layout_directionsForUse, 		// 使用手册
			R.id.layout_selector_8,
			R.id.layout_selector_9};


	/** 显示当前软件版本号 */
	private TextView mTvAppVersion;

	private int mCurrIndex = -1;

	/** Layout 集合 */
	private ArrayList<LinearLayout> mLayoutList;


	@Override
	public int getContentLayout() {
		return R.layout.activity_selector;
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

		mTvTitle.setText("功能选择");
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
						//showToast("加载数据！");
						intent = new Intent(SelectorActivity.this, LoadDataActivity.class);
						startActivity(intent);
						break;

					case R.id.layout_newElectricMeter: 						// 新装电表
						showToast("新装电表！");
						break;

					case R.id.layout_replaceMeter:							// 换表
						//showToast("换表！");
						intent = new Intent(SelectorActivity.this, ReplaceMeterActivity.class);
						startActivity(intent);
						break;

					case R.id.layout_newCollector:				 			// 新装采集器
						//showToast("新装采集器！");
						intent = new Intent(SelectorActivity.this, NewCollectorActivity.class);
						startActivity(intent);
						break;

					case R.id.layout_generateReports:				 			// generate_reports
						//showToast("generate_reports！");
						intent = new Intent(SelectorActivity.this, GenerateReportsActivity.class);
						startActivity(intent);
						break;

					case R.id.layout_query:									// 查询
						//showToast("查询！");
						intent = new Intent(SelectorActivity.this, StatisticsActivity.class);
						startActivity(intent);

						break;
					case R.id.layout_directionsForUse:				 		// 使用手册
						//showToast("使用手册！");
						intent = new Intent(SelectorActivity.this, DirectionsForUseActivity.class);
						startActivity(intent);

						break;

					case R.id.layout_selector_8:                            // 广播抄表

						break;

					case R.id.layout_selector_9:

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


//	/**
//	 *  物理键监听按键
//	 */
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.d("CaoBiaoChose", "press keyCode " + keyCode);
////		// 点击了返回 按钮
////		switch (keyCode) {
////
////			case 8:// 按键1 顺序抄表
////				mLayoutList.get(0).setFocusable(true);
////				mLayoutList.get(0).requestFocus();
////				orderCaoBiao();
////				mCurrIndex = 0;
////				break;
////			case 9:// 按键2 抄未抄户
////				mLayoutList.get(1).setFocusable(true);
////				mLayoutList.get(1).requestFocus();
////				mCurrIndex = 1;
////				startMeiChao();
////				break;
////			case 10:// 按键3 抄表统计
////				mLayoutList.get(2).setFocusable(true);
////				mLayoutList.get(2).requestFocus();
////				mCurrIndex = 2;
////				caoBiaoCount();
////				break;
////			case 11:// 按键4 查询信息
////				mLayoutList.get(3).setFocusable(true);
////				mLayoutList.get(3).requestFocus();
////				mCurrIndex = 3;
////				startQuery();
////				break;
////			case 12:// 按键5 集抄
////				mLayoutList.get(4).setFocusable(true);
////				mLayoutList.get(4).requestFocus();
////				mCurrIndex = 4;
//////			startFocusMeterRead();
////				Toast.makeText(this, "该功能为定制功能！", Toast.LENGTH_SHORT).show();
////				break;
////			case 13:// 按键6 蓝牙设置
////				mLayoutList.get(5).setFocusable(true);
////				mLayoutList.get(5).requestFocus();
////				mCurrIndex = 5;
////				startSetting();
////				break;
////			case 92://up
////				mCurrIndex--;
////				if(mCurrIndex <0){
////					mCurrIndex = 0;
////				}
////				mLayoutList.get(mCurrIndex).setFocusable(true);
////				mLayoutList.get(mCurrIndex).requestFocus();
////
////				break;
////
////			case 93://down
////
////				mCurrIndex++;
////				if(mCurrIndex >5){
////					mCurrIndex = 5;
////				}
////				mLayoutList.get(mCurrIndex).setFocusable(true);
////				mLayoutList.get(mCurrIndex).requestFocus();
////				break;
////
////		}
//
//		return super.onKeyDown(keyCode, event);
//
//	}
}
