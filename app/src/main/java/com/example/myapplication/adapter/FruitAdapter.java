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
 * 水果列表适配器（店铺详情界面中使用）
 */
public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {

    /** 加入购物车按钮点击回调 */
    public interface OnAddCartClickListener {
        void onAdd(Fruit fruit);
    }

    /** 水果条目点击回调（跳转水果详情对话框） */
    public interface OnItemClickListener {
        void onItemClick(Fruit fruit);
    }

    private final List<Fruit> fruitList;
    private final OnAddCartClickListener addListener;
    private OnItemClickListener itemClickListener;

    public FruitAdapter(List<Fruit> fruitList, OnAddCartClickListener addListener) {
        this.fruitList = fruitList;
        this.addListener = addListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fruit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Fruit fruit = fruitList.get(position);
        ImageUtils.load(holder.ivFruitPic, fruit.getFoodPic());
        holder.tvFruitName.setText(fruit.getFoodName());
        holder.tvFruitIngredients.setText(fruit.getIngredients());
        holder.tvFruitSaleNum.setText("月销 " + fruit.getSaleNum());
        holder.tvFruitPrice.setText("¥" + PriceUtils.fmt(fruit.getPrice()));
        // 加入购物车按钮
        holder.tvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addListener != null) {
                    addListener.onAdd(fruit);
                }
            }
        });
        // 点击整行条目，查看水果详情
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(fruit);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fruitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFruitPic;
        TextView tvFruitName, tvFruitIngredients, tvFruitSaleNum, tvFruitPrice, tvAddCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFruitPic = itemView.findViewById(R.id.iv_fruit_pic);
            tvFruitName = itemView.findViewById(R.id.tv_fruit_name);
            tvFruitIngredients = itemView.findViewById(R.id.tv_fruit_ingredients);
            tvFruitSaleNum = itemView.findViewById(R.id.tv_fruit_sale_num);
            tvFruitPrice = itemView.findViewById(R.id.tv_fruit_price);
            tvAddCart = itemView.findViewById(R.id.tv_add_cart);
        }
    }
}
