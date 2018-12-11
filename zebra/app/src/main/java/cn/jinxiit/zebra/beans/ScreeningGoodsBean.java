package cn.jinxiit.zebra.beans;

import java.util.List;

public class ScreeningGoodsBean
{
    private String stock;
    private String status;
    private List<String> platform;

    public String getStock()
    {
        return stock;
    }

    public void setStock(String stock)
    {
        this.stock = stock;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public List<String> getPlatform()
    {
        return platform;
    }

    public void setPlatform(List<String> platform)
    {
        this.platform = platform;
    }
}
