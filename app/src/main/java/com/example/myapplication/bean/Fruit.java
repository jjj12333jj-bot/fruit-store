package com.example.myapplication.bean;

import java.io.Serializable;

/**
 * 水果实体类，对应 JSON 数据 foodList 节点中的一条水果记录
 */
public class Fruit implements Serializable {

    private String foodId;      // 水果 id
    private String foodName;    // 水果名称
    private String ingredients; // 产地/品种说明
    private int saleNum;        // 月销量
    private double price;       // 价格
    private int count;          // 购物车中已加的数量
    private String foodPic;     // 水果图片

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
