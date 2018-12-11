package cn.jinxiit.zebra.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.NumberUtils;

public class ChartMarkerView extends MarkerView
{
    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
        isReverse = !(highlight.getX() > 3);
        StringBuffer content = new StringBuffer(1024);

        int position = (int) e.getX();
        String time = jsTime(position);

        if (pingTaiMap == null)
        {
            initNoMapData(e.getY(), content, time);
        }
        else
        {
            content.append(String.format("%s(%s)\n%s", item, unit, time));
            int size0 = pingtaiData(MyConstant.STR_ALLSTATIS, content, position);
            int size1 = pingtaiData(MyConstant.STR_JDDJ,content, position);
            int size2 = pingtaiData(MyConstant.STR_ELEME,content, position);
            int size3 = pingtaiData(MyConstant.STR_MT,content, position);

            isReverse = !(highlight.getX() >= Math.max(Math.max(size0, size1), Math.max(size2, size3)) / 2);
        }

        tv_indicator.setText(content.toString());
        content.reverse();
        super.refreshContent(e, highlight);
    }

    private int pingtaiData(String pingtai, StringBuffer content, int position)
    {
        List<Float> list = pingTaiMap.get(pingtai);
        if (list == null) return 0;

        Float aFloat = 0f;
        if (position < list.size())
        {
            aFloat = list.get(position);
        }
        String data = NumberUtils.floatQu0(aFloat);
        content.append(String.format("\n%s: %s", pingtai,data));
        return list.size();
    }

    private void initNoMapData(float f, StringBuffer content, String time)
    {
        content.append(time);
        content.append("\n");
        String strData = NumberUtils.floatQu0(f);
        content.append(item);
        content.append(strData);
        content.append(unit);
    }

    private String jsTime(int position)
    {
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            date = simpleDateFormat.parse(startTime);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        if (date != null)
        {
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, position);
            return simpleDateFormat.format(calendar.getTime());
        }
        else
        {
            return startTime;
        }
    }

    TextView tv_indicator;
    String item;
    String startTime;
    Map<String, List<Float>> pingTaiMap;

    String unit;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param layoutResource the layout resource to use for the MarkerView
     * @param pingTaiMap
     */
    public ChartMarkerView(Context context, int layoutResource, String item, String unit, String startTime, Map<String, List<Float>> pingTaiMap)
    {
        super(context, layoutResource);
        tv_indicator = (TextView) findViewById(R.id.tv_indicator);
        this.item = null == item ? "" : item;
        this.unit = null == unit ? "" : unit;
        this.startTime = null == startTime ? "1989-06-17" : startTime;
        this.pingTaiMap = pingTaiMap;
    }

    private boolean isReverse = true;

    @Override
    public MPPointF getOffset()
    {
        MPPointF mpPointF = super.getOffset();
        if (!isReverse)
        {
            mpPointF.x = -tv_indicator.getWidth();
        } else
        {
            mpPointF.x = 0;
        }
        mpPointF.y = -tv_indicator.getHeight();
        return mpPointF;
    }
}
