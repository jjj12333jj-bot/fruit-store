package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.util.BottomNav;
import com.example.myapplication.util.FavoriteDao;
import com.example.myapplication.util.OrderDao;
import com.example.myapplication.util.UserPrefs;

/**
 * 个人中心（底部导航「个人中心」页签）。
 * 展示用户信息（SharedPreferences）、订单与收藏数量，提供登录/修改资料、订单、收藏、退出登录入口。
 */
public class ProfileActivity extends BaseActivity {

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

        findViewById(R.id.ll_my_order).setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        findViewById(R.id.ll_stat_order).setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        findViewById(R.id.ll_my_favorite).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.ll_stat_favorite).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));

        // 「登录 / 修改资料」统一走登录页（edit_mode 时按钮变为保存）
        View.OnClickListener loginOrEdit = v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            if (userPrefs.isLogin()) {
                intent.putExtra(LoginActivity.EXTRA_EDIT_MODE, true);
            } else {
                intent.putExtra(LoginActivity.EXTRA_FORCE_SHOW, true);
            }
            startActivity(intent);
        };
        tvLoginState.setOnClickListener(loginOrEdit);
        tvNickname.setOnClickListener(loginOrEdit);
        findViewById(R.id.ll_edit_profile).setOnClickListener(loginOrEdit);
        findViewById(R.id.ll_logout).setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNav.bind(this, BottomNav.TAB_PROFILE);
        refresh();
    }

    /**
     * 刷新用户信息与统计数据
     */
    private void refresh() {
        boolean login = userPrefs.isLogin();
        tvNickname.setText(login ? userPrefs.getNickname() : "未登录用户");
        tvPhone.setText(login ? userPrefs.getMaskedPhone() : "登录后可查看订单与收藏");
        tvLoginState.setText(login ? "修改资料" : "登录");
        tvOrderCount.setText(String.valueOf(new OrderDao(this).count()));
        tvFavoriteCount.setText(String.valueOf(new FavoriteDao(this).count()));
    }

    /**
     * 退出登录：清除登录状态并回到登录页
     */
    private void confirmLogout() {
        if (!userPrefs.isLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_FORCE_SHOW, true);
            startActivity(intent);
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("退出", (dialog, which) -> {
                    userPrefs.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
