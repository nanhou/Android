package cn.jinxiit.zebra.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.views.ChartMarkerView;

public class ChartComponent
{
    @BindView(R.id.radiogroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.chart)
    LineChart mLineChart;

    private Context mContext;
    private List<String> mTitles;

    //类型 平台 数据
    private Map<String, Map<String, List<Float>>> mDataMap;
    private static final String[] mPingTai = {MyConstant.STR_ALLSTATIS, MyConstant.STR_JDDJ, MyConstant.STR_ELEME, MyConstant.STR_MT};

    private String mStartDate;//起始时间
    private Map<String, String> mItemMap;//不同类型的描述
    private Map<String, String> mUnitMap;//不同类型的单位

    public ChartComponent(View layout, List<String> titles)
    {
        if (mContext == null)
        {
            mContext = layout.getContext();
        }
        ButterKnife.bind(this, layout);

        this.mTitles = titles;
        initRadioGroup(mTitles);
        initChart(0);
        initListener();
    }

    public void setData(Map<String, Map<String, List<Float>>> mDataMap, String startDate, Map<String, String> itemMap, Map<String, String> unitMap)
    {
        for (Map.Entry<String, Map<String, List<Float>>> entry0 : mDataMap.entrySet())
        {
            Map<String, List<Float>> value0 = entry0.getValue();
            for (Map.Entry<String, List<Float>> entry1 : value0.entrySet())
            {
                List<Float> value1 = entry1.getValue();
                if (value1 == null || value1.size() == 0)
                {
                    return;
                }
            }
        }

        this.mDataMap = mDataMap;
        this.mStartDate = startDate;
        this.mItemMap = itemMap;
        this.mUnitMap = unitMap;
        int position = mRadioGroup.indexOfChild(mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId()));
        initChart(position);
        lineChartsetData(position);
        //        mRadioGroup.check(mRadioGroup.getChildAt(0)
        //                .getId());
    }

    private void initListener()
    {
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (mLineChart != null)
            {
                int position = group.indexOfChild(group.findViewById(checkedId));
                initChart(position);
                lineChartsetData(position);
            }
        });
    }

    private void initChart(int position)
    {
        mLineChart.setScaleEnabled(false);
        mLineChart.setDragEnabled(false);
        mLineChart.setTouchEnabled(true);
        XAxis xAxis = mLineChart.getXAxis();
        // X轴更多属性
        //        xAxis.setLabelRotationAngle(45);   // 标签倾斜
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // X轴绘制位置，默认是顶部
        //        YAxis yAxis = mLineChart.getAxisLeft();

        if (mUnitMap != null && mItemMap != null && mStartDate != null && mDataMap != null)
        {
            String key = mTitles.get(position);
            String unit = mUnitMap.get(key);
            String item = mItemMap.get(key);
            Map<String, List<Float>> pingTaiMap = mDataMap.get(key);
            mLineChart.setMarker(new ChartMarkerView(mContext, R.layout.item_chart_indicator, item, unit, mStartDate, pingTaiMap));
            xAxis.setValueFormatter((value, axis) -> {
                Calendar calendar = Calendar.getInstance();
                Date date = null;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
                try
                {
                    date = simpleDateFormat.parse(mStartDate);
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
                if (date != null)
                {
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, (int) value);

                    return dateFormat.format(calendar.getTime());
                } else
                {
                    return mStartDate;
                }
            });
        }

        // 轴值转换显示
        //        yAxis.setValueFormatter(new IAxisValueFormatter()
        //        { // 与上面值转换一样，这里就是转换出轴上label的显示。也有几个默认的，不多说了。
        //            @SuppressLint("DefaultLocale")
        //            @Override
        //            public String getFormattedValue(float value, AxisBase axis)
        //            {
        ////                return value + "℃";
        //                return String.format("%.2f", value);
        //            }
        //        });

        // **************************图表本身一般样式**************************** //
        mLineChart.setBackgroundColor(Color.WHITE); // 整个图标的背景色
        Description description = new Description();  // 这部分是深度定制描述文本，颜色，字体等
        description.setText("");//表名
        description.setTextColor(Color.RED);
        mLineChart.setDescription(description);
        mLineChart.setNoDataText("暂无数据");   // 没有数据时样式
        mLineChart.setDrawGridBackground(false);    // 绘制区域的背景（默认是一个灰色矩形背景）将绘制，默认false
        mLineChart.setGridBackgroundColor(Color.WHITE);  // 绘制区域的背景色
        mLineChart.setDrawBorders(false);    // 绘制区域边框绘制，默认false
        mLineChart.setBorderColor(Color.GREEN); // 边框颜色
        mLineChart.setBorderWidth(2);   // 边框宽度,dp
        mLineChart.setMaxVisibleValueCount(14);  // 数据点上显示的标签，最大数量，默认100。且dataSet.setDrawValues(true);必须为true。只有当数据数量小于该值才会绘制标签
    }

    private void lineChartsetData(int position)
    {
        if (mDataMap == null)
        {
            return;
        }

        String title = mTitles.get(position);

        Map<String, List<Float>> map = mDataMap.get(title);

        List<ILineDataSet> sets = new ArrayList<>();  // 多条线
        for (int j = 0; j < mPingTai.length; j++)
        {
            String label = mPingTai[j];
            List<Float> list = map.get(label);
            if (list == null)
            {
                continue;
            }

            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++)
            {
                float data = list.get(i);
                entries.add(new Entry(i, data));
            }

            int color;
            switch (j)
            {
                case 0:
                    //                    color = Color.BLUE;
                    color = Color.rgb(19, 127, 255);
                    break;
                case 1:
                    //                    color = Color.RED;
                    color = Color.rgb(19, 255, 197);
                    break;
                case 2:
                    //                    color = Color.GREEN;
                    color = Color.rgb(254, 85, 82);
                    break;
                case 3:
                    //                    color = Color.BLACK;
                    color = Color.rgb(0, 198, 255);

                    break;
                default:
                    //                    color = Color.BLUE;
                    color = Color.rgb(19, 127, 255);
                    break;
            }
            LineDataSet dataSet = new LineDataSet(entries, label);
            dataSet.setColors(color); // 每个点之间线的颜色，还有其他几个方法，自己看
            dataSet.setLineWidth(3);
            dataSet.setCircleRadius(1f);
            // 将值转换为想要显示的形式，比如，某点值为1，变为“1￥”,MP提供了三个默认的转换器，
            // LargeValueFormatter:将大数字变为带单位数字；PercentFormatter：将值转换为百分数；StackedValueFormatter，对于BarChart，是否只显示最大值图还是都显示
            //            dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> "￥" + value);
            sets.add(dataSet);
        }
        if (sets.size() == 0)
        {
            return;
        }
        LineData lineData = new LineData(sets);
        mLineChart.setData(lineData);
        mLineChart.animateY(1000, Easing.EasingOption.EaseInQuad);
    }

    private void initRadioGroup(List<String> titles)
    {
        mRadioGroup.removeAllViews();
        for (int i = 0; i < titles.size(); i++)
        {
            RadioButton radioButton = new RadioButton(mContext);
            int width = (WindowUtils.getScreenSize(mContext)[0] - WindowUtils.dip2px(mContext, 70)) / 5;
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(width, WindowUtils.dip2px(mContext, 30));
            //            lp.weight = 1.0f;
            //            if (i != 0)
            //            {
            lp.setMargins(WindowUtils.dip2px(mContext, 10), 0, 0, 0);
            //            }
            radioButton.setLayoutParams(lp);

            if (i == 0)
            {
                radioButton.setChecked(true);
            }
            radioButton.setSingleLine();
            radioButton.setTextSize(12);
            radioButton.setElevation(WindowUtils.dip2px(mContext, 5));
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setBackgroundResource(R.drawable.selector_radio0);
            radioButton.setButtonDrawable(null);
            radioButton.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            radioButton.setTextColor(ContextCompat.getColorStateList(mContext, R.color.selector_radio_text1));
            radioButton.setText(titles.get(i));
            radioButton.setId(i);
            mRadioGroup.addView(radioButton);
        }
    }
}