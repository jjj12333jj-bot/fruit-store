package com.example.myapplication.util;

/**
 * 全局常量类
 */
public class Const {

    /**
     * Tomcat 服务器上的数据地址（模拟器访问本机使用 10.0.2.2）。
     * 将 shop_list_data.json 和图片部署到 Tomcat 的 webapps/order 目录后即可联网加载；
     * 服务器未启动或请求失败时，程序会自动改用 app 内置的 assets/shop_list_data.json。
     */
    public static final String SERVER_URL = "http://10.0.2.2:8080/order/shop_list_data.json";

    /** assets 中内置的数据文件名（网络不可用时的备用数据） */
    public static final String ASSET_DATA = "shop_list_data.json";

    /** 默认收货地址 */
    public static final String DEFAULT_ADDRESS = "广东省广州市天河区五山路 381 号 悦华苑小区 6 栋 502 室";

    /** 默认收货人信息 */
    public static final String DEFAULT_RECEIVER = "张女士  138****6688";
}
