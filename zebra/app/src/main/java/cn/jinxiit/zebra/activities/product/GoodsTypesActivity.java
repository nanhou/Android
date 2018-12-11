package cn.jinxiit.zebra.activities.product;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TreeData;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jinxiit.zebra.MyApp;
import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.BaseActivity;
import cn.jinxiit.zebra.activities.product.types.Type1Activity;
import cn.jinxiit.zebra.activities.product.types.Type2Activity;
import cn.jinxiit.zebra.adapters.MyBaseSwipeDragTreeAdapter;
import cn.jinxiit.zebra.adapters.TestTreeState;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.beans.UserBean;
import cn.jinxiit.zebra.interfaces.ApiResultListener;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.toolbars.MyToolBar;
import cn.jinxiit.zebra.utils.ApiUtils;
import cn.jinxiit.zebra.utils.MyActivityUtils;
import cn.jinxiit.zebra.utils.MyToastUtils;
import cn.jinxiit.zebra.utils.NumberUtils;

public class GoodsTypesActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;

    private MyBaseSwipeDragTreeAdapter myBaseSwipeDragTreeAdapter;
    private ArrayList<TreeData> mDatas = new ArrayList<>();
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_types);
        ButterKnife.bind(this);
        myApp = (MyApp) getApplication();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initResumeData();
    }

    @OnClick({R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                clickAdd();
                break;
        }
    }

    private void initResumeData() {
        initData();
        myBaseSwipeDragTreeAdapter.init(mDatas);
    }

    private void initView() {
        initMyToolBar();
        initRecyclerView();
    }

    private void clickAdd() {
        Dialog addDialog = new Dialog(this, R.style.my_dialog);
        @SuppressLint("InflateParams") LinearLayout root = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.layout_12_category, null);
        root.findViewById(R.id.btn_1_category).setOnClickListener(v -> {
            MyActivityUtils.skipActivity(v.getContext(), Type1Activity.class);
            addDialog.cancel();
        });
        root.findViewById(R.id.btn_2_category).setOnClickListener(v -> {
            MyActivityUtils.skipActivity(v.getContext(), Type2Activity.class);
            addDialog.cancel();
        });
        root.findViewById(R.id.btn_cancel).setOnClickListener(view -> addDialog.cancel());
        addDialog.setContentView(root);
        Window dialogWindow = addDialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.dialogAnimations0); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = -20; // 新位置Y坐标//隐藏头部导航栏
            lp.width = getResources().getDisplayMetrics().widthPixels; // 宽度
            root.measure(0, 0);
            lp.height = root.getMeasuredHeight();
            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
            addDialog.setCanceledOnTouchOutside(true);
        }
        addDialog.show();
    }

    private void initRecyclerView() {
        DividerItemDecoration divider = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(
                Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.custom_divider)));
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.addItemDecoration(divider);
        myBaseSwipeDragTreeAdapter = new MyBaseSwipeDragTreeAdapter();
        mRecyclerview.setAdapter(myBaseSwipeDragTreeAdapter);

        myBaseSwipeDragTreeAdapter.setOnItemDragListener((int fromPosition, int toPosition) -> {
            myBaseSwipeDragTreeAdapter.notifyItemDrag(fromPosition, toPosition);
            ArrayList<TreeData> datas = myBaseSwipeDragTreeAdapter.getDatas();
            int[] positions0 = myBaseSwipeDragTreeAdapter.getPositions(fromPosition);
            int[] positions1 = myBaseSwipeDragTreeAdapter.getPositions(toPosition);
            if (positions0.length == 2) {
                TreeData treeData = datas.get(positions0[0]);
                CategoryBean fatherCategoryBean = (CategoryBean) treeData.getData();
                List<CategoryBean> children = fatherCategoryBean.getChildren();
                NumberUtils.swap2(children, positions0[1], positions1[1]);

                myBaseSwipeDragTreeAdapter.setDatas(datas);
            }
            return true;
        });
        myBaseSwipeDragTreeAdapter.setSwipeBackgroundColorEnabled(false);
        myBaseSwipeDragTreeAdapter.setLongPressDragEnabled(false);
        myBaseSwipeDragTreeAdapter.setMemoryExpandState(true);
        myBaseSwipeDragTreeAdapter.setOnItemViewClickListener((view, position) -> {
        });
    }

    private void initData() {
        if (myApp.mMarketCategoryList != null && myApp.mMarketCategoryList.size() > 0) {
            mDatas = new ArrayList<>();
            for (int i = 0; i < myApp.mMarketCategoryList.size(); i++) {
                ArrayList<TreeData> dataTrees1 = null;
                CategoryBean categoryBean0 = myApp.mMarketCategoryList.get(i);
                List<CategoryBean> children = categoryBean0.getChildren();
                for (int j = 0; j < children.size(); j++) {
                    ArrayList<TreeData> dataTrees2 = null;
                    if (dataTrees1 == null) {
                        dataTrees1 = new ArrayList<>();
                    }
                    CategoryBean categoryBean1 = children.get(j);
                    dataTrees1.add(new TreeData(categoryBean1, TestTreeState.TYPE_TEO, dataTrees2));
                }
                mDatas.add(new TreeData(categoryBean0, TestTreeState.TYPE_ONE, dataTrees1));
            }
        }
    }

    private void initMyToolBar() {
        MyToolBar myToolBar = new MyToolBar(mContext, "分类管理", "排序");
        myToolBar.setOnTopMenuClickListener(v -> {
            MyToastUtils.success("排序");
            if (myBaseSwipeDragTreeAdapter != null) {
                myBaseSwipeDragTreeAdapter.setSort(true);
            }
            myToolBar.setTvMenu("完成");
            myToolBar.setOnTopMenuClickListener(view -> {
                clickConfirm();
            });
        });
    }

    private void clickConfirm() {
        if (myBaseSwipeDragTreeAdapter != null && myApp.mUser != null) {
            ArrayList<TreeData> datas = myBaseSwipeDragTreeAdapter.getDatas();
            List<CategoryBean> tempCategoryList = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                tempCategoryList.add((CategoryBean) datas.get(i).getData());
            }

            String seqJson = new Gson()
                    .toJson(tempCategoryList, new TypeToken<List<CategoryBean>>() {
                    }.getType());

            UserBean.ExtraBean extra = myApp.mUser.getExtra();
            if (extra != null) {
                String market_id = extra.getMarket_id();
                if (!TextUtils.isEmpty(market_id)) {
                    ApiUtils.getInstance()
                            .okgoPostSortMarketCategory(this, myApp.mToken, market_id, seqJson,
                                    new ApiResultListener() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            if (response.body().contains(MyConstant.STR_OK)) {
                                                myApp.mMarketCategoryList = tempCategoryList;
                                                MyToastUtils.success("排序成功");
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onError(Response<String> response) {

                                        }
                                    });
                }
            }
        }
    }
}