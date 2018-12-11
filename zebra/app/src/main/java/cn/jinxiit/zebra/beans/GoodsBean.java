package cn.jinxiit.zebra.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class GoodsBean implements Parcelable
{

    /**
     * created_at : 1532921144
     * product_id : 9456
     * market_id : 8
     * cat_id : 22
     * product : {"created_at":1532921144,"timelines":["brand-9429","category-20535","product8","upcCode-6948195862093"],"product_status":"上架","sale_time":{"end_date":"2018-07-30","full_hour":false,"full_time":false,"limit_hour":["03:00-05:00"],"start_date":"2018-07-30"},"priceAndStock":[{"created_at":1532921144,"extra":null,"binded":0,"stock":0,"price":0,"third_type":"meituan","product_id":9456,"store_id":5},{"created_at":1532921144,"extra":null,"binded":1,"stock":278,"price":5200,"third_type":"jd","product_id":9456,"store_id":5},{"created_at":1532921144,"extra":null,"binded":1,"stock":277,"price":5200,"third_type":"eleme","product_id":9456,"store_id":5}],"extra":{"images":["168ca9b1c2bf11784797341b722e0decca8cdfc0","09927f3ebd3d966f738b18f49e1f9c01d62376cb","2dc2497fa5fb42f76bd7d27ef592ab3eb5df85c2"],"attribute":[{"att_label":["看看","图",""],"att_name":"流量卡"}],"weight":10,"category":"20535","brand":"9429","min_purchase":1,"unit":"份","extra_summary":"","weight_unit":"KG","product_code":"0000006238","cat_id":22,"price":5200,"pack_fee":5500,"place":"","upcCode":"6948195862093"},"images":["168ca9b1c2bf11784797341b722e0decca8cdfc0","09927f3ebd3d966f738b18f49e1f9c01d62376cb","2dc2497fa5fb42f76bd7d27ef592ab3eb5df85c2"],"min_purchase":1,"place":"","summary":"lol路","weight_unit":"KG","unit":"份","pack_fee":5500,"price":5200,"weight":10,"attribute":[{"att_label":["看看","图",""],"att_name":"流量卡"}],"product_code":"","cat_id":22,"name":"金龙鱼优质丝苗米10kg/包","id":9456}
     */

    private String message;
    private String status;
    private String created_at;
    private String product_id;
    private String market_id;
    private String cat_id;
    private ProductBean product;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCreated_at() { return created_at;}

    public void setCreated_at(String created_at) { this.created_at = created_at;}

    public String getProduct_id() { return product_id;}

    public void setProduct_id(String product_id) { this.product_id = product_id;}

    public String getMarket_id() { return market_id;}

    public void setMarket_id(String market_id) { this.market_id = market_id;}

    public String getCat_id() { return cat_id;}

    public void setCat_id(String cat_id) { this.cat_id = cat_id;}

    public ProductBean getProduct() { return product;}

    public void setProduct(ProductBean product) { this.product = product;}

    public static class ProductBean implements Parcelable
    {
        /**
         * created_at : 1532921144
         * timelines : ["brand-9429","category-20535","product8","upcCode-6948195862093"]
         * product_status : 上架
         * sale_time : {"end_date":"2018-07-30","full_hour":false,"full_time":false,"limit_hour":["03:00-05:00"],"start_date":"2018-07-30"}
         * priceAndStock : [{"created_at":1532921144,"extra":null,"binded":0,"stock":0,"price":0,"third_type":"meituan","product_id":9456,"store_id":5},{"created_at":1532921144,"extra":null,"binded":1,"stock":278,"price":5200,"third_type":"jd","product_id":9456,"store_id":5},{"created_at":1532921144,"extra":null,"binded":1,"stock":277,"price":5200,"third_type":"eleme","product_id":9456,"store_id":5}]
         * extra : {"images":["168ca9b1c2bf11784797341b722e0decca8cdfc0","09927f3ebd3d966f738b18f49e1f9c01d62376cb","2dc2497fa5fb42f76bd7d27ef592ab3eb5df85c2"],"attribute":[{"att_label":["看看","图",""],"att_name":"流量卡"}],"weight":10,"category":"20535","brand":"9429","min_purchase":1,"unit":"份","extra_summary":"","weight_unit":"KG","product_code":"0000006238","cat_id":22,"price":5200,"pack_fee":5500,"place":"","upcCode":"6948195862093"}
         * images : ["168ca9b1c2bf11784797341b722e0decca8cdfc0","09927f3ebd3d966f738b18f49e1f9c01d62376cb","2dc2497fa5fb42f76bd7d27ef592ab3eb5df85c2"]
         * min_purchase : 1
         * place :
         * summary : lol路
         * weight_unit : KG
         * unit : 份
         * pack_fee : 5500
         * price : 5200
         * weight : 10
         * attribute : [{"att_label":["看看","图",""],"att_name":"流量卡"}]
         * product_code :
         * cat_id : 22
         * name : 金龙鱼优质丝苗米10kg/包
         * id : 9456
         */

        private int top;
        private String created_at;
        private String product_status;
        private SaleTimeBean sale_time;
        private ExtraBean extra;
        private long min_purchase;
        private String place;
        private String summary;
        private String weight_unit;
        private String unit;
        private long pack_fee;
        private long price;
        private long purchase_price;
        private long cost_price;
        private String weight;
        private String product_code;
        private String cat_id;
        private String name;
        private String id;
        private List<String> timelines;
        private List<PriceAndStockBean> priceAndStock;
        private List<String> images;
        private List<AttributeBean> attribute;

        public long getPurchase_price()
        {
            return purchase_price;
        }

        public void setPurchase_price(long purchase_price)
        {
            this.purchase_price = purchase_price;
        }

        public long getCost_price()
        {
            return cost_price;
        }

        public void setCost_price(long cost_price)
        {
            this.cost_price = cost_price;
        }

        public int getTop()
        {
            return top;
        }

        public void setTop(int top)
        {
            this.top = top;
        }

        public String getCreated_at() { return created_at;}

        public void setCreated_at(String created_at) { this.created_at = created_at;}

        public String getProduct_status() { return product_status;}

        public void setProduct_status(String product_status) { this.product_status = product_status;}

        public SaleTimeBean getSale_time() { return sale_time;}

        public void setSale_time(SaleTimeBean sale_time) { this.sale_time = sale_time;}

        public ExtraBean getExtra() { return extra;}

        public void setExtra(ExtraBean extra) { this.extra = extra;}

        public long getMin_purchase() { return min_purchase;}

        public void setMin_purchase(long min_purchase) { this.min_purchase = min_purchase;}

        public String getPlace() { return place;}

        public void setPlace(String place) { this.place = place;}

        public String getSummary() { return summary;}

        public void setSummary(String summary) { this.summary = summary;}

        public String getWeight_unit() { return weight_unit;}

        public void setWeight_unit(String weight_unit) { this.weight_unit = weight_unit;}

        public String getUnit() { return unit;}

        public void setUnit(String unit) { this.unit = unit;}

        public long getPack_fee() { return pack_fee;}

        public void setPack_fee(long pack_fee) { this.pack_fee = pack_fee;}

        public long getPrice() { return price;}

        public void setPrice(long price) { this.price = price;}

        public String getWeight() { return weight;}

        public void setWeight(String weight) { this.weight = weight;}

        public String getProduct_code() { return product_code;}

        public void setProduct_code(String product_code) { this.product_code = product_code;}

        public String getCat_id() { return cat_id;}

        public void setCat_id(String cat_id) { this.cat_id = cat_id;}

        public String getName() { return name;}

        public void setName(String name) { this.name = name;}

        public String getId() { return id;}

        public void setId(String id) { this.id = id;}

        public List<String> getTimelines() { return timelines;}

        public void setTimelines(List<String> timelines) { this.timelines = timelines;}

        public List<PriceAndStockBean> getPriceAndStock() { return priceAndStock;}

        public void setPriceAndStock(List<PriceAndStockBean> priceAndStock) { this.priceAndStock = priceAndStock;}

        public List<String> getImages() { return images;}

        public void setImages(List<String> images) { this.images = images;}

        public List<AttributeBean> getAttribute() { return attribute;}

        public void setAttribute(List<AttributeBean> attribute) { this.attribute = attribute;}

        public static class SaleTimeBean implements Parcelable
        {
            /**
             * end_date : 2018-07-30
             * full_hour : false
             * full_time : false
             * limit_hour : ["03:00-05:00"]
             * start_date : 2018-07-30
             */

            private String end_date;
            private boolean full_hour;
            private boolean full_time;
            private String start_date;
            private List<String> limit_hour;

            public String getEnd_date() { return end_date;}

            public void setEnd_date(String end_date) { this.end_date = end_date;}

            public boolean isFull_hour() { return full_hour;}

            public void setFull_hour(boolean full_hour) { this.full_hour = full_hour;}

            public boolean isFull_time() { return full_time;}

            public void setFull_time(boolean full_time) { this.full_time = full_time;}

            public String getStart_date() { return start_date;}

            public void setStart_date(String start_date) { this.start_date = start_date;}

            public List<String> getLimit_hour() { return limit_hour;}

            public void setLimit_hour(List<String> limit_hour) { this.limit_hour = limit_hour;}

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel dest, int flags)
            {
                dest.writeString(this.end_date);
                dest.writeByte(this.full_hour ? (byte) 1 : (byte) 0);
                dest.writeByte(this.full_time ? (byte) 1 : (byte) 0);
                dest.writeString(this.start_date);
                dest.writeStringList(this.limit_hour);
            }

            public SaleTimeBean() {}

            protected SaleTimeBean(Parcel in)
            {
                this.end_date = in.readString();
                this.full_hour = in.readByte() != 0;
                this.full_time = in.readByte() != 0;
                this.start_date = in.readString();
                this.limit_hour = in.createStringArrayList();
            }

            public static final Parcelable.Creator<SaleTimeBean> CREATOR = new Parcelable.Creator<SaleTimeBean>()
            {
                @Override
                public SaleTimeBean createFromParcel(Parcel source) {return new SaleTimeBean(source);}

                @Override
                public SaleTimeBean[] newArray(int size) {return new SaleTimeBean[size];}
            };
        }

        public static class ExtraBean implements Parcelable
        {
            /**
             * images : ["168ca9b1c2bf11784797341b722e0decca8cdfc0","09927f3ebd3d966f738b18f49e1f9c01d62376cb","2dc2497fa5fb42f76bd7d27ef592ab3eb5df85c2"]
             * attribute : [{"att_label":["看看","图",""],"att_name":"流量卡"}]
             * weight : 10
             * category : 20535
             * brand : 9429
             * min_purchase : 1
             * unit : 份
             * extra_summary :
             * weight_unit : KG
             * product_code : 0000006238
             * cat_id : 22
             * price : 5200
             * pack_fee : 5500
             * place :
             * upcCode : 6948195862093
             */

            private String title;
            private String weight;
            private String category;
            private String brand;
            private long min_purchase;
            private String unit;
            private String extra_summary;
            private String weight_unit;
            private String product_code;
            private String cat_id;
            private long price;
            private long pack_fee;
            private String place;
            private String upcCode;
            private List<String> images;
            private List<AttributeBean> attribute;

            public String getTitle()
            {
                return title;
            }

            public void setTitle(String title)
            {
                this.title = title;
            }

            public String getWeight() { return weight;}

            public void setWeight(String weight) { this.weight = weight;}

            public String getCategory() { return category;}

            public void setCategory(String category) { this.category = category;}

            public String getBrand() { return brand;}

            public void setBrand(String brand) { this.brand = brand;}

            public long getMin_purchase() { return min_purchase;}

            public void setMin_purchase(long min_purchase) { this.min_purchase = min_purchase;}

            public String getUnit() { return unit;}

            public void setUnit(String unit) { this.unit = unit;}

            public String getExtra_summary() { return extra_summary;}

            public void setExtra_summary(String extra_summary) { this.extra_summary = extra_summary;}

            public String getWeight_unit() { return weight_unit;}

            public void setWeight_unit(String weight_unit) { this.weight_unit = weight_unit;}

            public String getProduct_code() { return product_code;}

            public void setProduct_code(String product_code) { this.product_code = product_code;}

            public String getCat_id() { return cat_id;}

            public void setCat_id(String cat_id) { this.cat_id = cat_id;}

            public long getPrice() { return price;}

            public void setPrice(long price) { this.price = price;}

            public long getPack_fee() { return pack_fee;}

            public void setPack_fee(long pack_fee) { this.pack_fee = pack_fee;}

            public String getPlace() { return place;}

            public void setPlace(String place) { this.place = place;}

            public String getUpcCode() { return upcCode;}

            public void setUpcCode(String upcCode) { this.upcCode = upcCode;}

            public List<String> getImages() { return images;}

            public void setImages(List<String> images) { this.images = images;}

            public List<AttributeBean> getAttribute() { return attribute;}

            public void setAttribute(List<AttributeBean> attribute) { this.attribute = attribute;}

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel dest, int flags)
            {
                dest.writeString(this.title);
                dest.writeString(this.weight);
                dest.writeString(this.category);
                dest.writeString(this.brand);
                dest.writeLong(this.min_purchase);
                dest.writeString(this.unit);
                dest.writeString(this.extra_summary);
                dest.writeString(this.weight_unit);
                dest.writeString(this.product_code);
                dest.writeString(this.cat_id);
                dest.writeLong(this.price);
                dest.writeLong(this.pack_fee);
                dest.writeString(this.place);
                dest.writeString(this.upcCode);
                dest.writeStringList(this.images);
                dest.writeTypedList(this.attribute);
            }

            public ExtraBean() {}

            protected ExtraBean(Parcel in)
            {
                this.title = in.readString();
                this.weight = in.readString();
                this.category = in.readString();
                this.brand = in.readString();
                this.min_purchase = in.readLong();
                this.unit = in.readString();
                this.extra_summary = in.readString();
                this.weight_unit = in.readString();
                this.product_code = in.readString();
                this.cat_id = in.readString();
                this.price = in.readLong();
                this.pack_fee = in.readLong();
                this.place = in.readString();
                this.upcCode = in.readString();
                this.images = in.createStringArrayList();
                this.attribute = in.createTypedArrayList(AttributeBean.CREATOR);
            }

            public static final Parcelable.Creator<ExtraBean> CREATOR = new Parcelable.Creator<ExtraBean>()
            {
                @Override
                public ExtraBean createFromParcel(Parcel source) {return new ExtraBean(source);}

                @Override
                public ExtraBean[] newArray(int size) {return new ExtraBean[size];}
            };
        }

        public static class PriceAndStockBean implements Parcelable
        {
            /**
             * created_at : 1532921144
             * extra : null
             * binded : 0
             * stock : 0
             * price : 0
             * third_type : meituan
             * product_id : 9456
             * store_id : 5
             */

            private String created_at;
            private int binded;
            private long stock;
            private long price;
            private String third_type;
            private String product_id;
            private String store_id;
            private String product_status;

            public String getProduct_status() {
                return product_status;
            }

            public void setProduct_status(String product_status) {
                this.product_status = product_status;
            }

            public String getCreated_at() { return created_at;}

            public void setCreated_at(String created_at) { this.created_at = created_at;}

            public int getBinded() { return binded;}

            public void setBinded(int binded) { this.binded = binded;}

            public long getStock() { return stock;}

            public void setStock(long stock) { this.stock = stock;}

            public long getPrice() { return price;}

            public void setPrice(long price) { this.price = price;}

            public String getThird_type() { return third_type;}

            public void setThird_type(String third_type) { this.third_type = third_type;}

            public String getProduct_id() { return product_id;}

            public void setProduct_id(String product_id) { this.product_id = product_id;}

            public String getStore_id() { return store_id;}

            public void setStore_id(String store_id) { this.store_id = store_id;}

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel dest, int flags)
            {
                dest.writeString(this.created_at);
                dest.writeInt(this.binded);
                dest.writeLong(this.stock);
                dest.writeLong(this.price);
                dest.writeString(this.third_type);
                dest.writeString(this.product_id);
                dest.writeString(this.store_id);
                dest.writeString(this.product_status);
            }

            public PriceAndStockBean() {}

            protected PriceAndStockBean(Parcel in)
            {
                this.created_at = in.readString();
                this.binded = in.readInt();
                this.stock = in.readLong();
                this.price = in.readLong();
                this.third_type = in.readString();
                this.product_id = in.readString();
                this.store_id = in.readString();
                this.product_status = in.readString();
            }

            public static final Parcelable.Creator<PriceAndStockBean> CREATOR = new Parcelable.Creator<PriceAndStockBean>()
            {
                @Override
                public PriceAndStockBean createFromParcel(Parcel source) {return new PriceAndStockBean(source);}

                @Override
                public PriceAndStockBean[] newArray(int size) {return new PriceAndStockBean[size];}
            };
        }

        public static class AttributeBean implements Parcelable
        {
            /**
             * att_label : ["看看","图",""]
             * att_name : 流量卡
             */

            private String att_name;
            private List<String> att_label;

            public String getAtt_name() { return att_name;}

            public void setAtt_name(String att_name) { this.att_name = att_name;}

            public List<String> getAtt_label() { return att_label;}

            public void setAtt_label(List<String> att_label) { this.att_label = att_label;}

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel dest, int flags)
            {
                dest.writeString(this.att_name);
                dest.writeStringList(this.att_label);
            }

            public AttributeBean() {}

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

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(this.top);
            dest.writeString(this.created_at);
            dest.writeString(this.product_status);
            dest.writeParcelable(this.sale_time, flags);
            dest.writeParcelable(this.extra, flags);
            dest.writeLong(this.min_purchase);
            dest.writeString(this.place);
            dest.writeString(this.summary);
            dest.writeString(this.weight_unit);
            dest.writeString(this.unit);
            dest.writeLong(this.pack_fee);
            dest.writeLong(this.price);
            dest.writeLong(this.cost_price);
            dest.writeLong(this.purchase_price);
            dest.writeString(this.weight);
            dest.writeString(this.product_code);
            dest.writeString(this.cat_id);
            dest.writeString(this.name);
            dest.writeString(this.id);
            dest.writeStringList(this.timelines);
            dest.writeTypedList(this.priceAndStock);
            dest.writeStringList(this.images);
            dest.writeTypedList(this.attribute);
        }

        public ProductBean() {}

        protected ProductBean(Parcel in)
        {
            this.top = in.readInt();
            this.created_at = in.readString();
            this.product_status = in.readString();
            this.sale_time = in.readParcelable(SaleTimeBean.class.getClassLoader());
            this.extra = in.readParcelable(ExtraBean.class.getClassLoader());
            this.min_purchase = in.readLong();
            this.place = in.readString();
            this.summary = in.readString();
            this.weight_unit = in.readString();
            this.unit = in.readString();
            this.pack_fee = in.readLong();
            this.price = in.readLong();
            this.cost_price = in.readLong();
            this.purchase_price = in.readLong();
            this.weight = in.readString();
            this.product_code = in.readString();
            this.cat_id = in.readString();
            this.name = in.readString();
            this.id = in.readString();
            this.timelines = in.createStringArrayList();
            this.priceAndStock = in.createTypedArrayList(PriceAndStockBean.CREATOR);
            this.images = in.createStringArrayList();
            this.attribute = in.createTypedArrayList(AttributeBean.CREATOR);
        }

        public static final Parcelable.Creator<ProductBean> CREATOR = new Parcelable.Creator<ProductBean>()
        {
            @Override
            public ProductBean createFromParcel(Parcel source) {return new ProductBean(source);}

            @Override
            public ProductBean[] newArray(int size) {return new ProductBean[size];}
        };
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.message);
        dest.writeString(this.status);
        dest.writeString(this.created_at);
        dest.writeString(this.product_id);
        dest.writeString(this.market_id);
        dest.writeString(this.cat_id);
        dest.writeParcelable(this.product, flags);
    }

    public GoodsBean() {}

    protected GoodsBean(Parcel in)
    {
        this.message = in.readString();
        this.status = in.readString();
        this.created_at = in.readString();
        this.product_id = in.readString();
        this.market_id = in.readString();
        this.cat_id = in.readString();
        this.product = in.readParcelable(ProductBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<GoodsBean> CREATOR = new Parcelable.Creator<GoodsBean>()
    {
        @Override
        public GoodsBean createFromParcel(Parcel source) {return new GoodsBean(source);}

        @Override
        public GoodsBean[] newArray(int size) {return new GoodsBean[size];}
    };
}
