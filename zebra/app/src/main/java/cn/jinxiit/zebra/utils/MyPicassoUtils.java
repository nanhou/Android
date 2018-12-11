package cn.jinxiit.zebra.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import cn.jinxiit.zebra.R;

public class MyPicassoUtils
{
    public static void downSizeImageForUrl(int width, int height, String url, ImageView imageView)
    {
        if (TextUtils.isEmpty(url))
        {
            return;
        }
        if (width == 0 && height == 0)
        {
            return;
        }

        Picasso.get()
                .load(url)
                .resize(width, height)
                .centerCrop()
                .placeholder(R.drawable.img_default)
                .error(R.drawable.img_default)
                .noFade()
                .into(imageView);
    }

    public static void downSizeImageForUri(int with, int height, Uri uri, ImageView imageView)
    {
        if (uri == null)
        {
            return;
        }
        if (with == 0 && height == 0)
        {
            return;
        }
        Picasso.get()
                .load(uri)
                .resize(with, height)
                .centerCrop()
                .noFade()
                .into(imageView);
    }

    public static void downSizeImageForPath(int with, int height, String path, ImageView imageView)
    {
        if (path == null)
        {
            return;
        }
        if (with == 0 && height == 0)
        {
            return;
        }
        Picasso.get()
                .load(new File(path))
                .resize(with, height)
                .placeholder(R.drawable.img_default)
                .error(R.drawable.img_default)
                .centerCrop()
                .noFade()
                .into(imageView);
    }
}
