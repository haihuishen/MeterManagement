package com.zh.metermanagement.bean;

/**
 * Created by Administrator on 2017/8/1.
 */

public class MeterBean {


    /** 序号 */
    private String sequenceNumber;
    /** 用户编号 */
    private String userNumber;
    /** 用户名称 */
    private String userName;
    /** 用户地址 */
    private String userAddr;
    /** 区域(哪个区域的表) */
    private String area;
    /** 旧表资产编号(导入的) */
    private String oldAssetNumbers;

    /** 旧表表地址(需扫描) */
    private String oldAddr;
    /** 旧表表地址 和 资产编码 比较 */
    private boolean oldAddrAndAsset;
    /** 旧表资产编号(需扫描) */
    private String oldAssetNumbersScan;
    /** 旧电能表止码-电量(需扫描) */
    private String oldElectricity;
    /** 新表表地址(需扫描) */
    private String newAddr;
    /** 新表表地址 和 资产编码 比较 */
    private boolean newAddrAndAsset;
    /** 新表资产编号(需扫描) */
    private String newAssetNumbersScan;
    /** 新电能表止码-电量(需扫描) */
    private String newElectricity;
    /** 采集器资产编号(需扫描) */
    private String collectorAssetNumbersScan;

    /** 完成换抄时间 */
    private String time;
    /** 拍照图片的路径 */
    private String picPath;

    /** 是否完成(扫描完) */
    private boolean isFinish;



    //------------------------------------------------------------

    /** 序号 */
    public String getSequenceNumber() {
        return sequenceNumber;
    }
    /** 序号 */
    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /** 用户编号 */
    public String getUserNumber() {
        return userNumber;
    }
    /** 用户编号 */
    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    /** 用户名称 */
    public String getUserName() {
        return userName;
    }
    /** 用户名称 */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** 用户地址 */
    public String getUserAddr() {
        return userAddr;
    }
    /** 用户地址 */
    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr;
    }

    /** 区域(哪个区域的表) */
    public String getArea() {
        return area;
    }
    /** 区域(哪个区域的表) */
    public void setArea(String area) {
        this.area = area;
    }

    /** 旧表资产编号(导入的) */
    public String getOldAssetNumbers() {
        return oldAssetNumbers;
    }
    /** 旧表资产编号(导入的) */
    public void setOldAssetNumbers(String oldAssetNumbers) {
        this.oldAssetNumbers = oldAssetNumbers;
    }

    /** 旧表表地址(需扫描) */
    public String getOldAddr() {
        return oldAddr;
    }
    /** 旧表表地址(需扫描) */
    public void setOldAddr(String oldAddr) {
        this.oldAddr = oldAddr;
    }

    /** 旧表表地址 和 资产编码 比较 */
    public boolean isOldAddrAndAsset() {
        return oldAddrAndAsset;
    }
    /** 旧表表地址 和 资产编码 比较 */
    public void setOldAddrAndAsset(boolean oldAddrAndAsset) {
        this.oldAddrAndAsset = oldAddrAndAsset;
    }

    /** 旧表资产编号(需扫描) */
    public String getOldAssetNumbersScan() {
        return oldAssetNumbersScan;
    }
    /** 旧表资产编号(需扫描) */
    public void setOldAssetNumbersScan(String oldAssetNumbersScan) {
        this.oldAssetNumbersScan = oldAssetNumbersScan;
    }

    /** 旧电能表止码-电量(需扫描) */
    public String getOldElectricity() {
        return oldElectricity;
    }
    /** 旧电能表止码-电量(需扫描) */
    public void setOldElectricity(String oldElectricity) {
        this.oldElectricity = oldElectricity;
    }

    /** 新表表地址(需扫描) */
    public String getNewAddr() {
        return newAddr;
    }
    /** 新表表地址(需扫描) */
    public void setNewAddr(String newAddr) {
        this.newAddr = newAddr;
    }

    /** 新表表地址 和 资产编码 比较 */
    public boolean isNewAddrAndAsset() {
        return newAddrAndAsset;
    }
    /** 新表表地址 和 资产编码 比较 */
    public void setNewAddrAndAsset(boolean newAddrAndAsset) {
        this.newAddrAndAsset = newAddrAndAsset;
    }

    /** 新表资产编号(需扫描) */
    public String getNewAssetNumbersScan() {
        return newAssetNumbersScan;
    }
    /** 新表资产编号(需扫描) */
    public void setNewAssetNumbersScan(String newAssetNumbersScan) {
        this.newAssetNumbersScan = newAssetNumbersScan;
    }

    /** 新电能表止码-电量(需扫描) */
    public String getNewElectricity() {
        return newElectricity;
    }
    /** 新电能表止码-电量(需扫描) */
    public void setNewElectricity(String newElectricity) {
        this.newElectricity = newElectricity;
    }

    /** 采集器资产编号(需扫描) */
    public String getCollectorAssetNumbersScan() {
        return collectorAssetNumbersScan;
    }
    /** 采集器资产编号(需扫描) */
    public void setCollectorAssetNumbersScan(String collectorAssetNumbersScan) {
        this.collectorAssetNumbersScan = collectorAssetNumbersScan;
    }

    /** 完成换抄时间 */
    public String getTime() {
        return time;
    }
    /** 完成换抄时间 */
    public void setTime(String time) {
        this.time = time;
    }

    /** 拍照图片的路径 */
    public String getPicPath() {
        return picPath;
    }
    /** 拍照图片的路径 */
    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    /** 是否完成(扫描完) */
    public boolean isFinish() {
        return isFinish;
    }
    /** 是否完成(扫描完) */
    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    @Override
    public String toString() {
        return "MeterBean{" +
                "sequenceNumber='" + sequenceNumber + '\'' +
                ", userNumber='" + userNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", userAddr='" + userAddr + '\'' +
                ", area='" + area + '\'' +
                ", oldAssetNumbers='" + oldAssetNumbers + '\'' +
                ", oldAddr='" + oldAddr + '\'' +
                ", oldAddrAndAsset=" + oldAddrAndAsset +
                ", oldAssetNumbersScan='" + oldAssetNumbersScan + '\'' +
                ", oldElectricity='" + oldElectricity + '\'' +
                ", newAddr='" + newAddr + '\'' +
                ", newAddrAndAsset=" + newAddrAndAsset +
                ", newAssetNumbersScan='" + newAssetNumbersScan + '\'' +
                ", newElectricity='" + newElectricity + '\'' +
                ", collectorAssetNumbersScan='" + collectorAssetNumbersScan + '\'' +
                ", time='" + time + '\'' +
                ", picPath='" + picPath + '\'' +
                ", isFinish=" + isFinish +
                '}';
    }
}
