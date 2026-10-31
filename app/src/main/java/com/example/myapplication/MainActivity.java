package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ShopAdapter;
import com.example.myapplication.bean.Shop;
import com.example.myapplication.util.BottomNav;
import com.example.myapplication.util.DataProvider;
import com.example.myapplication.util.UserPrefs;

import java.util.List;

/**
 * 主页（店铺页面）：展示店铺列表，是底部导航「主页」页签。
 * 数据通过异步线程从服务器（或内置 assets）加载 JSON，加载过程中显示进度条，
 * 失败时显示错误提示并支持点击重试（知识点：异常处理与加载状态提示）。
 */
public class MainActivity extends BaseActivity {

    private RecyclerView rvShopList;
    private ProgressBar progressBar;
    private TextView tvEmpty, tvGreeting, tvShopCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        loadShops();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 绑定底部导航（同时刷新购物车角标）
        BottomNav.bind(this, BottomNav.TAB_HOME);
        refreshGreeting();
    }

    private void initViews() {
        rvShopList = findViewById(R.id.rv_shop_list);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        tvGreeting = findViewById(R.id.tv_greeting);
        tvShopCount = findViewById(R.id.tv_shop_count);

        rvShopList.setLayoutManager(new LinearLayoutManager(this));
        // 首页整体是一个可滚动页面，列表自身不滚动
        rvShopList.setNestedScrollingEnabled(false);

        // 搜索入口
        findViewById(R.id.ll_search).setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
        findViewById(R.id.ll_quick_search).setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
        findViewById(R.id.ll_quick_favorite).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.ll_quick_order).setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        findViewById(R.id.ll_quick_cart).setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        // 未登录时点击右上角问候语可直接去登录
        tvGreeting.setOnClickListener(v -> {
            if (!new UserPrefs(this).isLogin()) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(LoginActivity.EXTRA_FORCE_SHOW, true);
                startActivity(intent);
            }
        });
        // 加载失败时可点击重试
        tvEmpty.setOnClickListener(v -> loadShops());
    }

    /**
     * 右上角问候语：登录用户显示昵称，游客提示登录
     */
    private void refreshGreeting() {
        UserPrefs prefs = new UserPrefs(this);
        if (prefs.isLogin()) {
            tvGreeting.setText("Hi，" + prefs.getNickname());
        } else {
            tvGreeting.setText("游客浏览 · 点击登录");
        }
    }

    /**
     * 异步加载店铺列表
     */
    private void loadShops() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        DataProvider.getShopList(this, new DataProvider.OnShopListener() {
            @Override
            public void onSuccess(List<Shop> list) {
                progressBar.setVisibility(View.GONE);
                if (list == null || list.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("附近还没有水果店，点击刷新试试");
                    return;
                }
                showShops(list);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText(message + "\n点击屏幕重试");
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showShops(List<Shop> list) {
        tvShopCount.setText("共 " + list.size() + " 家");
        ShopAdapter adapter = new ShopAdapter(list, shop -> {
            Intent intent = new Intent(MainActivity.this, ShopDetailActivity.class);
            // 用 Intent 传递 Serializable 对象到下一个页面
            intent.putExtra("shop", shop);
            startActivity(intent);
        });
        rvShopList.setAdapter(adapter);
    }
}
