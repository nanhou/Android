package cn.jinxiit.zebra.beans;

public class UserBean
{

    /**
     * id : 3
     * name : hpn512
     * groups : []
     * extra : {}
     * created_at : 1529392710
     */

    private String id;
    private String name;
    private ExtraBean extra;
    private String created_at;

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public ExtraBean getExtra() { return extra;}

    public void setExtra(ExtraBean extra) { this.extra = extra;}

    public String getCreated_at() { return created_at;}

    public void setCreated_at(String created_at) { this.created_at = created_at;}

    public static class ExtraBean
    {
        /**
         * phone : 18960199198
         */
        private String phone;
        private String market_id;
        private String mqttToken;

        public String getMqttToken() {
            return mqttToken == null ? "" : mqttToken;
        }

        public void setMqttToken(String mqttToken) {
            this.mqttToken = mqttToken;
        }

        public String getMarket_id()
        {
            return market_id;
        }

        public void setMarket_id(String market_id)
        {
            this.market_id = market_id;
        }

        public String getPhone() { return phone;}

        public void setPhone(String phone) { this.phone = phone;}
    }
}
