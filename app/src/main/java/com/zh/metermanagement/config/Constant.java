package com.zh.metermanagement.config;

import android.os.Environment;

import java.io.File;

/**
 * 全局变量
 */
public class Constant {

    /** 资源放置路径 */
    public final static String srcPathDir = Environment.getExternalStorageDirectory().getPath() + "/电表换装/";
    /** 电表换装(excel存放)路径 */
    public final static String excelPathDir = srcPathDir + "导入数据源" + File.separator;

    /** 导入excel文件名 */
    //public final static String importExcel = "import.xls";
    //public final static String importExcel = "台区户表清单.xlsx";
    public final static String importExcel = "XX供电所XX台区（抄表区段）电能表.xlsx";

    /** 导出excel文件名 */
    //public final static String exportExcel = "export.xls";
    public final static String exportExcel = "XX供电所XX台区户表.xlsx";


    /** 无工单 存放地址 <p>  /storage/emulated/0/电表换装/无工单/ <br>  如果使用"/"==>File.separator<p> */
    public final static String NoWorkOrder_PATH = srcPathDir + "无工单" + File.separator;

    /**
     * 电话导入文件 存放地址    <p>
     *
     *  /storage/emulated/0/电表换装/导入数据源/电话导入文件/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String IMPORT_PHONE_PATH = excelPathDir + "电话导入文件" + File.separator;
    /**
     * 换表导入文件 存放地址    <p>
     *
     *  /storage/emulated/0/电表换装/导入数据源/换表导入文件/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String IMPORT_METER_INFO_PATH = excelPathDir + "换表导入文件" + File.separator;


    /**
     * CacheImage图片文件存放地址    <p>
     *
     *  /storage/emulated/0/电表换装/CacheImage/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String CACHE_IMAGE_PATH = srcPathDir + "CacheImage" + File.separator;


    /**
     * Image图片文件存放地址   <p>
     *
     *  /storage/emulated/0/电表换装/Image/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String IMAGE_PATH = srcPathDir + "Image" + File.separator;



    /**
     * "说明Image图片"文件存放地址   <p>
     *
     *  /storage/emulated/0/电表换装/DirectionsForUseImage/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String DIRECTIONSFORUSEIMAGE_PATH = srcPathDir + "DirectionsForUseImage" + File.separator;

    //----------------------------------------------------------------------------------------

    public final static String all = "总户数";
    public final static String finish = "已完成";
    public final static String unfinished = "未完成";
    public final static String replaceMeter = "换表";
    public final static String newCollector = "加装采集器";
    public final static String assetsNumber_Mismatches = "编码无匹配用户";

    //----------------------------------------------------------------------------------------

    /** 数据库名 */
	public final static String DB_NAME = "MeterManagement.db";

    /** 表名 -- meterinfo */
    public final static String TABLE_METERINFO = "meterinfo";
    /** 表名 -- meterinfo1 */
    public final static String TABLE_METERINFO1 = "meterinfo1";
    /** 表名 -- assetnumber */
    public final static String TABLE_ASSETNUMBER = "assetnumber";
    /** 表名 -- collectornumber */
    public final static String TABLE_COLLECTORNUMBER = "collectornumber";


    /** 序号(数据库自动生成) */
    public final static String TABLE_METERINFO_STR__id = "_id";
    /** 序号 */
    public final static String TABLE_METERINFO_STR_sequenceNumber = "sequenceNumber";
    /** 用户编号 */
    public final static String TABLE_METERINFO_STR_userNumber = "userNumber";
    /** 用户名称 */
    public final static String TABLE_METERINFO_STR_userName = "userName";
    /** 区域(哪个区域的表) */
    public final static String TABLE_METERINFO_STR_area = "area";
    /** 用户地址 */
    public final static String TABLE_METERINFO_STR_userAddr = "userAddr";
    /** 旧表资产编号(导入的) */
    public final static String TABLE_METERINFO_STR_oldAssetNumbers = "oldAssetNumbers";
    /** 旧表表地址(需扫描) */
    public final static String TABLE_METERINFO_STR_oldAddr = "oldAddr";
    /** 旧表表地址 和 资产编码 比较 */
    public final static String TABLE_METERINFO_STR_oldAddrAndAsset = "oldAddrAndAsset";
    /** 旧表资产编号(需扫描) */
    public final static String TABLE_METERINFO_STR_oldAssetNumbersScan = "oldAssetNumbersScan";
    /** 旧电能表止码-电量(需扫描) */
    public final static String TABLE_METERINFO_STR_oldElectricity = "oldElectricity";
    /** 新表表地址(需扫描) */
    public final static String TABLE_METERINFO_STR_newAddr = "newAddr";
    /** 新表表地址 和 资产编码 比较 */
    public final static String TABLE_METERINFO_STR_newAddrAndAsset = "newAddrAndAsset";
    /** 新表资产编号(需扫描) */
    public final static String TABLE_METERINFO_STR_newAssetNumbersScan = "newAssetNumbersScan";
    /** 新电能表止码-电量(需扫描) */
    public final static String TABLE_METERINFO_STR_newElectricity = "newElectricity";
    /** 采集器资产编号(需扫描) */
    public final static String TABLE_METERINFO_STR_collectorAssetNumbersScan = "collectorAssetNumbersScan";
    /** 完成换抄时间 */
    public final static String TABLE_METERINFO_STR_time = "time";
    /** 拍照图片的路径 */
    public final static String TABLE_METERINFO_STR_picPath = "picPath";
    /** 是否完成(扫描完) */
    public final static String TABLE_METERINFO_STR_isFinish = "isFinish";
    
