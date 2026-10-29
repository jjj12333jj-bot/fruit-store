package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 用户信息与登录状态（知识点：SharedPreferences 保存登录状态、用户信息）。
 * 登录状态、昵称、手机号保存在本地，退出 App 后再次进入仍然是已登录状态。
 */
public class UserPrefs {

    private static final String SP_NAME = "fruit_mall_user";
    private static final String KEY_LOGIN = "is_login";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_PHONE = "phone";

    private final SharedPreferences sp;

    public UserPrefs(Context context) {
        this.sp = context.getApplicationContext()
                .getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLogin() {
        return sp.getBoolean(KEY_LOGIN, false);
    }

    /** 登录：保存登录状态与用户信息 */
    public void login(String nickname, String phone) {
        sp.edit()
                .putBoolean(KEY_LOGIN, true)
                .putString(KEY_NICKNAME, nickname)
                .putString(KEY_PHONE, phone)
                .apply();
    }

    /** 退出登录：只清除登录状态，保留已保存的昵称方便下次登录 */
    public void logout() {
        sp.edit().putBoolean(KEY_LOGIN, false).apply();
    }

    public String getNickname() {
        String name = sp.getString(KEY_NICKNAME, "");
        return TextUtils.isEmpty(name) ? "水果会员" : name;
    }

    public String getPhone() {
        String phone = sp.getString(KEY_PHONE, "");
        return TextUtils.isEmpty(phone) ? "未绑定手机号" : phone;
    }
}
