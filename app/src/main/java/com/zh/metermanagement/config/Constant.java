package com.zh.metermanagement.config;

import android.os.Environment;

import java.io.File;

/**
 * 全局变量
 */
public class Constant {

    /** 电表换装(excel存放)路径 */
    public final static String excelPathDir = "/mnt/sdcard/电表换装/";
    /** 导入excel文件名 */
    //public final static String importExcel = "import.xls";
    public final static String importExcel = "台区户表清单.xlsx";
    /** 导出excel文件名 */
    //public final static String exportExcel = "export.xls";
    public final static String exportExcel = "XX供电所XX台区户表.xlsx";

    /**
     * CacheImage图片文件存放地址    <p>
     *
     *  /storage/emulated/0/电表换装/CacheImage/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String CACHE_IMAGE_PATH = excelPathDir + "CacheImage" + File.separator;


    /**
     * Image图片文件存放地址   <p>
     *
     *  /storage/emulated/0/电表换装/Image/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String IMAGE_PATH = excelPathDir + "Image" + File.separator;



    /**
     * "说明Image图片"文件存放地址   <p>
     *
     *  /storage/emulated/0/电表换装/DirectionsForUseImage/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String DIRECTIONSFORUSEIMAGE_PATH = excelPathDir + "DirectionsForUseImage" + File.separator;

    //----------------------------------------------------------------------------------------

    public final static String all = "总户数";
    public final static String finish = "已换表";
    public final static String unfinished = "未换表";
    public final static String assetsNumber_Mismatches = "编码无匹配用户";

    //----------------------------------------------------------------------------------------

    /** 数据库名 */
	public final static String DB_NAME = "MeterManagement.db";

    /** 表名 -- meterinfo */
    public final static String TABLE_METERINFO = "meterinfo";
    /** 表名 -- assetnumbers */
    public final static String TABLE_ASSETNUMBERS = "assetnumbers";


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
    /** 旧电能表止码-电量(需扫描) */
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


    /** 序号(数据库自动生成) */
    public final static String TABLE_ASSETNUMBERS_STR__id = "_id";
    /** 资产编号 */
    public final static String TABLE_ASSETNUMBERS_STR_assetNumbers = "assetNumbers";
}
