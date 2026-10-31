package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 用户信息与登录状态（知识点：SharedPreferences 保存登录状态、用户信息）。
 * 登录状态、昵称、手机号保存在本地，退出 App 后再次进入仍然是已登录状态。
 * 另外记录一个「游客」标记：用户选择先逛逛时，不再反复跳转登录页。
 */
public class UserPrefs {

    private static final String SP_NAME = "fruit_mall_user";
    private static final String KEY_LOGIN = "is_login";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_GUEST = "is_guest";

    private final SharedPreferences sp;

    public UserPrefs(Context context) {
        this.sp = context.getApplicationContext()
                .getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLogin() {
        return sp.getBoolean(KEY_LOGIN, false);
    }

    /** 是否以游客身份进入过（用于判断是否需要再弹登录页） */
    public boolean isGuest() {
        return sp.getBoolean(KEY_GUEST, false);
    }

    /** 登录：保存登录状态与用户信息 */
    public void login(String nickname, String phone) {
        sp.edit()
                .putBoolean(KEY_LOGIN, true)
                .putBoolean(KEY_GUEST, false)
                .putString(KEY_NICKNAME, nickname)
                .putString(KEY_PHONE, phone)
                .apply();
    }

    /** 先逛逛：只记录游客状态，不保存登录信息 */
    public void passAsGuest() {
        sp.edit().putBoolean(KEY_GUEST, true).apply();
    }

    /** 退出登录：只清除登录状态，保留已保存的昵称方便下次登录 */
    public void logout() {
        sp.edit()
                .putBoolean(KEY_LOGIN, false)
                .putBoolean(KEY_GUEST, false)
                .apply();
    }

    public String getNickname() {
        String name = sp.getString(KEY_NICKNAME, "");
        return TextUtils.isEmpty(name) ? "水果会员" : name;
    }

    public String getPhone() {
        String phone = sp.getString(KEY_PHONE, "");
        return TextUtils.isEmpty(phone) ? "未绑定手机号" : phone;
    }

    /** 手机号脱敏显示：138****6688 */
    public String getMaskedPhone() {
        String phone = sp.getString(KEY_PHONE, "");
        if (phone.length() == 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return "未绑定手机号";
    }
}
