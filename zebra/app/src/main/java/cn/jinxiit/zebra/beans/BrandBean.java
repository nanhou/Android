package cn.jinxiit.zebra.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class BrandBean implements Parcelable
{
    /**
     * created_at : 1528973021
     * status : 2
     * name : 金龙鱼
     * id : 9429
     */

    private String name;
    private String id;

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.name);
        dest.writeString(this.id);
    }

    public BrandBean() {}

    protected BrandBean(Parcel in)
    {
        this.name = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<BrandBean> CREATOR = new Parcelable.Creator<BrandBean>()
    {
        @Override
        public BrandBean createFromParcel(Parcel source) {return new BrandBean(source);}

        @Override
        public BrandBean[] newArray(int size) {return new BrandBean[size];}
    };
}
