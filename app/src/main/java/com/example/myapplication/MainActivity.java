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
import com.example.myapplication.util.DataProvider;

import java.util.List;

/**
 * 店铺页面（首页）：展示店铺列表。
 * 数据通过异步线程从服务器（或内置 assets）加载 JSON，加载过程中显示进度条，
 * 失败时显示错误提示并支持点击重试（知识点：异常处理与加载状态提示）。
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView rvShopList;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        loadShops();
    }

    private void initViews() {
        rvShopList = findViewById(R.id.rv_shop_list);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        rvShopList.setLayoutManager(new LinearLayoutManager(this));
        // 搜索入口
        findViewById(R.id.ll_search).setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
        // 个人中心入口
        findViewById(R.id.tv_mine).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
        // 加载失败时可点击重试
        tvEmpty.setOnClickListener(v -> loadShops());
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
        ShopAdapter adapter = new ShopAdapter(list, shop -> {
            Intent intent = new Intent(MainActivity.this, ShopDetailActivity.class);
            // 用 Intent 传递 Serializable 对象到下一个页面
            intent.putExtra("shop", shop);
            startActivity(intent);
        });
        rvShopList.setAdapter(adapter);
    }
}
