package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.OrderRecordAdapter;
import com.example.myapplication.bean.Order;
import com.example.myapplication.util.OrderDao;

import java.util.List;

/**
 * 我的订单页：从 SQLite 读取历史订单（含商品明细）并展示。
 */
public class OrderListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        List<Order> orders = new OrderDao(this).queryAll();
        RecyclerView rv = findViewById(R.id.rv_orders);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new OrderRecordAdapter(orders));
        TextView tvEmpty = findViewById(R.id.tv_empty);
        tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
