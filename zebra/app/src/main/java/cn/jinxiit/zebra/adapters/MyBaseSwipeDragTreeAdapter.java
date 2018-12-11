package cn.jinxiit.zebra.adapters;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragTreeAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TreeData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TypeData;

import java.util.ArrayList;
import java.util.List;

import cn.jinxiit.zebra.R;
import cn.jinxiit.zebra.activities.product.types.Type1Activity;
import cn.jinxiit.zebra.activities.product.types.Type2Activity;
import cn.jinxiit.zebra.beans.CategoryBean;
import cn.jinxiit.zebra.interfaces.MyConstant;
import cn.jinxiit.zebra.utils.MyActivityUtils;

public class MyBaseSwipeDragTreeAdapter extends BaseSwipeDragTreeAdapter
{
    private boolean isSort = false;

    public MyBaseSwipeDragTreeAdapter()
    {
        super();
    }

    public void setSort(boolean sort)
    {
        isSort = sort;
        notifyDataSetChanged();
    }

    @Override
    public void initIds()
    {
        int[] clickFlags = new int[3];
        clickFlags[0] = ClickFlag.CANNOT;
        clickFlags[1] = ClickFlag.BOTH;
        clickFlags[2] = ClickFlag.BOTH;
        putTypeLayoutViewIds(TestTreeState.TYPE_ONE, R.layout.item1_swipe_drag_tree_recycler_view, new int[]{R.id.item1_sdtrv_tv, R.id.btn_edit, R.id.btn_tuo}, clickFlags);
        putTypeLayoutViewIds(TestTreeState.TYPE_TEO, R.layout.item2_swipe_drag_tree_recycler_view, new int[]{R.id.item2_sdtrv_tv, R.id.btn_edit, R.id.btn_tuo}, clickFlags);

        int[] startDragFlags = new int[1];
        startDragFlags[0] = StartDragFlag.TOUCH;
        putTypeStartDragViewIds(TestTreeState.TYPE_ONE, new int[]{R.id.btn_tuo}, startDragFlags);
        putTypeStartDragViewIds(TestTreeState.TYPE_TEO, new int[]{R.id.btn_tuo}, startDragFlags);
    }

    @Override
    protected void bindData(BaseViewHolder holder, TypeData data)
    {
        SwipeDragTreeViewHolder viewHolder = (SwipeDragTreeViewHolder) holder;
        ImageView ivSort = (ImageView) viewHolder.getView(R.id.btn_edit);
        Button btn = (Button) viewHolder.getView(R.id.btn_tuo);
        if (isSort)
        {
            ivSort.setVisibility(View.VISIBLE);
            btn.setText("置顶");
        } else
        {
            ivSort.setVisibility(View.GONE);
            btn.setText("编辑");
        }
        switch (holder.getItemViewType())
        {
            case TestTreeState.TYPE_ONE:
                TextView textView0 = (TextView) viewHolder.getView(R.id.item1_sdtrv_tv);
                textView0.setText(((CategoryBean) data.getData()).getName());
                viewHolder.getView(R.id.btn_tuo)
                        .setOnClickListener(v -> {
                            if (isSort)
                            {
                                ArrayList datas = getDatas();
                                if (datas != null)
                                {
                                    datas.remove(data);
                                    datas.add(0, data);
                                    init(datas);
                                }
                            } else
                            {
                                int[] positions = getPositions(holder.getLayoutPosition());
                                Bundle bundle = new Bundle();
                                bundle.putIntArray(MyConstant.STR_POSITION, positions);
                                MyActivityUtils.skipActivity(v.getContext(), Type1Activity.class, bundle);
                            }
                        });
                break;
            case TestTreeState.TYPE_TEO:
                TextView textView1 = (TextView) viewHolder.getView(R.id.item2_sdtrv_tv);
                CategoryBean currentCategoryBean = (CategoryBean) data.getData();
                textView1.setText(currentCategoryBean.getName());
                viewHolder.getView(R.id.btn_tuo)
                        .setOnClickListener(v -> {
                            if (isSort)
                            {
                                ArrayList<TreeData> datas = getDatas();
                                for (int i = 0; i < datas.size(); i++)
                                {
                                    CategoryBean fatherCategoryBean = (CategoryBean) datas.get(i)
                                            .getData();
                                    List<CategoryBean> children = fatherCategoryBean.getChildren();
                                    for (int j = 0; j < children.size(); j++)
                                    {
                                        CategoryBean categoryBean = children.get(j);
                                        if (currentCategoryBean.getId()
                                                .equals(categoryBean.getId()))
                                        {
                                            int layoutPosition = holder.getLayoutPosition();
                                            for (int q = 0; q < j; q++)
                                            {
                                                notifyItemDrag(layoutPosition, --layoutPosition);
                                            }
                                            children.add(0, children.remove(j));
                                            setDatas(datas);
                                            return;
                                        }
                                    }
                                }
                            } else
                            {
                                int[] positions = getPositions(holder.getLayoutPosition());
                                Bundle bundle = new Bundle();
                                bundle.putIntArray(MyConstant.STR_POSITION, positions);
                                MyActivityUtils.skipActivity(v.getContext(), Type2Activity.class, bundle);
                            }
                        });
                break;
            default:
                break;
        }
    }

    @Override
    public ArrayList getDatas()
    {
        return super.getDatas();
    }

    @Override
    public void setDatas(ArrayList datas)
    {
        super.setDatas(datas);
    }
}
