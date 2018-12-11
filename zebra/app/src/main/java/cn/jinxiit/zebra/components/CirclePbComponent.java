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
import cn.jinxiit.zebra.views.CircleProgressBar;

public class CirclePbComponent
{
    @BindView(R.id.tv_all_tip)
    TextView mTvAllTip;
    @BindView(R.id.tv_new_tip)
    TextView mTvNewTip;
    @BindView(R.id.tv_old_tip)
    TextView mTvOldTip;
    @BindView(R.id.tv_compared_tip0)
    TextView mTvComparedTip0;
    @BindView(R.id.tv_compared_tip1)
    TextView mTvComparedTip1;
    @BindView(R.id.tv_compared_tip2)
    TextView mTvComparedTip2;
    @BindView(R.id.cpb)
    CircleProgressBar mCpb;
    @BindView(R.id.tv_all_count)
    TextView mTvAllCount;
    @BindView(R.id.tv_compared_all)
    TextView mTvComparedAll;
    @BindView(R.id.tv_new_count)
    TextView mTvNewCount;
    @BindView(R.id.tv_compared_new)
    TextView mTvComparedNew;
    @BindView(R.id.tv_compared_old)
    TextView mTvComparedOld;
    @BindView(R.id.tv_old_count)
    TextView mTvOldCount;

    private Context mContext;

    @SuppressLint("SetTextI18n")
    public CirclePbComponent(View layout, String type, String dateType, int one, int beforOne, int two, int beforTwo, int three, int beforThree)
    {
        if (mContext == null)
        {
            mContext = layout.getContext();
        }
        ButterKnife.bind(this, layout);

        ZebraUtils zebraUtils = ZebraUtils.getInstance();
        String strCompared = "自定义趋势";
        switch (dateType)
        {
            case MyConstant.STR_TODAY:
            case MyConstant.STR_YESTERDAY:
                strCompared = "较前日";
                break;
            case MyConstant.STR_SEVENDAY:
                strCompared = "较前7日";
                break;
            case MyConstant.STR_MONTH:
                strCompared = "较前30日";
                break;
            case MyConstant.STR_DIYDAY:
                strCompared = "自定义趋势";
                break;
        }
        mTvComparedTip0.setHint(strCompared);
        mTvComparedTip1.setHint(strCompared);
        mTvComparedTip2.setHint(strCompared);

        switch (type)
        {
            case MyConstant.STR_ORDER:
                mTvAllTip.setHint("订单总数");
                mTvNewTip.setHint("有效订单数");
                mTvOldTip.setHint("无效订单数");
                break;
            case MyConstant.STR_CUSTOMER:
                mTvAllTip.setHint("顾客总数");
                mTvNewTip.setHint("新顾客总数");
                mTvOldTip.setHint("老顾客总数");
                break;
        }

        mTvAllCount.setText(one + "");
        zebraUtils.jsjComparedProportion(one, beforOne, mTvComparedAll);

        mTvNewCount.setText(two + "");
        zebraUtils.jsjComparedProportion(two, beforTwo, mTvComparedNew);

        mTvOldCount.setText(three + "");
        zebraUtils.jsjComparedProportion(three, beforThree, mTvComparedOld);

        mCpb.setProgress((int) ((float)two / (float)one * 360f));
    }
}
