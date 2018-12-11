package cn.jinxiit.zebra.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CategoryBean implements Parcelable
{
    /**
     * created_at : 1528968901
     * extra : {"full_path":"22664","level":0,"status":1}
     * seq : 0
     * cat_id : 0
     * name : 健康服务
     * id : 22664
     */

    private String cat_id;
    private String name;
    private String id;
    private List<CategoryBean> children;
    private ExtraBean extra;
    private String market_id;

    public CategoryBean(String name, List<CategoryBean> children)
    {
        this.name = name;
        this.children = children;
    }

    public String getMarket_id()
    {
        return market_id;
    }

    public void setMarket_id(String market_id)
    {
        this.market_id = market_id;
    }

    public List<CategoryBean> getChildren()
    {
        return children;
    }

    public void setChildren(List<CategoryBean> children)
    {
        this.children = children;
    }

    public String getCat_id()
    {
        return cat_id;
    }

    public void setCat_id(String cat_id)
    {
        this.cat_id = cat_id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public ExtraBean getExtra() { return extra;}

    public void setExtra(ExtraBean extra) { this.extra = extra;}

    public static class ExtraBean implements Parcelable
    {
        /**
         * full_path : 22664
         * level : 0
         * status : 1
         */

        private int level;

        public int getLevel() { return level;}

        public void setLevel(int level) { this.level = level;}

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {dest.writeInt(this.level);}

        public ExtraBean() {}

        protected ExtraBean(Parcel in) {this.level = in.readInt();}

        public static final Parcelable.Creator<ExtraBean> CREATOR = new Parcelable.Creator<ExtraBean>()
        {
            @Override
            public ExtraBean createFromParcel(Parcel source) {return new ExtraBean(source);}

            @Override
            public ExtraBean[] newArray(int size) {return new ExtraBean[size];}
        };
    }

    @Override
    public String toString()
    {
        return "CategoryBean{" + "cat_id='" + cat_id + '\'' + ", name='" + name + '\'' + ", id='" + id + '\'' + ", children=" + children + ", extra=" + extra + ", market_id='" + market_id + '\'' + '}';
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.cat_id);
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeList(this.children);
        dest.writeParcelable(this.extra, flags);
        dest.writeString(this.market_id);
    }

    protected CategoryBean(Parcel in)
    {
        this.cat_id = in.readString();
        this.name = in.readString();
        this.id = in.readString();
        this.children = new ArrayList<CategoryBean>();
        in.readList(this.children, CategoryBean.class.getClassLoader());
        this.extra = in.readParcelable(ExtraBean.class.getClassLoader());
        this.market_id = in.readString();
    }

    public static final Parcelable.Creator<CategoryBean> CREATOR = new Parcelable.Creator<CategoryBean>()
    {
        @Override
        public CategoryBean createFromParcel(Parcel source) {return new CategoryBean(source);}

        @Override
        public CategoryBean[] newArray(int size) {return new CategoryBean[size];}
    };
}
