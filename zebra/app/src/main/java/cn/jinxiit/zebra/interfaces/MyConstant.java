package cn.jinxiit.zebra.interfaces;

public interface MyConstant {

//    String SERVER = "http://zebra.huabot.com";
    String SERVER = "http://www.bmxs.top";

    //    String URL_GET_FILE = SERVER + "/upload/";
    String URL_GET_FILE = "https://zebrafiles.oss-cn-shanghai.aliyuncs.com/";

    String STR_ERR = "err";
    String STR_POSITION = "position";
    String STR_TOKEN = "token";
    String STR_PASSWORD = "passwd";
    String STR_CODE = "code";
    String STR_USERNAME = "username";
    String STR_NAME = "name";
    String STR_TITLE = "title";
    String STR_CATEGORY = "category";
    String STR_CATID = "cat_id";
    String STR_ID = "id";
    String STR_PRICE = "price";
    String STR_SUMMARY = "summary";
    String STR_PATH = "path";
    String STR_FILEKEY = "file_key";
    String STR_FILE = "file";
    String STR_EXTRA = "extra";
    String STR_USER = "user";
    String STR_FROM = "from";
    String STR_SIZE = "size";
    String STR_SEQ = "seq";
    String STR_STORES = "stores";
    String STR_LOGO = "logo";
    String STR_TELEPHONE = "telephone";
    String STR_STANDBY_PHONE = "standby_phone";
    String STR_PHONE = "phone";
    String STR_START_TIME = "start_time";
    String STR_END_TIME = "end_time";
    String STR_CITY = "city";
    String STR_BEAN = "bean";
    String STR_JSON = "json";
    String STR_TOP = "top";
    String STR_PLATFORM = "platform";
    String STR_BEAN_LIST = "bean_list";
    String STR_ADDRESS = "address";
    String STR_IMAGE = "image";
    String STR_IMAGES = "images";
    String STR_BUSINESS_STATUS = "business_status";
    String STR_BUSINESS_TIME = "business_time";
    String STR_TYPE = "type";
    String STR_All = "all";
    String STR_LOWER_FRAME = "lowerFrame";
    String STR_ON_FRAME = "onFrame";
    String STR_OUT_STOCK = "outStock";
    String STR_LESS_STOCK = "lessStock";
    String STR_IN_STOCK = "inStock";
    String STR_APPLYING = "applying";
    String STR_FAILED = "failed";
    String STR_ONE = "one";
    String STR_Q = "q";
    String STR_QUERY = "query";
    String STR_UPC_CODE = "upcCode";
    String STR_TODAY = "today";
    String STR_YESTERDAY = "yesterday";
    String STR_SEVENDAY = "sevenDay";
    String STR_MONTH = "month";
    String STR_DIYDAY = "diy_day";
    String STR_RESULT = "result";
    String STR_OK = "OK";
    String STR_ORDER = "order";
    String STR_ORDERS = "orders";
    String STR_CUSTOMER = "customer";
    String STR_ISSIGNUP = "isSignUp";
    String STR_IS = "is";
    String STR_PRODUCTS = "products";
    String STR_PRODUCT = "product";
    String STR_BRAND = "brand";
    String STR_BRANDS = "brands";
    String STR_TRUE = "true";
    String STR_FALSE = "false";
    String STR_STATUS = "status";
    String STR_EXAMIN = "examin";
    String STR_MESSAGE = "message";
    String STR_MESSAGES = "messages";
    String STR_NOTICE = "notice";
    String STR_OTHER = "other";

    String STR_PRODUCT_STATUS = "product_status";
    String STR_AUTO_STATUS = "auto_status";
    String STR_AUTO_PRINT = "auto_print";
    String STR_STATUS_TIME = "status_time";
    String STR_STOCK = "stock";
    String STR_SALE_TIME = "sale_time";
    String STR_PACK_FEE = "pack_fee";
    String STR_MIN_PURCHASE = "min_purchase";
    //    serial 打印机编号
    //    name 打印机名称
    //    secret 打印机密钥
    String STR_SERIAL = "serial";
    String STR_SECRET = "secret";
    String STR_PRINTER = "printer";
    String STR_PRINTERS = "printers";

    //    status  "cancel"-- 订单取消, "new_order" -- 新订单, "picking" -- 捡货, "distribution"  -- 待配送, "distributioning" -- 配送中, "success" --已完成
    //    abnormal” --异常 “after_sale" --售后
    String STR_CANCEL = "cancel";
    String STR_NEW_ORDER = "new_order";
    String STR_PICKING = "picking";
    String STR_DISTRIBUTION = "distribution";
    String STR_DISTRIBUTIONING = "distributioning";
    String STR_SUCCESS = "success";
    String STR_ABNORMAL = "abnormal";
    String STR_DELIVERY = "delivery";
    String STR_AFTER_SALE = "after_sale";
    String STR_DAY_TIME = "dayTime";
    String STR_THIRD_TYPE = "third_type";
    String STR_ORDER_ID = "order_id";

    String STR_TOTAL = "total";

    String STR_SEARCH_CREATE = "SearchCreate";
    String STR_SEARCH_BRAND = "SearchBrand";
    String STR_SEARCH_ORDER = "SearchOrder";
    String STR_SEARCH_BATCH_CREATE = "SearchBatchCreate";

    String STR_ALLSTATIS = "全部统计";
    String STR_JDDJ = "京东到家";
    String STR_ELEME = "饿了么";
    String STR_MT = "美团外卖";

    String STR_EN_JDDJ = "jd";
    String STR_EN_ELEME = "eleme";
    String STR_EN_MT = "meituan";
    String STR_EN_EBAI = "ebai";
    String STR_EN_ALL = "all";

    String STR_EN_JDDJ_STOCK = "jd_stock";
    String STR_EN_ELEME_STOCK = "eleme_stock";
    String STR_EN_EBAI_STOCK = "ebai_stock";
    String STR_EN_MT_STOCK = "meituan_stock";

    //    today, yesteday, sevenDay, month

    String STR_REQUEST_TOKEN = "X-REQUEST-TOKEN";

    int REQUEST_CAMERA_PERMISSION = 10;
    int REQUEST_CODE_QR = 11;

    String STR_TAG = "TAG";

    String SP_NAME = "zebra.sp";
    //token   token用户登录生成
    //userId  storeId 用户选择的门店


    String ACTION_ORDER_RECEIVE = "action_order_receive";
    String ACTION_ORDER_REFUSE = "action_order_refuse";
    String ACTION_ORDER_PUSH = "action_order_push";

    String ACTION_ORDER_REJECT = "reject";
    String ACTION_ORDER_AGREE = "agree";

    String ACTION_ORDER_RECEIVE_FALSE = "receiveFalse";
    String ACTION_ORDER_RECEIVE_AGREE = "receiveAgree";
    String ACTION_ORDER_INCREASE_THE_TIP = "IncreaseTheTip";

    String ACTION_ORDER_DELIVERY_AGREE = "deliveryAgree";
    String ACTION_ORDER_DELIVER_REJECT = "deliveryReject";

    //再次下配送 跟自配送
    String ACTION_ORDER_UN_SELF_PICK = "un_self_pick";
    String ACTION_ORDER_SELF_PICK = "self_pick";

    String ACTION_ORDER_DELIVERFAIL_LOCK = "deliveryFailOrLock";
}
