package cn.jinxiit.zebra.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AttributeBean implements Parcelable
{
    private String att_name;
    private List<String> att_label;

    @Override
    public String toString()
    {
        return "AttributeBean{" + "att_name='" + att_name + '\'' + ", att_label=" + att_label + '}';
    }

    public String getAtt_name()
    {
        return att_name;
    }

    public void setAtt_name(String att_name)
    {
        this.att_name = att_name;
    }

    public List<String> getAtt_label()
    {
        return att_label;
    }

    public void setAtt_label(List<String> att_label)
    {
        this.att_label = att_label;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.att_name);
        dest.writeStringList(this.att_label);
    }

    public AttributeBean()
    {
        att_label = new ArrayList<>();
        att_label.add("");
        att_label.add("");
        att_label.add("");
    }

    protected AttributeBean(Parcel in)
    {
        this.att_name = in.readString();
        this.att_label = in.createStringArrayList();
    }

    public static final Parcelable.Creator<AttributeBean> CREATOR = new Parcelable.Creator<AttributeBean>()
    {
        @Override
        public AttributeBean createFromParcel(Parcel source) {return new AttributeBean(source);}

        @Override
        public AttributeBean[] newArray(int size) {return new AttributeBean[size];}
    };
}
