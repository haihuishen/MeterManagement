package com.zh.metermanagement.view.linkagerecyclerview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 *
 *
 */

public class RightBean implements Parcelable {
    private String name;
    private String titleName;
    private String tag;
    private boolean isTitle;
    private String imgsrc;

    /** "抄表区段" */
    private String meteringSection;
    /** "台区" */
    private String courts;


    public RightBean(String name) {
        this.name = name;
    }

    protected RightBean(Parcel in) {
        name = in.readString();
        titleName = in.readString();
        tag = in.readString();
        isTitle = in.readByte() != 0;
        imgsrc = in.readString();
        meteringSection = in.readString();
        courts = in.readString();
    }

    public static final Creator<RightBean> CREATOR = new Creator<RightBean>() {
        @Override
        public RightBean createFromParcel(Parcel in) {
            return new RightBean(in);
        }

        @Override
        public RightBean[] newArray(int size) {
            return new RightBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getMeteringSection() {
        return meteringSection;
    }

    public void setMeteringSection(String meteringSection) {
        this.meteringSection = meteringSection;
    }

    public String getCourts() {
        return courts;
    }

    public void setCourts(String courts) {
        this.courts = courts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(titleName);
        dest.writeString(tag);
        dest.writeByte((byte) (isTitle ? 1 : 0));
        dest.writeString(imgsrc);
        dest.writeString(meteringSection);
        dest.writeString(courts);
    }
}
