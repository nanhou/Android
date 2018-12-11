package cn.jinxiit.zebra.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xiaosu.lib.permission.OnRequestPermissionsCallBack;
import com.xiaosu.lib.permission.PermissionCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.jinxiit.zebra.R;

import static com.vondear.rxtools.RxPhotoTool.getImageAbsolutePath;

/**
 * Created by jinxi on 2017/6/6.
 * 头像上传
 * 1、创建照相、图像选择窗口
 * 2、选择窗口的点击事件
 * 3、权限申请onResultPerssion 设置
 * 4、在onActivityResult 设置
 */

public class AvatarUtils
{
    //裁剪图片 请求码 onResult
    public static final int REQUEST_CODE_PHOTOZOOM = 9527;
    public static final int REQUEST_CODE_OPENCAMER = 9529;
    public static final int REQUEST_CODE_CHANGEIMG = 9530;
    //相机权限请求码 onResultPermission
    public static final int REQUEST_PERMISSION_CODE = 9528;

    private static Dialog mCameraDialog;
    private static ImageView mImageView;

    /**
     * 创建选择相机或相册的窗口
     *
     * @param activity
     * @return
     */
    public static void creatmenuWindowDialog(Activity activity, ImageView imageView)
    {
        mImageView = imageView;
        mCameraDialog = new Dialog(activity, R.style.my_dialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(activity)
                .inflate(R.layout.layout_camera_control, null);
        root.findViewById(R.id.btn_open_camera)
                .setOnClickListener((View.OnClickListener) activity);
        root.findViewById(R.id.btn_choose_img)
                .setOnClickListener((View.OnClickListener) activity);
        root.findViewById(R.id.btn_cancel)
                .setOnClickListener((View.OnClickListener) activity);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        if (dialogWindow != null)
        {
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.dialogAnimations0); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = -20; // 新位置Y坐标//隐藏头部导航栏
            lp.width = (int) activity.getResources()
                    .getDisplayMetrics().widthPixels; // 宽度
            //      lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
            //      lp.alpha = 9f; // 透明度
            root.measure(0, 0);
            lp.height = root.getMeasuredHeight();
            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
        }
        mCameraDialog.setCanceledOnTouchOutside(true);
        mCameraDialog.show();
    }

