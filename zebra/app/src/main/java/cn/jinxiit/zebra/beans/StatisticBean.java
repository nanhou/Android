package cn.jinxiit.zebra.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StatisticBean
{

    @Override
    public String toString()
    {
        return "StatisticBean{" + "visiter=" + visiter + ", exposure=" + exposure + ", totalOrder=" + totalOrder + ", preVisiter=" + preVisiter + ", preExposure=" + preExposure + ", preTotalOrder=" + preTotalOrder + ", totalIncoming=" + totalIncoming + ", totalCost=" + totalCost + ", loseIncoming=" + loseIncoming + ", finishOrder=" + finishOrder + ", failOrder=" + failOrder + ", preTotalIncoming=" + preTotalIncoming + ", preTotalCost=" + preTotalCost + ", statistic=" + statistic + '}';
    }

    /**
     * visiter : 0
     * exposure : 0
     * totalOrder : 69
     * preVisiter : 2654
     * preExposure : 4656
     * preTotalOrder : 70
     * totalIncoming : 297878
     * totalCost : 81500
     * loseIncoming : 11590
     * finishOrder : 66
     * failOrder : 3
     * preTotalIncoming : 297878
     * preTotalCost : 81500
     * statistic : [{"totalIncoming":297878,"totalCost":81500,"loseIncoming":11590,"totalOrder":69,"finishOrder":66,"failOrder":3,"dayTime":"2018-06-27"}]
     */

    private int visiter;
    private int exposure;
    private int totalOrder;
    private int preVisiter;
    private int preExposure;
    private int preTotalOrder;
    private int totalIncoming;
    private int totalCost;
    private int loseIncoming;
    private int finishOrder;
    private int failOrder;
    private int preTotalIncoming;
    private int preTotalCost;
    private int preFailOrder;
    private int newUser;
    private int oldUser;
    private int preNewUser;
    private int preOldUser;
    private List<StatisticBean0> statistic;
    private List<VisiterResultBean> visiterResult;
    private List<UserStatBean> userStats;
    private List<StatisticBean0> elemeStatistic;
    private List<VisiterResultBean> elemeVisiterResult;
    private List<UserStatBean> elemeUserStats;
    private List<StatisticBean0> meituanStatistic;
    private List<VisiterResultBean> meituanVisiterResult;
    private List<UserStatBean> meituanUserStats;
    private List<String> dayArr;

    public List<StatisticBean0> getMeituanStatistic()
    {
        return meituanStatistic;
    }

    public void setMeituanStatistic(List<StatisticBean0> meituanStatistic)
    {
        this.meituanStatistic = meituanStatistic;
    }

    public List<VisiterResultBean> getMeituanVisiterResult()
    {
        return meituanVisiterResult;
    }

    public void setMeituanVisiterResult(List<VisiterResultBean> meituanVisiterResult)
    {
        this.meituanVisiterResult = meituanVisiterResult;
    }

    public List<UserStatBean> getMeituanUserStats()
    {
        return meituanUserStats;
    }

    public void setMeituanUserStats(List<UserStatBean> meituanUserStats)
    {
        this.meituanUserStats = meituanUserStats;
    }

    public List<String> getDayArr()
    {
        return dayArr;
    }

    public void setDayArr(List<String> dayArr)
    {
        this.dayArr = dayArr;
    }

    public List<StatisticBean0> getElemeStatistic()
    {
        return elemeStatistic;
    }

    public void setElemeStatistic(List<StatisticBean0> elemeStatistic)
    {
        this.elemeStatistic = elemeStatistic;
    }

    public List<VisiterResultBean> getElemeVisiterResult()
    {
        return elemeVisiterResult;
    }

    public void setElemeVisiterResult(List<VisiterResultBean> elemeVisiterResult)
    {
        this.elemeVisiterResult = elemeVisiterResult;
    }

    public List<UserStatBean> getElemeUserStats()
    {
        return elemeUserStats;
    }

    public void setElemeUserStats(List<UserStatBean> elemeUserStats)
    {
        this.elemeUserStats = elemeUserStats;
    }

    public int getNewUser()
    {
        return newUser;
    }

    public void setNewUser(int newUser)
    {
        this.newUser = newUser;
    }

    public int getOldUser()
    {
        return oldUser;
    }

    public void setOldUser(int oldUser)
    {
        this.oldUser = oldUser;
    }

    public int getPreNewUser()
    {
        return preNewUser;
    }

    public void setPreNewUser(int preNewUser)
    {
        this.preNewUser = preNewUser;
    }

    public int getPreOldUser()
    {
        return preOldUser;
    }

    public void setPreOldUser(int preOldUser)
    {
        this.preOldUser = preOldUser;
    }

    public List<UserStatBean> getUserStats()
    {
        return userStats;
    }

    public void setUserStats(List<UserStatBean> userStats)
    {
        this.userStats = userStats;
    }

    public List<VisiterResultBean> getVisiterResult()
    {
        return visiterResult;
    }

    public void setVisiterResult(List<VisiterResultBean> visiterResult)
    {
        this.visiterResult = visiterResult;
    }

    public int getPreFailOrder()
    {
        return preFailOrder;
    }

    public void setPreFailOrder(int preFailOrder)
    {
        this.preFailOrder = preFailOrder;
    }

    public int getVisiter() { return visiter;}

    public void setVisiter(int visiter) { this.visiter = visiter;}

    public int getExposure() { return exposure;}

    public void setExposure(int exposure) { this.exposure = exposure;}

    public int getTotalOrder() { return totalOrder;}

    public void setTotalOrder(int totalOrder) { this.totalOrder = totalOrder;}

    public int getPreVisiter() { return preVisiter;}

    public void setPreVisiter(int preVisiter) { this.preVisiter = preVisiter;}

    public int getPreExposure() { return preExposure;}

    public void setPreExposure(int preExposure) { this.preExposure = preExposure;}

    public int getPreTotalOrder() { return preTotalOrder;}

    public void setPreTotalOrder(int preTotalOrder) { this.preTotalOrder = preTotalOrder;}

    public int getTotalIncoming() { return totalIncoming;}

    public void setTotalIncoming(int totalIncoming) { this.totalIncoming = totalIncoming;}

    public int getTotalCost() { return totalCost;}

    public void setTotalCost(int totalCost) { this.totalCost = totalCost;}

    public int getLoseIncoming() { return loseIncoming;}

    public void setLoseIncoming(int loseIncoming) { this.loseIncoming = loseIncoming;}

    public int getFinishOrder() { return finishOrder;}

    public void setFinishOrder(int finishOrder) { this.finishOrder = finishOrder;}

    public int getFailOrder() { return failOrder;}

    public void setFailOrder(int failOrder) { this.failOrder = failOrder;}

    public int getPreTotalIncoming() { return preTotalIncoming;}

    public void setPreTotalIncoming(int preTotalIncoming) { this.preTotalIncoming = preTotalIncoming;}

    public int getPreTotalCost() { return preTotalCost;}

    public void setPreTotalCost(int preTotalCost) { this.preTotalCost = preTotalCost;}

    public List<StatisticBean0> getStatistic() { return statistic;}

    public void setStatistic(List<StatisticBean0> statistic) { this.statistic = statistic;}

    public static class StatisticBean0
    {
        @Override
        public String toString()
        {
            return "StatisticBean{" + "totalIncoming=" + totalIncoming + ", totalCost=" + totalCost + ", loseIncoming=" + loseIncoming + ", totalOrder=" + totalOrder + ", finishOrder=" + finishOrder + ", failOrder=" + failOrder + ", dayTime='" + dayTime + '\'' + '}';
        }

        /**
         * totalIncoming : 297878
         * totalCost : 81500
         * loseIncoming : 11590
         * totalOrder : 69
         * finishOrder : 66
         * failOrder : 3
         * dayTime : 2018-06-27
         */



        private int totalIncoming;
        private int totalCost;
        private int loseIncoming;
        private int totalOrder;
        private int finishOrder;
        private int failOrder;
        private String dayTime;

        public int getTotalIncoming() { return totalIncoming;}

        public void setTotalIncoming(int totalIncoming) { this.totalIncoming = totalIncoming;}

        public int getTotalCost() { return totalCost;}

        public void setTotalCost(int totalCost) { this.totalCost = totalCost;}

        public int getLoseIncoming() { return loseIncoming;}

        public void setLoseIncoming(int loseIncoming) { this.loseIncoming = loseIncoming;}

        public int getTotalOrder() { return totalOrder;}

        public void setTotalOrder(int totalOrder) { this.totalOrder = totalOrder;}

        public int getFinishOrder() { return finishOrder;}

        public void setFinishOrder(int finishOrder) { this.finishOrder = finishOrder;}

        public int getFailOrder() { return failOrder;}

        public void setFailOrder(int failOrder) { this.failOrder = failOrder;}

        public String getDayTime() { return dayTime;}

        public void setDayTime(String dayTime) { this.dayTime = dayTime;}
    }

    public static class UserStatBean
    {
        /**
         * old : 8
         * new : 42
         * dayTime : 2018-07-04
         * stationNo : 11718876
         * orgCode : 319107
         */

        private int old;
        @SerializedName("new")
        private int newX;
        private String dayTime;

        public int getOld() { return old;}

        public void setOld(int old) { this.old = old;}

        public int getNewX() { return newX;}

        public void setNewX(int newX) { this.newX = newX;}

        public String getDayTime() { return dayTime;}

        public void setDayTime(String dayTime) { this.dayTime = dayTime;}
    }

    public static class VisiterResultBean
    {
        /**
         * visiter : 0
         * exposure : 0
         * dayTime : 2018-06-27
         */

        private int visiter;
        private int exposure;
        private String dayTime;

        public int getVisiter() { return visiter;}

        public void setVisiter(int visiter) { this.visiter = visiter;}

        public int getExposure() { return exposure;}

        public void setExposure(int exposure) { this.exposure = exposure;}

        public String getDayTime() { return dayTime;}

        public void setDayTime(String dayTime) { this.dayTime = dayTime;}
    }
}
