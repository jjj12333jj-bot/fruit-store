package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.util.Const;
import com.example.myapplication.util.OrderDao;
import com.example.myapplication.util.PriceUtils;
import com.example.myapplication.util.QRCodeUtils;

import java.util.ArrayList;

/**
 * 订单页面：展示收货地址、订单明细、商品小计、配送费与合计，
 * 点击「提交订单」把订单写入 SQLite，并弹出二维码完成支付。
 */
public class OrderActivity extends AppCompatActivity {

    private int shopId;
    private String shopName;
    private double subtotal = 0;      // 商品小计
    private double distributionCost;  // 配送费
    private double total = 0;         // 订单总价
    private ArrayList<Fruit> fruitList = new ArrayList<>();
    private OrderDao orderDao;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        // 接收店铺详情页传递过来的数据
        shopId = getIntent().getIntExtra("shopId", -1);
        shopName = getIntent().getStringExtra("shopName");
        distributionCost = getIntent().getDoubleExtra("distributionCost", 0);
        ArrayList<Fruit> list = (ArrayList<Fruit>) getIntent().getSerializableExtra("fruitList");
        if (list != null) {
            fruitList = list;
        }
        orderDao = new OrderDao(this);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        // 收货地址
        ((TextView) findViewById(R.id.tv_address)).setText(Const.DEFAULT_ADDRESS);
        ((TextView) findViewById(R.id.tv_receiver)).setText(Const.DEFAULT_RECEIVER);
        // 店铺名称
        ((TextView) findViewById(R.id.tv_order_shop_name))
                .setText("店铺：" + (shopName == null ? "" : shopName));
        // 订单列表
        RecyclerView rvOrder = findViewById(R.id.rv_order_list);
        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        rvOrder.setNestedScrollingEnabled(false);
        rvOrder.setAdapter(new OrderAdapter(fruitList));
        // 计算商品小计与总价
        for (Fruit f : fruitList) {
            subtotal += f.getPrice() * f.getCount();
        }
        total = subtotal + distributionCost;
        ((TextView) findViewById(R.id.tv_subtotal))
                .setText("商品小计：¥" + PriceUtils.fmt(subtotal));
        ((TextView) findViewById(R.id.tv_distribution))
                .setText("配送费：¥" + PriceUtils.fmt(distributionCost));
        ((TextView) findViewById(R.id.tv_total)).setText("¥" + PriceUtils.fmt(total));
        // 提交订单
        findViewById(R.id.tv_pay).setOnClickListener(v -> submitOrder());
    }

    /**
     * 提交订单：写入 SQLite 后再弹出支付二维码
     */
    private void submitOrder() {
        if (fruitList.isEmpty()) {
            Toast.makeText(this, "订单中没有商品", Toast.LENGTH_SHORT).show();
            return;
        }
        long orderId = orderDao.submitOrder(shopName, total,
                Const.DEFAULT_ADDRESS, Const.DEFAULT_RECEIVER, fruitList);
        if (orderId == -1) {
            Toast.makeText(this, "订单提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        showPayDialog(orderId);
    }

    /**
     * 二维码支付对话框
     */
    private void showPayDialog(long orderId) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pay, null);
        ImageView ivQr = view.findViewById(R.id.iv_qr_code);
        TextView tvAmount = view.findViewById(R.id.tv_pay_amount);
        TextView tvDone = view.findViewById(R.id.tv_pay_done);
        tvAmount.setText("¥" + PriceUtils.fmt(total));
        // 生成支付二维码（内容为模拟的订单支付信息）
        String qrContent = "FruitMall|订单支付|单号" + orderId + "|金额¥" + PriceUtils.fmt(total);
        Bitmap qrBitmap = QRCodeUtils.createQRCode(qrContent, 600);
        if (qrBitmap != null) {
            ivQr.setImageBitmap(qrBitmap);
        }
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
        tvDone.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "下单成功，订单号 " + orderId, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
