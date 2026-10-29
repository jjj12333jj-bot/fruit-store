package com.example.myapplication.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺实体类，对应服务器 JSON 数据中的一条店铺记录
 */
public class Shop implements Serializable {

    private int id;                 // 店铺 id
    private String shopName;        // 店铺名称
    private int saleNum;            // 月销量
    private int offerPrice;         // 起送价
    private int distributionCost;   // 配送费
    private String welfare;         // 优惠信息
    private String time;            // 配送时间
    private double score;           // 店铺评分
    private String distance;        // 距离
    private String shopPic;         // 店铺图片
    private String shopNotice;      // 店铺公告
    private List<Fruit> fruitList = new ArrayList<>();  // 店铺内的水果列表

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public int getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(int offerPrice) {
        this.offerPrice = offerPrice;
    }

    public int getDistributionCost() {
        return distributionCost;
    }

    public void setDistributionCost(int distributionCost) {
        this.distributionCost = distributionCost;
    }

    public String getWelfare() {
        return welfare;
    }

    public void setWelfare(String welfare) {
        this.welfare = welfare;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getShopPic() {
        return shopPic;
    }
    public void setShopPic(String shopPic) {
        this.shopPic = shopPic;
    }

    public String getShopNotice() {
        return shopNotice;
    }

    public void setShopNotice(String shopNotice) {
        this.shopNotice = shopNotice;
    }

    public List<Fruit> getFruitList() {
        return fruitList;
    }

    public void setFruitList(List<Fruit> fruitList) {
        this.fruitList = fruitList;
    }
}
