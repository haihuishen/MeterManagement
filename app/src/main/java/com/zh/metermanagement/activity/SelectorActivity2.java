package com.zh.metermanagement.activity;


import android.content.Intent;
import android.text.Html;
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
public class SelectorActivity2 extends BaseActivity implements OnClickListener {

	private long exitTime;


	/** 标题 */
	TextView mTvTitle;
	/** 返回按钮 -- 按钮 */
	Button mBtnBack;
	/** 菜单按钮 -- 按钮 */
	Button mBtnMenu;


	/** 按键ID数组 */
	private int[] mLayoutIds = {
			R.id.layout_newElectricMeter, 		// 新装电表
			R.id.layout_replaceMeter,			// 换表
			R.id.layout_newCollector, 			// 新装采集器
			R.id.layout_generateReports, 		// generate_reports
			R.id.layout_statistics,				// 统计
			R.id.layout_query,					// 查询
			R.id.layout_directionsForUse, 		// 使用手册
			R.id.layout_sendEmail,				// 发送邮件
			R.id.layout_concentrator,			// 新装集中器
			R.id.layout_transformer				// 变压器
	};


	/** 显示当前软件版本号 */
	private TextView mTvAppVersion;

	private int mCurrIndex = -1;

	/** Layout 集合 */
	private ArrayList<LinearLayout> mLayoutList;


	@Override
	public int getContentLayout() {
		return R.layout.activity_selector2;
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

		mTvTitle.setText("抄表区段：");
		mTvTitle.setTextSize(12);
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

		Intent intent = getIntent();
		String courts = intent.getStringExtra("台区");
		String meteringSection = intent.getStringExtra("抄表区段");

//		String html="<html><head><title>TextView使用HTML</title></head><body><p><strong>强调</strong></p><p><em>斜体</em></p>"
//				+"<p><a href=\"http://www.dreamdu.com/xhtml/\">超链接HTML入门</a>学习HTML!</p><p><font color=\"#aabb00\">颜色1"
//				+"</p><p><font color=\"#00bbaa\">颜色2</p><h1>标题1</h1><h3>标题2</h3><h6>标题3</h6><p>大于>小于<</p><p>" +
//				"下面是网络图片</p><img src=\"http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg\"/></body></html>";

		String html = "<h1>"+"功能选择"+"</h1><h6>("+courts + " -- " + meteringSection+")</h6>";
		mTvTitle.setText(Html.fromHtml(html));

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
		switch (v.getId()) {
			case R.id.btn_back_left:

				finish();
				break;

			case R.id.btn_menu_right:
				break;

			default:
				scaleAnimation(v);
				break;
		}
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

					case R.id.layout_newElectricMeter: 						// 新装电表
						showToast("新装电表！");
						break;

					case R.id.layout_replaceMeter:							// 换表
						//showToast("换表！");
						intent = new Intent(SelectorActivity2.this, ReplaceMeterActivity1.class);
						startActivity(intent);
						break;

					case R.id.layout_newCollector:				 			// 新装采集器
						//showToast("新装采集器！");
						intent = new Intent(SelectorActivity2.this, NewCollectorActivity1.class);
						startActivity(intent);
						break;

					case R.id.layout_generateReports:				 			// generate_reports
						//showToast("generate_reports！");
						intent = new Intent(SelectorActivity2.this, GenerateReportsActivity1.class);
						startActivity(intent);
						break;

					case R.id.layout_statistics:									// 统计
						//showToast("查询！");
						intent = new Intent(SelectorActivity2.this, StatisticsActivity.class);
						startActivity(intent);

						break;

					case R.id.layout_query:									// 查询
						//showToast("查询！");
						intent = new Intent(SelectorActivity2.this, SearchActivity.class);
						startActivity(intent);

						break;
					case R.id.layout_directionsForUse:				 		// 使用手册
						//showToast("使用手册！");
						intent = new Intent(SelectorActivity2.this, DirectionsForUseActivity.class);
						startActivity(intent);

						break;

					case R.id.layout_sendEmail:                            // 发送邮件
						intent = new Intent(SelectorActivity2.this, SendEmailActivity.class);
						startActivity(intent);
						break;

					case R.id.layout_concentrator:							// 新装集中器
						showToast("新装集中器！");
						break;

					case R.id.layout_transformer:							// 变压器
						showToast("变压器！");
						break;

					default:
						break;
				}
			}
		});

		view.startAnimation(sa);
	}





}
