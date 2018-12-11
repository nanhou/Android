package cn.jinxiit.zebra.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class CountBean implements Parcelable
{
    private long lowerFrame;
    private long lessStock;
    private long outStock;
    private long all;
    private long failed;
    private long success;
    private long applying;
    private long examinAll;
    private long other;
    private long notice;
    private long product;
    private long new_order;
    private long picking;
    private long distribution;
    private long distributioning;
    private long abnormal;
    private long after_sale;
    private long cancel;

    public long getLowerFrame()
    {
        return lowerFrame;
    }

    public void setLowerFrame(long lowerFrame)
    {
        this.lowerFrame = lowerFrame;
    }

    public long getLessStock()
    {
        return lessStock;
    }

    public void setLessStock(long lessStock)
    {
        this.lessStock = lessStock;
    }

    public long getOutStock()
    {
        return outStock;
    }

    public void setOutStock(long outStock)
    {
        this.outStock = outStock;
    }

    public long getAll()
    {
        return all;
    }

    public void setAll(long all)
    {
        this.all = all;
    }

    public long getFailed()
    {
        return failed;
    }

    public void setFailed(long failed)
    {
        this.failed = failed;
    }

    public long getSuccess()
    {
        return success;
    }

    public void setSuccess(long success)
    {
        this.success = success;
    }

    public long getApplying()
    {
        return applying;
    }

    public void setApplying(long applying)
    {
        this.applying = applying;
    }

    public long getExaminAll()
    {
        return examinAll;
    }

    public void setExaminAll(long examinAll)
    {
        this.examinAll = examinAll;
    }

    public long getOther()
    {
        return other;
    }

    public void setOther(long other)
    {
        this.other = other;
    }

    public long getNotice()
    {
        return notice;
    }

    public void setNotice(long notice)
    {
        this.notice = notice;
    }

    public long getProduct()
    {
        return product;
    }

    public void setProduct(long product)
    {
        this.product = product;
    }

    public long getNew_order()
    {
        return new_order;
    }

    public void setNew_order(long new_order)
    {
        this.new_order = new_order;
    }

    public long getPicking()
    {
        return picking;
    }

    public void setPicking(long picking)
    {
        this.picking = picking;
    }

    public long getDistribution()
    {
        return distribution;
    }

    public void setDistribution(long distribution)
    {
        this.distribution = distribution;
    }

    public long getDistributioning()
    {
        return distributioning;
    }

    public void setDistributioning(long distributioning)
    {
        this.distributioning = distributioning;
    }

    public long getAbnormal()
    {
        return abnormal;
    }

    public void setAbnormal(long abnormal)
    {
        this.abnormal = abnormal;
    }

    public long getAfter_sale()
    {
        return after_sale;
    }

    public void setAfter_sale(long after_sale)
    {
        this.after_sale = after_sale;
    }

    public long getCancel()
    {
        return cancel;
    }

    public void setCancel(long cancel)
    {
        this.cancel = cancel;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(this.lowerFrame);
        dest.writeLong(this.lessStock);
        dest.writeLong(this.outStock);
        dest.writeLong(this.all);
        dest.writeLong(this.failed);
        dest.writeLong(this.success);
        dest.writeLong(this.applying);
        dest.writeLong(this.examinAll);
        dest.writeLong(this.other);
        dest.writeLong(this.notice);
        dest.writeLong(this.product);
        dest.writeLong(this.new_order);
        dest.writeLong(this.picking);
        dest.writeLong(this.distribution);
        dest.writeLong(this.distributioning);
        dest.writeLong(this.abnormal);
        dest.writeLong(this.after_sale);
        dest.writeLong(this.cancel);
    }

    public CountBean() {}

    protected CountBean(Parcel in)
    {
        this.lowerFrame = in.readLong();
        this.lessStock = in.readLong();
        this.outStock = in.readLong();
        this.all = in.readLong();
        this.failed = in.readLong();
        this.success = in.readLong();
        this.applying = in.readLong();
        this.examinAll = in.readLong();
        this.other = in.readLong();
        this.notice = in.readLong();
        this.product = in.readLong();
        this.new_order = in.readLong();
        this.picking = in.readLong();
        this.distribution = in.readLong();
        this.distributioning = in.readLong();
        this.abnormal = in.readLong();
        this.after_sale = in.readLong();
        this.cancel = in.readLong();
    }

    public static final Parcelable.Creator<CountBean> CREATOR = new Parcelable.Creator<CountBean>()
    {
        @Override
        public CountBean createFromParcel(Parcel source) {return new CountBean(source);}

        @Override
        public CountBean[] newArray(int size) {return new CountBean[size];}
    };
}
