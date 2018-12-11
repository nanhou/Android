package cn.jinxiit.zebra.fragments.products;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.beans.GoodsBean;
import cn.jinxiit.zebra.components.UpdatePriceAndInventoryComponent;
import cn.jinxiit.zebra.interfaces.MyConstant;

public class UpdateInventoryPriceFragment extends Fragment {
    @BindView(R.id.view0)
    View mView0;
    @BindView(R.id.view1)
    View mView1;
    @BindView(R.id.view2)
    View mView2;
    @BindView(R.id.view3)
    View mView3;
    Unbinder unbinder;

    private String mType;
    private GoodsBean mGoodsBean;
    private UpdatePriceAndInventoryComponent mUpdatePriceAndInventoryComponentAll;
    private UpdatePriceAndInventoryComponent mUpdatePriceAndInventoryComponentJD;
    private UpdatePriceAndInventoryComponent mUpdatePriceAndInventoryComponentMT;
    private UpdatePriceAndInventoryComponent mUpdatePriceAndInventoryComponentELEME;
    private UpdatePriceAndInventoryComponent mUpdatePriceAndInventoryComponentEBAI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_inventory_price, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {
        initData();
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getString(MyConstant.STR_TYPE);
            mGoodsBean = arguments.getParcelable(MyConstant.STR_BEAN);

            if (!TextUtils.isEmpty(mType) && mGoodsBean != null) {
                if (mType.equals(MyConstant.STR_All)) {
                    mView1.setVisibility(View.GONE);
                    mView2.setVisibility(View.GONE);
                    mView3.setVisibility(View.GONE);
                    mUpdatePriceAndInventoryComponentAll = new UpdatePriceAndInventoryComponent(
                            mView0, mGoodsBean, MyConstant.STR_All);
                } else {
                    mUpdatePriceAndInventoryComponentJD = new UpdatePriceAndInventoryComponent(
                            mView0, mGoodsBean, MyConstant.STR_EN_JDDJ);
                    mUpdatePriceAndInventoryComponentMT = new UpdatePriceAndInventoryComponent(
                            mView1, mGoodsBean, MyConstant.STR_EN_MT);
                    mUpdatePriceAndInventoryComponentELEME = new UpdatePriceAndInventoryComponent(
                            mView2, mGoodsBean, MyConstant.STR_EN_ELEME);
                    mUpdatePriceAndInventoryComponentEBAI = new UpdatePriceAndInventoryComponent(
                            mView3, mGoodsBean, MyConstant.STR_EN_EBAI);
                }
            }
        }
    }

    public List<String> updatePriceAndStock() {
        if (mType.equals(MyConstant.STR_All)) {
            if (mUpdatePriceAndInventoryComponentAll != null) {
                return mUpdatePriceAndInventoryComponentAll.updatePriceAndStock();
            }
        } else {
            if (mUpdatePriceAndInventoryComponentJD != null && mUpdatePriceAndInventoryComponentMT != null
                    && mUpdatePriceAndInventoryComponentELEME != null && mUpdatePriceAndInventoryComponentEBAI != null) {
                List<String> list = new ArrayList<>();
                list.addAll(mUpdatePriceAndInventoryComponentJD.updatePriceAndStock());
                list.addAll(mUpdatePriceAndInventoryComponentMT.updatePriceAndStock());
                list.addAll(mUpdatePriceAndInventoryComponentELEME.updatePriceAndStock());
                list.addAll(mUpdatePriceAndInventoryComponentEBAI.updatePriceAndStock());
                return list;
            }
        }
        return null;
    }

    public void refreshShangjiaStatus(GoodsBean goodsBean) {
        if (goodsBean != null) {
            this.mGoodsBean = goodsBean;
            if (!TextUtils.isEmpty(mType) && mGoodsBean != null) {
                if (mUpdatePriceAndInventoryComponentAll != null) {
                    mUpdatePriceAndInventoryComponentAll.setmGoodsBean(mGoodsBean);
                    mUpdatePriceAndInventoryComponentAll.initShangJiaStatus();
                }

                if (mUpdatePriceAndInventoryComponentJD != null) {
                    mUpdatePriceAndInventoryComponentJD.setmGoodsBean(mGoodsBean);
                    mUpdatePriceAndInventoryComponentJD.initShangJiaStatus();
                }

                if (mUpdatePriceAndInventoryComponentMT != null) {
                    mUpdatePriceAndInventoryComponentMT.setmGoodsBean(mGoodsBean);
                    mUpdatePriceAndInventoryComponentMT.initShangJiaStatus();
                }

                if (mUpdatePriceAndInventoryComponentELEME != null) {
                    mUpdatePriceAndInventoryComponentELEME.setmGoodsBean(mGoodsBean);
                    mUpdatePriceAndInventoryComponentELEME.initShangJiaStatus();
                }

                if (mUpdatePriceAndInventoryComponentEBAI != null) {
                    mUpdatePriceAndInventoryComponentEBAI.setmGoodsBean(mGoodsBean);
                    mUpdatePriceAndInventoryComponentEBAI.initShangJiaStatus();
                }
            }
        }
    }
}