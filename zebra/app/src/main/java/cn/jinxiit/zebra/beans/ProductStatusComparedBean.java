package cn.jinxiit.zebra.beans;

import java.util.List;

public class ProductStatusComparedBean
{
    /**
     * preCount : 639
     * prePrice : 331119
     * count : 448
     * price : 265789
     * productStatsByCount : [{"value":20,"skuId":2011219947,"skuName":"精选 黄玉米 甜玉米带皮 约350g /份"},{"value":19,"skuId":2011213114,"skuName":"精选 红萝卜 约250g /根"},{"value":16,"skuId":2011229572,"skuName":"青葱 约100g /份"},{"value":15,"skuId":2011293377,"skuName":"排骨 切块 约300g /份"},{"value":12,"skuId":2011211312,"skuName":"精选 新鲜菠菜  约400g/份"}]
     * productStatsByPrice : [{"value":19952,"skuId":2011293377,"skuName":"排骨 切块 约300g /份"},{"value":13860,"skuId":2011298910,"skuName":"鲜活 明虾 白对虾 约250g/份"},{"value":10320,"skuId":2011293413,"skuName":"精选 猪蹄 猪脚 约650g /份"},{"value":6580,"skuId":2011339690,"skuName":"好年东五常大米5kg/袋"},{"value":6240,"skuId":2011335238,"skuName":"伊利 无菌砖纯牛奶 250ml*24盒/提"}]
     */

    private int preCount;
    private int prePrice;
    private int count;
    private int price;
    private List<ProductStatusBean> productStatsByCount;
    private List<ProductStatusBean> productStatsByPrice;
    private List<ProductStatusBean> stats;
    private List<ProductStatusBean> elemeStats;
    private List<ProductStatusBean> meituanStats;
    private List<String> dayArr;

    public List<ProductStatusBean> getMeituanStats()
    {
        return meituanStats;
    }

    public void setMeituanStats(List<ProductStatusBean> meituanStats)
    {
        this.meituanStats = meituanStats;
    }

    public List<String> getDayArr()
    {
        return dayArr;
    }

    public void setDayArr(List<String> dayArr)
    {
        this.dayArr = dayArr;
    }

    public List<ProductStatusBean> getElemeStats()
    {
        return elemeStats;
    }

    public void setElemeStats(List<ProductStatusBean> elemeStats)
    {
        this.elemeStats = elemeStats;
    }

    public List<ProductStatusBean> getStats()
    {
        return stats;
    }

    public void setStats(List<ProductStatusBean> stats)
    {
        this.stats = stats;
    }

    public int getPreCount() { return preCount;}

    public void setPreCount(int preCount) { this.preCount = preCount;}

    public int getPrePrice() { return prePrice;}

    public void setPrePrice(int prePrice) { this.prePrice = prePrice;}

    public int getCount() { return count;}

    public void setCount(int count) { this.count = count;}

    public int getPrice() { return price;}

    public void setPrice(int price) { this.price = price;}

    public List<ProductStatusBean> getProductStatsByCount() { return productStatsByCount;}

    public void setProductStatsByCount(List<ProductStatusBean> productStatsByCount) { this.productStatsByCount = productStatsByCount;}

    public List<ProductStatusBean> getProductStatsByPrice() { return productStatsByPrice;}

    public void setProductStatsByPrice(List<ProductStatusBean> productStatsByPrice) { this.productStatsByPrice = productStatsByPrice;}

    public static class ProductStatusBean
    {
        /**
         * dayTime : 2018-06-26
         * count : 569
         * price : 311950
         * skuId : 2011209814
         * skuName : 三全 灌汤简装猪肉水饺(口味随机) 500g/袋
         */

        private String dayTime;
        private int count;
        private int price;
        private String skuId;
        private String skuName;

        public String getDayTime() { return dayTime;}

        public void setDayTime(String dayTime) { this.dayTime = dayTime;}

        public int getCount() { return count;}

        public void setCount(int count) { this.count = count;}

        public int getPrice() { return price;}

        public void setPrice(int price) { this.price = price;}

        public String getSkuId() { return skuId;}

        public void setSkuId(String skuId) { this.skuId = skuId;}

        public String getSkuName() { return skuName;}

        public void setSkuName(String skuName) { this.skuName = skuName;}
    }
}
