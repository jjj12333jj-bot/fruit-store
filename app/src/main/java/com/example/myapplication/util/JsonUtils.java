package com.example.myapplication.util;

import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Shop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON 解析工具类，把服务器（或 assets）返回的 JSON 字符串解析成实体类集合
 */
public class JsonUtils {

    /**
     * 解析店铺列表 JSON
     */
    public static List<Shop> parseShopList(String json) throws Exception {
        List<Shop> shopList = new ArrayList<>();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Shop shop = new Shop();
            shop.setId(obj.optInt("id"));
            shop.setShopName(obj.optString("shopName"));
            shop.setSaleNum(obj.optInt("saleNum"));
            shop.setOfferPrice(obj.optInt("offerPrice"));
            shop.setDistributionCost(obj.optInt("distributionCost"));
            shop.setWelfare(obj.optString("welfare"));
            shop.setTime(obj.optString("time"));
            shop.setShopPic(obj.optString("shopPic"));
            shop.setShopNotice(obj.optString("shopNotice"));
            // 解析该店铺中的水果列表
            JSONArray foods = obj.optJSONArray("foodList");
            if (foods != null) {
                List<Fruit> fruitList = new ArrayList<>();
                for (int j = 0; j < foods.length(); j++) {
                    JSONObject f = foods.getJSONObject(j);
                    Fruit fruit = new Fruit();
                    fruit.setFoodId(f.optString("foodId"));
                    fruit.setFoodName(f.optString("foodName"));
                    fruit.setIngredients(f.optString("ingredients"));
                    fruit.setSaleNum(f.optInt("saleNum"));
                    fruit.setPrice(f.optDouble("price"));
                    fruit.setCount(f.optInt("count"));
                    fruit.setFoodPic(f.optString("foodPic"));
                    fruitList.add(fruit);
                }
                shop.setFruitList(fruitList);
            }
            shopList.add(shop);
        }
        return shopList;
    }
}
