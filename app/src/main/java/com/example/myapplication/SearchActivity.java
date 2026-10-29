package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.SearchAdapter;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Shop;
import com.example.myapplication.util.DataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页：把全部店铺的水果摊平后按关键字过滤（水果名称 / 产地 / 店铺名都参与匹配）。
 */
public class SearchActivity extends AppCompatActivity {

    private EditText etKeyword;
    private RecyclerView rvResult;
    private ProgressBar progressBar;
    private TextView tvTip;
    private SearchAdapter adapter;

    private final List<Fruit> allFruits = new ArrayList<>();
    private final List<Fruit> result = new ArrayList<>();
    private boolean dataReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();
        loadData();
    }

    private void initViews() {
        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        etKeyword = findViewById(R.id.et_keyword);
        rvResult = findViewById(R.id.rv_search_result);
        progressBar = findViewById(R.id.progress_bar);
        tvTip = findViewById(R.id.tv_tip);
        rvResult.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(result, "查看", new SearchAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(Fruit fruit) {
                Intent intent = new Intent(SearchActivity.this, FruitDetailActivity.class);
                intent.putExtra("fruit", fruit);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(Fruit fruit) {
                return false;
            }
        });
        rvResult.setAdapter(adapter);

        findViewById(R.id.tv_search).setOnClickListener(v -> doSearch());
        // 键盘上的「搜索」按钮
        etKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
                return true;
            }
            return false;
        });
        // 热门关键字
        bindHotWord(R.id.tv_hot_1);
        bindHotWord(R.id.tv_hot_2);
        bindHotWord(R.id.tv_hot_3);
        bindHotWord(R.id.tv_hot_4);
    }

    private void bindHotWord(int resId) {
        TextView tv = findViewById(resId);
        tv.setOnClickListener(v -> {
            etKeyword.setText(tv.getText());
            etKeyword.setSelection(tv.getText().length());
            doSearch();
        });
    }

    /**
     * 异步加载全部店铺数据，再把菜品摊平用于搜索
     */
    private void loadData() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        tvTip.setText("正在加载商品数据…");
        DataProvider.getShopList(this, new DataProvider.OnShopListener() {
            @Override
            public void onSuccess(List<Shop> shopList) {
                progressBar.setVisibility(android.view.View.GONE);
                allFruits.clear();
                for (Shop shop : shopList) {
                    if (shop.getFruitList() != null) {
                        allFruits.addAll(shop.getFruitList());
                    }
                }
                dataReady = true;
                tvTip.setText(allFruits.isEmpty()
                        ? "暂无可搜索的商品" : "共 " + allFruits.size() + " 件商品，输入关键字开始搜索");
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(android.view.View.GONE);
                tvTip.setText(message + "\n请返回重试");
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 执行搜索：按水果名、产地、店铺名匹配关键字
     */
    private void doSearch() {
        String keyword = etKeyword.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(this, "请输入要搜索的水果或店铺", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dataReady) {
            Toast.makeText(this, "数据还在加载，请稍候", Toast.LENGTH_SHORT).show();
            return;
        }
        result.clear();
        for (Fruit fruit : allFruits) {
            if (contains(fruit.getFoodName(), keyword)
                    || contains(fruit.getIngredients(), keyword)
                    || contains(fruit.getShopName(), keyword)) {
                result.add(fruit);
            }
        }
        adapter.notifyDataSetChanged();
        if (result.isEmpty()) {
            tvTip.setVisibility(android.view.View.VISIBLE);
            tvTip.setText("没有找到与「" + keyword + "」相关的商品\n换个关键字试试");
        } else {
            tvTip.setVisibility(android.view.View.GONE);
        }
    }

    private boolean contains(String text, String keyword) {
        return text != null && text.contains(keyword);
    }
}
