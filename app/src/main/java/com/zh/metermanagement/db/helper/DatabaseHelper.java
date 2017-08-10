package com.zh.metermanagement.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库管理
 *
 * */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     *
     * @param context 	     上下文对象
     * @param dbName 		数据库名称
     * @param factory       游标结果集工厂，如果需要使用则需要自定义结果集工厂，null值代表使用默认结果集工厂
     * @param version 	     数据库版本号，必须大于等于1
     */
	private DatabaseHelper(Context context, String dbName, CursorFactory factory, int version) {
		super(context, dbName, factory, version);
	}

    /**
     *  新建数据库
     *
     * @param context 	     上下文对象
     * @param dbName		数据库名称
     * @param version	     数据库版本号，必须大于等于1
     */
	public DatabaseHelper(Context context, String dbName, int version) {
		this(context, dbName, null, version); // this -->"私有的构造函数"

	}

	/**
	 * 数据库第一次被调用时调用该方法，
     *
     * 	private DatabaseHelper dbHelper = null;
     *  private SQLiteDatabase db = null;
     * 		dbHelper = new DatabaseHelper(mContext, Constant.DB_NAME, dbVersion);
     *      db = dbHelper.getWritableDatabase();        -----------> 这句调用时，此方法被调用
     *
     * 这里面主要进行对数据库的初始化操作
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		System.out.println("DBHelper onCreate");

		/** 资产编号无匹配的(资产编号) */
		db.execSQL("create table if not exists assetnumbers(" +
				"_id integer primary key autoincrement," +
				"assetNumbers varchar(50) not null unique )");									// 是否完成(扫描完)


		/** 用户数据(表) */
		db.execSQL("create table if not exists meterinfo(" +
				"_id integer primary key autoincrement," +
				"sequenceNumber varchar(50) not null," +					// 序号
				"userNumber varchar(50) not null," +						// 用户编号
				"userName varchar(50) not null," +							// 用户名称
				"userAddr varchar(50) not null," +							// 用户地址
				"area varchar(50) not null," +								// 区域(哪个区域的表)
				"oldAssetNumbers varchar(50) not null," +					// 旧表资产编号(导入的)
				"oldAddr varchar(50)," +									// 旧表表地址(需扫描)
				"oldAddrAndAsset tinyint(1)," +								// 旧表表地址 和 资产编码 比较
				"oldAssetNumbersScan varchar(50)," +						// 旧表资产编号(需扫描)
				"oldElectricity varchar(50)," +								// 旧电能表止码-电量(需扫描)
				"newAddr varchar(50)," +									// 新表表地址(需扫描)
				"newAddrAndAsset tinyint(1)," +								// 新表表地址 和 资产编码 比较
				"newAssetNumbersScan varchar(50)," +						// 新表资产编号(需扫描)
				"newElectricity varchar(50)," +								// 新电能表止码-电量(需扫描)
				"collectorAssetNumbersScan varchar(50)," +					// 采集器资产编号(需扫描)
				"time date," +												// 完成换抄时间
				"picPath varchar(500)," +									// 拍照图片的路径
				"isFinish tinyint(1))");									// 是否完成(扫描完)

    }



    /**
     * 数据库更新的时候调用该方法
     * @param db 				当前操作的数据库对象
     * @param oldVersion 		老版本号
     * @param newVersion 		新版本号
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("DBHelper onUpgrade");

		String sql1,sql2,sql3,sql4,sql5,sql6;
		try {
            // // 备份数据库到SD卡的/aDBTest/DBTest.db
            // CopyDBToSDCard.CopyDB(mContext);
			for (int i = oldVersion; i < newVersion; i++) {
				switch (i) {
				case 1:
//					sql1 = " ALTER TABLE sbinfo DROP COLUMN TableNumber";
//
//					sql2 = " ALTER TABLE sbinfo ADD COLUMN OldMeterNumber varchar(200) not null";
//
//					db.execSQL(sql1);
//					db.execSQL(sql2);
					//LogUtils.sysout("==========升级数据库", "");
					break;

				default:
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
