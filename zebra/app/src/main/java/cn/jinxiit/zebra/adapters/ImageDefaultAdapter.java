package cn.jinxiit.zebra.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.WindowUtils;

public class ImageDefaultAdapter extends RecyclerView.Adapter<ImageDefaultAdapter.ViewHolder>
{
    private Context mContext;
    private List<String> mDataList;
    private GridImageAdapter.onAddPicClickListener mOnAddPicClickListener;

    public ImageDefaultAdapter(List<String> list, GridImageAdapter.onAddPicClickListener onAddPicClickListener)
    {
        mDataList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
        {
            if (i > 2) break;
            mDataList.add(list.get(i));
        }
        mOnAddPicClickListener = onAddPicClickListener;
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
                .inflate(R.layout.recycler_item_img_default, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.ivImg.setOnClickListener(v -> mOnAddPicClickListener.onAddPicClick());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        String fileKey = mDataList.get(position);
        if (!TextUtils.isEmpty(fileKey))
        {
            int width = WindowUtils.dip2px(mContext, 90);
            String url = MyConstant.URL_GET_FILE + fileKey + ".jpg";
            MyPicassoUtils.downSizeImageForUrl(width, width, url, holder.ivImg);
            Picasso.get()
                    .load(url)
                    .into(mTarget);
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_img)
        ImageView ivImg;

        ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private Target mTarget = new Target()
    {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            saveImage(bitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable)
        {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable)
        {

        }
    };

    private String saveImage(Bitmap mBitmap)
    {
        String urlpath = "";

        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED))
        {
            File file = Environment.getExternalStorageDirectory();

            File file1 = new File(file + File.separator + "zebra");

            if (!file1.exists())
            {
                file1.mkdirs();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();

            StringBuilder builder = new StringBuilder(1024);
            builder.append("img_");
            for (int i = datas.length - 1; i >= 0; i--)
            {
                if (i < datas.length - 16) break;
                builder.append(datas[i]);
            }

            String fileName = builder.toString() + ".jpg";
            builder.reverse();
            File file2 = new File(file1 + File.separator + fileName);

            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(file2);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
                fos.flush();
                fos.close();
                urlpath = file2.getAbsolutePath();
                Log.e("bitmap", "保存图片成功！！" + urlpath);
                // 其次把文件插入到系统图库
                try
                {
                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), urlpath, fileName, null);
                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                // 最后通知图库更新
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + urlpath)));

            } catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                if (fos != null)
                {
                    try
                    {
                        fos.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return urlpath;
    }
}
