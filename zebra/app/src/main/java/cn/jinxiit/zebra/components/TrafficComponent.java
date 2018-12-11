package cn.jinxiit.zebra.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class TrafficComponent
{
    @BindView(R.id.tv_tip0)
    TextView mTvTip0;
    @BindView(R.id.tv_tip1)
    TextView mTvTip1;
    @BindView(R.id.tv_tip2)
    TextView mTvTip2;
    @BindView(R.id.tv_tip3)
    TextView mTvTip3;
    @BindView(R.id.tv_tip4)
    TextView mTvTip4;
    @BindView(R.id.tv_exposure_yesterday)
    TextView mTvYesterdayExposure;
    @BindView(R.id.tv_exposure_yesterday_compared)
    TextView mTvYesterdayExposureCompared;
    @BindView(R.id.tv_visiter_yesterday)
    TextView mTvYesterdayVisiter;
    @BindView(R.id.tv_visiter_yesterday_compared)
    TextView mTvYesterdayVisiterCompared;
    @BindView(R.id.tv_order_count_yesterday)
    TextView mTvYesterdayOrderCount;
    @BindView(R.id.tv_order_count_yesterday_compared)
    TextView mTvYesterdayOrderCountCompared;
    @BindView(R.id.tv_visiter_yesterday_conversion)
    TextView mTvYesterdayVisiterConversion;
    @BindView(R.id.tv_visiter_yesterday_conversion_compared)
    TextView mTvYesterdayVisiterConversionCompared;
    @BindView(R.id.tv_ordered_yesterday_conversion)
    TextView mTvYesterdayOrderedConversion;
    @BindView(R.id.tv_ordered_yesterday_conversion_compared)
    TextView mTvYesterdayOrderedConversionCompared;

    private Context mContext;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public TrafficComponent(View layout, String dateType, int exposure0, int exposure1, int visiter0, int visiter1, int totalOrder0, int totalOrder1)
    {
        if (mContext == null)
        {
            mContext = layout.getContext();
        }
        ButterKnife.bind(this, layout);
        ZebraUtils zebraUtils = ZebraUtils.getInstance();
        String strCompared = "较前日";
        switch (dateType)
        {
            case MyConstant.STR_TODAY:
            case MyConstant.STR_YESTERDAY:
                break;
            case MyConstant.STR_SEVENDAY:
                strCompared = "较前7日";
                break;
            case MyConstant.STR_MONTH:
                strCompared = "较前30日";
                break;
        }
        mTvTip0.setHint(strCompared);
        mTvTip1.setHint(strCompared);
        mTvTip2.setHint(strCompared);
        mTvTip3.setHint(strCompared);
        mTvTip4.setHint(strCompared);

        mTvYesterdayExposure.setText(exposure0 + "");
        zebraUtils.jsjComparedProportion((float) exposure0, (float) exposure1, mTvYesterdayExposureCompared);

        mTvYesterdayVisiter.setText(visiter0 + "");
        zebraUtils.jsjComparedProportion((float) visiter0, (float) visiter1, mTvYesterdayVisiterCompared);

        mTvYesterdayOrderCount.setText(totalOrder0 + "");
        zebraUtils.jsjComparedProportion((float) totalOrder0, (float) totalOrder1, mTvYesterdayOrderCountCompared);

        float yesterdayVisiterConversion0 = exposure0 == 0 ? visiter0 == 0 ? 0 : 1 : (float) visiter0 / (float) exposure0;
        float yesterdayVisiterConversion1 = exposure1 == 0 ? visiter1 == 0 ? 0 : 1 : (float) visiter1 / (float) exposure1;

        mTvYesterdayVisiterConversion.setText(String.format("%.2f%s", yesterdayVisiterConversion0 * 100, "%"));
        zebraUtils.jsjComparedDifference(yesterdayVisiterConversion0, yesterdayVisiterConversion1, mTvYesterdayVisiterConversionCompared);

        float yesterdayOrderedConversion0 = visiter0 == 0 ? totalOrder0 == 0 ? 0 : 1 : (float) totalOrder0 / (float) visiter0;
        float yesterdayOrderedConversion1 = visiter1 == 0 ? totalOrder1 == 0 ? 0 : 1 : (float) totalOrder1 / (float) visiter1;

        mTvYesterdayOrderedConversion.setText(String.format("%.2f%s", yesterdayOrderedConversion0 * 100, "%"));
        zebraUtils.jsjComparedDifference(yesterdayOrderedConversion0, yesterdayOrderedConversion1, mTvYesterdayOrderedConversionCompared);
    }
}
