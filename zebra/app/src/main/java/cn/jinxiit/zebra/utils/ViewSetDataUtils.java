package cn.jinxiit.zebra.utils;

import android.text.TextUtils;
import android.widget.TextView;

public class ViewSetDataUtils
{
    public static void textViewSetText(TextView textView, String text)
    {
        if (!TextUtils.isEmpty(text))
        {
            textView.setText(text);
        }
        else
        {
            textView.setText("");
        }
    }

    public static void textViewSetHint(TextView textView, String text)
    {
        if (!TextUtils.isEmpty(text))
        {
            textView.setHint(text);
        }
        else
        {
            textView.setHint("");
        }
    }
}
