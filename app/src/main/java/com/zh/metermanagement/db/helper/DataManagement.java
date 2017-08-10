package com.zh.metermanagement.db.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zh.metermanagement.config.Constant;

/**
 * Created by Administrator on 2017/8/1.
 */

public class DataManagement {


    /**
     * 清空表
     * @param context
     * @param dbName        数据库名称
     * @param table         表名称
     */
    public static void deleteTable(Context context, String dbName, String table) {

        int dbVersion = 0;
        try {
            dbVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        DatabaseHelper dbHelper = new DatabaseHelper(context, dbName, dbVersion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                // 1. delete  from  table
                // 2.update sqlite_sequence set seq=0 where name='table'
                //   或
                //   delete from sqlite_sequence where name='table'
                // delete 表操作 不会修改seq值
                db.execSQL("delete from " + table);
                db.execSQL("update sqlite_sequence set seq=0 where name='"+table+"'");

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("shen", "表删除失败：" + e.getMessage());
            }
    }
}
