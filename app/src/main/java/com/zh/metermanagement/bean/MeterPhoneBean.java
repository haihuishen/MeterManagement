package com.zh.metermanagement.bean;

/**
 * Created by Administrator on 2017/8/14.
 */

public class MeterPhoneBean {

    /** 用户编号 */
    private String userNumber;
    /** 用户电话 */
    private String userPhone;
    /** 抄表区段 */
    private String theMeteringSection;

    /** 用户编号 */
    public String getUserNumber() {
        return userNumber;
    }
    /** 用户编号 */
    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    /** 用户电话 */
    public String getUserPhone() {
        return userPhone;
    }
    /** 用户电话 */
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    /** 抄表区段 */
    public String getTheMeteringSection() {
        return theMeteringSection;
    }
    /** 抄表区段 */
    public void setTheMeteringSection(String theMeteringSection) {
        this.theMeteringSection = theMeteringSection;
    }

    @Override
    public String toString() {
        return "MeterPhoneBean{" +
                "userNumber='" + userNumber + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", theMeteringSection='" + theMeteringSection + '\'' +
                '}';
    }
}
