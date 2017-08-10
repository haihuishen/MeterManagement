package com.zh.metermanagement.application;

import android.app.Application;
import android.content.Context;
import android.serialport.MeterController;
import android.util.Log;


import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.shen.sweetdialog.SweetAlertDialog;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.MeterBean;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

	private final String Tag = "MyApplication";


	private static Context context;
	/** Activity集合 */
	public static ArrayList<BaseActivity> listActivity;

	private static MyApplication instance;

	/** 用户/表主的信息 */
	private static List<MeterBean> mMeterBeanList;
	/** 无匹配的资产编码 */
	private static List<AssetNumberBean> mAssetNumberBeanList;



	/** 从驱动文件中(缓冲区)获取数据的类 -- 红外的！ */
	private MeterController mMeterController;


	/**
	 * 获取 MyApplication
	 * @return
	 */
	public static MyApplication getInstance() {
		if(instance == null){
			return new MyApplication();
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		listActivity = new ArrayList<BaseActivity>();

		context = this;

		mMeterBeanList = new ArrayList<MeterBean>();
		mAssetNumberBeanList = new ArrayList<AssetNumberBean>();

	}


	public static Context getContext() {
		return context;
	}


	/** 用户/表主的信息 */
	public static List<MeterBean> getMeterBeanList() {
		return mMeterBeanList;
	}

	/** 用户/表主的信息 */
	public static void setMeterBeanList(List<MeterBean> meterBeanList) {
		mMeterBeanList = meterBeanList;
	}

	/** 无匹配的资产编码 */
	public static List<AssetNumberBean> getAssetNumberBeanList() {
		return mAssetNumberBeanList;
	}

	/** 无匹配的资产编码 */
	public static void setAssetNumberBeanList(List<AssetNumberBean> mAssetNumberBeanList) {
		MyApplication.mAssetNumberBeanList = mAssetNumberBeanList;
	}

	/**
	 * 退出APP
	 */
	public static void exitApp() {
		for (BaseActivity activity : listActivity) {
			if (activity != null) {
				activity.finish();
			}
		}
		Log.e("MyApplication", "exitApp");
		// 杀进程
		android.os.Process.killProcess(android.os.Process.myPid());
		// 终止虚拟机
		System.exit(0);
	}


	@Override
	public void onTerminate() {
		// 程序终止的时候执行
		Log.i("shen", "onTerminate");
		super.onTerminate();
	}
	@Override
	public void onLowMemory() {
		// 低内存的时候执行
		Log.i("shen", "onLowMemory");
		super.onLowMemory();
	}
	@Override
	public void onTrimMemory(int level) {
		// 程序在内存清理的时候执行
		Log.i("shen", "onTrimMemory");
		super.onTrimMemory(level);
	}



}
