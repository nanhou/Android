package cn.jinxiit.zebra.beans;

import java.util.List;

public class OrderUpBean
{
    //辅助展开
    private boolean isGoodsOpen;
    private boolean isDistributionInfoOpen;

    public boolean isDistributionInfoOpen()
    {
        return isDistributionInfoOpen;
    }

    public void setDistributionInfoOpen(boolean distributionInfoOpen)
    {
        isDistributionInfoOpen = distributionInfoOpen;
    }

    public boolean isGoodsOpen()
    {
        return isGoodsOpen;
    }

    public void setGoodsOpen(boolean goodsOpen)
    {
        isGoodsOpen = goodsOpen;
    }

    /**
     * created_at : 1533033294
     * order : {"buyerMobile":"13880771405","buyerFullName":"以晴","buyerFullAddress":"厦门市湖里区火炬·新天地青派公寓 216C","orderStartTime":"2018-07-31 18:34:54","discountMoney":0,"buyerPayableMoney":3086,"totalMoney":2636,"freightMoney":400,"product":[{"skuId":2011293196,"isGift":false,"skuName":"精选 五花肉 约250g /份","skuCount":1,"skuSpuId":0,"skuWeight":0.25,"adjustMode":0,"categoryId":"20248,20352,20354","skuJdPrice":850,"canteenMoney":0,"promotionType":1,"skuStockOwner":0,"skuStorePrice":850,"firstCategoryName":"肉品","specialServiceTag":"lengcang;"},{"skuId":2011461055,"isGift":false,"skuName":"优质东北珍珠 散装大米  约1kg/份","skuCount":1,"skuSpuId":0,"skuWeight":1,"adjustMode":0,"categoryId":"20392,23321,23326","skuJdPrice":689,"canteenMoney":0,"promotionType":1,"skuStockOwner":0,"skuStorePrice":689,"firstCategoryName":"粮油副食"},{"skuId":2011293406,"isGift":false,"skuName":"现搅前腿 肉馅  约250g/份","skuCount":1,"skuSpuId":0,"skuWeight":0.25,"adjustMode":0,"categoryId":"20248,20352,20357","skuJdPrice":698,"canteenMoney":0,"promotionType":1,"skuStockOwner":0,"skuStorePrice":698,"firstCategoryName":"肉品","specialServiceTag":"lengcang;"},{"skuId":2011229593,"isGift":false,"skuName":"蒜苗 约200g /份","skuCount":1,"skuSpuId":0,"skuWeight":0.2,"adjustMode":0,"categoryId":"20307,20353,20361","skuJdPrice":399,"canteenMoney":0,"promotionType":1,"skuStockOwner":0,"skuStorePrice":399,"firstCategoryName":"水果/蔬菜"}]}
     * dayTime : 2018-07-31
     * third_type : jd
     * status : cancel
     * shop_id : 11718876
     * market_id : 8
     * store_id : 5
     * order_id : 818297295000721
     * id : 3177
     */

    private String created_at;
    private OrderBean order;
    private String dayTime;
    private String third_type;
    private String status;
    private String shop_id;
    private String market_id;
    private String store_id;
    private String order_id;
    private String id;

    public String getCreated_at() { return created_at;}

    public void setCreated_at(String created_at) { this.created_at = created_at;}

    public OrderBean getOrder() { return order;}

    public void setOrder(OrderBean order) { this.order = order;}

    public String getDayTime() { return dayTime;}

    public void setDayTime(String dayTime) { this.dayTime = dayTime;}

    public String getThird_type() { return third_type;}

    public void setThird_type(String third_type) { this.third_type = third_type;}

    public String getStatus() { return status;}

    public void setStatus(String status) { this.status = status;}

    public String getShop_id() { return shop_id;}

    public void setShop_id(String shop_id) { this.shop_id = shop_id;}

    public String getMarket_id() { return market_id;}

    public void setMarket_id(String market_id) { this.market_id = market_id;}

    public String getStore_id() { return store_id;}

    public void setStore_id(String store_id) { this.store_id = store_id;}

    public String getOrder_id() { return order_id;}

