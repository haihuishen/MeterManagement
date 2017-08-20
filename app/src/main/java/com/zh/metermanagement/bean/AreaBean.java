package com.zh.metermanagement.bean;

/**
 * 区域
 */
public class AreaBean {

    /** 供电局(供电单位) */
    private String powerSupplyBureau;
    /** 抄表区段 */
    private String theMeteringSection;
    /** 台区 */
    private String courts;

    /** 供电局(供电单位) */
    public String getPowerSupplyBureau() {
        return powerSupplyBureau;
    }
    /** 供电局(供电单位) */
    public void setPowerSupplyBureau(String powerSupplyBureau) {
        this.powerSupplyBureau = powerSupplyBureau;
    }

    /** 抄表区段 */
    public String getTheMeteringSection() {
        return theMeteringSection;
    }
    /** 抄表区段 */
    public void setTheMeteringSection(String theMeteringSection) {
        this.theMeteringSection = theMeteringSection;
    }

    /** 台区 */
    public String getCourts() {
        return courts;
    }
    /** 台区 */
    public void setCourts(String courts) {
        this.courts = courts;
    }
}
