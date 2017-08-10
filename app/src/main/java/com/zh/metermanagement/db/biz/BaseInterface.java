package com.zh.metermanagement.db.biz;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 业务操作接口，可自定义适合自己的查询方法
 *
 * */
public interface BaseInterface {

	/**
	 * 新增
	 * 
	 * @param values 新增内容 key-value
	 * */
	abstract long Add(String table, ContentValues values);

	/**
	 * 更新
	 * 
	 * @param values        更新内容 key-value
	 * @param whereClause   更新条件 例如：id=?，?为通配符
	 * @param whereArgs     条件集合 例如：new String[]{"1"}
	 * 
	 * */
	abstract int Update(String table, ContentValues values, String whereClause,
                        String[] whereArgs);

	/**
	 * 删除
	 * 
	 * @param whereClause   删除条件 例如：id=?，?为通配符
	 * @param whereArgs     删除集合 例如：new String[]{"1"}
	 * 
	 * */
	abstract int Delete(String table, String whereClause, String[] whereArgs);

	/**
	 * 查询<p>
	 * 
	 * 必须在Cursor使用之后，才可以关闭数据库连接。 例如：Cursor.moveToNext()执行的时候，才会去查询数据库中是否有数据。<p>
	 *
     * Cursor cursor = database.query("person"(这个在某段代码填上了：表名),
     *                                  new String[]{"name,age,phone"}, "name=?", new String[]{"shen"}, null, null, null);
     *
	 * @param columns           返回列(要查询的字段)
	 * @param selection         查询条件(过滤字段) 例如：id=?，?为通配符
	 * @param selectionArgs     条件集合(过滤字段的值) 例如： new String[]{"1"}
	 * @param groupBy           分组
	 * @param having
	 * @param orderBy           排序
	 * 
	 */
	abstract Cursor Query(String table, String[] columns, String selection,
                          String[] selectionArgs, String groupBy, String having,
                          String orderBy);


}
