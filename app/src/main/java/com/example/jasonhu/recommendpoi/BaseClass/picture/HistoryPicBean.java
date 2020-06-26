package com.example.jasonhu.recommendpoi.BaseClass.picture;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryPicBean implements Parcelable{
    private boolean isSelected = false;
    //选中的标记
    private int selectedSign=0;
    private String title;
    private long time;
    private String imgPath;
    private long size;
    //所属文件夹
    private String belongFileName;

    public HistoryPicBean(String title, long time, String imgPath,long size,String belongFileName) {
        this.title = title;
        this.time = time;
        this.imgPath = imgPath;
        this.size = size;
        this.belongFileName = belongFileName;
    }

    protected HistoryPicBean(Parcel in) {
        isSelected = in.readByte() != 0;
        title = in.readString();
        time = in.readLong();
        imgPath = in.readString();
        size=in.readLong();
        belongFileName = in.readString();
        selectedSign = in.readInt();
    }

    public static final Parcelable.Creator<HistoryPicBean> CREATOR = new Parcelable.Creator<HistoryPicBean>() {
        @Override
        public HistoryPicBean createFromParcel(Parcel in) {
            return new HistoryPicBean(in);
        }

        @Override
        public HistoryPicBean[] newArray(int size) {
            return new HistoryPicBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getBelongFileName() {
        return belongFileName;
    }

    public void setBelongFileName(String belongFileName) {
        this.belongFileName = belongFileName;
    }

    public int getSelectedSign() {
        return selectedSign;
    }

    public void setSelectedSign(int selectedSign) {
        this.selectedSign = selectedSign;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(title);
        dest.writeLong(time);
        dest.writeString(imgPath);
        dest.writeLong(size);
        dest.writeString(belongFileName);
        dest.writeInt(selectedSign);
    }
}
