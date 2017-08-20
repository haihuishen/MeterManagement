package com.zh.metermanagement.bean;

import android.content.Context;

import com.zh.metermanagement.utils.FilesUtils;
import com.zh.metermanagement.utils.StringUtils;

import java.io.File;

/**
 * 无工单要生成的文件夹
 */

public class NoWorkOrderPathBean {

    public static String ReplaceMeter = "换表" + File.separator;
    public static String NewCollector = "加装采集器" + File.separator;

    public static String Photo = "图片" + File.separator;
    public static String ExportExcel = "导出Excel" + File.separator;


    /** 抄表区段 */
    private String areaPath;
    /** 抄表区段/换表 */
    private String areaReplaceMeterPath;
    /** 抄表区段/加装采集器 */
    private String areaNewCollectorPath;

    /** 抄表区段/导出Excel */
    private String areaExportPath;


    /** 抄表区段/换表/图片 */
    private String replaceMeterPhotoPath;
    /** 抄表区段/加装采集器/图片 */
    private String newCollectorPhotoPath;


    /** 抄表区段 */
    public String getAreaPath() {
        return areaPath;
    }
    /** 抄表区段 */
    public void setAreaPath(String areaPath) {
        this.areaPath = areaPath;
    }

    /** 抄表区段/换表 */
    public String getAreaReplaceMeterPath() {
        return areaReplaceMeterPath;
    }
    /** 抄表区段/换表 */
    public void setAreaReplaceMeterPath(String areaReplaceMeterPath) {
        this.areaReplaceMeterPath = areaReplaceMeterPath;
    }

    /** 抄表区段/加装采集器 */
    public String getAreaNewCollectorPath() {
        return areaNewCollectorPath;
    }
    /** 抄表区段/加装采集器 */
    public void setAreaNewCollectorPath(String areaNewCollectorPath) {
        this.areaNewCollectorPath = areaNewCollectorPath;
    }

    /** 抄表区段/导出Excel */
    public String getAreaExportPath() {
        return areaExportPath;
    }
    /** 抄表区段/导出Excel */
    public void setAreaExportPath(String areaExportPath) {
        this.areaExportPath = areaExportPath;
    }

    /** 抄表区段/换表/图片 */
    public String getReplaceMeterPhotoPath() {
        return replaceMeterPhotoPath;
    }
    /** 抄表区段/换表/图片 */
    public void setReplaceMeterPhotoPath(String replaceMeterPhotoPath) {
        this.replaceMeterPhotoPath = replaceMeterPhotoPath;
    }


    /** 抄表区段/加装采集器/图片 */
    public String getNewCollectorPhotoPath() {
        return newCollectorPhotoPath;
    }
    /** 抄表区段/加装采集器/图片 */
    public void setNewCollectorPhotoPath(String newCollectorPhotoPath) {
        this.newCollectorPhotoPath = newCollectorPhotoPath;
    }


    public void onCreate(Context context){

        FilesUtils.createFile(context, areaPath);
        FilesUtils.createFile(context, areaReplaceMeterPath);
        FilesUtils.createFile(context, areaNewCollectorPath);
        FilesUtils.createFile(context, areaExportPath);
        FilesUtils.createFile(context, replaceMeterPhotoPath);
        FilesUtils.createFile(context, newCollectorPhotoPath);
    }

}
