package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.util.ImageUtils;
import com.example.myapplication.util.PriceUtils;

import java.util.List;

/**
 * 通用商品列表适配器：搜索结果与收藏列表共用
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onItemClick(Fruit fruit);

        /** 长按（收藏列表用于取消收藏），不需要时可返回 false */
        boolean onItemLongClick(Fruit fruit);
    }

    private final List<Fruit> fruitList;
    private final OnItemActionListener listener;
    private final String actionText;

    public SearchAdapter(List<Fruit> fruitList, String actionText,
                         OnItemActionListener listener) {
        this.fruitList = fruitList;
        this.actionText = actionText;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Fruit fruit = fruitList.get(position);
        ImageUtils.load(holder.ivPic, fruit.getFoodPic());
        holder.tvName.setText(fruit.getFoodName());
        holder.tvShop.setText("店铺：" + (fruit.getShopName() == null ? "" : fruit.getShopName()));
        holder.tvPrice.setText("¥" + PriceUtils.fmt(fruit.getPrice()));
        holder.tvAction.setText(actionText);
        View.OnClickListener click = v -> {
            if (listener != null) {
                listener.onItemClick(fruit);
            }
        };
        holder.itemView.setOnClickListener(click);
        holder.tvAction.setOnClickListener(click);
        holder.itemView.setOnLongClickListener(v -> listener != null
                && listener.onItemLongClick(fruit));
    }

    @Override
    public int getItemCount() {
        return fruitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic;
        TextView tvName, tvShop, tvPrice, tvAction;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPic = itemView.findViewById(R.id.iv_pic);
            tvName = itemView.findViewById(R.id.tv_name);
            tvShop = itemView.findViewById(R.id.tv_shop);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvAction = itemView.findViewById(R.id.tv_action);
        }
    }
}
