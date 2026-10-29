package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.Shop;
import com.example.myapplication.util.ImageUtils;

import java.util.List;
import java.util.Locale;

/**
 * 店铺列表适配器（首页使用）
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    /** 店铺点击回调 */
    public interface OnShopClickListener {
        void onShopClick(Shop shop);
    }

    private final List<Shop> shopList;
    private final OnShopClickListener listener;

    public ShopAdapter(List<Shop> shopList, OnShopClickListener listener) {
        this.shopList = shopList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Shop shop = shopList.get(position);
        ImageUtils.load(holder.ivShopPic, shop.getShopPic());
        holder.tvShopName.setText(shop.getShopName());
        holder.tvShopScore.setText(String.format(Locale.CHINA, "%.1f 分", shop.getScore()));
        holder.tvShopSale.setText("月售 " + shop.getSaleNum());
        holder.tvShopDistance.setText(shop.getDistance());
        holder.tvShopTime.setText(shop.getTime());
        holder.tvShopWelfare.setText(shop.getWelfare());
        holder.tvShopDelivery.setText("起送 ¥" + shop.getOfferPrice()
                + " · 配送 ¥" + shop.getDistributionCost());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShopClick(shop);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivShopPic;
        TextView tvShopName, tvShopScore, tvShopSale, tvShopDistance,
                tvShopTime, tvShopWelfare, tvShopDelivery;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShopPic = itemView.findViewById(R.id.iv_shop_pic);
            tvShopName = itemView.findViewById(R.id.tv_shop_name);
            tvShopScore = itemView.findViewById(R.id.tv_shop_score);
            tvShopSale = itemView.findViewById(R.id.tv_shop_sale);
            tvShopDistance = itemView.findViewById(R.id.tv_shop_distance);
            tvShopTime = itemView.findViewById(R.id.tv_shop_time);
            tvShopWelfare = itemView.findViewById(R.id.tv_shop_welfare);
            tvShopDelivery = itemView.findViewById(R.id.tv_shop_delivery);
        }
    }
}
