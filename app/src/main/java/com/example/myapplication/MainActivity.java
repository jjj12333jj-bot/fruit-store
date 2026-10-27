package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.adapter.FruitAdapter;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Shop;
import com.example.myapplication.util.Const;
import com.example.myapplication.util.DataProvider;
import com.example.myapplication.util.ImageUtils;
import com.example.myapplication.util.PriceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面：进入程序后直接展示“鲜果乐园”店铺的水果选购页面。
 * 页面从上至下分为四部分：标题栏与广告图片、店铺信息、水果清单（可滚动）、底部购物车栏。
 * 水果数据通过异步线程从服务器获取 JSON 解析而来（服务器不可用时自动使用内置数据）。
 */
public class MainActivity extends AppCompatActivity {

    private Shop shop;
    private FruitAdapter fruitAdapter;
    private CartAdapter cartAdapter;
    private PopupWindow cartPopup;

    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TextView tvCartCount, tvTotalPrice, tvCartHint, tvCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        // 异步加载店铺与水果数据
        loadData();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        tvCartCount = findViewById(R.id.tv_cart_count);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvCartHint = findViewById(R.id.tv_cart_hint);
        tvCheckout = findViewById(R.id.tv_checkout);
        // 点击购物车图标，弹出已选商品列表
        findViewById(R.id.fl_cart).setOnClickListener(v -> showCartPopup());
        // 点击去结算按钮
        tvCheckout.setOnClickListener(v -> onCheckoutClick());
    }

    /**
     * 异步加载店铺数据，取第一家店铺作为本店
     */
    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        DataProvider.getShopList(this, new DataProvider.OnShopListener() {
            @Override
            public void onSuccess(List<Shop> list) {
                progressBar.setVisibility(View.GONE);
                if (list == null || list.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("暂无水果数据");
                    return;
                }
                shop = list.get(0);
                showShopInfo();
                initFruitList();
                refreshCartBar();
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText(message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 展示店铺信息
     */
    private void showShopInfo() {
        ImageUtils.load(findViewById(R.id.iv_shop_pic), shop.getShopPic());
        TextView tvName = findViewById(R.id.tv_shop_name);
        TextView tvWelfare = findViewById(R.id.tv_shop_welfare);
        TextView tvInfo = findViewById(R.id.tv_shop_info);
        tvName.setText(shop.getShopName());
        tvWelfare.setText(shop.getWelfare());
        tvInfo.setText("月销 " + shop.getSaleNum()
                + "  " + shop.getTime()
                + "  起送 ¥" + shop.getOfferPrice()
                + "  配送费 ¥" + shop.getDistributionCost());
    }

    /**
     * 展示水果列表
     */
    private void initFruitList() {
        RecyclerView rvFruit = findViewById(R.id.rv_fruit_list);
        rvFruit.setLayoutManager(new LinearLayoutManager(this));
        fruitAdapter = new FruitAdapter(shop.getFruitList(), new FruitAdapter.OnAddCartClickListener() {
            @Override
            public void onAdd(Fruit fruit) {
                // 将水果加入购物车
                fruit.setCount(fruit.getCount() + 1);
                refreshCartBar();
                refreshCartPopup();
                Toast.makeText(MainActivity.this,
                        "已将「" + fruit.getFoodName() + "」加入购物车", Toast.LENGTH_SHORT).show();
            }
        });
        // 点击水果条目，弹出水果详情对话框
        fruitAdapter.setOnItemClickListener(new FruitAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Fruit fruit) {
                showFruitDetailDialog(fruit);
            }
        });
        rvFruit.setAdapter(fruitAdapter);
    }

    /**
     * 水果详情对话框（对话框样式）
     */
    private void showFruitDetailDialog(Fruit fruit) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_fruit_detail, null);
        ImageView ivPic = view.findViewById(R.id.iv_detail_pic);
        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvIngredients = view.findViewById(R.id.tv_detail_ingredients);
        TextView tvSaleNum = view.findViewById(R.id.tv_detail_sale_num);
        TextView tvPrice = view.findViewById(R.id.tv_detail_price);
        ImageUtils.load(ivPic, fruit.getFoodPic());
        tvName.setText(fruit.getFoodName());
        tvIngredients.setText(fruit.getIngredients());
        tvSaleNum.setText("月销量：" + fruit.getSaleNum());
        tvPrice.setText("¥" + PriceUtils.fmt(fruit.getPrice()));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    /**
     * 刷新底部购物车栏（总价、角标、去结算按钮状态）
     */
    private void refreshCartBar() {
        if (shop == null) {
            return;
        }
        int totalCount = 0;
        double totalPrice = 0;
        for (Fruit f : shop.getFruitList()) {
            totalCount += f.getCount();
            totalPrice += f.getCount() * f.getPrice();
        }
        // 角标
        if (totalCount > 0) {
            tvCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(totalCount));
        } else {
            tvCartCount.setVisibility(View.GONE);
        }
        // 总价与提示
        tvTotalPrice.setText("¥" + PriceUtils.fmt(totalPrice));
        tvCartHint.setText(totalCount == 0 ? "未选购商品" : "已选 " + totalCount + " 件商品");
        // 起送判断：总价达到起送价时显示“去结算”，否则显示还差多少钱
        if (totalCount > 0 && totalPrice >= shop.getOfferPrice()) {
            tvCheckout.setText("去结算");
            tvCheckout.setBackgroundResource(R.drawable.shape_btn_yellow);
            tvCheckout.setTag(Boolean.TRUE);
        } else {
            double diff = shop.getOfferPrice() - totalPrice;
            tvCheckout.setText("还差¥" + PriceUtils.fmt(Math.max(diff, 0)) + "起送");
            tvCheckout.setBackgroundResource(R.drawable.shape_btn_gray);
            tvCheckout.setTag(Boolean.FALSE);
        }
    }

    /**
     * 弹出购物车弹层（已选商品列表）
     */
    private void showCartPopup() {
        if (shop == null || getSelectedFruits().isEmpty()) {
            Toast.makeText(this, "购物车还是空的，快去挑选水果吧~", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cartPopup != null && cartPopup.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.popup_cart, null);
        RecyclerView rvCart = view.findViewById(R.id.rv_cart_list);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        TextView tvClear = view.findViewById(R.id.tv_clear_cart);
        // 清空购物车：弹出确认对话框
        tvClear.setOnClickListener(v -> confirmClearCart());
        cartAdapter = new CartAdapter(getSelectedFruits(), new CartAdapter.OnCountChangeListener() {
            @Override
            public void onChange(Fruit fruit, int delta) {
                int newCount = fruit.getCount() + delta;
                if (newCount < 0) {
                    newCount = 0;
                }
                fruit.setCount(newCount);
                refreshCartBar();
                if (getSelectedFruits().isEmpty()) {
                    hideCartPopup();
                } else if (cartAdapter != null) {
                    cartAdapter.notifyDataSetChanged();
                }
            }
        });
        rvCart.setAdapter(cartAdapter);
        cartPopup = new PopupWindow(view,
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp2px(360), true);
        cartPopup.setOutsideTouchable(true);
        cartPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 显示在底部购物车栏上方
        cartPopup.showAtLocation(findViewById(R.id.cart_bar), Gravity.BOTTOM, 0, dp2px(58));
    }

    /**
     * 确认清空购物车的对话框
     */
    private void confirmClearCart() {
        new AlertDialog.Builder(this)
                .setMessage("确定要清空购物车吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    for (Fruit f : shop.getFruitList()) {
                        f.setCount(0);
                    }
                    hideCartPopup();
                    refreshCartBar();
                    Toast.makeText(this, "购物车已清空", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void hideCartPopup() {
        if (cartPopup != null && cartPopup.isShowing()) {
            cartPopup.dismiss();
        }
    }

    private void refreshCartPopup() {
        if (cartAdapter != null && cartPopup != null && cartPopup.isShowing()) {
            cartAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 去结算：总价达到起送价时跳转到订单界面
     */
    private void onCheckoutClick() {
        boolean canCheckout = Boolean.TRUE.equals(tvCheckout.getTag());
        if (!canCheckout) {
            Toast.makeText(this, "未达到起送价，再挑一点水果吧~", Toast.LENGTH_SHORT).show();
            return;
        }
        hideCartPopup();
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("shopName", shop.getShopName());
        intent.putExtra("distributionCost", shop.getDistributionCost());
        intent.putExtra("fruitList", new ArrayList<>(getSelectedFruits()));
        startActivity(intent);
    }

    /**
     * 获取购物车中数量大于 0 的水果
     */
    private List<Fruit> getSelectedFruits() {
        List<Fruit> selected = new ArrayList<>();
        if (shop == null) {
            return selected;
        }
        for (Fruit f : shop.getFruitList()) {
            if (f.getCount() > 0) {
                selected.add(f);
            }
        }
        return selected;
    }

    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