    public void setOrder_id(String order_id) { this.order_id = order_id;}

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public static class OrderBean
    {
        /**
         * buyerMobile : 13880771405
         * buyerFullName : 以晴
         * buyerFullAddress : 厦门市湖里区火炬·新天地青派公寓 216C
         * orderStartTime : 2018-07-31 18:34:54
         * discountMoney : 0
         * packagingMoney : 0
         * buyerPayableMoney : 3086
         * totalMoney : 2636
         * freightMoney : 400
         * product : [{"skuId":2011293196,"isGift":false,"skuName":"精选 五花肉 约250g /份","skuCount":1,"skuSpuId":0,"skuWeight":0.25,"adjustMode":0,"categoryId":"20248,20352,20354","skuJdPrice":850,"canteenMoney":0,"promotionType":1,"skuStockOwner":0,"skuStorePrice":850,"firstCategoryName":"肉品","specialServiceTag":"lengcang;"},{"skuId":2011461055,"isGift":false,"skuName":"优质东北珍珠 散装大米  约1kg/份","skuCount":1,"skuSpuId":0,"skuWeight":1,"adjustMode":0,"categoryId":"20392,23321,23326","skuJdPrice":689,"canteenMoney":0,"promotionType":1,"skuStockOwner":0,"skuStorePrice":689,"firstCategoryName":"粮油副食"},{"skuId":2011293406,"isGift":false,"skuName":"现搅前腿 肉馅  约250g/份","skuCount":1,"skuSpuId":0,"skuWeight":0.25,"adjustMode":0,"categoryId":"20248,20352,20357","skuJdPrice":698,"canteenMoney":0,"promotionType":1,"skuStockOwner":0,"skuStorePrice":698,"firstCategoryName":"肉品","specialServiceTag":"lengcang;"},{"skuId":2011229593,"isGift":false,"skuName":"蒜苗 约200g /份","skuCount":1,"skuSpuId":0,"skuWeight":0.2,"adjustMode":0,"categoryId":"20307,20353,20361","skuJdPrice":399,"canteenMoney":0,"promotionType":1,"skuStockOwner":0,"skuStorePrice":399,"firstCategoryName":"水果/蔬菜"}]
         */

        private double cost_price;
        private String orderNum;
        private String buyerMobile;
        private String buyerFullName;
        private String buyerFullAddress;
        private String orderStartTime;
        private double discountMoney;
        private double hongbao;
        private double buyerPayableMoney;
        private double totalMoney;
        private double freightMoney;
        private double packagingMoney;
        private double pointMoney;//积分抵扣
        private long count;
        private String remark;
        private long income;
        private String deliveryManPhone;
        private String deliveryManName;
        private String deliveryTime;
        private OrderStatus orderStatus;
        private long tips;

        public long getTips() {
            return tips;
        }

        public void setTips(long tips) {
            this.tips = tips;
        }

        public static class OrderStatus
        {
            /**
             * logistics_completed_time : 0
             * logistics_fetch_time : 0
             * logistics_cancel_time : 0
             * logistics_confirm_time : 0
             * logistics_send_time : 0
             * order_completed_time : 0
             * order_cancel_time : 0
             * order_confirm_time : 1535524163
             * order_receive_time : 0
             * order_send_time : 1535524152
             */

            private int logistics_completed_time;
            private int logistics_fetch_time;
            private int logistics_cancel_time;
            private int logistics_confirm_time;
            private int logistics_send_time;
            private int order_completed_time;
            private int order_cancel_time;
            private int order_confirm_time;
            private int order_receive_time;
            private int order_send_time;

            public int getLogistics_completed_time() { return logistics_completed_time;}

            public void setLogistics_completed_time(int logistics_completed_time) { this.logistics_completed_time = logistics_completed_time;}

            public int getLogistics_fetch_time() { return logistics_fetch_time;}

            public void setLogistics_fetch_time(int logistics_fetch_time) { this.logistics_fetch_time = logistics_fetch_time;}

            public int getLogistics_cancel_time() { return logistics_cancel_time;}

            public void setLogistics_cancel_time(int logistics_cancel_time) { this.logistics_cancel_time = logistics_cancel_time;}

            public int getLogistics_confirm_time() { return logistics_confirm_time;}

            public void setLogistics_confirm_time(int logistics_confirm_time) { this.logistics_confirm_time = logistics_confirm_time;}

            public int getLogistics_send_time() { return logistics_send_time;}

            public void setLogistics_send_time(int logistics_send_time) { this.logistics_send_time = logistics_send_time;}

            public int getOrder_completed_time() { return order_completed_time;}

            public void setOrder_completed_time(int order_completed_time) { this.order_completed_time = order_completed_time;}

            public int getOrder_cancel_time() { return order_cancel_time;}

            public void setOrder_cancel_time(int order_cancel_time) { this.order_cancel_time = order_cancel_time;}

            public int getOrder_confirm_time() { return order_confirm_time;}

            public void setOrder_confirm_time(int order_confirm_time) { this.order_confirm_time = order_confirm_time;}

            public int getOrder_receive_time() { return order_receive_time;}

            public void setOrder_receive_time(int order_receive_time) { this.order_receive_time = order_receive_time;}

            public int getOrder_send_time() { return order_send_time;}

            public void setOrder_send_time(int order_send_time) { this.order_send_time = order_send_time;}
        }

        public OrderStatus getOrderStatus()
        {
            return orderStatus;
        }

        public void setOrderStatus(OrderStatus orderStatus)
        {
            this.orderStatus = orderStatus;
        }

        public double getCost_price()
        {
            return cost_price;
        }

        public void setCost_price(double cost_price)
        {
            this.cost_price = cost_price;
        }

        public String getOrderNum()
        {
            return orderNum;
        }

        public void setOrderNum(String orderNum)
        {
            this.orderNum = orderNum;
        }

        public double getPointMoney()
        {
            return pointMoney;
        }

        public void setPointMoney(double pointMoney)
        {
            this.pointMoney = pointMoney;
        }

        public String getDeliveryManPhone()
        {
            return deliveryManPhone;
        }

        public void setDeliveryManPhone(String deliveryManPhone)
        {
            this.deliveryManPhone = deliveryManPhone;
        }

        public String getDeliveryManName()
        {
            return deliveryManName;
        }

        public void setDeliveryManName(String deliveryManName)
        {
            this.deliveryManName = deliveryManName;
        }

        public String getDeliveryTime()
        {
            return deliveryTime;
        }

        public void setDeliveryTime(String deliveryTime)
        {
            this.deliveryTime = deliveryTime;
        }

        public long getIncome()
        {
            return income;
        }

        public void setIncome(long income)
        {
            this.income = income;
        }

        public String getRemark()
        {
            return remark;
        }

        public void setRemark(String remark)
        {
            this.remark = remark;
        }

        public long getCount()
        {
            return count;
        }

        public void setCount(long count)
        {
            this.count = count;
        }

        private List<ProductBean> product;

        public double getHongbao()
        {
            return hongbao;
        }

        public void setHongbao(double hongbao)
        {
            this.hongbao = hongbao;
        }

        public double getPackagingMoney()
        {
            return packagingMoney;
        }

        public void setPackagingMoney(double packagingMoney)
        {
            this.packagingMoney = packagingMoney;
        }

        public String getBuyerMobile() { return buyerMobile;}

        public void setBuyerMobile(String buyerMobile) { this.buyerMobile = buyerMobile;}

        public String getBuyerFullName() { return buyerFullName;}

        public void setBuyerFullName(String buyerFullName) { this.buyerFullName = buyerFullName;}

        public String getBuyerFullAddress() { return buyerFullAddress;}

        public void setBuyerFullAddress(String buyerFullAddress) { this.buyerFullAddress = buyerFullAddress;}

        public String getOrderStartTime() { return orderStartTime;}

        public void setOrderStartTime(String orderStartTime) { this.orderStartTime = orderStartTime;}

        public double getDiscountMoney() { return discountMoney;}

        public void setDiscountMoney(double discountMoney) { this.discountMoney = discountMoney;}

        public double getBuyerPayableMoney() { return buyerPayableMoney;}

        public void setBuyerPayableMoney(double buyerPayableMoney) { this.buyerPayableMoney = buyerPayableMoney;}

        public double getTotalMoney() { return totalMoney;}

        public void setTotalMoney(double totalMoney) { this.totalMoney = totalMoney;}

        public double getFreightMoney() { return freightMoney;}

        public void setFreightMoney(double freightMoney) { this.freightMoney = freightMoney;}

        public List<ProductBean> getProduct() { return product;}

        public void setProduct(List<ProductBean> product) { this.product = product;}

        public static class ProductBean
        {
            /**
             * totalMoney : 1093
             * count : 1
             * price : 1093
             * name : 丘比 沙拉酱 150g/瓶
             */

            private double totalMoney;
            private long count;
            private double price;
            private String name;
            private String upc;
            private List<String> images;

            public List<String> getImages()
            {
                return images;
            }

            public void setImages(List<String> images)
            {
                this.images = images;
            }

            public String getUpc()
            {
                return upc;
            }

            public void setUpc(String upc)
            {
                this.upc = upc;
            }

            public double getTotalMoney() { return totalMoney;}

            public void setTotalMoney(double totalMoney) { this.totalMoney = totalMoney;}

            public long getCount() { return count;}

            public void setCount(long count) { this.count = count;}

            public double getPrice() { return price;}

            public void setPrice(double price) { this.price = price;}

            public String getName() { return name;}

            public void setName(String name) { this.name = name;}
        }
    }
}
