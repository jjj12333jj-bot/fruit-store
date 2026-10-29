package com.example.myapplication.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 订单数据访问类：提交订单时把订单主表 + 明细表一起写入 SQLite。
 */
public class OrderDao {

    private final DBHelper helper;

    public OrderDao(Context context) {
        this.helper = DBHelper.getInstance(context);
    }

    /**
     * 提交订单：插入订单主表与明细表，并清空该店铺的购物车
     *
     * @return 新订单的订单号，失败返回 -1
     */
    public long submitOrder(String shopName, double total, String address,
                            String receiver, List<Fruit> items) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put("shop_name", shopName);
            orderValues.put("total", total);
            orderValues.put("address", address);
            orderValues.put("receiver", receiver);
            orderValues.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm",
                    Locale.CHINA).format(new Date()));
            orderValues.put("status", "已支付");
            long orderId = db.insert(DBHelper.TABLE_ORDER, null, orderValues);
            if (orderId == -1) {
                return -1;
            }
            for (Fruit fruit : items) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("order_id", orderId);
                itemValues.put("food_name", fruit.getFoodName());
                itemValues.put("price", fruit.getPrice());
                itemValues.put("count", fruit.getCount());
                itemValues.put("pic", fruit.getFoodPic());
                db.insert(DBHelper.TABLE_ORDER_ITEM, null, itemValues);
            }
            // 下单成功后清空购物车中该店铺的商品
            db.delete(DBHelper.TABLE_CART, "shop_id = ?",
                    new String[]{String.valueOf(items.isEmpty() ? -1 : items.get(0).getShopId())});
            db.setTransactionSuccessful();
            return orderId;
        } catch (Exception e) {
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    /** 订单数量（个人中心展示） */
    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_ORDER, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    /** 查询全部订单，按下单时间倒序 */
    public List<Order> queryAll() {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_ORDER,
                new String[]{"_id", "shop_name", "total", "address", "receiver", "create_time", "status"},
                null, null, null, null, "_id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Order order = new Order();
                order.setId(cursor.getLong(0));
                order.setShopName(cursor.getString(1));
                order.setTotal(cursor.getDouble(2));
                order.setAddress(cursor.getString(3));
                order.setReceiver(cursor.getString(4));
                order.setCreateTime(cursor.getString(5));
                order.setStatus(cursor.getString(6));
                order.setItems(queryItems(db, order.getId()));
                list.add(order);
            }
            cursor.close();
        }
        return list;
    }

    private List<Fruit> queryItems(SQLiteDatabase db, long orderId) {
        List<Fruit> items = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TABLE_ORDER_ITEM,
                new String[]{"food_name", "price", "count", "pic"},
                "order_id = ?", new String[]{String.valueOf(orderId)},
                null, null, "_id ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Fruit fruit = new Fruit();
                fruit.setFoodName(cursor.getString(0));
                fruit.setPrice(cursor.getDouble(1));
                fruit.setCount(cursor.getInt(2));
                fruit.setFoodPic(cursor.getString(3));
                items.add(fruit);
            }
            cursor.close();
        }
        return items;
    }
}
