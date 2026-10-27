package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.util.PriceUtils;

import java.util.List;

/**
 * 购物车弹层中的已选商品适配器，支持“＋”“－”修改数量
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    /** 数量变化回调（＋ / － 按钮） */
    public interface OnCountChangeListener {
        void onChange(Fruit fruit, int delta);
    }

    private final List<Fruit> cartList;
    private final OnCountChangeListener listener;

    public CartAdapter(List<Fruit> cartList, OnCountChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Fruit fruit = cartList.get(position);
        holder.tvCartName.setText(fruit.getFoodName());
        holder.tvCartPrice.setText("¥" + PriceUtils.fmt(fruit.getPrice()));
        holder.tvCount.setText(String.valueOf(fruit.getCount()));
        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChange(fruit, 1);
                }
            }
        });
        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChange(fruit, -1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCartName, tvCartPrice, tvMinus, tvCount, tvPlus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCartName = itemView.findViewById(R.id.tv_cart_name);
            tvCartPrice = itemView.findViewById(R.id.tv_cart_price);
            tvMinus = itemView.findViewById(R.id.tv_minus);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvPlus = itemView.findViewById(R.id.tv_plus);
        }
    }
}
