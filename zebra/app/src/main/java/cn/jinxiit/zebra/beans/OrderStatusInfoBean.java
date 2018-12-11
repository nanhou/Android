package cn.jinxiit.zebra.beans;

public class OrderStatusInfoBean
{
    @Override
    public String toString()
    {
        return "OrderStatusInfoBean{" + "time=" + time + ", info='" + info + '\'' + '}';
    }

    private long time;
    private String info;

    public OrderStatusInfoBean(long time, String info)
    {
        this.time = time;
        this.info = info;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }
}
