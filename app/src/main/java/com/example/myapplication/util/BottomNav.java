package com.example.myapplication.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.CartActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;

/**
 * 底部导航栏工具类（知识点：多个 Activity 通过 Intent 组织页面 + FLAG_ACTIVITY_REORDER_TO_FRONT 复用页面）。
 * 主页、购物车、个人中心三个页面都 include 了同一份 view_bottom_nav.xml，
 * 这里统一处理选中状态、点击切换与购物车角标，避免每个页面重复写一遍。
 */
public class BottomNav {

    /** 三个页签的标识 */
    public static final int TAB_HOME = 0;
    public static final int TAB_CART = 1;
    public static final int TAB_PROFILE = 2;

    private BottomNav() {
    }

    /**
     * 绑定底部导航栏
     *
     * @param activity 当前页面
     * @param current  当前选中的页签（TAB_HOME / TAB_CART / TAB_PROFILE）
     */
    public static void bind(final Activity activity, final int current) {
        View navHome = activity.findViewById(R.id.nav_home);
        View navCart = activity.findViewById(R.id.nav_cart);
        View navProfile = activity.findViewById(R.id.nav_profile);
        if (navHome == null || navCart == null || navProfile == null) {
            return;
        }

        // 1. 设置选中态（图标与文字跟随主题色）
        style(activity, navHome, R.id.nav_home_icon, R.id.nav_home_label, current == TAB_HOME);
        style(activity, navCart, R.id.nav_cart_icon, R.id.nav_cart_label, current == TAB_CART);
        style(activity, navProfile, R.id.nav_profile_icon, R.id.nav_profile_label,
                current == TAB_PROFILE);

        // 2. 点击切换页面
        navHome.setOnClickListener(v -> switchTo(activity, MainActivity.class, current, TAB_HOME));
        navCart.setOnClickListener(v -> switchTo(activity, CartActivity.class, current, TAB_CART));
        navProfile.setOnClickListener(v ->
                switchTo(activity, ProfileActivity.class, current, TAB_PROFILE));

        // 3. 刷新购物车角标（数量来自 SQLite）
        refreshCartBadge(activity);
    }

    /** 只刷新购物车角标，购物车内增减商品后可单独调用 */
    public static void refreshCartBadge(Activity activity) {
        TextView badge = activity.findViewById(R.id.tv_nav_cart_badge);
        if (badge == null) {
            return;
        }
        int count = new CartDao(activity).getTotalCount();
        if (count > 0) {
            badge.setVisibility(View.VISIBLE);
            badge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    private static void style(Activity activity, View item, int iconId, int labelId,
                              boolean selected) {
        int color = activity.getResources().getColor(
                selected ? R.color.brand : R.color.nav_unselected);
        ImageView icon = item.findViewById(iconId);
        TextView label = item.findViewById(labelId);
        if (icon != null) {
            icon.setColorFilter(color);
        }
        if (label != null) {
            label.setTextColor(color);
            label.getPaint().setFakeBoldText(selected);
        }
    }

    /**
     * 切换页签：优先复用已经打开的页面实例，避免页面越堆越多
     */
    private static void switchTo(Activity activity, Class<?> target, int current, int tab) {
        if (current == tab) {
            return;
        }
        Intent intent = new Intent(activity, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }
}
