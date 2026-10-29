package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Order;
import com.example.myapplication.util.PriceUtils;

import java.util.List;

/**
 * 订单历史列表适配器
 */
public class OrderRecordAdapter extends RecyclerView.Adapter<OrderRecordAdapter.ViewHolder> {

    private final List<Order> orderList;

    public OrderRecordAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvShopName.setText(order.getShopName());
        holder.tvStatus.setText(order.getStatus());
        holder.tvTime.setText(order.getCreateTime() + "   共 " + order.getItemCount() + " 件");
        holder.tvTotal.setText("¥" + PriceUtils.fmt(order.getTotal()));
        StringBuilder sb = new StringBuilder();
        for (Fruit fruit : order.getItems()) {
            if (sb.length() > 0) {
                sb.append("、");
            }
            sb.append(fruit.getFoodName()).append(" × ").append(fruit.getCount());
        }
        holder.tvItems.setText(sb.length() == 0 ? "（无商品明细）" : sb.toString());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvShopName, tvStatus, tvItems, tvTime, tvTotal;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tv_shop_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvItems = itemView.findViewById(R.id.tv_items);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTotal = itemView.findViewById(R.id.tv_total);
        }
    }
}
