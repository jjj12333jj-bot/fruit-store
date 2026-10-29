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
import com.example.myapplication.util.CartDao;
import com.example.myapplication.util.ImageUtils;
import com.example.myapplication.util.PriceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 店铺详情页：展示店铺基本信息（名称、公告、优惠、配送信息）与菜品列表，
 * 支持加入购物车、修改数量、清空购物车、去结算。
 * 购物车数据保存在 SQLite 中（知识点：SQLite 保存购物车）。
 */
public class ShopDetailActivity extends AppCompatActivity {

    private Shop shop;
    private CartDao cartDao;
    private FruitAdapter fruitAdapter;
    private CartAdapter cartAdapter;
    private PopupWindow cartPopup;

    private TextView tvEmpty;
    private TextView tvCartCount, tvTotalPrice, tvCartHint, tvCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        // 接收首页传过来的店铺对象
        shop = (Shop) getIntent().getSerializableExtra("shop");
        if (shop == null) {
            Toast.makeText(this, "店铺信息不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        cartDao = new CartDao(this);
        initViews();
        showShopInfo();
        initFruitList();
        refreshCartBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从菜品详情页返回时，重新同步购物车数量
        if (shop != null && fruitAdapter != null) {
            cartDao.fillCartCount(shop.getId(), shop.getFruitList());
            fruitAdapter.notifyDataSetChanged();
            refreshCartBar();
        }
    }

    private void initViews() {
        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        tvEmpty = findViewById(R.id.tv_empty);
        tvCartCount = findViewById(R.id.tv_cart_count);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvCartHint = findViewById(R.id.tv_cart_hint);
        tvCheckout = findViewById(R.id.tv_checkout);
        findViewById(R.id.fl_cart).setOnClickListener(v -> showCartPopup());
        tvCheckout.setOnClickListener(v -> onCheckoutClick());
    }

    /**
     * 展示店铺基本信息
     */
    private void showShopInfo() {
        ImageUtils.load(findViewById(R.id.iv_shop_pic), shop.getShopPic());
        ((TextView) findViewById(R.id.tv_shop_name)).setText(shop.getShopName());
        ((TextView) findViewById(R.id.tv_shop_welfare)).setText(shop.getWelfare());
        ((TextView) findViewById(R.id.tv_shop_notice)).setText(shop.getShopNotice());
        ((TextView) findViewById(R.id.tv_shop_info)).setText(String.format(Locale.CHINA,
                "%.1f 分   月售 %d   %s   起送 ¥%d   配送费 ¥%d",
                shop.getScore(), shop.getSaleNum(), shop.getTime(),
                shop.getOfferPrice(), shop.getDistributionCost()));
    }

    /**
     * 菜品（水果）列表
     */
    private void initFruitList() {
        RecyclerView rvFruit = findViewById(R.id.rv_fruit_list);
        rvFruit.setLayoutManager(new LinearLayoutManager(this));
        // 先从 SQLite 中恢复购物车数量
        cartDao.fillCartCount(shop.getId(), shop.getFruitList());
        if (shop.getFruitList() == null || shop.getFruitList().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        }
        fruitAdapter = new FruitAdapter(shop.getFruitList(), fruit -> addToCart(fruit));
        fruitAdapter.setOnItemClickListener(this::showFruitDetailDialog);
        rvFruit.setAdapter(fruitAdapter);
    }

    /**
     * 加入购物车：写入 SQLite 并刷新底部栏
     */
    private void addToCart(Fruit fruit) {
        int count = cartDao.changeCount(shop.getId(), fruit, 1);
        fruit.setCount(count);
        refreshCartBar();
        refreshCartPopup();
        Toast.makeText(this, "已将「" + fruit.getFoodName() + "」加入购物车",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * 菜品详情对话框（点击列表条目时展示）
     */
    private void showFruitDetailDialog(Fruit fruit) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_fruit_detail, null);
        ImageUtils.load(view.findViewById(R.id.iv_detail_pic), fruit.getFoodPic());
        ((TextView) view.findViewById(R.id.tv_detail_name)).setText(fruit.getFoodName());
        ((TextView) view.findViewById(R.id.tv_detail_ingredients)).setText(fruit.getIngredients());
        ((TextView) view.findViewById(R.id.tv_detail_sale_num)).setText("月销量：" + fruit.getSaleNum());
        ((TextView) view.findViewById(R.id.tv_detail_price))
                .setText("¥" + PriceUtils.fmt(fruit.getPrice()));
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    /**
     * 刷新底部购物车栏
     */
    private void refreshCartBar() {
        int totalCount = 0;
        double totalPrice = 0;
        for (Fruit f : shop.getFruitList()) {
            totalCount += f.getCount();
            totalPrice += f.getCount() * f.getPrice();
        }
        if (totalCount > 0) {
            tvCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(totalCount));
        } else {
            tvCartCount.setVisibility(View.GONE);
        }
        tvTotalPrice.setText("¥" + PriceUtils.fmt(totalPrice));
        tvCartHint.setText(totalCount == 0 ? "未选购商品" : "已选 " + totalCount + " 件商品");
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
     * 弹出购物车弹层
     */
    private void showCartPopup() {
        if (getSelectedFruits().isEmpty()) {
            Toast.makeText(this, "购物车还是空的，快去挑选水果吧~", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cartPopup != null && cartPopup.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.popup_cart, null);
        RecyclerView rvCart = view.findViewById(R.id.rv_cart_list);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        view.findViewById(R.id.tv_clear_cart).setOnClickListener(v -> confirmClearCart());
        cartAdapter = new CartAdapter(getSelectedFruits(), (fruit, delta) -> {
            int count = cartDao.changeCount(shop.getId(), fruit, delta);
            fruit.setCount(count);
            refreshCartBar();
            if (getSelectedFruits().isEmpty()) {
                hideCartPopup();
            } else {
                cartAdapter.notifyDataSetChanged();
            }
        });
        rvCart.setAdapter(cartAdapter);
        cartPopup = new PopupWindow(view,
                LinearLayout.LayoutParams.MATCH_PARENT, dp2px(360), true);
        cartPopup.setOutsideTouchable(true);
        cartPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cartPopup.showAtLocation(findViewById(R.id.cart_bar), Gravity.BOTTOM, 0, dp2px(58));
    }

    /**
     * 清空购物车确认对话框
     */
    private void confirmClearCart() {
        new AlertDialog.Builder(this)
                .setMessage("确定要清空购物车吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    cartDao.clearCart(shop.getId());
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
     * 去结算：跳转到确认订单页
     */
    private void onCheckoutClick() {
        if (!Boolean.TRUE.equals(tvCheckout.getTag())) {
            Toast.makeText(this, "未达到起送价，再挑一点水果吧~", Toast.LENGTH_SHORT).show();
            return;
        }
        hideCartPopup();
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("shopId", shop.getId());
        intent.putExtra("shopName", shop.getShopName());
        intent.putExtra("distributionCost", shop.getDistributionCost());
        intent.putExtra("fruitList", new ArrayList<>(getSelectedFruits()));
        startActivity(intent);
    }

    /**
     * 购物车中数量大于 0 的水果
     */
    private List<Fruit> getSelectedFruits() {
        List<Fruit> selected = new ArrayList<>();
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
