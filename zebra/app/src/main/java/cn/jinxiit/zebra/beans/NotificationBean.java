package cn.jinxiit.zebra.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationBean implements Parcelable
{
    /**
     * created_at : 1534405260
     * extra : null
     * is_read : 0
     * content : what do you want to do
     * title : 提交商品被驳回
     * message_type : product
     * store_id : 5
     * market_id : 8
     * id : 1
     */

    private long created_at;
    private int is_read;
    private String content;
    private String title;
    private String message_type;
    private String store_id;
    private String market_id;
    private String id;

    public long getCreated_at() { return created_at;}

    public void setCreated_at(long created_at) { this.created_at = created_at;}

    public int getIs_read() { return is_read;}

    public void setIs_read(int is_read) { this.is_read = is_read;}

    public String getContent() { return content;}

    public void setContent(String content) { this.content = content;}

    public String getTitle() { return title;}

    public void setTitle(String title) { this.title = title;}

    public String getMessage_type() { return message_type;}

    public void setMessage_type(String message_type) { this.message_type = message_type;}

    public String getStore_id() { return store_id;}

    public void setStore_id(String store_id) { this.store_id = store_id;}

    public String getMarket_id() { return market_id;}

    public void setMarket_id(String market_id) { this.market_id = market_id;}

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(this.created_at);
        dest.writeInt(this.is_read);
        dest.writeString(this.content);
        dest.writeString(this.title);
        dest.writeString(this.message_type);
        dest.writeString(this.store_id);
        dest.writeString(this.market_id);
        dest.writeString(this.id);
    }

    public NotificationBean() {}

    protected NotificationBean(Parcel in)
    {
        this.created_at = in.readLong();
        this.is_read = in.readInt();
        this.content = in.readString();
        this.title = in.readString();
        this.message_type = in.readString();
        this.store_id = in.readString();
        this.market_id = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<NotificationBean> CREATOR = new Parcelable.Creator<NotificationBean>()
    {
        @Override
        public NotificationBean createFromParcel(Parcel source) {return new NotificationBean(source);}

        @Override
        public NotificationBean[] newArray(int size) {return new NotificationBean[size];}
    };
}
