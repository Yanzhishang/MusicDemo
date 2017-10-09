package com.yq.yzs.musicdemo21;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Parcelable {
    private boolean isShow;
    private String title;
    private String name;
    private String time;

    public Data(boolean isShow, String title,String name,  String time) {
        this.isShow = isShow;
        this.title = title;
        this.name = name;
        this.time = time;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Data{" +
                "isShow=" + isShow +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isShow ? 1 : 0));
        dest.writeString(title);
        dest.writeString(name);
        dest.writeString(time);
    }

    protected Data(Parcel in) {
        isShow = in.readByte() != 0;
        title = in.readString();
        name = in.readString();
        time = in.readString();
    }

    public Data(boolean isShow) {
        this.isShow = isShow;
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
