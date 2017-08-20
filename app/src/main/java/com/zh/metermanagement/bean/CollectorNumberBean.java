package com.zh.metermanagement.bean;

/**
 * 采集器(资产编号)
 */

public class CollectorNumberBean {

    /** 采集器资产编码 */
    private String collectorNumbers;
    /** 抄表区段 */
    private String theMeteringSection;
    /** 采集器图片 */
    private String collectorPicPath;




    /** 采集器资产编码 */
    public String getCollectorNumbers() {
        return collectorNumbers;
    }
    /** 采集器资产编码 */
    public void setCollectorNumbers(String collectorNumbers) {
        this.collectorNumbers = collectorNumbers;
    }

    /** 抄表区段 */
    public String getTheMeteringSection() {
        return theMeteringSection;
    }
    /** 抄表区段 */
    public void setTheMeteringSection(String theMeteringSection) {
        this.theMeteringSection = theMeteringSection;
    }

    /** 采集器图片 */
    public String getCollectorPicPath() {
        return collectorPicPath;
    }
    /** 采集器图片 */
    public void setCollectorPicPath(String collectorPicPath) {
        this.collectorPicPath = collectorPicPath;
    }


    @Override
    public String toString() {
        return "CollectorNumberBean{" +
                "collectorNumbers='" + collectorNumbers + '\'' +
                ", theMeteringSection='" + theMeteringSection + '\'' +
                ", collectorPicPath='" + collectorPicPath + '\'' +
                '}';
    }

    public void clean(){
        collectorNumbers = "";
        theMeteringSection = "";
        collectorPicPath = "";
    }
}
