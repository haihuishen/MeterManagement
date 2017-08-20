package com.zh.metermanagement.trasks;


import android.content.ContentValues;
import android.content.Context;

import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.CollectorNumberBean;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.view.linkagerecyclerview.ShenBean;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;

/**
 *
 */
public interface TaskPresenter1 {

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
     * 将"表信息"excel导入到数据库中
     *
     * @param observer
     */
    void importMeterInfoExcelToDb(Observer observer, File[] files);

    /**
     * 将"表信息-电话"excel导入到数据库中
     *
     * @param observer
     */
    void importMeterPhoneExcelToDb(Observer observer, File[] files);

    /**
     * 将数据库中的数据读取到Bean
     *
     * @param observer
     * @return
     */
    List<MeterBean1> readDbToBean(Observer observer, final String meteringSection);


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
     * @param excelPath1
     * @param excelPath2
     * @param excelPath3
     */
    void generateReports(Observer observer, Context context, List<MeterBean1> meterBeanList,
                         List<AssetNumberBean> assetNumberBeenList,
                         String excelPath1, String excelPath2, String excelPath3);


    /**
     * 获取 -- 抄表区段
     *
     * @param observer
     *
     */
    void getMeteringSection(Observer observer);


    /**
     * 获取--采集器
     *
     * @param observer
     * @param meteringSection       抄表区段
     * @param collector             采集器
     */
    void getCollector(Observer observer, String meteringSection, String collector);

    /**
     * 获取--采集器 -- 抄表区段所有的
     *
     * @param observer
     * @param meteringSection       抄表区段
     */
    void getCollectorList(Observer observer, String meteringSection);


    /**
     * 将数据库中的数据读取到Bean -- 根据采集器
     *
     * @param observer
     * @param meteringSection       抄表区段
     * @param collectorNumber       采集器
     * @return
     */
    List<MeterBean1> readDbToBeanForCollector(Observer observer, final String meteringSection, String collectorNumber);

    /**
     * 添加采集器 -- 到"电表详情的那张表"
     *
     * @param observer
     * @param meteringSection       抄表区段
     * @param collectorNumber       采集器资产编码
     * @param meterBean1List        要添加到采集器的表 -- 列表
     */
    void addCollectorToMeterInfo(Observer observer, String meteringSection, String collectorNumber, List<MeterBean1> meterBean1List);

    /**
     * 添加采集器 -- 到"所有采集器那张表"
     *
     * @param observer
     * @param meteringSection               抄表区段
     * @param collectorNumberBean           采集器 信息
     */
    void addCollectorToCollectorTable(Observer observer, String meteringSection, CollectorNumberBean collectorNumberBean);


    /**
     * 根据条件查询表
     *
     * @param observer
     * @param meteringSection       抄表区段
     * @param conditionMap          查询条件
     * @return
     */
    List<MeterBean1> searchMeterInfo(Observer observer, final String meteringSection, HashMap<String, String> conditionMap);


}
