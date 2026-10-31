package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Shop;
import com.example.myapplication.util.BottomNav;
import com.example.myapplication.util.CartDao;
import com.example.myapplication.util.DataProvider;
import com.example.myapplication.util.ImageUtils;
import com.example.myapplication.util.PriceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 购物车页（底部导航「购物车」页签）。
 * 购物车数据来自 SQLite，按店铺分成一张张卡片展示，支持＋/－ 修改数量、清空、按店铺去结算。
 */
public class CartActivity extends BaseActivity {

    private CartDao cartDao;
    private List<Shop> shopList;

    private LinearLayout llGroups, llEmpty;
    private TextView tvClearAll;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartDao = new CartDao(this);
        initViews();
        loadShops();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从店铺详情页或订单页返回时，重新读取 SQLite 中的购物车
        BottomNav.bind(this, BottomNav.TAB_CART);
        if (shopList != null) {
            renderCart();
        }
    }

    private void initViews() {
        llGroups = findViewById(R.id.ll_cart_groups);
        llEmpty = findViewById(R.id.ll_cart_empty);
        tvClearAll = findViewById(R.id.tv_clear_all);
        progressBar = findViewById(R.id.progress_bar);

        tvClearAll.setOnClickListener(v -> confirmClearAll());
        findViewById(R.id.tv_go_shopping).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    /**
     * 取得店铺数据（优先用内存缓存，避免每次进购物车都等网络超时）
     */
    private void loadShops() {
        shopList = DataProvider.getCachedShopList();
        if (shopList != null) {
            renderCart();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        DataProvider.getShopList(this, new DataProvider.OnShopListener() {
            @Override
            public void onSuccess(List<Shop> list) {
                progressBar.setVisibility(View.GONE);
                shopList = list;
                renderCart();
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 重新渲染购物车：从 SQLite 读出全部条目，按店铺分组生成卡片
     */
    private void renderCart() {
        Map<Integer, List<Fruit>> cartMap = cartDao.queryAllByShop();
        llGroups.removeAllViews();

        int groupCount = 0;
        for (Shop shop : shopList) {
            List<Fruit> items = cartMap.get(shop.getId());
            if (items == null || items.isEmpty()) {
                continue;
            }
            groupCount++;
            addShopGroup(shop, items);
        }

        boolean empty = groupCount == 0;
        llEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        llGroups.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvClearAll.setVisibility(empty ? View.GONE : View.VISIBLE);
        BottomNav.refreshCartBadge(this);
    }

    /**
     * 生成一张店铺购物车卡片
     */
    private void addShopGroup(final Shop shop, final List<Fruit> items) {
        View group = LayoutInflater.from(this)
                .inflate(R.layout.item_cart_group, llGroups, false);
        ((TextView) group.findViewById(R.id.tv_group_shop_name)).setText(shop.getShopName());
        ((TextView) group.findViewById(R.id.tv_group_shop_extra)).setText(
                "起送 ¥" + shop.getOfferPrice() + " · 配送 ¥" + shop.getDistributionCost()
                        + " · " + shop.getTime());

        // 点击店铺名回到店铺详情
        group.findViewById(R.id.ll_group_header).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopDetailActivity.class);
            intent.putExtra("shop", shop);
            startActivity(intent);
        });

        LinearLayout llItems = group.findViewById(R.id.ll_group_items);
        TextView tvTotal = group.findViewById(R.id.tv_group_total);
        for (Fruit fruit : items) {
            llItems.addView(buildItemRow(shop, fruit, items, tvTotal));
        }
        updateGroupTotal(tvTotal, items);

        group.findViewById(R.id.tv_group_checkout).setOnClickListener(v ->
                checkout(shop, items));
        llGroups.addView(group);
    }

    /**
     * 生成一行商品（含 ＋/－ 按钮）
     */
    private View buildItemRow(final Shop shop, final Fruit fruit,
                              final List<Fruit> items, final TextView tvTotal) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_cart_row, null, false);
        ImageUtils.load(row.findViewById(R.id.iv_row_pic), fruit.getFoodPic());
        ((TextView) row.findViewById(R.id.tv_row_name)).setText(fruit.getFoodName());
        ((TextView) row.findViewById(R.id.tv_row_price))
                .setText("¥" + PriceUtils.fmt(fruit.getPrice()));

        final TextView tvCount = row.findViewById(R.id.tv_row_count);
        tvCount.setText(String.valueOf(fruit.getCount()));

        row.findViewById(R.id.tv_row_minus).setOnClickListener(v -> {
            int count = cartDao.changeCount(shop.getId(), fruit, -1);
            fruit.setCount(count);
            if (count == 0) {
                // 数量减到 0 时整行移除，重新渲染
                renderCart();
            } else {
                tvCount.setText(String.valueOf(count));
                updateGroupTotal(tvTotal, items);
                BottomNav.refreshCartBadge(this);
            }
        });
        row.findViewById(R.id.tv_row_plus).setOnClickListener(v -> {
            int count = cartDao.changeCount(shop.getId(), fruit, 1);
            fruit.setCount(count);
            tvCount.setText(String.valueOf(count));
            updateGroupTotal(tvTotal, items);
            BottomNav.refreshCartBadge(this);
        });
        return row;
    }

    /** 计算一组商品的合计金额 */
    private double groupTotal(List<Fruit> items) {
        double total = 0;
        for (Fruit f : items) {
            total += f.getPrice() * f.getCount();
        }
        return total;
    }

    private int groupCount(List<Fruit> items) {
        int count = 0;
        for (Fruit f : items) {
            count += f.getCount();
        }
        return count;
    }

    private void updateGroupTotal(TextView tvTotal, List<Fruit> items) {
        tvTotal.setText("合计 ¥" + PriceUtils.fmt(groupTotal(items))
                + "   共 " + groupCount(items) + " 件");
    }

    /**
     * 去结算：校验起送价后跳转订单页
     */
    private void checkout(Shop shop, List<Fruit> items) {
        double total = groupTotal(items);
        if (total < shop.getOfferPrice()) {
            double diff = shop.getOfferPrice() - total;
            Toast.makeText(this, "还差 ¥" + PriceUtils.fmt(diff) + " 达到起送价，再挑一点吧~",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("shopId", shop.getId());
        intent.putExtra("shopName", shop.getShopName());
        intent.putExtra("distributionCost", shop.getDistributionCost());
        intent.putExtra("fruitList", new ArrayList<>(items));
        startActivity(intent);
    }

    /**
     * 清空购物车（需二次确认）
     */
    private void confirmClearAll() {
        new AlertDialog.Builder(this)
                .setMessage("确定要清空购物车吗？")
                .setPositiveButton("清空", (dialog, which) -> {
                    cartDao.clearAll();
                    renderCart();
                    Toast.makeText(this, "购物车已清空", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
