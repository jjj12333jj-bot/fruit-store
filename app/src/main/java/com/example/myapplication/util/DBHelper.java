package com.example.myapplication.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite 数据库帮助类（知识点：SQLite 本地存储）。
 * 共四张表：
 * cart       购物车（按店铺 + 商品区分，退出 App 后仍然保留）
 * favorite   收藏的菜品
 * orders     订单主表
 * order_item 订单明细表（一个订单对应多条商品）
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fruit_mall.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_CART = "cart";
    public static final String TABLE_FAVORITE = "favorite";
    public static final String TABLE_ORDER = "orders";
    public static final String TABLE_ORDER_ITEM = "order_item";

    private static DBHelper instance;

    private DBHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    /** 单例，避免多处重复打开数据库 */
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 购物车表：同一店铺同一菜品只有一条记录，用 UNIQUE 约束保证
        db.execSQL("CREATE TABLE " + TABLE_CART + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "shop_id INTEGER NOT NULL,"
                + "food_id TEXT NOT NULL,"
                + "food_name TEXT,"
                + "price REAL,"
                + "pic TEXT,"
                + "count INTEGER DEFAULT 0,"
                + "UNIQUE(shop_id, food_id))");

        // 收藏表
        db.execSQL("CREATE TABLE " + TABLE_FAVORITE + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "food_id TEXT UNIQUE,"
                + "food_name TEXT,"
                + "price REAL,"
                + "pic TEXT,"
                + "description TEXT,"
                + "shop_id INTEGER,"
                + "shop_name TEXT)");

        // 订单主表
        db.execSQL("CREATE TABLE " + TABLE_ORDER + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "shop_name TEXT,"
                + "total REAL,"
                + "address TEXT,"
                + "receiver TEXT,"
                + "create_time TEXT,"
                + "status TEXT)");

        // 订单明细表
        db.execSQL("CREATE TABLE " + TABLE_ORDER_ITEM + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "order_id INTEGER,"
                + "food_name TEXT,"
                + "price REAL,"
                + "count INTEGER,"
                + "pic TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEM);
        onCreate(db);
    }
}
