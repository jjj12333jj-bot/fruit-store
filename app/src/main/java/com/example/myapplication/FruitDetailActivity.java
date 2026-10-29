package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.bean.Fruit;
import com.example.myapplication.util.CartDao;
import com.example.myapplication.util.FavoriteDao;
import com.example.myapplication.util.ImageUtils;
import com.example.myapplication.util.PriceUtils;

import java.util.Locale;

/**
 * 菜品详情页：展示菜品图片、名称、价格、描述，支持加入购物车与收藏。
 * 收藏状态保存在 SQLite 中（知识点：SQLite 保存收藏数据）。
 */
public class FruitDetailActivity extends AppCompatActivity {

    private Fruit fruit;
    private CartDao cartDao;
    private FavoriteDao favoriteDao;

    private TextView tvFavoriteIcon, tvFavoriteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail);
        fruit = (Fruit) getIntent().getSerializableExtra("fruit");
        if (fruit == null) {
            Toast.makeText(this, "商品信息不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        cartDao = new CartDao(this);
        favoriteDao = new FavoriteDao(this);
        initViews();
        showFruit();
    }

    private void initViews() {
        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        tvFavoriteIcon = findViewById(R.id.tv_favorite_icon);
        tvFavoriteText = findViewById(R.id.tv_favorite_text);
        findViewById(R.id.ll_favorite).setOnClickListener(v -> toggleFavorite());
        findViewById(R.id.tv_add_cart).setOnClickListener(v -> addToCart());
    }

    private void showFruit() {
        ImageUtils.load(findViewById(R.id.iv_fruit_pic), fruit.getFoodPic());
        ((TextView) findViewById(R.id.tv_fruit_name)).setText(fruit.getFoodName());
        ((TextView) findViewById(R.id.tv_fruit_price))
                .setText("¥" + PriceUtils.fmt(fruit.getPrice()));
        ((TextView) findViewById(R.id.tv_fruit_ingredients)).setText(fruit.getIngredients());
        ((TextView) findViewById(R.id.tv_fruit_desc)).setText(fruit.getDescription());
        ((TextView) findViewById(R.id.tv_fruit_shop))
                .setText("店铺：" + (fruit.getShopName() == null ? "" : fruit.getShopName()));
        ((TextView) findViewById(R.id.tv_fruit_sale))
                .setText(String.format(Locale.CHINA, "月销 %d 件", fruit.getSaleNum()));
        refreshFavoriteState();
    }

    /**
     * 刷新收藏按钮状态
     */
    private void refreshFavoriteState() {
        boolean favorite = favoriteDao.isFavorite(fruit.getFoodId());
        if (favorite) {
            tvFavoriteIcon.setText("★");
            tvFavoriteIcon.setTextColor(getColor(R.color.price_red));
            tvFavoriteText.setText("已收藏");
            tvFavoriteText.setTextColor(getColor(R.color.price_red));
        } else {
            tvFavoriteIcon.setText("☆");
            tvFavoriteIcon.setTextColor(getColor(R.color.text_gray));
            tvFavoriteText.setText("收藏");
            tvFavoriteText.setTextColor(getColor(R.color.text_gray));
        }
    }

    private void toggleFavorite() {
        boolean favorite = favoriteDao.toggle(fruit);
        refreshFavoriteState();
        Toast.makeText(this, favorite ? "已加入收藏" : "已取消收藏", Toast.LENGTH_SHORT).show();
    }

    /**
     * 加入购物车：写入 SQLite
     */
    private void addToCart() {
        int count = cartDao.changeCount(fruit.getShopId(), fruit, 1);
        fruit.setCount(count);
        Toast.makeText(this, "已加入购物车（" + fruit.getFoodName() + " × " + count + "）",
                Toast.LENGTH_SHORT).show();
    }
}
