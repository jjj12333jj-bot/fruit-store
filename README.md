# 水果商城（Fruit Store）

Android 期末项目，参照教材第 15 章「网上订餐」的架构实现，主题替换为水果商城。

## 功能

- **主页面**：水果清单浏览、加入购物车、购物车弹层（数量增减 / 清空确认）、水果详情弹窗、起送价判断
- **订单页面**：收货地址、订单明细、小计 / 配送费 / 合计、二维码扫码支付
- **技术要点**：异步线程请求数据 + Handler 回主线程更新 UI、JSON 解析、RecyclerView 列表、PopupWindow、AlertDialog、Serializable 页面传参

## 运行

1. 用 Android Studio 打开工程，等待 Gradle 同步完成
2. 直接运行 app（默认读取 `app/src/main/assets/shop_list_data.json`，无需联网）
3. 如需还原「访问服务器」的演示方式，把 `shop_list_data.json` 与图片放到 Tomcat 的 `webapps/order/` 目录，并修改 `util/Const.java` 中的 `SERVER_URL`

## 目录结构

```
app/src/main/
├── java/com/example/myapplication/
│   ├── MainActivity.java          主页面（水果选购 + 购物车）
│   ├── OrderActivity.java         订单页面（确认订单 + 支付）
│   ├── adapter/                   FruitAdapter、CartAdapter、OrderAdapter
│   ├── bean/                      Shop、Fruit 实体
│   └── util/                      DataProvider、JsonUtils、ImageUtils、QRCodeUtils、PriceUtils、Const
├── res/layout/                    各页面与列表项、弹窗布局
├── res/drawable*/                 形状资源与图片素材
└── assets/shop_list_data.json     水果数据（字段与教材一致）
```

## 开发流程

按功能拆分分支开发，每个功能一个 Pull Request 合入 `main`：

| PR | 功能 | 主要内容 |
|---|---|---|
| #1 | 初始化工程 | Gradle 配置、路由注册、实体与工具类、数据源、全局资源 |
| #2 | 完成主页面 | 主页面布局、水果列表、购物车组件、水果详情弹窗 |
| #3 | 完成订单页面 | 订单布局、订单列表、支付二维码弹窗 |
