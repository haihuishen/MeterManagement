package com.zh.metermanagement.view.linkagerecyclerview;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/15.
 */

public class ShenBean implements Parcelable {

    /** 供电局(供电单位) */
    private String powerSupplyBureau;

    private ArrayList<SupplyBureau> supplyBureaus;

    //----------------------------------------------
    public ShenBean(){

    }
    protected ShenBean(Parcel in) {
        powerSupplyBureau = in.readString();
    }




    /** 供电局(供电单位) */
    public String getPowerSupplyBureau() {
        return powerSupplyBureau;
    }
    /** 供电局(供电单位) */
    public void setPowerSupplyBureau(String powerSupplyBureau) {
        this.powerSupplyBureau = powerSupplyBureau;
    }


    public ArrayList<SupplyBureau> getSupplyBureaus() {
        return supplyBureaus;
    }

    public void setSupplyBureaus(ArrayList<SupplyBureau> supplyBureaus) {
        this.supplyBureaus = supplyBureaus;
    }




    public static final Creator<ShenBean> CREATOR = new Creator<ShenBean>() {
        @Override
        public ShenBean createFromParcel(Parcel in) {
            return new ShenBean(in);
        }

        @Override
        public ShenBean[] newArray(int size) {
            return new ShenBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(powerSupplyBureau);
    }

    public static class SupplyBureau{
        /** 抄表区段 */
        private String theMeteringSection;
        /** 台区 */
        private String courts;

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


    @Override
    public String toString() {
        String s = "";
        for(int i=0; i<supplyBureaus.size(); i++){
            s += "ShenBean{" +
                    "powerSupplyBureau='" + powerSupplyBureau + '\'' +
                    ", courts=" + supplyBureaus.get(i).getCourts() +
                    ", theMeteringSection=" + supplyBureaus.get(i).getTheMeteringSection() +
                    '}';
        }
        return s;
    }
}
