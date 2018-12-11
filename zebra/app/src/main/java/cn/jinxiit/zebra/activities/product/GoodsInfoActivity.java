package cn.jinxiit.zebra.activities.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.beans.BrandBean;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyPicassoUtils;
import cn.jinxiit.zebra.utils.ViewSetDataUtils;
import cn.jinxiit.zebra.utils.WindowUtils;
import cn.jinxiit.zebra.utils.ZebraUtils;

public class GoodsInfoActivity extends BaseActivity
{
    @BindView(R.id.iv_img)
    ImageView mIvImg;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_goods_code)
    TextView mTvGoodsCode;
    @BindView(R.id.tv_category_product)
    TextView mTvCategoryProduct;
    @BindView(R.id.tv_brand)
    TextView mTvBrand;
    @BindView(R.id.tv_upc_code)
    TextView mTvUpcCode;
    @BindView(R.id.tv_weight)
    TextView mTvWeight;

    private GoodsBean mGoodsBean;
    private MyApp myApp;

    private static final int REQUEST_CODE_UPDATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_UPDATE && resultCode == 1)
        {
            if (data != null)
            {
                GoodsBean goodsBean = data.getParcelableExtra(MyConstant.STR_BEAN);
                if (goodsBean != null)
                {
                    mGoodsBean = goodsBean;
                    dataToView();
                    setResult(1, data);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.btn_update})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_update:
                if (mGoodsBean != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MyConstant.STR_BEAN, mGoodsBean);
                    bundle.putBoolean(MyConstant.STR_IS, true);
                    MyActivityUtils.skipActivityForResult(mContext, UpdateOrAddGoodsActivity.class, bundle, REQUEST_CODE_UPDATE);
                }
                break;
        }
    }

    private void initView()
    {
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData()
    {
        myApp = (MyApp) getApplication();
        mGoodsBean = getIntent().getParcelableExtra(MyConstant.STR_BEAN);
        dataToView();
    }

    @SuppressLint("SetTextI18n")
    private void dataToView()
    {
        if (mGoodsBean != null)
        {
            GoodsBean.ProductBean product = mGoodsBean.getProduct();
            if (product != null)
            {
                List<String> images = product.getImages();
                if (images != null && images.size() > 0)
                {
                    String imgUrl = MyConstant.URL_GET_FILE + images.get(0) + ".jpg";
                    int width = WindowUtils.getScreenSize(this)[0];
                    int height = WindowUtils.dip2px(this, 200);
                    MyPicassoUtils.downSizeImageForUrl(width, height, imgUrl, mIvImg);
                }

                String name = product.getName();
                if (!TextUtils.isEmpty(name))
                {
                    new MyToolBar(this, name, null);
                }
                ViewSetDataUtils.textViewSetText(mTvGoodsCode, product.getProduct_code());

                GoodsBean.ProductBean.ExtraBean extra = product.getExtra();
                if (extra != null)
                {
                    String category = extra.getCategory();
                    httpGetCategoryNameOfProductById(category);

                    String brand = extra.getBrand();
                    httpGetBrandNameById(brand);

                    ViewSetDataUtils.textViewSetText(mTvUpcCode, extra.getUpcCode());

                    ViewSetDataUtils.textViewSetText(mTvName, ZebraUtils.getInstance().showGoodName(mGoodsBean));
                }

                String weight = product.getWeight();
                String weight_unit = product.getWeight_unit();
                if (!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(weight_unit))
                {
                    mTvWeight.setText(weight + weight_unit);
                }
            }
        }
    }

    private void httpGetBrandNameById(String brandId)
    {
        if (!TextUtils.isEmpty(brandId) && myApp.mToken != null)
        {
            ApiUtils.getInstance()
                    .okgoGetBrandName(this, myApp.mToken, brandId, new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.has(MyConstant.STR_BRAND))
                                {
                                    BrandBean brandBean = new Gson().fromJson(jsonObject.getString(MyConstant.STR_BRAND), BrandBean.class);
                                    if (brandBean != null)
                                    {
                                        ViewSetDataUtils.textViewSetText(mTvBrand, brandBean.getName());
                                    }
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response<String> response)
                        {

                        }
                    });
        }
    }

    private void httpGetCategoryNameOfProductById(String categoryId)
    {
        if (!TextUtils.isEmpty(categoryId) && myApp.mToken != null)
        {
            ApiUtils.getInstance()
                    .okgoGet3ProductCategoryName(this, myApp.mToken, categoryId, new ApiResultListener()
                    {
                        @Override
                        public void onSuccess(Response<String> response)
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.has(MyConstant.STR_CATEGORY))
                                {
                                    ViewSetDataUtils.textViewSetText(mTvCategoryProduct, jsonObject.getString(MyConstant.STR_CATEGORY));
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Response<String> response)
                        {

                        }
                    });

        }
    }
}