    public static void imgOnClick(final View view, final Activity activity)
    {
        MyDailogUtils.dailogDismiss(mCameraDialog, activity);
        PermissionCompat.create(activity)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .explain("该操作需要手机存储权限")
                .retry(true)
                .callBack(new OnRequestPermissionsCallBack()
                {
                    @Override
                    public void onGrant()
                    {
                        // todo 权限授权成功回调
                        switch (view.getId())
                        {
                            case R.id.btn_open_camera:
                                openCamera(activity);
                                break;
                            case R.id.btn_choose_img:
                                changeImage(activity);
                                break;
                            case R.id.btn_cancel:
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onDenied(String permission, boolean retry)
                    {
                        // todo 权限授权失败回调
                        Toast.makeText(activity, "手机存储权限，授权失败，请手动获取", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .build()
                .request();
    }

    /**
     * 选择打开相机
     * 使用前记得动态申请权限
     *
     * @param activity
     */
    private static void openCamera(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
                return;
            }
        }

        File tempFile = new File(Environment.getExternalStorageDirectory(), "img.png");
        //        File tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), "img.png");

        try
        {
            if (tempFile.exists())
            {
                tempFile.delete();
            }
            tempFile.createNewFile();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Uri uriAvatarForFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            uriAvatarForFile = FileProvider.getUriForFile(activity, "cn.jinxiit.zebra.fileprovider", tempFile);
        } else
        {
            uriAvatarForFile = Uri.fromFile(tempFile);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriAvatarForFile);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(intent, REQUEST_CODE_OPENCAMER);
    }

    private static void openCameraOnResult(Activity activity, boolean isAvatar)
    {
        File temp = new File(Environment.getExternalStorageDirectory() + "/" + "img.png");
        //        File temp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "img.png");

        Uri imageContentUri = getImageContentUri(activity, temp);

        startPhotoZoom(imageContentUri, activity, isAvatar);
        //        startPhotoZoom(imageContentUri, activity);
    }

    /**
     * 转换uri
     *
     * @param context
     * @param imageFile
     * @return
     */
    private static Uri getImageContentUri(Context context, File imageFile)
    {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst())
        {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else
        {
            if (imageFile.exists())
            {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else
            {
                return null;
            }
        }
    }

    //    android中如何把`content://media/external/images/media/Y`转换为`file:///storage/sdcard0/Pictures/X.jpg`？
    private static String getRealPathFromUri(Context context, Uri contentUri)
    {
        Cursor cursor = null;
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver()
                    .query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    private static void startPhotoZoom(Uri uri, Activity activity, boolean isAvatar)
    //    private static void startPhotoZoom(Uri uri, Activity activity)
    {
        Log.e("裁剪", uri.toString());

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);

        if (!isAvatar)
        {
            intent.putExtra("aspectX", 800);
            intent.putExtra("aspectY", 500);
            intent.putExtra("outputX", 440);
            intent.putExtra("outputY", 275);
        } else
        {
            //            intent.putExtra("aspectX", 1);
            //            intent.putExtra("aspectY", 1);
            //            intent.putExtra("outputX", 150);
            //            intent.putExtra("outputY", 150);

            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            //            intent.putExtra("crop", "true");
            //            intent.putExtra("scale", true);
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);

            // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);

        }

        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        //        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //        intent.putExtra("uri", uri);

        //        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //设置输出的地址
        //        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //不启用人脸识别
        //        intent.putExtra("noFaceDetection", true);

        activity.startActivityForResult(intent, REQUEST_CODE_PHOTOZOOM);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     * @param activity
     * @return
     */
    private static String setPicToViewOnResult(Intent picdata, Activity activity)
    {
        Bundle extras = picdata.getExtras();
        if (extras != null)
        {
            Bitmap photo = null;
            //            Uri uri = Uri.parse(picdata.getStringExtra(MediaStore.EXTRA_OUTPUT));
            //            try
            //            {
            //                photo = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            //            } catch (IOException e)
            //            {
            //                e.printStackTrace();
            //            }

            // 取得SDCard图片路径做显示
            photo = extras.getParcelable("data");
            //                        Drawable drawable = new BitmapDrawable(null, photo);
            //保持裁剪图片路径
            String urlFile = "";
            if (photo != null)
            {
                urlFile = saveMeImage(activity, photo);
                //            upTouXiang(urlFile, drawable);
                //            mIvAvatar.setImageBitmap(photo);
                if (mImageView != null)
                {
                    mImageView.setImageBitmap(photo);
                    mImageView.setBackgroundDrawable(activity.getResources()
                            .getDrawable(R.color.colorTm));
                }
            }
            return urlFile;
        }
        return null;
    }

    /**
     * 保存用户图片
     *
     * @param mBitmap 图片资源
     * @return 图片路径
     */
    private static String saveMeImage(Activity activity, Bitmap mBitmap)
    {
        String urlpath = "";

        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED))
        {
            File file = Environment.getExternalStorageDirectory();

            //            urlpath = file + "/codecard/me/me.png";

            String packageName = activity.getPackageName();

//            File file1 = new File(file + File.separator + "SleepyKitty/me");
            File file1 = new File(file + File.separator + packageName);

            if (!file1.exists())
            {
                file1.mkdirs();
            }

            File file2 = new File(file1 + File.separator + "me.png");

            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(file2);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
                fos.flush();
                fos.close();
                Log.e("bitmap", "保存图片成功！！");
                urlpath = file2.getAbsolutePath();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
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


    /**
     * 选择相册
     *
     * @param activity
     */
    private static void changeImage(Activity activity)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_CODE_CHANGEIMG);
    }

    /**
     * @param requestCode
     * @param data
     * @param activity
     * @return
     */
    public static String onActivityResult(int requestCode, Intent data, Activity activity, boolean isAvatar, boolean isYuan)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_OPENCAMER:
                if (isYuan)
                {
                    String path = Environment.getExternalStorageDirectory() + "/" + "img.png";
                    int with = WindowUtils.dip2px(activity, 80);
                    Picasso.get()
                            .load(new File(path))
                            .resize(with, with)
                            .centerCrop()
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .noFade()
                            .into(mImageView);
                    return path;
                } else
                {
                    AvatarUtils.openCameraOnResult(activity, isAvatar);
                }
                break;
            case REQUEST_CODE_CHANGEIMG:
                if (isYuan)
                {
                    String path = getImageAbsolutePath(activity, data.getData());
                    int with = WindowUtils.dip2px(activity, 80);
                    MyPicassoUtils.downSizeImageForPath(with, with, path, mImageView);
                    return path;
                }

                try
                {
                    AvatarUtils.startPhotoZoom(data.getData(), activity, isAvatar);
                } catch (NullPointerException e)
                {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUEST_CODE_PHOTOZOOM:
                if (null != data)
                {
                    return AvatarUtils.setPicToViewOnResult(data, activity);
                }
                break;
            default:
                break;
        }
        return null;
    }

    public static void onRequestPermissionsResult(int requestCode, int[] grantResults, Activity activity)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    AvatarUtils.openCamera(activity);
                } else
                {
                    Toast.makeText(activity, "权限获取失败！！请手动获取", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                break;
        }
    }
}
