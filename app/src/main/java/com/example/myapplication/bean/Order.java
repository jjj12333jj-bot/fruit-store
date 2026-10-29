package com.example.myapplication.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类，对应 SQLite 中的一条订单记录
 */
public class Order implements Serializable {

    private long id;                // 订单号
    private String shopName;        // 店铺名称
    private double total;           // 订单总价
    private String address;         // 收货地址
    private String receiver;        // 收货人
    private String createTime;      // 下单时间
    private String status;          // 订单状态
    private List<Fruit> items = new ArrayList<>();   // 订单中的商品明细

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Fruit> getItems() {
        return items;
    }

    public void setItems(List<Fruit> items) {
        this.items = items;
    }

    /** 商品总件数 */
    public int getItemCount() {
        int count = 0;
        for (Fruit fruit : items) {
            count += fruit.getCount();
        }
        return count;
    }
}
