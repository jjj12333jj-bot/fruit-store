package com.example.myapplication.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.bean.Fruit;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏数据访问类：收藏记录保存在 SQLite 中。
 */
public class FavoriteDao {

    private final DBHelper helper;

    public FavoriteDao(Context context) {
        this.helper = DBHelper.getInstance(context);
    }

    /** 是否已收藏 */
    public boolean isFavorite(String foodId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_FAVORITE, new String[]{"_id"},
                "food_id = ?", new String[]{foodId}, null, null, null);
        boolean result = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    /**
     * 切换收藏状态
     *
     * @return true 表示收藏成功，false 表示取消收藏
     */
    public boolean toggle(Fruit fruit) {
        if (isFavorite(fruit.getFoodId())) {
            remove(fruit.getFoodId());
            return false;
        }
        add(fruit);
        return true;
    }

    public void add(Fruit fruit) {
        ContentValues values = new ContentValues();
        values.put("food_id", fruit.getFoodId());
        values.put("food_name", fruit.getFoodName());
        values.put("price", fruit.getPrice());
        values.put("pic", fruit.getFoodPic());
        values.put("description", fruit.getDescription());
        values.put("shop_id", fruit.getShopId());
        values.put("shop_name", fruit.getShopName());
        helper.getWritableDatabase().insertWithOnConflict(DBHelper.TABLE_FAVORITE,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void remove(String foodId) {
        helper.getWritableDatabase().delete(DBHelper.TABLE_FAVORITE,
                "food_id = ?", new String[]{foodId});
    }

    /** 收藏总数（个人中心展示） */
    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_FAVORITE, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    /** 收藏列表，按收藏时间倒序 */
    public List<Fruit> queryAll() {
        List<Fruit> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_FAVORITE,
                new String[]{"food_id", "food_name", "price", "pic", "description", "shop_id", "shop_name"},
                null, null, null, null, "_id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Fruit fruit = new Fruit();
                fruit.setFoodId(cursor.getString(0));
                fruit.setFoodName(cursor.getString(1));
                fruit.setPrice(cursor.getDouble(2));
                fruit.setFoodPic(cursor.getString(3));
                fruit.setDescription(cursor.getString(4));
                fruit.setShopId(cursor.getInt(5));
                fruit.setShopName(cursor.getString(6));
                list.add(fruit);
            }
            cursor.close();
        }
        return list;
    }
}
