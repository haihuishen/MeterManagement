package com.zh.metermanagement.db.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.zh.metermanagement.utils.LogUtils;


/**
 * User表业务操作
 *
 */
public class TableEx extends BaseEx {


    public TableEx(Context context) {
        super(context);
    }
    //    SQLiteDatabase dataBase = getDataBase();
    //    ContentValues values = new ContentValues();
    //    values.put("name", "heima");						// 字段  ： 值
    //    values.put("age", 5);
    //    values.put("phone", "010-82826816");
		/*
		 * 第一个参数 table，代表要将数据插入哪家表
		 * 第二个参数nullColumnHack，字符串类型，指明如果某一字段没有值，那么会将该字段的值设为NULL
		 * ，一般给该参数传递null就行如果没有特殊要求
		 * ，在这里我传递了phone字符串，也就是说当我的ContentValues中phone字段为空的时候系统自动给其值设置为NULL
		 * 第三个参数ContentValues 类似一个Map<key,value>的数据结构，key是表中的字段，value是值
		 */
    // dataBase.insert("person", null, values);  使用这个就可以个
    // 第二个参数的使用,基本都没用
    //    dataBase.insert("person", "phone", values);
    //    返回值是  -1  失败
	@Override
	public long Add(String table, ContentValues values) {
        long i = 0;         // 返回的是，表中流水号(最大的那个)
		try {
			openDBConnect();
            i = getDb().insert(table, "", values);
		} catch (Exception e) {
			Log.i("shen", "db Add -- e.printStackTrace():" + e.getMessage());
		} finally {
			closeDBConnect();
		}
        return i;
	}

	@Override
	public int Update(String table, ContentValues values, String whereClause,
                      String[] whereArgs) {
        int num = 0;
		try {
			openDBConnect();
			num = getDb().update(table, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.i("Update -- e.getMessage()" + e.getMessage());
		} finally {
			closeDBConnect();
		}
        return num;
	}

	@Override
	public int Delete(String table, String whereClause, String[] whereArgs) {
        int num = 0;                    // 删除的条数
		try {
			openDBConnect();
			num = getDb().delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDBConnect();
		}
        return num;
	}

	// 该方法可以修改返回值参数为List<T>或其他自定义返回值，注意关闭数据库连接。
	@Override
	public Cursor Query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
		Cursor cursor = null;
		try {
			openDBConnect();
			cursor = getDb().query(table,columns,selection,selectionArgs,groupBy, having, orderBy);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO:关闭数据库连接的动作(super.stopDBConnect())，需在Cursor使用结束之后执行。
		}
        return cursor;
	}

}
