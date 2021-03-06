package cn.jinxiit.zebra.activities.codescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.simonlee.xcodescanner.core.CameraScanner;
import cn.simonlee.xcodescanner.core.GraphicDecoder;
import cn.simonlee.xcodescanner.core.NewCameraScanner;
import cn.simonlee.xcodescanner.core.OldCameraScanner;
import cn.simonlee.xcodescanner.core.ZBarDecoder;
import cn.simonlee.xcodescanner.view.AdjustTextureView;

public class ScannerCodeActivity extends BaseActivity implements CameraScanner.CameraListener, TextureView.SurfaceTextureListener, GraphicDecoder.DecodeListener, View.OnClickListener
{
    private AdjustTextureView mTextureView;
    private View mScannerFrameView;

    private CameraScanner mCameraScanner;
    protected GraphicDecoder mGraphicDecoder;

    protected String TAG = "XCodeScanner";
    private Button mButton_Flash;
    private int[] mCodeType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_code);
        mTextureView = findViewById(R.id.textureview);
        mTextureView.setSurfaceTextureListener(this);


        mScannerFrameView = findViewById(R.id.scannerframe);

        mButton_Flash = findViewById(R.id.btn_flash);
        mButton_Flash.setOnClickListener(this);

        mCodeType = new int[]{ZBarDecoder.CODABAR, ZBarDecoder.CODE39, ZBarDecoder.CODE93, ZBarDecoder.CODE128, ZBarDecoder.DATABAR, ZBarDecoder.DATABAR_EXP, ZBarDecoder.EAN8, ZBarDecoder.EAN13, ZBarDecoder.I25, ZBarDecoder.ISBN10, ZBarDecoder.ISBN13, ZBarDecoder.PDF417, ZBarDecoder.QRCODE, ZBarDecoder.UPCA, ZBarDecoder.UPCE};
//        mCodeType = new int[]{ZBarDecoder.CODE128, ZBarDecoder.QRCODE};

        Intent intent = getIntent();
        /*
         * 注意，SDK21的设备是可以使用NewCameraScanner的，但是可能存在对新API支持不够的情况，比如红米Note3（双网通Android5.0.2）
         * 开发者可自行配置使用规则，比如针对某设备型号过滤，或者针对某SDK版本过滤
         * */
        if (intent.getBooleanExtra("newAPI", false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mCameraScanner = new NewCameraScanner(this);
        } else
        {
            mCameraScanner = new OldCameraScanner(this);
        }
    }

    @Override
    protected void onRestart()
    {
        if (mTextureView.isAvailable())
        {
            //部分机型转到后台不会走onSurfaceTextureDestroyed()，因此isAvailable()一直为true，转到前台后不会再调用onSurfaceTextureAvailable()
            //因此需要手动开启相机
            mCameraScanner.setPreviewTexture(mTextureView.getSurfaceTexture());
            mCameraScanner.setPreviewSize(mTextureView.getWidth(), mTextureView.getHeight());
            mCameraScanner.openCamera(this.getApplicationContext());
        }
        super.onRestart();
    }

    @Override
    protected void onPause()
    {
        mCameraScanner.closeCamera();
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        setResult(0);
        mCameraScanner.setGraphicDecoder(null);
        if (mGraphicDecoder != null)
        {
            mGraphicDecoder.setDecodeListener(null);
            mGraphicDecoder.detach();
        }
        mCameraScanner.detach();
        super.onDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        Log.e(TAG, getClass().getName() + ".onSurfaceTextureAvailable() width = " + width + " , height = " + height);
        mCameraScanner.setPreviewTexture(surface);
        mCameraScanner.setPreviewSize(width, height);
        mCameraScanner.openCamera(this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
    {
        Log.e(TAG, getClass().getName() + ".onSurfaceTextureSizeChanged() width = " + width + " , height = " + height);
        // TODO 当View大小发生变化时，要进行调整。
        //        mTextureView.setImageFrameMatrix();
        //        mCameraScanner.setPreviewSize(width, height);
        //        mCameraScanner.setFrameRect(mScannerFrameView.getLeft(), mScannerFrameView.getTop(), mScannerFrameView.getRight(), mScannerFrameView.getBottom());
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        Log.e(TAG, getClass().getName() + ".onSurfaceTextureDestroyed()");
        return true;
    }

    @Override// 每有一帧画面，都会回调一次此方法
    public void onSurfaceTextureUpdated(SurfaceTexture surface)
    {
    }

    @Override
    public void openCameraSuccess(int frameWidth, int frameHeight, int frameDegree)
    {
        Log.e(TAG, getClass().getName() + ".openCameraSuccess() frameWidth = " + frameWidth + " , frameHeight = " + frameHeight + " , frameDegree = " + frameDegree);
        mTextureView.setImageFrameMatrix(frameWidth, frameHeight, frameDegree);
        if (mGraphicDecoder == null)
        {
            mGraphicDecoder = new DebugZBarDecoder(this, mCodeType);//使用带参构造方法可指定条码识别的格式
        }
        //该区域坐标为相对于父容器的左上角顶点。
        //TODO 应考虑TextureView与ScannerFrameView的Margin与padding的情况
        mCameraScanner.setFrameRect(mScannerFrameView.getLeft(), mScannerFrameView.getTop(), mScannerFrameView.getRight(), mScannerFrameView.getBottom());
        mCameraScanner.setGraphicDecoder(mGraphicDecoder);
    }

    @Override
    public void openCameraError()
    {
        Toast.makeText(this, "出错了", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void noCameraPermission()
    {
        Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void cameraDisconnected()
    {
        Toast.makeText(this, "断开了链接", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void cameraBrightnessChanged(int brightness)
    {
        if (brightness <= 40)
        {
            mButton_Flash.setVisibility(View.VISIBLE);
        } else if (!mCameraScanner.isFlashOpened())
        {
            mButton_Flash.setVisibility(View.GONE);
        }
        Log.e(TAG, getClass().getName() + ".cameraBrightnessChanged() brightness = " + brightness);
    }

    int mCount = 0;
    String mResult = null;

    @SuppressLint("SetTextI18n")
    @Override
    public void decodeComplete(String result, int type, int quality, int requestCode)
    {
        if (result == null)
        {
            return;
        }
        if (result.equals(mResult))
        {
            if (++mCount > 3)
            {//连续四次相同则显示结果（主要过滤脏数据，也可以根据条码类型自定义规则）
                if (quality < 10)
                {
//                    mTvShow.setText("[类型" + type + "/精度00" + quality + "]" + result);
                } else if (quality < 100)
                {
//                    mTvShow.setText("[类型" + type + "/精度0" + quality + "]" + result);
                } else
                {
//                    mTvShow.setText("[类型" + type + "/精度" + quality + "]" + result);
                }

                if (!TextUtils.isEmpty(result))
                {
                    Intent data = new Intent();
                    data.putExtra("result", result);
                    setResult(1, data);
                    finish();
                }
            }
        } else
        {
            mCount = 1;
            mResult = result;
        }
        Log.d(TAG, getClass().getName() + ".decodeComplete() -> " + mResult);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_flash:
            {
                if (v.isSelected())
                {
                    ((Button) v).setText("开启闪光灯");
                    v.setSelected(false);
                    mCameraScanner.closeFlash();
                } else
                {
                    ((Button) v).setText("关闭闪光灯");
                    v.setSelected(true);
                    mCameraScanner.openFlash();
                }
                break;
            }
        }
    }

}
