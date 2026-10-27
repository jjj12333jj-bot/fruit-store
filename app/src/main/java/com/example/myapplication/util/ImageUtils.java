package com.example.myapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片加载工具类（知识点：异步线程加载网络图片 + Handler 回主线程更新）。
 * 1. foodPic 是本地资源名（如 "fruit_orange"）时，直接加载 drawable 中的图片；
 * 2. foodPic 是 http 开头的图片地址时，开启子线程下载，下载完成后回主线程设置到 ImageView。
 */
public class ImageUtils {

    /** 网络图片内存缓存，避免重复下载 */
    private static final Map<String, Bitmap> CACHE = new HashMap<>();

    public static void load(final ImageView imageView, final String pic) {
        if (imageView == null || TextUtils.isEmpty(pic)) {
            return;
        }
        // 本地 drawable 资源
        if (!pic.startsWith("http")) {
            int resId = imageView.getResources().getIdentifier(
                    pic, "drawable", imageView.getContext().getPackageName());
            if (resId != 0) {
                imageView.setImageResource(resId);
            }
            return;
        }
        // 网络图片
        Bitmap cached = CACHE.get(pic);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(pic);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                        conn.disconnect();
                        if (bitmap != null) {
                            CACHE.put(pic, bitmap);
                            imageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(CACHE.get(pic));
                                }
                            });
                        }
                    } else {
                        conn.disconnect();
                    }
                } catch (Exception ignored) {
                }
            }
        }).start();
    }
}
