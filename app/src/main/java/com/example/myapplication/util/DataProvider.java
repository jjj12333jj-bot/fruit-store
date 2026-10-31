package com.example.myapplication.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 数据提供类：使用异步线程访问网络获取 JSON 数据（知识点：异步线程 + HttpURLConnection），
 * 通过 Handler 把结果发送到主线程更新 UI。
 * 网络请求失败（如 Tomcat 未启动）时，自动回退读取内置的 assets 数据，保证程序可离线演示。
 */
public class DataProvider {

    /** 数据加载结果回调接口 */
    public interface OnShopListener {
        void onSuccess(List<com.example.myapplication.bean.Shop> shopList);

        void onError(String message);
    }

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    /** 店铺数据内存缓存：购物车页等页面直接复用，避免每次都等待网络超时 */
    private static volatile List<com.example.myapplication.bean.Shop> sCache;

    /** 获取已缓存的店铺数据（可能为 null，表示还没加载过） */
    public static List<com.example.myapplication.bean.Shop> getCachedShopList() {
        return sCache;
    }

    /**
     * 获取店铺列表数据（子线程请求，主线程回调）
     */
    public static void getShopList(Context context, final OnShopListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = null;
                // 1. 先尝试从 Tomcat 服务器获取
                try {
                    json = requestFromServer();
                } catch (Exception e) {
                    json = null;
                }
                // 2. 服务器不可用时，读取内置 assets 数据
                if (TextUtils.isEmpty(json)) {
                    try {
                        json = readFromAssets(context, Const.ASSET_DATA);
                    } catch (Exception e) {
                        json = null;
                    }
                }
                final String result = json;
                if (TextUtils.isEmpty(result)) {
                    MAIN_HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError("数据加载失败，请检查网络或 Tomcat 服务器");
                        }
                    });
                } else {
                    try {
                        final List<com.example.myapplication.bean.Shop> list =
                                JsonUtils.parseShopList(result);
                        // 写入缓存，购物车页等页面可以直接取用
                        sCache = list;
                        MAIN_HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(list);
                            }
                        });
                    } catch (final Exception e) {
                        MAIN_HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError("数据解析失败：" + e.getMessage());
                            }
                        });
                    }
                }
            }
        }).start();
    }

    /**
     * 通过 HttpURLConnection 请求服务器上的 JSON 数据
     */
    private static String requestFromServer() throws Exception {
        URL url = new URL(Const.SERVER_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("GET");
        conn.connect();
        if (conn.getResponseCode() != 200) {
            conn.disconnect();
            throw new IllegalStateException("HTTP " + conn.getResponseCode());
        }
        String json = readStream(conn.getInputStream());
        conn.disconnect();
        return json;
    }

    /**
     * 读取 assets 目录中的内置 JSON 数据
     */
    private static String readFromAssets(Context context, String fileName) throws Exception {
        InputStream is = context.getAssets().open(fileName);
        String json = readStream(is);
        is.close();
        return json;
    }

    private static String readStream(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
}
