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
import com.example.myapplication.util.PriceUtils;
import com.example.myapplication.util.QRCodeUtils;

import java.util.ArrayList;

/**
 * 订单界面：展示收货地址、订单列表、商品小计、配送费以及订单总价，
 * 点击“去支付”按钮会弹出一个二维码支付界面供用户付款。
 */
public class OrderActivity extends AppCompatActivity {

    private double subtotal = 0;      // 商品小计
    private double distributionCost;  // 配送费
    private double total = 0;         // 订单总价

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        // 接收店铺详情界面传递过来的数据
        String shopName = getIntent().getStringExtra("shopName");
        distributionCost = getIntent().getDoubleExtra("distributionCost", 0);
        ArrayList<Fruit> fruitList =
                (ArrayList<Fruit>) getIntent().getSerializableExtra("fruitList");
        if (fruitList == null) {
            fruitList = new ArrayList<>();
        }
        initViews(shopName, fruitList);
    }

    private void initViews(String shopName, final ArrayList<Fruit> fruitList) {
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> finish());
        // 收货地址
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvReceiver = findViewById(R.id.tv_receiver);
        tvAddress.setText(Const.DEFAULT_ADDRESS);
        tvReceiver.setText(Const.DEFAULT_RECEIVER);
        // 店铺名称
        TextView tvShopName = findViewById(R.id.tv_order_shop_name);
        tvShopName.setText(TextUtilsJoin(shopName));
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
        TextView tvSubtotal = findViewById(R.id.tv_subtotal);
        TextView tvDistribution = findViewById(R.id.tv_distribution);
        TextView tvTotal = findViewById(R.id.tv_total);
        tvSubtotal.setText("商品小计：¥" + PriceUtils.fmt(subtotal));
        tvDistribution.setText("配送费：¥" + PriceUtils.fmt(distributionCost));
        tvTotal.setText("¥" + PriceUtils.fmt(total));
        // 去支付：弹出二维码支付界面
        TextView tvPay = findViewById(R.id.tv_pay);
        tvPay.setOnClickListener(v -> showPayDialog());
    }

    private String TextUtilsJoin(String shopName) {
        return "店铺：" + (shopName == null ? "" : shopName);
    }

    /**
     * 二维码支付对话框
     */
    private void showPayDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pay, null);
        ImageView ivQr = view.findViewById(R.id.iv_qr_code);
        TextView tvAmount = view.findViewById(R.id.tv_pay_amount);
        TextView tvDone = view.findViewById(R.id.tv_pay_done);
        tvAmount.setText("¥" + PriceUtils.fmt(total));
        // 生成支付二维码（内容为模拟的订单支付信息）
        String qrContent = "FruitMall|订单支付|金额¥" + PriceUtils.fmt(total)
                + "|单号" + System.currentTimeMillis();
        Bitmap qrBitmap = QRCodeUtils.createQRCode(qrContent, 600);
        if (qrBitmap != null) {
            ivQr.setImageBitmap(qrBitmap);
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
        // 模拟支付完成
        tvDone.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "支付成功，感谢惠顾！", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
