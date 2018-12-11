package cn.jinxiit.zebra.fragments.mes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jinxiit.zebra.R;

public class AllStoreStateFragment extends Fragment
{

    Unbinder unbinder;
    @BindView(R.id.tv_pay_status)
    TextView mTvStatus;
    @BindView(R.id.btn_business)
    Button mBtnBusiness;

    private boolean isOpen = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_all_store_state, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_business})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_business:
                clickBusiness();
                break;
        }
    }

    private void clickBusiness()
    {
        isOpen = !isOpen;
        FragmentActivity activity = Objects.requireNonNull(getActivity());
        if (isOpen)
        {
            mTvStatus.setTextColor(ContextCompat.getColor(activity, R.color.green));
            mTvStatus.setText("营业中");
            mTvStatus.setCompoundDrawablesWithIntrinsicBounds(R.color.colorTm, R.drawable.business_on, R.color.colorTm, R.color.colorTm);
            mBtnBusiness.setText("停止营业");
            mBtnBusiness.setTextColor(ContextCompat.getColor(activity, R.color.red));
        }
        else
        {
            mTvStatus.setTextColor(ContextCompat.getColor(activity, R.color.red));
            mTvStatus.setText("已停止营业");
            mTvStatus.setCompoundDrawablesWithIntrinsicBounds(R.color.colorTm, R.drawable.business_off, R.color.colorTm, R.color.colorTm);
            mBtnBusiness.setText("恢复营业");
            mBtnBusiness.setTextColor(ContextCompat.getColor(activity, R.color.green));
        }
    }
}
