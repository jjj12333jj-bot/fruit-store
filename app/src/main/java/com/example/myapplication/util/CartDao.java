package com.example.myapplication.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.Fruit;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车数据访问类：购物车数据保存在 SQLite 中，切换页面或重启 App 都不会丢失。
 */
public class CartDao {

    private final DBHelper helper;

    public CartDao(Context context) {
        this.helper = DBHelper.getInstance(context);
    }

    /**
     * 增加 / 减少购物车中某个菜品的数量（delta 为 +1 或 -1）
     *
     * @return 修改后的数量
     */
    public int changeCount(int shopId, Fruit fruit, int delta) {
        int current = getCount(shopId, fruit.getFoodId());
        int target = Math.max(current + delta, 0);
        updateCount(shopId, fruit, target);
        return target;
    }

    /** 直接设置数量（数量为 0 时删除该条记录） */
    public void updateCount(int shopId, Fruit fruit, int count) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (count <= 0) {
            db.delete(DBHelper.TABLE_CART, "shop_id = ? AND food_id = ?",
                    new String[]{String.valueOf(shopId), fruit.getFoodId()});
            return;
        }
        ContentValues values = new ContentValues();
        values.put("shop_id", shopId);
        values.put("food_id", fruit.getFoodId());
        values.put("food_name", fruit.getFoodName());
        values.put("price", fruit.getPrice());
        values.put("pic", fruit.getFoodPic());
        values.put("count", count);
        // 冲突时直接替换（对应 UNIQUE(shop_id, food_id)）
        db.insertWithOnConflict(DBHelper.TABLE_CART, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /** 查询某个菜品在购物车中的数量 */
    public int getCount(int shopId, String foodId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CART, new String[]{"count"},
                "shop_id = ? AND food_id = ?",
                new String[]{String.valueOf(shopId), foodId},
                null, null, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    /**
     * 查询某家店铺的购物车内容，并回填到 Fruit 列表的 count 字段上
     */
    public void fillCartCount(int shopId, List<Fruit> fruits) {
        if (fruits == null) {
            return;
        }
        for (Fruit fruit : fruits) {
            fruit.setCount(getCount(shopId, fruit.getFoodId()));
        }
    }

    /** 购物车中该店铺的商品种类数 */
    public int getCartSize(int shopId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_CART
                        + " WHERE shop_id = ? AND count > 0",
                new String[]{String.valueOf(shopId)});
        int size = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                size = cursor.getInt(0);
            }
            cursor.close();
        }
        return size;
    }

    /** 某个店铺的全部购物车条目（用于订单页） */
    public List<Fruit> queryCart(int shopId) {
        List<Fruit> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CART,
                new String[]{"food_id", "food_name", "price", "pic", "count"},
                "shop_id = ? AND count > 0",
                new String[]{String.valueOf(shopId)},
                null, null, "_id ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Fruit fruit = new Fruit();
                fruit.setFoodId(cursor.getString(0));
                fruit.setFoodName(cursor.getString(1));
                fruit.setPrice(cursor.getDouble(2));
                fruit.setFoodPic(cursor.getString(3));
                fruit.setCount(cursor.getInt(4));
                fruit.setShopId(shopId);
                list.add(fruit);
            }
            cursor.close();
        }
        return list;
    }

    /** 清空某家店铺的购物车 */
    public void clearCart(int shopId) {
        helper.getWritableDatabase().delete(DBHelper.TABLE_CART, "shop_id = ?",
                new String[]{String.valueOf(shopId)});
    }

    /** 提交订单后清空 */
    public void clearAll() {
        helper.getWritableDatabase().delete(DBHelper.TABLE_CART, null, null);
    }
}
