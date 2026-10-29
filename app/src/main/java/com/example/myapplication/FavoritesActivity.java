package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.SearchAdapter;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.util.FavoriteDao;

import java.util.List;

/**
 * 我的收藏页：从 SQLite 读取收藏列表，点击查看详情，长按取消收藏。
 */
public class FavoritesActivity extends AppCompatActivity {

    private FavoriteDao favoriteDao;
    private SearchAdapter adapter;
    private List<Fruit> favoriteList;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        favoriteDao = new FavoriteDao(this);
        tvEmpty = findViewById(R.id.tv_empty);
        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        favoriteList = favoriteDao.queryAll();
        RecyclerView rv = findViewById(R.id.rv_favorite);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(favoriteList, "看看", new SearchAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(Fruit fruit) {
                Intent intent = new Intent(FavoritesActivity.this, FruitDetailActivity.class);
                intent.putExtra("fruit", fruit);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(Fruit fruit) {
                confirmRemove(fruit);
                return true;
            }
        });
        rv.setAdapter(adapter);
        tvEmpty.setVisibility(favoriteList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void confirmRemove(Fruit fruit) {
        new AlertDialog.Builder(this)
                .setMessage("取消收藏「" + fruit.getFoodName() + "」？")
                .setPositiveButton("取消收藏", (dialog, which) -> {
                    favoriteDao.remove(fruit.getFoodId());
                    Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    loadFavorites();
                })
                .setNegativeButton("再想想", null)
                .show();
    }
}