    //-------------------------------------------------------------------------------

    /** 序号(数据库自动生成) */
    public final static String TABLE_METERINFO1_STR__id = "_id";
    /** 用户编号 */
    public final static String TABLE_METERINFO1_STR_userNumber = "userNumber";
    /** 用户名称 */
    public final static String TABLE_METERINFO1_STR_userName = "userName";
    /** 用户地址 */
    public final static String TABLE_METERINFO1_STR_userAddr = "userAddr";
    /** 用户电话 */
    public final static String TABLE_METERINFO1_STR_userPhone = "userPhone";
    /** 计量点编号 */
    public final static String TABLE_METERINFO1_STR_measurementPointNumber = "measurementPointNumber";
    /** 供电局(供电单位) */
    public final static String TABLE_METERINFO1_STR_powerSupplyBureau = "powerSupplyBureau";
    /** 抄表区段 */
    public final static String TABLE_METERINFO1_STR_theMeteringSection = "theMeteringSection";
    /** 台区 */
    public final static String TABLE_METERINFO1_STR_courts = "courts";
    /** 计量点地址 */
    public final static String TABLE_METERINFO1_STR_measuringPointAddress = "measuringPointAddress";

    /** 旧表资产编号(导入的) */
    public final static String TABLE_METERINFO1_STR_oldAssetNumbers = "oldAssetNumbers";
    /** 旧表表地址(需扫描) */
    public final static String TABLE_METERINFO1_STR_oldAddr = "oldAddr";
    /** 旧表表地址 和 资产编码 比较 */
    public final static String TABLE_METERINFO1_STR_oldAddrAndAsset = "oldAddrAndAsset";
    /** 旧电能表止码-电量(需扫描) */
    public final static String TABLE_METERINFO1_STR_oldElectricity = "oldElectricity";
    /** 新表表地址(需扫描) */
    public final static String TABLE_METERINFO1_STR_newAddr = "newAddr";
    /** 新表表地址 和 资产编码 比较 */
    public final static String TABLE_METERINFO1_STR_newAddrAndAsset = "newAddrAndAsset";
    /** 新表资产编号(需扫描) */
    public final static String TABLE_METERINFO1_STR_newAssetNumbersScan = "newAssetNumbersScan";
    /** 新电能表止码-电量(需扫描) */
    public final static String TABLE_METERINFO1_STR_newElectricity = "newElectricity";
    /** 采集器资产编号(需扫描) */
    public final static String TABLE_METERINFO1_STR_collectorAssetNumbersScan = "collectorAssetNumbersScan";
    /** 完成换抄时间 */
    public final static String TABLE_METERINFO1_STR_time = "time";
    /** 拍照图片的路径(换表) */
    public final static String TABLE_METERINFO1_STR_picPath = "picPath";
    /** 拍照图片的路径(新装采集器对应的电表) */
    public final static String TABLE_METERINFO1_STR_meterPicPath = "meterPicPath";
    /** 拍照图片的路径(新装采集器) */
    public final static String TABLE_METERINFO1_STR_meterContentPicPath = "meterContentPicPath";
    /** 0:"换表"； 1："新装采集器" -- 要先判断是否抄完 */
    public final static String TABLE_METERINFO1_STR_relaceOrAnd = "relaceOrAnd";
    /** 是否完成(扫描完) */
    public final static String TABLE_METERINFO1_STR_isFinish = "isFinish";

    //-------------------------------------------------------------------------------
    /** 序号(数据库自动生成) */
    public final static String TABLE_ASSETNUMBER_STR__id = "_id";
    /** 资产编号 */
    public final static String TABLE_ASSETNUMBER_STR_assetNumbers = "assetNumbers";



    //-------------------------------- 采集器(资产编号) --------------------------------
    /** 序号(数据库自动生成) */
    public final static String TABLE_COLLECTORNUMBER_STR__id = "_id";
    /** 采集器资产编码 */
    public final static String TABLE_COLLECTORNUMBER_STR_collectorNumbers = "collectorNumbers";
    /** 抄表区段 */
    public final static String TABLE_COLLECTORNUMBER_STR_theMeteringSection = "theMeteringSection";
    /** 采集器图片 */
    public final static String TABLE_COLLECTORNUMBER_STR_collectorPicPath = "collectorPicPath";

}
