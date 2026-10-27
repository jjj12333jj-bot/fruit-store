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
import java.util.Locale;

/**
 * 订单界面中的商品列表适配器
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<Fruit> fruitList;

    public OrderAdapter(List<Fruit> fruitList) {
        this.fruitList = fruitList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fruit fruit = fruitList.get(position);
        ImageUtils.load(holder.ivOrderPic, fruit.getFoodPic());
        holder.tvOrderName.setText(String.format(Locale.CHINA, "%s  × %d",
                fruit.getFoodName(), fruit.getCount()));
        holder.tvOrderPrice.setText("¥" + PriceUtils.fmt(fruit.getPrice() * fruit.getCount()));
    }

    @Override
    public int getItemCount() {
        return fruitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOrderPic;
        TextView tvOrderName, tvOrderPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrderPic = itemView.findViewById(R.id.iv_order_pic);
            tvOrderName = itemView.findViewById(R.id.tv_order_name);
            tvOrderPrice = itemView.findViewById(R.id.tv_order_price);
        }
    }
}
