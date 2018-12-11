package cn.jinxiit.zebra.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bigkoo.pickerview.builder.TimePickerBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.interfaces.OnRecyclerViewItemClickListener;

public class StoreTimeAdapter extends RecyclerView.Adapter<StoreTimeAdapter.ViewHolder>
{
    private List<String> mDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mDeleteItemClickListener;
    private SimpleDateFormat mDateFormat;

    public void setDeleteItemClickListener(OnRecyclerViewItemClickListener mDeleteItemClickListener)
    {
        this.mDeleteItemClickListener = mDeleteItemClickListener;
    }

    @SuppressLint("SimpleDateFormat")
    public StoreTimeAdapter()
    {
        this.mDataList = new ArrayList<>();
        mDateFormat = new SimpleDateFormat("HH:mm");
    }

    public void addDataList(List<String> timeList)
    {
        if (timeList != null && timeList.size() > 0)
        {
            mDataList.addAll(timeList);
            myNotifyDataSetChanged();
        }
    }

    public void addData(String time)
    {
        mDataList.add(time);
        notifyItemChanged(mDataList.size() - 1);
    }

    public List<String> getmDataList()
    {
        return mDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_item_stroe_time, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.btnDelete.setOnClickListener(v -> clickDelete(v));
        holder.btnStartTime.setOnClickListener(v -> clickStartTime(v));
        holder.btnEndTime.setOnClickListener(v -> clickEndTime(v));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.btnDelete.setTag(position);
        holder.btnStartTime.setTag(position);
        holder.btnEndTime.setTag(position);

        holder.btnStartTime.setText("");
        holder.btnEndTime.setText("");

        String time = mDataList.get(position);
        if (!TextUtils.isEmpty(time))
        {
            String[] split = time.split("-");

            if (split.length > 0)
            {
                holder.btnStartTime.setText(split[0]);
            }

            if (split.length > 1)
            {
                holder.btnEndTime.setText(split[1]);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    private void myNotifyDataSetChanged()
    {
        List<String> tempList = new ArrayList<>();
        int size = mDataList.size();
        for (int i = 0; i < size; i++)
        {
            String tempTime = mDataList.get(0);
            for (int j = 1; j < mDataList.size(); j++)
            {
                String time = mDataList.get(j);
                if (tempTime == null)
                {
                    tempTime = time;
                } else
                {
                    if (!TextUtils.isEmpty(time))
                    {
                        String[] timeSplit = time.split("-");
                        if (!TextUtils.isEmpty(timeSplit[0]))
                        {
                            String[] tempTimeSplit = tempTime.split("-");
                            if (TextUtils.isEmpty(tempTimeSplit[0]))
                            {
                                tempTime = time;
                            } else
                            {
                                try
                                {
                                    if (mDateFormat.parse(timeSplit[0])
                                            .getTime() < mDateFormat.parse(tempTimeSplit[0])
                                            .getTime())
                                    {
                                        tempTime = time;
                                    }
                                } catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            mDataList.remove(tempTime);
            tempList.add(tempTime);
        }
        mDataList = tempList;
        notifyDataSetChanged();
    }

    private void clickDelete(View v)
    {
        int position = (int) v.getTag();
        mDataList.remove(position);
        notifyDataSetChanged();
        if (mDeleteItemClickListener != null)
        {
            mDeleteItemClickListener.onClick(v, position);
        }
    }

    private void clickEndTime(View view)
    {
        int position = (int) view.getTag();
        String time = mDataList.get(position);

        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(mContext, (date, v) -> {//选中事件回调
            String format = mDateFormat.format(date);
            if (!TextUtils.isEmpty(time))
            {
                String[] split = time.split("-");
                if (split.length > 0)
                {
                    mDataList.set(position, split[0] + "-" + format);
                }
            } else
            {
                mDataList.set(position, "-" + format);
            }
            notifyItemChanged(position);
        }).setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                //                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("结束时刻")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                //                .setDate(selectedCalendar)// 如果不设置的话，默认是系统时间*/
                .setLabel(null, null, null, ":\t\t", "", null)//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true);//是否显示为对话框样式

        if (!TextUtils.isEmpty(time))
        {
            String[] split = time.split("-");
            if (split.length > 1)
            {
                if (!TextUtils.isEmpty(split[1]))
                {
                    try
                    {
                        Date parse = mDateFormat.parse(split[1]);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(parse);
                        timePickerBuilder.setDate(calendar);
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        timePickerBuilder.build()
                .show();
    }

    private void clickStartTime(View view)
    {
        int position = (int) view.getTag();
        String time = mDataList.get(position);

        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(mContext, (date, v) -> {//选中事件回调

            String format = mDateFormat.format(date);
            StringBuilder builder = new StringBuilder(1024);
            builder.append(format);
            builder.append("-");
            if (!TextUtils.isEmpty(time))
            {
                String[] split = time.split("-");
                if (split.length > 1)
                {
                    builder.append(split[1]);
                }
            }
            mDataList.set(position, builder.toString());
            builder.reverse();
            myNotifyDataSetChanged();

        }).setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                //                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("开始时刻")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                //                .setDate(selectedCalendar)// 如果不设置的话，默认是系统时间*/
                .setLabel(null, null, null, ":\t\t", "", null)//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true);//是否显示为对话框样式

        if (!TextUtils.isEmpty(time))
        {
            String[] split = time.split("-");
            if (split.length > 0)
            {
                if (!TextUtils.isEmpty(split[0]))
                {
                    try
                    {
                        Date parse = mDateFormat.parse(split[0]);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(parse);
                        timePickerBuilder.setDate(calendar);
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        timePickerBuilder.build()
                .show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.btn_start_time)
        Button btnStartTime;
        @BindView(R.id.btn_end_time)
        Button btnEndTime;
        @BindView(R.id.btn_delete)
        Button btnDelete;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
