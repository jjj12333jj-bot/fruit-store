package com.example.myapplication.bean;

import java.io.Serializable;

/**
 * 水果实体类，对应 JSON 数据 foodList 节点中的一条水果记录
 */
public class Fruit implements Serializable {

    private String foodId;      // 水果 id
    private String foodName;    // 水果名称
    private String ingredients; // 产地/品种说明
    private String description; // 商品描述（详情页展示）
    private int saleNum;        // 月销量
    private double price;       // 价格
    private int count;          // 购物车中已加的数量
    private String foodPic;     // 水果图片
    private int shopId;         // 所属店铺 id
    private String shopName;    // 所属店铺名称

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFoodPic() {
        return foodPic;
    }

    public void setFoodPic(String foodPic) {
        this.foodPic = foodPic;
    }
}
