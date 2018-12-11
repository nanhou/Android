package cn.jinxiit.zebra.toolbars;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jinxiit.zebra.R;

public class MyToolBar
{
    private OnTopMenuClickListener onTopMenuClickListener;
    private TextView tvMenu;

    public MyToolBar(final Activity activity, String title, String menu)
    {
        ImageView ivBack = activity.findViewById(R.id.iv_back);
        TextView tvTitle = activity.findViewById(R.id.tv_title);

        tvMenu = activity.findViewById(R.id.tv_menu);

        if (ivBack == null || tvTitle == null || tvMenu == null)
        {
            return;
        }

        ivBack.setOnClickListener(v -> activity.onBackPressed());
        tvTitle.setText(title);

        if (!TextUtils.isEmpty(menu))
        {
            tvMenu.setVisibility(View.VISIBLE);
            tvMenu.setText(menu);
            tvMenu.setOnClickListener(v -> {
                if (onTopMenuClickListener != null)
                {
                    onTopMenuClickListener.onMenuClick(v);
                }
            });
        }
    }

    public void setTvMenu(String menu)
    {
        if (!TextUtils.isEmpty(menu) && tvMenu != null)
        {
            tvMenu.setText(menu);
        }
    }

    public void setOnTopMenuClickListener(OnTopMenuClickListener onTopMenuClickListener)
    {
        this.onTopMenuClickListener = onTopMenuClickListener;
    }

    public interface OnTopMenuClickListener
    {
        void onMenuClick(View v);
    }
}
