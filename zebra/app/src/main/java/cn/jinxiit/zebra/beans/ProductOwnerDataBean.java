package cn.jinxiit.zebra.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

public class ProductOwnerDataBean implements Parcelable
{
    /**
     * product_id : 37
     * product : {"summary":"","extra":{"images":["ce1a780df5f8ffd6f8f5875ccc5b54359ac02bc7"],"weight":0.3,"price":6500},"content":"","id":37,"timelines":["brand-35247","category-20086","jdOrgCode-317773"],"title":"test-123456"}
     */

    private String product_id;
    private ProductDataBean product;
    private String created_at;
    private String market_id;
    private String cat_id;


    public String getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at(String created_at)
    {
        this.created_at = created_at;
    }

    public String getMarket_id()
    {
        return market_id;
    }

    public void setMarket_id(String market_id)
    {
        this.market_id = market_id;
    }

    public String getCat_id()
    {
        return cat_id;
    }

    public void setCat_id(String cat_id)
    {
        this.cat_id = cat_id;
    }

    public String getProduct_id() { return product_id;}

    public void setProduct_id(String product_id) { this.product_id = product_id;}

    public ProductDataBean getProduct() { return product;}

    public void setProduct(ProductDataBean product) { this.product = product;}

    public static class ProductDataBean implements Parcelable
    {
        /**
         * summary :
         * extra : {"images":["ce1a780df5f8ffd6f8f5875ccc5b54359ac02bc7"],"weight":0.3,"price":6500}
         * content :
         * id : 37
         * timelines : ["brand-35247","category-20086","jdOrgCode-317773"]
         * title : test-123456
         */

        private String summary;
        private ExtraBean extra;
        private String content;
        private String id;
        private String title;
        private List<String> timelines;

        public String getSummary() { return summary;}

        public void setSummary(String summary) { this.summary = summary;}

        public ExtraBean getExtra() { return extra;}

        public void setExtra(ExtraBean extra) { this.extra = extra;}

        public String getContent() { return content;}

        public void setContent(String content) { this.content = content;}

        public String getId() { return id;}

        public void setId(String id) { this.id = id;}

        public String getTitle() { return title;}

        public void setTitle(String title) { this.title = title;}

        public List<String> getTimelines() { return timelines;}

        public void setTimelines(List<String> timelines) { this.timelines = timelines;}

        public static class ExtraBean implements Parcelable
        {
            /**
             * images : ["ce1a780df5f8ffd6f8f5875ccc5b54359ac02bc7"]
             * weight : 0.3
             * price : 6500
             */

            private double weight;
            private long price;
            private List<String> images;
            private String category;
            private String brand;
            private String upcCode;

            public String getCategory()
            {
                return category;
            }

            public void setCategory(String category)
            {
                this.category = category;
            }

            public String getBrand()
            {
                return brand;
            }

            public void setBrand(String brand)
            {
                this.brand = brand;
            }

            public String getUpcCode()
            {
                return upcCode;
            }

            public void setUpcCode(String upcCode)
            {
                this.upcCode = upcCode;
            }

            public double getWeight() { return weight;}

            public void setWeight(double weight) { this.weight = weight;}

            public long getPrice() { return price;}

            public void setPrice(long price) { this.price = price;}

            public List<String> getImages() { return images;}

            public void setImages(List<String> images) { this.images = images;}

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel dest, int flags)
            {
                dest.writeDouble(this.weight);
                dest.writeLong(this.price);
                dest.writeStringList(this.images);
                dest.writeString(this.category);
                dest.writeString(this.brand);
                dest.writeString(this.upcCode);
            }

            public ExtraBean() {}

            protected ExtraBean(Parcel in)
            {
                this.weight = in.readDouble();
                this.price = in.readLong();
                this.images = in.createStringArrayList();
                this.category = in.readString();
                this.brand = in.readString();
                this.upcCode = in.readString();
            }

            public static final Parcelable.Creator<ExtraBean> CREATOR = new Parcelable.Creator<ExtraBean>()
            {
                @Override
                public ExtraBean createFromParcel(Parcel source) {return new ExtraBean(source);}

                @Override
                public ExtraBean[] newArray(int size) {return new ExtraBean[size];}
            };
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(this.summary);
            dest.writeParcelable(this.extra, flags);
            dest.writeString(this.content);
            dest.writeString(this.id);
            dest.writeString(this.title);
            dest.writeStringList(this.timelines);
        }

        public ProductDataBean() {}

        protected ProductDataBean(Parcel in)
        {
            this.summary = in.readString();
            this.extra = in.readParcelable(ExtraBean.class.getClassLoader());
            this.content = in.readString();
            this.id = in.readString();
            this.title = in.readString();
            this.timelines = in.createStringArrayList();
        }

        public static final Parcelable.Creator<ProductDataBean> CREATOR = new Parcelable.Creator<ProductDataBean>()
        {
            @Override
            public ProductDataBean createFromParcel(Parcel source) {return new ProductDataBean(source);}

            @Override
            public ProductDataBean[] newArray(int size) {return new ProductDataBean[size];}
        };
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.product_id);
        dest.writeParcelable(this.product, flags);
        dest.writeString(this.created_at);
        dest.writeString(this.market_id);
        dest.writeString(this.cat_id);
    }

    public ProductOwnerDataBean() {}

    protected ProductOwnerDataBean(Parcel in)
    {
        this.product_id = in.readString();
        this.product = in.readParcelable(ProductDataBean.class.getClassLoader());
        this.created_at = in.readString();
        this.market_id = in.readString();
        this.cat_id = in.readString();
    }

    public static final Parcelable.Creator<ProductOwnerDataBean> CREATOR = new Parcelable.Creator<ProductOwnerDataBean>()
    {
        @Override
        public ProductOwnerDataBean createFromParcel(Parcel source) {return new ProductOwnerDataBean(source);}

        @Override
        public ProductOwnerDataBean[] newArray(int size) {return new ProductOwnerDataBean[size];}
    };
}
