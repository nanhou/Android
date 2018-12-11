package cn.jinxiit.zebra.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.vondear.rxtools.view.dialog.RxDialogSureCancel;

public class MyDailogUtils
{
    public static void dailogDismiss(Dialog dialog, Activity activity)
    {
        if (dialog != null && dialog.isShowing())
        {
//            Activity activity = dialog.getOwnerActivity();
            if (activity != null && !activity.isFinishing())
            {
                dialog.dismiss();
            }
        }
    }

    public static RxDialogSureCancel createReverseCanSureCancelDialog(Context context, String title, String content, String cancel, String sure) {
        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(context);
        rxDialogSureCancel.setTitle(title);
        rxDialogSureCancel.setContent(content);
        rxDialogSureCancel.getTitleView()
                .setTextSize(17);
        rxDialogSureCancel.getContentView()
                .setTextSize(14);
        TextView sureView = rxDialogSureCancel.getSureView();
        TextView cancelView = rxDialogSureCancel.getCancelView();
        sureView.setText(cancel);
        cancelView.setText(sure);
        return rxDialogSureCancel;
    }

}
