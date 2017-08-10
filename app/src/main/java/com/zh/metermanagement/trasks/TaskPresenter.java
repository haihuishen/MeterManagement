package com.zh.metermanagement.trasks;


import android.content.ContentValues;
import android.content.Context;

import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.MeterBean;

import java.util.List;

import io.reactivex.Observer;

/**
 *
 */
public interface TaskPresenter {

    /**
     * 登录
     *
     * @param id
     * @param pwd
     */
    void loginData(String id, String pwd);


    /**
     * 查询
     *  @param plateNumber   车牌
     * @param observer    订阅
     */
    void queryData(String plateNumber, Observer observer);

    /**
     * 查询<p>
     *
     * 必须在Cursor使用之后，才可以关闭数据库连接。 例如：Cursor.moveToNext()执行的时候，才会去查询数据库中是否有数据。<p>
     *
     * Cursor cursor = database.query("person"(这个在某段代码填上了：表名),
     *                                  new String[]{"name,age,phone"}, "name=?", new String[]{"shen"}, null, null, null);
     *
     * @param table             表名
     * @param columns           返回列(要查询的字段)
     * @param selection         查询条件(过滤字段) 例如：id=?，?为通配符
     * @param selectionArgs     条件集合(过滤字段的值) 例如： new String[]{"1"}
     * @param groupBy           分组
     * @param having
     * @param orderBy           排序
     *
     * @param observer          订阅
     *
     */
     void queryData(String table, String[] columns, String selection,
                    String[] selectionArgs, String groupBy, String having,
                    String orderBy, Observer observer);


    /**
     * 添加车牌
     *
     */
    void addPlateNumber();


    /**
     * 将excel导入到数据库中
     *
     * @param observer
     */
    void importExcelToDb(Observer observer);


    /**
     * 将数据库中的数据读取到Bean
     *
     * @param observer
     * @return
     */
    List<MeterBean> readDbToBean(Observer observer);


    /**
     * 保存数据
     *
     * @param observer
     * @return
     */
    void saveData(Observer observer, ContentValues values, String whereClause, String[] whereArgs);

    /**
     * 保存"新采集器"
     *
     * @param observer
     * @return
     */
    void saveNewCollector(Observer observer, String NewCollector, String[] MeterList);


    /**
     * 查询(统计)数据
     *
     * @param observer
     * @return
     */
    void statisticsData(Observer observer);


    /**
     * 添加不匹配的资产编码到数据库
     *
     * @param observer
     * @param values
     */
    void addMismatchingAssetNumbers(Observer observer, ContentValues values);


    /**
     * 生成excel
     *
     * @param observer
     * @param context
     * @param meterBeanList
     * @param assetNumberBeenList
     */
    void generateReports(Observer observer, Context context, List<MeterBean> meterBeanList, List<AssetNumberBean> assetNumberBeenList);

}
