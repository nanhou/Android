package cn.jinxiit.zebra.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class LimitDateBean implements Parcelable
{
    //    full_time: 全时段售卖 boolean, start_date: 开始售卖日期, end_date: 结束售卖日期, full_hour: 全天售卖, limit_hour: []售卖限制时间段

    private boolean full_time;
    private String start_date;
    private String end_date;
    private boolean full_hour;
    private List<String> limit_hour;

    public void initData()
    {
        full_time = false;
        start_date = null;
        end_date = null;
        full_hour = false;
        limit_hour = null;
    }

    @Override
    public String toString()
    {
        return "LimitDateBean{" + "full_time=" + full_time + ", start_date='" + start_date + '\'' + ", end_date='" + end_date + '\'' + ", full_hour=" + full_hour + ", limit_hour=" + limit_hour + '}';
    }

    public boolean isFull_time()
    {
        return full_time;
    }

    public void setFull_time(boolean full_time)
    {
        this.full_time = full_time;
    }

    public String getStart_date()
    {
        return start_date;
    }

    public void setStart_date(String start_date)
    {
        this.start_date = start_date;
    }

    public String getEnd_date()
    {
        return end_date;
    }

    public void setEnd_date(String end_date)
    {
        this.end_date = end_date;
    }

    public boolean isFull_hour()
    {
        return full_hour;
    }

    public void setFull_hour(boolean full_hour)
    {
        this.full_hour = full_hour;
    }

    public List<String> getLimit_hour()
    {
        return limit_hour;
    }

    public void setLimit_hour(List<String> limit_hour)
    {
        this.limit_hour = limit_hour;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeByte(this.full_time ? (byte) 1 : (byte) 0);
        dest.writeString(this.start_date);
        dest.writeString(this.end_date);
        dest.writeByte(this.full_hour ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.limit_hour);
    }

    public LimitDateBean() {}

    protected LimitDateBean(Parcel in)
    {
        this.full_time = in.readByte() != 0;
        this.start_date = in.readString();
        this.end_date = in.readString();
        this.full_hour = in.readByte() != 0;
        this.limit_hour = in.createStringArrayList();
    }

    public static final Parcelable.Creator<LimitDateBean> CREATOR = new Parcelable.Creator<LimitDateBean>()
    {
        @Override
        public LimitDateBean createFromParcel(Parcel source) {return new LimitDateBean(source);}

        @Override
        public LimitDateBean[] newArray(int size) {return new LimitDateBean[size];}
    };
}
