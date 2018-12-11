package cn.jinxiit.zebra.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.vondear.rxtools.view.RxBarCode;
import com.vondear.rxtools.view.dialog.RxDialogEditSureCancel;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

import net.posprinter.posprinterface.UiExecute;
import net.posprinter.utils.BitmapToByteData;
import net.posprinter.utils.DataForSendToPrinterPos58;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.OrderStatusInfoBean;
import cn.jinxiit.zebra.beans.OrderUpBean;
import cn.jinxiit.zebra.beans.StoreOwnerBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyDateUtils;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.MyUtils;
import cn.jinxiit.zebra.utils.StringUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

import static cn.jinxiit.zebra.MyApp.mBlueBinder;
import static cn.jinxiit.zebra.MyApp.mIsBlueLink;

/**
 *
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private static final String BTN_CONFIRM = "confirm";
    private static final String BTN_REFUSE = "refuse";
    private static final String BTN_ONE = "one";
    private String mOrderStatus;//请求的status  跟订单实际的status无关  //全部为null
    private List<OrderUpBean> mDataList;
    private Activity mContext;
    private MyApp myApp;
    private OnRecyclerViewItemClickListener mOnRecyclerViewBottomOffClick;
    private OnRecyclerViewItemClickListener mOnRecyclerViewPrinterOrderClick;

    @SuppressLint("SimpleDateFormat")
    public OrderAdapter(String orderStatus) {
        this.mOrderStatus = orderStatus;
        this.mDataList = new ArrayList<>();
    }

    public void setmOnRecyclerViewPrinterOrderClick(OnRecyclerViewItemClickListener mOnRecyclerViewPrinterOrderClick) {
        this.mOnRecyclerViewPrinterOrderClick = mOnRecyclerViewPrinterOrderClick;
    }

    public void setmOnRecyclerViewBottomOffClick(OnRecyclerViewItemClickListener mOnRecyclerViewBottomOffClick) {
        this.mOnRecyclerViewBottomOffClick = mOnRecyclerViewBottomOffClick;
    }

    public OrderUpBean getItemData(int position) {
        if (position < mDataList.size()) {
            return mDataList.get(position);
        }
        return null;
    }

    public void addDataList(List<OrderUpBean> list) {
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearDataList() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = (Activity) parent.getContext();
            myApp = (MyApp) mContext.getApplication();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_order_abnormal, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.tvOn.setOnClickListener(this::clickOnOff);
        holder.tvDistributionAction.setOnClickListener(this::clickDistributionAction);
        holder.ivPhone.setOnClickListener(this::clickIvPhone);
        holder.ivDeliveryPhone.setOnClickListener(this::clickDeliveryPhone);
        holder.tvPrinter.setOnClickListener(this::clickPrinter);
        holder.tvOrderNo.setOnClickListener(this::clickCopyOrderNo);
        holder.tvBottomOff.setOnClickListener(this::clickBottomOff);
        holder.tvBtnConfirm.setOnClickListener(v -> confirmAction(v, BTN_CONFIRM));
        holder.tvBtnRefuse.setOnClickListener(v -> confirmAction(v, BTN_REFUSE));
        holder.tvBtnOne.setOnClickListener(v -> confirmAction(v, BTN_ONE));
        return holder;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale", "InflateParams"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvOn.setTag(position);
        holder.tvDistributionAction.setTag(position);
        holder.ivPhone.setTag(position);
        holder.ivDeliveryPhone.setTag(position);
        holder.tvPrinter.setTag(position);
        holder.tvBottomOff.setTag(position);
        holder.tvBtnConfirm.setTag(position);
        holder.tvBtnRefuse.setTag(position);
        holder.tvBtnOne.setTag(position);
        holder.tvOrderNo.setTag(position);

        holder.tvTip.setText("");
        holder.llGoods.removeAllViews();
        holder.llDiscount.removeAllViews();
        holder.tvDeliveryName.setText("配送员·未接单");
        holder.tvStatusOrder.setText("订单状态");
        holder.tvDeliveryTime.setText("");
        holder.tvOrderTime.setText("");
        holder.tvOrderNo.setText("");
        holder.tvPosition.setText("编号");
        holder.tvBuyerName.setText("");
        holder.tvAddress.setText("");
        holder.tvProductCount.setText("");
        holder.llDoubleBtn.setVisibility(View.GONE);
        holder.tvBtnOne.setVisibility(View.GONE);
        holder.tvOrderInfoLatest.setText("");
        holder.tvOrderInfos.setText("");
        holder.clDelivery.setVisibility(View.VISIBLE);
        holder.tvMaoLi.setText("");
        holder.tvBarCode.setText("");
        holder.llRemainingTime.setVisibility(View.GONE);

        bindData(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bindData(@NonNull ViewHolder holder, int position) {
        OrderUpBean orderUpBean = mDataList.get(position);

        if (orderUpBean != null) {
            orderSetStatusAndBtn(holder, orderUpBean);

            String order_id = orderUpBean.getOrder_id();
            if (!TextUtils.isEmpty(order_id)) {
                holder.tvOrderNo.setText("订单号：" + order_id);
            }
            OrderUpBean.OrderBean order = orderUpBean.getOrder();
            boolean isGoodsOpen = orderUpBean.isGoodsOpen();
            boolean isDistributionInfoOpen = orderUpBean.isDistributionInfoOpen();
            String third_type = orderUpBean.getThird_type();
            switch (third_type) {
                case MyConstant.STR_EN_JDDJ:
                    holder.ivTag.setImageResource(R.drawable.tag_jd);
                    holder.tvBtnConfirm.setBackgroundResource(R.drawable.gradually_green);
                    holder.tvBtnOne.setBackgroundResource(R.drawable.gradually_green);
                    holder.tvPayStatus.setBackgroundResource(R.drawable.gradually_green);
                    break;
                case MyConstant.STR_EN_MT:
                    holder.ivTag.setImageResource(R.drawable.tag_mt);
                    holder.tvBtnConfirm.setBackgroundResource(R.drawable.gradually_yellow);
                    holder.tvBtnOne.setBackgroundResource(R.drawable.gradually_yellow);
                    holder.tvPayStatus.setBackgroundResource(R.drawable.gradually_yellow);
                    break;
                case MyConstant.STR_EN_ELEME:
                    holder.ivTag.setImageResource(R.drawable.tag_eleme);
                    holder.tvBtnConfirm.setBackgroundResource(R.drawable.gradually_blue);
                    holder.tvBtnOne.setBackgroundResource(R.drawable.gradually_blue);
                    holder.tvPayStatus.setBackgroundResource(R.drawable.gradually_blue);
                    break;
                case MyConstant.STR_EN_EBAI:
                    holder.ivTag.setImageResource(R.drawable.tag_ebai);
                    holder.tvBtnConfirm.setBackgroundResource(R.drawable.gradually_purple);
                    holder.tvBtnOne.setBackgroundResource(R.drawable.gradually_purple);
                    holder.tvPayStatus.setBackgroundResource(R.drawable.gradually_purple);
                    break;
            }

            if (order != null) {
                orderBindInfos(order, holder);
                String orderNum = order.getOrderNum();
                if (!TextUtils.isEmpty(orderNum)) {
                    holder.tvPosition.setText("#" + orderNum);
                }
                //下单时间
                ViewSetDataUtils.textViewSetText(holder.tvOrderTime, order.getOrderStartTime());
                //预计送达时间
                String deliveryTime = order.getDeliveryTime();
                if (!TextUtils.isEmpty(deliveryTime)) {
                    Calendar deliveryCalendar = Calendar.getInstance();
                    Calendar currentCalendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    try {
                        Date deliveryDate = dateFormat.parse(deliveryTime);
                        deliveryCalendar.setTime(deliveryDate);

                        if (deliveryCalendar.get(Calendar.YEAR) == currentCalendar
                                .get(Calendar.YEAR)) {
                            int difDay = deliveryCalendar
                                    .get(Calendar.DAY_OF_YEAR) - currentCalendar
                                    .get(Calendar.DAY_OF_YEAR);
                            if (difDay > -1) {
                                remainingTimeSetData(holder, deliveryTime);
                                StringBuilder strOrderTime = new StringBuilder(1024);
                                switch (difDay) {
                                    case 0:
                                        break;
                                    case 1:
                                        strOrderTime.append("明天");
                                        break;
                                    case 2:
                                        strOrderTime.append("后天");
                                        break;
                                    default:
                                        strOrderTime.append("三天后");
                                        break;
                                }
                                if (deliveryTime.length() > 16) {
                                    strOrderTime.append(deliveryTime.substring(11, 16));
                                    strOrderTime.append("送达");
                                }
                                holder.tvDeliveryTime.setText(strOrderTime);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                String deliveryManName = order.getDeliveryManName();
                if (!TextUtils.isEmpty(deliveryManName)) {
                    holder.tvDeliveryName.setText("配送员·" + deliveryManName);
                } else {
                    String deliveryManPhone = order.getDeliveryManPhone();
                    if (!TextUtils.isEmpty(deliveryManPhone)) {
                        holder.tvDeliveryName.setText("配送员·已接单");
                    }
                }

                ViewSetDataUtils.textViewSetText(holder.tvBuyerName, order.getBuyerFullName());
                ViewSetDataUtils.textViewSetText(holder.tvAddress, order.getBuyerFullAddress());
                long count = order.getCount();
                if (isGoodsOpen) {
                    holder.tvProductCount.setText(String.format("共%d件商品", count));
                    holder.tvOn.setText("收起");
                    String remark = order.getRemark();
                    if (!TextUtils.isEmpty(remark)) {
                        if ("\n".equals(remark.substring(0, 1))) {
                            remark = remark.substring(1);
                        }
                        holder.tvTip.setText("备注:" + remark);
                    }

                    List<OrderUpBean.OrderBean.ProductBean> productBeanList = order.getProduct();
                    if (productBeanList != null) {
                        for (int i = 0; i < productBeanList.size(); i++) {
                            @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext)
                                    .inflate(R.layout.order_goods_item, null);
                            ImageView ivImg = view.findViewById(R.id.iv_goods_img);
                            TextView tvGoodsName = view.findViewById(R.id.tv_goods_name);
                            TextView tvGoodsUpc = view.findViewById(R.id.tv_goods_upc);
                            TextView tvGoodsCount = view.findViewById(R.id.tv_goods_count);
                            TextView tvGoodsTotalMoney = view
                                    .findViewById(R.id.tv_goods_total_money);
                            TextView tvNumJian = view.findViewById(R.id.tv_num_jian);
                            OrderUpBean.OrderBean.ProductBean productBean = productBeanList.get(i);
                            if (productBean != null) {
                                ViewSetDataUtils
                                        .textViewSetText(tvGoodsName, productBean.getName());
                                ViewSetDataUtils.textViewSetText(tvGoodsUpc, productBean.getUpc());
                                long productCount = productBean.getCount();
                                tvGoodsCount.setText(String.format("X %d", productCount));

                                tvNumJian.setTag(productCount);
                                tvNumJian.setOnClickListener(v -> {
                                    long vCount = (long) v.getTag();
                                    if (vCount > 0) {
                                        tvGoodsCount.setText(String.format("X %d", --vCount));
                                        v.setTag(vCount);
                                    }
                                });

                                tvGoodsTotalMoney.setText(String.format("¥ %.2f",
                                        productBean.getTotalMoney() * 0.01));
                                List<String> images = productBean.getImages();
                                if (images != null && images.size() > 0) {
                                    String url = MyConstant.URL_GET_FILE + images.get(0) + ".jpg";
                                    int width = WindowUtils.dip2px(mContext, 80);
                                    MyPicassoUtils.downSizeImageForUrl(width, width, url, ivImg);
                                }
                            }
                            holder.llGoods.addView(view);
                        }
                    }
                    holder.tvMaoLi.setText(String.format("¥ %.2f",
                            (order.getTotalMoney() - order.getCost_price()) * 0.01));
                    holder.tvFreightMoney
                            .setText(String.format("¥ %.2f", order.getFreightMoney() * 0.01));
                    holder.tvPackagingMoney
                            .setText(String.format("¥ %.2f", order.getPackagingMoney() * 0.01));
                    double hongbao = order.getHongbao();
                    double discountMoney = order.getDiscountMoney();
                    double pointMoney = order.getPointMoney();
                    createDiscountLayout(holder, "红包", Math.abs(hongbao));
                    createDiscountLayout(holder, "优惠", Math.abs(discountMoney));
                    createDiscountLayout(holder, "折扣", Math.abs(pointMoney));
                    holder.tvPayMoney0.setText(
                            String.format("用户实付   ¥%.2f", order.getBuyerPayableMoney() * 0.01));
                    holder.getTvPayMoney1.setText(String.format("¥%.2f", order.getIncome() * 0.01));
                    RxBarCode.builder(order_id).backColor(Color.argb(0, 0, 0, 0))
                            .into(holder.ivBarCode);
                    holder.tvBarCode.setText(order_id);
                } else {
                    holder.tvProductCount.setText(String.format("共%d件商品  实付%.2f元", count,
                            order.getBuyerPayableMoney() * 0.01));
                    holder.tvOn.setText("展开");
                }
            }

            if (isGoodsOpen) {
                holder.clOn.setVisibility(View.VISIBLE);
            } else {
                holder.clOn.setVisibility(View.GONE);
            }

            if (isDistributionInfoOpen) {
                holder.tvDistributionAction.setText("收起");
                holder.llDistributionInfo.setVisibility(View.VISIBLE);
            } else {
                holder.tvDistributionAction.setText("展开");
                holder.llDistributionInfo.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void remainingTimeSetData(ViewHolder holder, String deliveryTime) {
        if (TextUtils.isEmpty(mOrderStatus)) {
            return;
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        try {
            long remainingTimeMine = 0;
            long time = dateFormat.parse(deliveryTime).getTime();

            switch (mOrderStatus) {
                case MyConstant.STR_NEW_ORDER:
                case MyConstant.STR_PICKING:
                    holder.tvRemainingTimeType.setText("拣货\n剩余");
                    remainingTimeMine = (time - 30 * 60 * 1000 - System.currentTimeMillis()) / 60000;
                    break;
                case MyConstant.STR_DISTRIBUTION:
                case MyConstant.STR_DISTRIBUTIONING:
                    holder.tvRemainingTimeType.setText("送达\n剩余");
                    remainingTimeMine = (time - System.currentTimeMillis()) / 60000;
                    break;
                default:
                    break;
            }
            if (remainingTimeMine > 0 && remainingTimeMine < 99) {
                holder.llRemainingTime.setVisibility(View.VISIBLE);
                holder.tvRemainingTimeTime.setText(remainingTimeMine + "分钟");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void orderSetStatusAndBtn(@NonNull ViewHolder holder, OrderUpBean orderUpBean) {
        String status = orderUpBean.getStatus();
        if (!TextUtils.isEmpty(status)) {
            switch (status) {
                case "abnormal":
                    holder.tvStatusOrder.setText("申请取消");
                    holder.llDoubleBtn.setVisibility(View.VISIBLE);
                    holder.tvBtnRefuse.setText("拒绝");
                    holder.tvBtnConfirm.setText("同意");
                    break;
                case "delivery":
                    holder.tvStatusOrder.setText("配送异常");
                    holder.llDoubleBtn.setVisibility(View.VISIBLE);
                    holder.tvBtnRefuse.setText("拒绝");
                    holder.tvBtnConfirm.setText("同意");
                    break;
                case "self_pick":
                    holder.tvStatusOrder.setText("取货失败");
                    holder.llDoubleBtn.setVisibility(View.VISIBLE);
                    holder.tvBtnRefuse.setText("转自配送");
                    holder.tvBtnConfirm.setText("再次下发配送");
                    break;
                case "pick":
                    holder.tvStatusOrder.setText("取货失败");
                    holder.llDoubleBtn.setVisibility(View.VISIBLE);
                    holder.tvBtnRefuse.setText("拒绝");
                    holder.tvBtnConfirm.setText("同意");
                    break;
                case "deliveryFail":
                    holder.tvStatusOrder.setText("投递失败");
                    holder.tvBtnOne.setVisibility(View.VISIBLE);
                    holder.tvBtnOne.setText("确定");
                    break;
                case "lock":
                    holder.tvStatusOrder.setText("锁定状态");
                    holder.tvBtnOne.setVisibility(View.VISIBLE);
                    holder.tvBtnOne.setText("确定");
                    break;
                case "cancel":
                    holder.tvStatusOrder.setText("订单取消");
                    break;
                case "system_cancel":
                    holder.tvStatusOrder.setText("超时未支付系统取消");
                    break;
                case "system_remoke":
                    holder.tvStatusOrder.setText("系统撤销订单");
                    break;
                case "wait_pay":
                    holder.tvStatusOrder.setText("待支付");
                    break;
                case "new_order":
                    holder.tvStatusOrder.setText("新订单");
                    holder.llDoubleBtn.setVisibility(View.VISIBLE);
                    break;
                case "picking":
                    holder.tvStatusOrder.setText("拣货");
                    holder.tvBtnOne.setVisibility(View.VISIBLE);
                    holder.tvBtnOne.setText("下发配送");
                    break;
                case "robbed":
                    holder.tvStatusOrder.setText("已抢单");
                    break;
                case "distribution":
                    holder.tvStatusOrder.setText("待配送");
                    break;
                case "wait":
                case "cwait":
                    holder.tvStatusOrder.setText("待配送");
                    holder.llDoubleBtn.setVisibility(View.VISIBLE);
                    holder.tvBtnConfirm.setText("增加小费");
                    OrderUpBean.OrderBean order = orderUpBean.getOrder();
                    if (order != null) {
                        long tips = order.getTips();
                        if (tips != 0) {
                            holder.tvBtnConfirm.append("(已加小费" + (tips * 0.01) + "元)");
                        }
                    }
                    holder.tvBtnRefuse.setText("转自配送");
                    break;
                case "distributioning":
                    holder.tvStatusOrder.setText("配送中");
                    break;
                case "delivered":
                    holder.tvStatusOrder.setText("妥投");
                    break;
                case "receivables":
                    holder.tvStatusOrder.setText("已收款");
                    break;
                case "success":
                    holder.tvStatusOrder.setText("已完成");
                    break;
                case "submit":
                    holder.tvStatusOrder.setText("用户已提交订单");
                    break;
                case "push":
                    holder.tvStatusOrder.setText("向商家推送订单");
                    break;
            }
        }
    }

    //设置订单动态
    private void orderBindInfos(OrderUpBean.OrderBean order, ViewHolder holder) {
        if (order != null) {
            OrderUpBean.OrderBean.OrderStatus orderStatus = order.getOrderStatus();
            if (orderStatus != null) {
                StringBuilder builder = new StringBuilder(1024);
                String latestInfo = null;

                List<OrderStatusInfoBean> orderStatusInfoBeansList = new ArrayList<>();
                int logistics_completed_time = orderStatus.getLogistics_completed_time();
                if (logistics_completed_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(logistics_completed_time, "配送单完成"));
                }
                int logistics_cancel_time = orderStatus.getLogistics_cancel_time();
                if (logistics_cancel_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(logistics_cancel_time, "配送单取消"));
                }
                int order_completed_time = orderStatus.getOrder_completed_time();
                if (order_completed_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(order_completed_time, "订单完成"));
                }
                int order_cancel_time = orderStatus.getOrder_cancel_time();
                if (order_cancel_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(order_cancel_time, "订单取消"));
                }
                int logistics_fetch_time = orderStatus.getLogistics_fetch_time();
                if (logistics_fetch_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(logistics_fetch_time, "骑手取单"));
                }
                int logistics_confirm_time = orderStatus.getLogistics_confirm_time();
                if (logistics_confirm_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(logistics_confirm_time, "配送单确认"));
                }
                int logistics_send_time = orderStatus.getLogistics_send_time();
                if (logistics_send_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(logistics_send_time, "配送单下单"));
                }
                int order_confirm_time = orderStatus.getOrder_confirm_time();
                if (order_confirm_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(order_confirm_time, "商家确认"));
                }
                int order_send_time = orderStatus.getOrder_send_time();
                if (order_send_time != 0) {
                    orderStatusInfoBeansList.add(new OrderStatusInfoBean(order_send_time, "用户下单"));
                }
                int order_receive_time = orderStatus.getOrder_receive_time();
                if (order_receive_time != 0) {
                    orderStatusInfoBeansList
                            .add(new OrderStatusInfoBean(order_receive_time, "等待商家确认"));
                }
                //时间排序
                int size = orderStatusInfoBeansList.size();
                for (int i = 0; i < size - 1; i++) {
                    for (int j = 0; j < size - 1 - i; j++) {
                        OrderStatusInfoBean orderStatusInfoBean0 = orderStatusInfoBeansList.get(j);
                        OrderStatusInfoBean orderStatusInfoBean1 = orderStatusInfoBeansList
                                .get(j + 1);
                        if (orderStatusInfoBean0.getTime() < orderStatusInfoBean1.getTime()) {
                            orderStatusInfoBeansList.set(j,
                                    new OrderStatusInfoBean(orderStatusInfoBean1.getTime(),
                                            orderStatusInfoBean1.getInfo()));
                            orderStatusInfoBeansList.set(j + 1,
                                    new OrderStatusInfoBean(orderStatusInfoBean0.getTime(),
                                            orderStatusInfoBean0.getInfo()));
                        }
                    }
                }
                for (int i = 0; i < size; i++) {
                    OrderStatusInfoBean orderStatusInfoBean = orderStatusInfoBeansList.get(i);
                    String orderInfo = orderStatusInfo(builder, orderStatusInfoBean.getTime(),
                            orderStatusInfoBean.getInfo());
                    if (i == 0) {
                        latestInfo = orderInfo;
                    }
                }
                ViewSetDataUtils.textViewSetText(holder.tvOrderInfoLatest, latestInfo);
                ViewSetDataUtils.textViewSetText(holder.tvOrderInfos, builder.toString());
                builder.setLength(0);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void createDiscountLayout(@NonNull ViewHolder holder, String discountName, double hongbao) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, WindowUtils.dip2px(mContext, 30));
        @SuppressLint("InflateParams") View view0 = LayoutInflater.from(mContext)
                .inflate(R.layout.item_discount, null);
        TextView tvDicountName = view0.findViewById(R.id.tv_discount_name);
        TextView tvDicountMoney = view0.findViewById(R.id.tv_discount_money);
        tvDicountName.setText(discountName);
        tvDicountMoney.setText(String.format("- ¥ %.2f", hongbao * 0.01));
        view0.setLayoutParams(lp);
        holder.llDiscount.addView(view0);
    }

    @NonNull
    private String orderStatusInfo(StringBuilder builder, long order_completed_time, String statusInfo) {
        String latestInfo;
        String info = createOrderInfo(order_completed_time, statusInfo);
        builder.append(info);
        builder.append("\n");
        latestInfo = info;
        return latestInfo;
    }

    private String createOrderInfo(long time, String info) {
        StringBuilder builder = new StringBuilder(1024);
        String strTime = MyDateUtils.stampFormatToDate(time + "000", "MM-dd HH:mm");
        builder.append("[");
        builder.append(strTime);
        builder.append("] ");
        builder.append(info);
        return builder.toString();
    }

    private void confirmAction(View view, String whoBtn) {
        int position = (int) view.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        String status = orderUpBean.getStatus();
        if (TextUtils.isEmpty(status)) {
            return;
        }
        switch (whoBtn) {
            case BTN_CONFIRM:
                switch (status) {
                    case "new_order":
                        printerOrder(orderUpBean);
                        clickBtnAction(view, MyConstant.ACTION_ORDER_RECEIVE, null);
                        break;
                    case "abnormal":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_AGREE, null);
                        break;
                    case "pick":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_RECEIVE_AGREE, null);
                        break;
                    case "wait":
                        showIncreaseTheTip(view, MyConstant.ACTION_ORDER_INCREASE_THE_TIP);
                        break;
                    case "delivery":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_DELIVERY_AGREE, null);
                        break;
                    case "self_pick":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_UN_SELF_PICK, null);
                        break;
                    default:
                        break;
                }
                break;
            case BTN_REFUSE:
                switch (status) {
                    case "new_order":
                        showSureDialog(view, MyConstant.ACTION_ORDER_REFUSE);
                        break;
                    case "abnormal":
                        showRefuseOrderReason(view, MyConstant.ACTION_ORDER_REJECT);
                        break;
                    case "pick":
                        showEditDialog(view, MyConstant.ACTION_ORDER_RECEIVE_FALSE);
                        break;
                    case "delivery":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_DELIVER_REJECT, null);
                        break;
                    case "self_pick":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_SELF_PICK, null);
                        break;
                    default:
                        break;
                }
                break;
            case BTN_ONE:
                switch (status) {
                    case "picking":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_PUSH, null);
                        break;
                    case "deliveryFail":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_DELIVERFAIL_LOCK, null);
                        break;
                    case "lock":
                        clickBtnAction(view, MyConstant.ACTION_ORDER_DELIVERFAIL_LOCK, null);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void showSureDialog(View view, String action) {
        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(mContext);
        rxDialogSureCancel.setTitle("确认拒单");
        rxDialogSureCancel.getTitleView().setTextSize(17);
        rxDialogSureCancel.setContent("是否确认拒单？");
        rxDialogSureCancel.getContentView().setTextSize(14);
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {
            rxDialogSureCancel.cancel();
            clickBtnAction(view, action, null);
        });
        sureView.setOnClickListener(v -> rxDialogSureCancel.cancel());
        rxDialogSureCancel.show();
    }

    //增加小费的接口
    private void showIncreaseTheTip(View view, String action) {
        final Dialog dialog = new Dialog(mContext, R.style.my_dialog);
        @SuppressLint("InflateParams") View dialogContent = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_increase_tip, null);
        dialog.setContentView(dialogContent);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mContext.getResources().getColor(R.color.colorTm));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        dialogContent.measure(0, 0);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //        lp.width = view.getMeasuredWidth();
        lp.width = WindowUtils.dip2px(mContext, 280);
        lp.alpha = 5f; // 透明度
        window.setAttributes(lp);
        RadioGroup radioGroup = dialogContent.findViewById(R.id.rg);
        dialogContent.findViewById(R.id.btn_no).setOnClickListener(v -> dialog.cancel());
        dialogContent.findViewById(R.id.btn_yes).setOnClickListener(v -> {
            int position = radioGroup
                    .indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));
            clickBtnAction(view, action, (position + 1) * 100 + "");
            dialog.cancel();
        });

        dialog.show();
    }

    //    需要理由
    private void showEditDialog(View view, String action) {
        RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(mContext);
        rxDialogEditSureCancel.setTitle("");
        EditText editText = rxDialogEditSureCancel.getEditText();
        editText.setHint("请输入理由");
        TextView sureView = rxDialogEditSureCancel.getSureView();
        TextView cancelView = rxDialogEditSureCancel.getCancelView();
        sureView.setText("取消");
        cancelView.setText("确认");
        cancelView.setOnClickListener(v -> {
            String reason = editText.getText().toString().trim();
            if (TextUtils.isEmpty(reason)) {
                MyToastUtils.error("请输入理由！！");
                return;
            }
            clickBtnAction(view, action, reason);
            rxDialogEditSureCancel.cancel();
        });
        sureView.setOnClickListener(v -> rxDialogEditSureCancel.cancel());
        rxDialogEditSureCancel.show();
    }

    private void clickBtnAction(View v, String action, String reason) {
        int position = (int) v.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        if (orderUpBean != null) {
            if (mOnRecyclerViewPrinterOrderClick != null) {
                mOnRecyclerViewPrinterOrderClick.onClick(v, position);
            }
            String third_type = orderUpBean.getThird_type();
            if (!TextUtils.isEmpty(third_type)) {
                switch (third_type) {
                    case MyConstant.STR_EN_JDDJ:
                        jdBtnAction(position, orderUpBean, action, reason, third_type);
                        break;
                    case MyConstant.STR_EN_MT:
                        meituanBtnAction(position, orderUpBean, action, reason, third_type);
                        break;
                    case MyConstant.STR_EN_ELEME:
                        elmeBtnAction(position, orderUpBean, action, reason, third_type);
                        break;
                    case MyConstant.STR_EN_EBAI:
                        ebaiBtnAction(position, orderUpBean, action, reason, third_type);
                        break;
                }
            }
        }
    }

    private void elmeBtnAction(int position, OrderUpBean orderUpBean, String action, String reason, String platform) {
        if (orderUpBean != null && !TextUtils.isEmpty(action)) {
            switch (action) {
                case MyConstant.ACTION_ORDER_RECEIVE:
                    httpPostReceiveOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_REFUSE:
                    httpPostRefuseOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_PUSH:
                    httpPostPushOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_AGREE:
                case MyConstant.ACTION_ORDER_REJECT:
                    httpPostOrderIsCanCancel(position, orderUpBean, platform, action, reason);
                    break;
                case MyConstant.ACTION_ORDER_INCREASE_THE_TIP:
                    httpPostAddTip(platform, orderUpBean, reason);
                    break;
                case MyConstant.ACTION_ORDER_DELIVERY_AGREE:
                    httpPostOrderAbnormalDeliveryAction(position, platform, orderUpBean, action);
                    break;
                case MyConstant.ACTION_ORDER_DELIVER_REJECT:
                    httpPostOrderAbnormalDeliveryAction(position, platform, orderUpBean, action);
                    break;
            }
        }
    }

    private void ebaiBtnAction(int position, OrderUpBean orderUpBean, String action, String reason, String platform) {
        if (orderUpBean != null && !TextUtils.isEmpty(action)) {
            switch (action) {
                case MyConstant.ACTION_ORDER_RECEIVE:
                    httpPostReceiveOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_REFUSE:
                    httpPostRefuseOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_PUSH:
                    httpPostPushOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_AGREE:
                case MyConstant.ACTION_ORDER_REJECT:
                    httpPostOrderIsCanCancel(position, orderUpBean, platform, action, reason);
                    break;
                case MyConstant.ACTION_ORDER_DELIVERY_AGREE:
                    httpPostOrderAbnormalDeliveryAction(position, platform, orderUpBean, action);
                    break;
                case MyConstant.ACTION_ORDER_DELIVER_REJECT:
                    httpPostOrderAbnormalDeliveryAction(position, platform, orderUpBean, action);
                    break;
            }
        }
    }

    private void jdBtnAction(int position, OrderUpBean orderUpBean, String action, String reason, String platform) {
        if (orderUpBean != null && !TextUtils.isEmpty(action)) {
            switch (action) {
                case MyConstant.ACTION_ORDER_RECEIVE:
                    httpPostReceiveOrder(position, orderUpBean, platform);
                    httpPostReceiveOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_REFUSE:
                    httpPostRefuseOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_PUSH:
                    httpPostPushOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_AGREE:
                case MyConstant.ACTION_ORDER_REJECT:
                case MyConstant.ACTION_ORDER_RECEIVE_FALSE:
                case MyConstant.ACTION_ORDER_RECEIVE_AGREE:
                    httpPostOrderIsCanCancel(position, orderUpBean, platform, action, reason);
                    break;
                case MyConstant.ACTION_ORDER_DELIVERFAIL_LOCK:
                    httpPostConfirmReBack(position, orderUpBean);
                    break;
                case MyConstant.ACTION_ORDER_INCREASE_THE_TIP:
                    httpPostAddTip(platform, orderUpBean, reason);
                    break;
                case MyConstant.ACTION_ORDER_UN_SELF_PICK:
                    httpPostDispatchPushOrder(position, orderUpBean, platform);
                    break;
                default:
                    break;
            }
        }
    }

    private void meituanBtnAction(int position, OrderUpBean orderUpBean, String action, String reason, String platform) {
        if (orderUpBean != null && !TextUtils.isEmpty(action)) {
            switch (action) {
                case MyConstant.ACTION_ORDER_RECEIVE:
                    httpPostReceiveOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_REFUSE:
                    httpPostRefuseOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_PUSH:
                    httpPostPushOrder(position, orderUpBean, platform);
                    break;
                case MyConstant.ACTION_ORDER_AGREE:
                case MyConstant.ACTION_ORDER_REJECT:
                    httpPostMeituanTuikuan(position, orderUpBean, action, reason);
                    break;
                case MyConstant.ACTION_ORDER_INCREASE_THE_TIP:
                    httpPostAddTip(platform, orderUpBean, reason);
                    break;
            }
        }
    }

    private void httpPostOrderAbnormalDeliveryAction(int position, String platform, OrderUpBean orderUpBean, String action) {
        ApiUtils.getInstance().okgoPostOrderAbnormalDeliveryAction(mContext, myApp.mToken, platform,
                orderUpBean.getOrder_id(), action, new ApiResultListener() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response.body().contains(MyConstant.STR_OK)) {
                            mDataList.remove(position);
                            notifyItemRemoved(position);
                        } else {
                            MyToastUtils.error("操作失败");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        MyToastUtils.error("操作失败");
                    }
                });
    }

    private void httpPostDispatchPushOrder(int position, OrderUpBean orderUpBean, String platform) {
        ApiUtils.getInstance().okgoPostDispatchPushOrder(mContext, myApp.mToken, platform,
                orderUpBean.getOrder_id(), new ApiResultListener() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response.body().contains(MyConstant.STR_OK)) {
                            MyToastUtils.success("操作成功");
                            changeViewByOrderStatus(position, orderUpBean);
                        } else {
                            MyToastUtils.error("操作失败");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        MyToastUtils.error("操作失败");
                    }
                });
    }

    private void httpPostAddTip(String platform, OrderUpBean orderUpBean, String tips) {
        ApiUtils.getInstance()
                .okgoPostAddTip(mContext, myApp.mToken, platform, orderUpBean.getOrder_id(), tips,
                        new ApiResultListener() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (response.body().contains(MyConstant.STR_OK)) {
                                    MyToastUtils.success("操作成功");
                                } else {
                                    MyToastUtils.error("操作失败");
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                MyToastUtils.error("操作失败");
                            }
                        });
    }

    private void httpPostConfirmReBack(int position, OrderUpBean orderUpBean) {
        ApiUtils.getInstance()
                .okgoPostJdConfirmReBackGoods(mContext, myApp.mToken, orderUpBean.getOrder_id(),
                        new ApiResultListener() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (response.body().contains(MyConstant.STR_OK)) {
                                    MyToastUtils.success("操作成功");
                                    changeViewByOrderStatus(position, orderUpBean);
                                } else {
                                    MyToastUtils.error("操作失败");
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                MyToastUtils.error("操作失败");
                            }
                        });
    }

    private void httpPostOrderIsCanCancel(int position, OrderUpBean orderUpBean, String platform, String action, String reason) {
        ApiUtils.getInstance().okgoPostJdIsCanCancel(mContext, myApp.mToken, platform, action,
                orderUpBean.getOrder_id(), reason, new ApiResultListener() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response.body().contains(MyConstant.STR_OK)) {
                            MyToastUtils.success("操作成功");
                            changeViewByOrderStatus(position, orderUpBean);
                        } else {
                            MyToastUtils.error("操作失败");
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        MyToastUtils.error("操作失败");
                    }
                });
    }

    private void httpPostMeituanTuikuan(int position, OrderUpBean orderUpBean, String action, String reason) {
        ApiUtils.getInstance()
                .okgoPostMeituanTuikuan(mContext, myApp.mToken, action, orderUpBean.getOrder_id(),
                        reason, new ApiResultListener() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (response.body().contains(MyConstant.STR_OK)) {
                                    MyToastUtils.success("操作成功");
                                    changeViewByOrderStatus(position, orderUpBean);
                                } else {
                                    MyToastUtils.error("操作失败");
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                MyToastUtils.error("操作失败");
                            }
                        });
    }

    private void httpPostPushOrder(int position, OrderUpBean orderUpBean, String platform) {
        ApiUtils.getInstance()
                .okgoPostPushOrder(mContext, myApp.mToken, platform, orderUpBean.getOrder_id(),
                        new ApiResultListener() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (response.body().contains(MyConstant.STR_OK)) {
                                    MyToastUtils.success("操作成功");
                                    changeViewByOrderStatus(position, orderUpBean);
                                } else {
                                    MyToastUtils.error("操作失败");
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                MyToastUtils.error("操作失败");
                            }
                        });
    }

    private void httpPostRefuseOrder(int position, OrderUpBean orderUpBean, String platform) {
        ApiUtils.getInstance()
                .okgoPostRefuseOrder(mContext, myApp.mToken, platform, orderUpBean.getOrder_id(),
                        new ApiResultListener() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (response.body().contains(MyConstant.STR_OK)) {
                                    MyToastUtils.success("拒单成功");
                                    changeViewByOrderStatus(position, orderUpBean);
                                } else {
                                    MyToastUtils.error("拒单失败");
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                MyToastUtils.error("拒单失败");
                            }
                        });
    }

    private void showRefuseOrderReason(View view, String action) {
        final Dialog dialog = new Dialog(mContext, R.style.my_dialog);
        @SuppressLint("InflateParams") View dialogContent = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_refuse_order_reason, null);
        dialog.setContentView(dialogContent);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mContext.getResources().getColor(R.color.colorMain));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        dialogContent.measure(0, 0);
        int[] screenSize = WindowUtils.getScreenSize(mContext);
        lp.height = screenSize[1];
        //        lp.width = view.getMeasuredWidth();
        lp.width = screenSize[0];
        lp.alpha = 5f; // 透明度
        window.setAttributes(lp);
        dialog.show();

        dialogContent.findViewById(R.id.iv_back).setOnClickListener(v -> dialog.cancel());
        RadioGroup rgReason = dialogContent.findViewById(R.id.rg_reason);
        EditText etReason = dialogContent.findViewById(R.id.et_reason);
        dialogContent.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            if (TextUtils.isEmpty(reason)) {
                reason = ((RadioButton) rgReason.findViewById(rgReason.getCheckedRadioButtonId()))
                        .getText().toString().trim();
            }
            clickBtnAction(view, action, reason);
            dialog.cancel();
        });
    }

    private void httpPostReceiveOrder(int position, OrderUpBean orderUpBean, String platform) {
        ApiUtils.getInstance()
                .okgoPostReceiveOrder(mContext, myApp.mToken, platform, orderUpBean.getOrder_id(),
                        new ApiResultListener() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (response.body().contains(MyConstant.STR_OK)) {
                                    MyToastUtils.success("操作成功");
                                    changeViewByOrderStatus(position, orderUpBean);
                                } else {
                                    MyToastUtils.error("接单失败");
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                MyToastUtils.error("接单失败");
                            }
                        });
    }

    private void changeViewByOrderStatus(int position, OrderUpBean orderUpBean) {
        if (!TextUtils.isEmpty(mOrderStatus)) {
            switch (mOrderStatus) {
                case MyConstant.STR_NEW_ORDER:
                case MyConstant.STR_PICKING:
                case MyConstant.STR_DISTRIBUTION:
                case MyConstant.STR_ABNORMAL:
                    mDataList.remove(orderUpBean);
                    notifyDataSetChanged();
                    //                    notifyItemRemoved(position);//不会更改position
                    //                    notifyItemRangeChanged(0, mDataList.size());//修改position
                    break;
            }
        }
    }

    private void clickBottomOff(View v) {
        if (mOnRecyclerViewBottomOffClick != null) {
            mOnRecyclerViewBottomOffClick.onClick(v, (int) v.getTag());
            clickOff(v);
        }
    }

    private void clickOff(View v) {
        int position = (int) v.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        orderUpBean.setGoodsOpen(false);
        notifyItemChanged(position);
    }

    private void clickPrinter(View view) {
        int position = (int) view.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        printerOrder(orderUpBean);
    }

    private void printerOrder(OrderUpBean orderUpBean) {
        //蓝牙
        checkBlueLink(orderUpBean);
        //wifi 打印
        httpPrinterOrder(orderUpBean);
    }

    /*
    1、检查连接
     */
    private void checkBlueLink(OrderUpBean orderUpBean) {
        if (mBlueBinder != null) {
            mBlueBinder.checkLinkedState(new UiExecute() {
                @Override
                public void onsucess() {
                }

                @Override
                public void onfailed() {
                    mIsBlueLink = false;
                    MyToastUtils.error("蓝牙设备未连接");
                }
            });
            bluePrinterOrder(orderUpBean);
        }
    }

    private void httpPrinterOrder(OrderUpBean itemData) {
        if (myApp.mCurrentStoreOwnerBean != null) {
            String store_id = myApp.mCurrentStoreOwnerBean.getStore_id();
            if (!TextUtils.isEmpty(store_id)) {
                if (itemData != null) {
                    String id = itemData.getId();
                    if (!TextUtils.isEmpty(id)) {
                        ApiUtils.getInstance()
                                .okgoPostPrinterOrder(mContext, myApp.mToken, store_id, id,
                                        new ApiResultListener() {
                                            @Override
                                            public void onSuccess(Response<String> response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(
                                                            response.body());
                                                    if (jsonObject.has(MyConstant.STR_RESULT)) {
                                                        String result = jsonObject
                                                                .getString(MyConstant.STR_RESULT);
                                                        if (result.equals("OK")) {
                                                            MyToastUtils.success("打印成功");
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(Response<String> response) {

                                            }
                                        });
                    }
                }
            }
        }
    }

    //2、蓝牙打印订单
    @SuppressLint("DefaultLocale")
    private void bluePrinterOrder(OrderUpBean orderUpBean) {
        if (mBlueBinder != null && mIsBlueLink) {
            mBlueBinder.writeDataByYouself(new UiExecute() {
                @Override
                public void onsucess() {

                }

                @Override
                public void onfailed() {

                }
            }, () -> {
                List<byte[]> list = new ArrayList<>();
                list.add(DataForSendToPrinterPos58.initializePrinter());
                list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                //创建一段我们想打印的文本,转换为byte[]类型，并添加到要发送的数据的集合list中
                if (orderUpBean != null) {
                    String third_type = orderUpBean.getThird_type();
                    OrderUpBean.OrderBean order = orderUpBean.getOrder();
                    if (order != null) {
                        //订单编号
                        String orderNum = order.getOrderNum();
                        if (!TextUtils.isEmpty(orderNum)) {
                            if (third_type != null) {
                                list.add(DataForSendToPrinterPos58.selectAlignment(1));
                                // 0-3位高度 4-7宽度
                                list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00000000));
                                list.add(StringUtils.strTobytes("***"));
                                list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00010001));
                                list.add(StringUtils.strTobytes(" #" + orderNum + " "));
                                list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00000000));
                                switch (third_type) {
                                    case MyConstant.STR_EN_JDDJ:
                                        list.add(StringUtils.strTobytes("京东到家订单 ***"));
                                        break;
                                    case MyConstant.STR_EN_MT:
                                        list.add(StringUtils.strTobytes("美团外卖订单 ***"));
                                        break;
                                    case MyConstant.STR_EN_ELEME:
                                        list.add(StringUtils.strTobytes("饿了么订单 ***"));
                                        break;
                                    case MyConstant.STR_EN_EBAI:
                                        list.add(StringUtils.strTobytes("饿百订单 ***"));
                                        break;
                                }
                                list.add(DataForSendToPrinterPos58.printAndFeedForward(2));
                            }
                        }
                        //店铺名称
                        if (myApp.mCurrentStoreOwnerBean != null) {
                            StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean
                                    .getStore();
                            if (store != null) {
                                String storeName = store.getName();
                                if (!TextUtils.isEmpty(storeName)) {
                                    list.add(StringUtils.strTobytes(storeName));
                                    list.add(DataForSendToPrinterPos58.printAndFeedForward(2));
                                }
                            }
                        }
                        // 订单完成
                        list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00010001));
                        list.add(StringUtils.strTobytes("--订单完成--"));
                        list.add(DataForSendToPrinterPos58.printAndFeedForward(2));

                        //下单 送达时间
                        //左对齐
                        list.add(DataForSendToPrinterPos58.selectAlignment(0));
                        // 0-3位高度 4-7宽度
                        list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00000000));
                        list.add(StringUtils.strTobytes("-------------------------------"));
                        list.add(DataForSendToPrinterPos58.printAndFeedLine());
                        String orderStartTime = order.getOrderStartTime();
                        if (!TextUtils.isEmpty(orderStartTime)) {
                            list.add(StringUtils
                                    .strTobytes(String.format("下单时间：%s", orderStartTime)));
                            list.add(DataForSendToPrinterPos58.printAndFeedLine());
                        }
                        String deliveryTime = order.getDeliveryTime();
                        if (!TextUtils.isEmpty(deliveryTime)) {
                            list.add(
                                    StringUtils.strTobytes(String.format("预计送达：%s", deliveryTime)));
                            list.add(DataForSendToPrinterPos58.printAndFeedLine());
                        }
                        list.add(StringUtils.strTobytes("-------------------------------"));
                        list.add(DataForSendToPrinterPos58.printAndFeedLine());

                        //商品
                        list.add(DataForSendToPrinterPos58.selectAlignment(1));
                        list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00010001));
                        list.add(StringUtils.strTobytes("请核对数量"));
                        list.add(DataForSendToPrinterPos58.printAndFeedForward(2));
                        List<OrderUpBean.OrderBean.ProductBean> productList = order.getProduct();
                        if (productList != null && productList.size() > 0) {
                            int size = productList.size();
                            long sumCount = 0;
                            for (int i = 0; i < size; i++) {
                                OrderUpBean.OrderBean.ProductBean productBean = productList.get(i);
                                if (productBean != null) {
                                    String productName = productBean.getName();
                                    if (!TextUtils.isEmpty(productName)) {
                                        list.add(DataForSendToPrinterPos58.selectAlignment(0));
                                        list.add(DataForSendToPrinterPos58
                                                .selectCharacterSize(0b00000000));
                                        list.add(StringUtils.strTobytes(productName));
                                        list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                                        String upc = productBean.getUpc();
                                        if (!TextUtils.isEmpty(upc)) {
                                            list.add(StringUtils.strTobytes("条形码：" + upc));
                                            list.add(DataForSendToPrinterPos58
                                                    .printAndFeedForward(1));
                                        }
                                        list.add(DataForSendToPrinterPos58.selectAlignment(2));
                                        list.add(StringUtils.strTobytes("x"));
                                        list.add(DataForSendToPrinterPos58
                                                .selectCharacterSize(0b00010001));
                                        long count = productBean.getCount();
                                        sumCount += count;
                                        list.add(StringUtils.strTobytes(count + "  "));
                                        list.add(DataForSendToPrinterPos58
                                                .selectCharacterSize(0b00000000));
                                        list.add(StringUtils.strTobytes(String.format("%.2f",
                                                productBean.getTotalMoney() * 0.01)));
                                        list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                                    }
                                }
                            }
                            //总计份数
                            list.add(DataForSendToPrinterPos58.selectAlignment(0));
                            list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00000000));
                            list.add(StringUtils.strTobytes("总计份数："));
                            list.add(DataForSendToPrinterPos58.selectAlignment(2));
                            list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00010001));
                            list.add(StringUtils.strTobytes(sumCount + "份"));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(2));
                        }

                        //亲
                        if (myApp.mCurrentStoreOwnerBean != null) {
                            StoreOwnerBean.StoreBean store = myApp.mCurrentStoreOwnerBean
                                    .getStore();
                            if (store != null) {
                                StoreOwnerBean.StoreBean.ExtraBean extra = store.getExtra();
                                if (extra != null) {
                                    String telephone = extra.getTelephone();
                                    if (telephone != null) {
                                        list.add(DataForSendToPrinterPos58.selectAlignment(0));
                                        list.add(DataForSendToPrinterPos58
                                                .selectCharacterSize(0b00000000));
                                        list.add(StringUtils.strTobytes("亲有任何问题请拨打"));
                                        list.add(DataForSendToPrinterPos58
                                                .selectCharacterSize(0b00010001));
                                        list.add(StringUtils.strTobytes(telephone));
                                        list.add(DataForSendToPrinterPos58
                                                .selectCharacterSize(0b00000000));
                                        list.add(StringUtils.strTobytes("，我们将第一时间给您处理哦。"));
                                        list.add(DataForSendToPrinterPos58.printAndFeedForward(2));
                                    }
                                }
                            }
                        }

                        //左对齐
                        list.add(DataForSendToPrinterPos58.selectAlignment(0));
                        // 0-3位高度 4-7宽度
                        list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00000000));
                        //其他费用
                        list.add(StringUtils.strTobytes("-------------------------------"));
                        list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                        otherMoneyPrint(list, order.getFreightMoney(), "配送费：                 %.2f");
                        otherMoneyPrint(list, order.getPackagingMoney(),
                                "包装费：                 %.2f");
                        //优惠费用
                        list.add(StringUtils.strTobytes("-------------------------------"));
                        list.add(DataForSendToPrinterPos58.printAndFeedLine());
                        otherMoneyPrint(list, order.getHongbao(), "红包：                  %.2f");
                        otherMoneyPrint(list, order.getDiscountMoney(),
                                "优惠：                  %.2f");
                        otherMoneyPrint(list, order.getPointMoney(), "折扣：                  %.2f");

                        list.add(StringUtils.strTobytes("-------------------------------"));
                        list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                        otherMoneyPrint(list, order.getBuyerPayableMoney(),
                                "实付：                   %.2f");
                        list.add(StringUtils.strTobytes("-------------------------------"));
                        list.add(DataForSendToPrinterPos58.printAndFeedForward(2));

                        //用户信息
                        list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00010001));
                        String buyerFullAddress = order.getBuyerFullAddress();
                        if (!TextUtils.isEmpty(buyerFullAddress)) {
                            list.add(StringUtils.strTobytes(buyerFullAddress));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                        }
                        String buyerFullName = order.getBuyerFullName();
                        if (!TextUtils.isEmpty(buyerFullName)) {
                            list.add(StringUtils.strTobytes(buyerFullName));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                        }
                        String buyerMobile = order.getBuyerMobile();
                        if (!TextUtils.isEmpty(buyerMobile)) {
                            list.add(StringUtils.strTobytes(buyerMobile));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                        }
                        list.add(DataForSendToPrinterPos58.printAndFeedForward(1));

                        String order_id = orderUpBean.getOrder_id();
                        if (!TextUtils.isEmpty(order_id)) {
                            Bitmap barCode = RxBarCode
                                    .createBarCode(order_id, 400, 100, Color.rgb(255, 255, 255),
                                            Color.rgb(0, 0, 0));
                            list.add(DataForSendToPrinterPos58
                                    .printRasterBmp(0, barCode, BitmapToByteData.BmpType.Threshold,
                                            BitmapToByteData.AlignType.Left, 300));
                            //                            byte[] bytes = DataForSendToPrinterPos58.printRasterBmp(0, barCode, BitmapToByteData.BmpType.Threshold, BitmapToByteData.AlignType.Left, 300);
                            //                            for (int i = 0; i < bytes.length; i ++)
                            //                            {
                            //                                Log.e("data" + i, String.valueOf(bytes[i]));
                            //                                Log.e("char" + i, (char)bytes[i] + "");
                            //                            }
                            list.add(DataForSendToPrinterPos58.selectAlignment(1));
                            list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00000000));
                            list.add(StringUtils.strTobytes(order_id));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(2));
                        }

                        if (!TextUtils.isEmpty(orderNum)) {
                            list.add(DataForSendToPrinterPos58.selectAlignment(1));
                            list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00000000));
                            list.add(StringUtils.strTobytes("***"));
                            list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00010001));
                            list.add(StringUtils.strTobytes(" #" + orderNum + " 完成 "));
                            list.add(DataForSendToPrinterPos58.selectCharacterSize(0b00000000));
                            list.add(StringUtils.strTobytes("***"));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
                        }
                    }
                }
                list.add(DataForSendToPrinterPos58.printAndFeedForward(2));
                return list;
            });
        }
    }

    private void otherMoneyPrint(List<byte[]> list, double freightMoney, String s) {
        if (freightMoney != 0) {
            list.add(StringUtils.strTobytes(String.format(s, freightMoney * 0.01)));
            list.add(DataForSendToPrinterPos58.printAndFeedForward(1));
        }
    }

    private void clickCopyOrderNo(View view) {
        int position = (int) view.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        if (orderUpBean != null) {
            String order_id = orderUpBean.getOrder_id();
            if (!TextUtils.isEmpty(order_id)) {
                MyUtils.copyText(mContext, order_id);
            }
        }
    }

    private void clickDeliveryPhone(View view) {
        int position = (int) view.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        if (orderUpBean != null) {
            OrderUpBean.OrderBean order = orderUpBean.getOrder();
            if (order != null) {
                String deliveryManPhone = order.getDeliveryManPhone();
                if (TextUtils.isEmpty(deliveryManPhone)) {
                    MyToastUtils.error("配送员手机号未知");
                } else {
                    MyUtils.call(mContext, deliveryManPhone);
                }
            }
        }
    }

    private void clickIvPhone(View v) {
        int position = (int) v.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        if (orderUpBean != null) {
            OrderUpBean.OrderBean order = orderUpBean.getOrder();
            if (order != null) {
                String buyerMobile = order.getBuyerMobile();
                if (!TextUtils.isEmpty(buyerMobile)) {
                    MyUtils.call(mContext, buyerMobile);
                }
            }
        }
    }

    private void clickOnOff(View v) {
        int position = (int) v.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        orderUpBean.setGoodsOpen(!orderUpBean.isGoodsOpen());
        notifyItemChanged(position);
    }

    private void clickDistributionAction(View v) {
        int position = (int) v.getTag();
        OrderUpBean orderUpBean = mDataList.get(position);
        orderUpBean.setDistributionInfoOpen(!orderUpBean.isDistributionInfoOpen());
        notifyItemChanged(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cl_on)
        ConstraintLayout clOn;
        @BindView(R.id.tv_on)
        TextView tvOn;
        @BindView(R.id.cl_off)
        ConstraintLayout clOff;
        @BindView(R.id.tv_distribution_action)
        TextView tvDistributionAction;
        @BindView(R.id.ll_distribution_info)
        LinearLayout llDistributionInfo;
        @BindView(R.id.tv_order_no)
        TextView tvOrderNo;
        @BindView(R.id.tv_buyer_name)
        TextView tvBuyerName;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.tv_product_count)
        TextView tvProductCount;
        @BindView(R.id.tv_tip)
        TextView tvTip;
        @BindView(R.id.ll_goods)
        LinearLayout llGoods;
        @BindView(R.id.tv_freight_money)
        TextView tvFreightMoney;
        @BindView(R.id.tv_packaging_money)
        TextView tvPackagingMoney;
        @BindView(R.id.tv_pay_money0)
        TextView tvPayMoney0;
        @BindView(R.id.tv_pay_money1)
        TextView getTvPayMoney1;
        @BindView(R.id.ll_discount)
        LinearLayout llDiscount;
        @BindView(R.id.iv_tag)
        ImageView ivTag;
        @BindView(R.id.iv_phone)
        ImageView ivPhone;
        @BindView(R.id.tv_delivery_name)
        TextView tvDeliveryName;
        @BindView(R.id.tv_delivery_time)
        TextView tvDeliveryTime;
        @BindView(R.id.iv_delivery_phone)
        ImageView ivDeliveryPhone;
        @BindView(R.id.tv_printer)
        TextView tvPrinter;
        @BindView(R.id.tv_status_order)
        TextView tvStatusOrder;
        @BindView(R.id.tv_position)
        TextView tvPosition;
        @BindView(R.id.tv_bottom_off)
        View tvBottomOff;
        @BindView(R.id.cl_delivery)
        ConstraintLayout clDelivery;
        @BindView(R.id.tv_order_info_latest)
        TextView tvOrderInfoLatest;//订单最新动态
        @BindView(R.id.tv_order_infos)
        TextView tvOrderInfos;//订单全部动态
        @BindView(R.id.tv_maoli)
        TextView tvMaoLi;
        @BindView(R.id.ll_double_btn)
        LinearLayout llDoubleBtn;
        @BindView(R.id.tv_order_time)
        TextView tvOrderTime;
        @BindView(R.id.tv_one_btn)
        TextView tvBtnOne;
        @BindView(R.id.tv_btn_confirm)
        TextView tvBtnConfirm;
        @BindView(R.id.tv_btn_refuse)
        TextView tvBtnRefuse;
        @BindView(R.id.iv_bar_code)
        ImageView ivBarCode;
        @BindView(R.id.tv_bar_code)
        TextView tvBarCode;
        @BindView(R.id.tv_pay_status)
        TextView tvPayStatus;
        //剩余时间 拣货、送达
        @BindView(R.id.ll_remaining_time)
        LinearLayout llRemainingTime;
        @BindView(R.id.tv_remaining_time_type)
        TextView tvRemainingTimeType;
        @BindView(R.id.tv_remaining_time_time)
        TextView tvRemainingTimeTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
