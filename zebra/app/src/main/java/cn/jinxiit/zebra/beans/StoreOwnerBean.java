package cn.jinxiit.zebra.beans;

import java.util.List;

public class StoreOwnerBean
{
    /**
     * id : 2
     * user_id : 3
     * store_id : 2
     * extra : null
     * created_at : 1529734924
     * store : {"id":2,"name":"黄培南","summary":"","extra":{"telephone":"18589674112","standby_phone":"0596-56897878","city":"福建厦门","address":"福建厦门集美","jd_stationNo":"11717617"},"created_at":1529734924}
     */

    private StoreBean store;
    private String store_id;

    public String getStore_id()
    {
        return store_id;
    }

    public void setStore_id(String store_id)
    {
        this.store_id = store_id;
    }

    public StoreBean getStore() { return store;}

    public void setStore(StoreBean store) { this.store = store;}

    public static class StoreBean
    {
        /**
         * id : 2
         * name : 黄培南
         * summary : 
         * extra : {"telephone":"18589674112","standby_phone":"0596-56897878","city":"福建厦门","address":"福建厦门集美","jd_stationNo":"11717617"}
         * created_at : 1529734924
         */

        private String id;
        private String name;
        private String summary;
        private ExtraBean extra;
        private String created_at;

        public String getId() { return id;}

        public void setId(String id) { this.id = id;}

        public String getName() { return name;}

        public void setName(String name) { this.name = name;}

        public String getSummary() { return summary;}

        public void setSummary(String summary) { this.summary = summary;}

        public ExtraBean getExtra() { return extra;}

        public void setExtra(ExtraBean extra) { this.extra = extra;}

        public String getCreated_at() { return created_at;}

        public void setCreated_at(String created_at) { this.created_at = created_at;}

        public static class ExtraBean
        {
            /**
             * telephone : 18589674112
             * standby_phone : 0596-56897878
             * city : 福建厦门
             * address : 福建厦门集美
             * jd_stationNo : 11717617
             */

            private String telephone;
            private String standby_phone;
            private String city;
            private String address;
            private String jd_stationNo;
            private String logo;
            private String start_time;
            private String end_time;
//            private String image;
            private boolean business_status;
            private String print_name;
            private String print_serial;
            private boolean auto_print;
            private List<String> business_time;

            public List<String> getBusiness_time()
            {
                return business_time;
            }

            public void setBusiness_time(List<String> business_time)
            {
                this.business_time = business_time;
            }

            public boolean isAuto_print()
            {
                return auto_print;
            }

            public void setAuto_print(boolean auto_print)
            {
                this.auto_print = auto_print;
            }

            public String getPrint_serial()
            {
                return print_serial;
            }

            public void setPrint_serial(String print_serial)
            {
                this.print_serial = print_serial;
            }

            public String getPrint_name()
            {
                return print_name;
            }

            public void setPrint_name(String print_name)
            {
                this.print_name = print_name;
            }

            public String getLogo()
            {
                return logo;
            }

            public void setLogo(String logo)
            {
                this.logo = logo;
            }

            public String getStart_time()
            {
                return start_time;
            }

            public void setStart_time(String start_time)
            {
                this.start_time = start_time;
            }

            public String getEnd_time()
            {
                return end_time;
            }

            public void setEnd_time(String end_time)
            {
                this.end_time = end_time;
            }

//            public String getImage()
//            {
//                return image;
//            }
//
//            public void setImage(String image)
//            {
//                this.image = image;
//            }

            public boolean isBusiness_status()
            {
                return business_status;
            }

            public void setBusiness_status(boolean business_status)
            {
                this.business_status = business_status;
            }

            public String getTelephone() { return telephone;}

            public void setTelephone(String telephone) { this.telephone = telephone;}

            public String getStandby_phone() { return standby_phone;}

            public void setStandby_phone(String standby_phone) { this.standby_phone = standby_phone;}

            public String getCity() { return city;}

            public void setCity(String city) { this.city = city;}

            public String getAddress() { return address;}

            public void setAddress(String address) { this.address = address;}

            public String getJd_stationNo() { return jd_stationNo;}

            public void setJd_stationNo(String jd_stationNo) { this.jd_stationNo = jd_stationNo;}
        }
    }
}
