package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.util.FavoriteDao;
import com.example.myapplication.util.OrderDao;
import com.example.myapplication.util.UserPrefs;

/**
 * 个人中心：展示用户信息（SharedPreferences）、订单与收藏数量，
 * 提供我的订单、我的收藏、退出登录入口。
 */
public class ProfileActivity extends AppCompatActivity {

    private UserPrefs userPrefs;
    private TextView tvNickname, tvPhone, tvLoginState, tvOrderCount, tvFavoriteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userPrefs = new UserPrefs(this);
        tvNickname = findViewById(R.id.tv_nickname);
        tvPhone = findViewById(R.id.tv_phone);
        tvLoginState = findViewById(R.id.tv_login_state);
        tvOrderCount = findViewById(R.id.tv_order_count);
        tvFavoriteCount = findViewById(R.id.tv_favorite_count);

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());
        findViewById(R.id.ll_user).setOnClickListener(v -> showLoginDialog());
        findViewById(R.id.tv_login_state).setOnClickListener(v -> showLoginDialog());
        findViewById(R.id.ll_my_order).setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        findViewById(R.id.ll_stat_order).setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        findViewById(R.id.ll_my_favorite).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.ll_stat_favorite).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.ll_logout).setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * 刷新用户信息与统计数据
     */
    private void refresh() {
        boolean login = userPrefs.isLogin();
        tvNickname.setText(login ? userPrefs.getNickname() : "未登录用户");
        tvPhone.setText(login ? userPrefs.getPhone() : "登录后可查看订单与收藏");
        tvLoginState.setText(login ? "修改资料" : "登录");
        tvOrderCount.setText(String.valueOf(new OrderDao(this).count()));
        tvFavoriteCount.setText(String.valueOf(new FavoriteDao(this).count()));
    }

    /**
     * 登录对话框：输入昵称与手机号，带基本校验，登录状态保存到 SharedPreferences
     */
    private void showLoginDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        EditText etNickname = view.findViewById(R.id.et_nickname);
        EditText etPhone = view.findViewById(R.id.et_phone);
        // 回显已保存的信息
        etNickname.setText(userPrefs.getNickname());
        etPhone.setText("未绑定手机号".equals(userPrefs.getPhone()) ? "" : userPrefs.getPhone());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
        // 手动处理点击事件，校验不通过时不关闭对话框
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nickname = etNickname.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(nickname)) {
                Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.length() != 11 || !TextUtils.isDigitsOnly(phone)) {
                Toast.makeText(this, "请输入 11 位手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            userPrefs.login(nickname, phone);
            refresh();
            dialog.dismiss();
            Toast.makeText(this, "登录成功，欢迎 " + nickname, Toast.LENGTH_SHORT).show();
        });
    }

    private void confirmLogout() {
        if (!userPrefs.isLogin()) {
            Toast.makeText(this, "当前还未登录", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("退出", (dialog, which) -> {
                    userPrefs.logout();
                    refresh();
                    Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
