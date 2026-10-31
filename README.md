# 水果商城（Fruit Store）

Android 期末项目，以教材综合项目「网上订餐」为原型，主题改为水果商城。App 名称、图标、配色、商品数据与界面布局均为自定义。

## 页面结构

```
登录页（手机号 + 密码，可先逛逛）
  └─ 主页（店铺页面：店铺列表）——┐
        └─ 店铺详情页（店铺信息 + 水果清单 + 购物车）
              └─ 菜品详情页（图片 / 名称 / 价格 / 描述，加入购物车、收藏）
                    └─ 订单页面（确认订单 → 提交 → 扫码支付）

底部导航：主页 / 购物车 / 个人中心
  ├─ 购物车页（按店铺分组结算）
  └─ 个人中心（登录 / 修改资料 / 我的订单 / 我的收藏 / 退出登录）
搜索页、我的订单、我的收藏为二级页面
```

## 必做模块

| 模块 | 说明 |
|---|---|
| 店铺页面 | `MainActivity`：渐变标题栏、定位、搜索框、广告位、四个快捷入口、店铺列表（评分 / 月售 / 距离 / 起送价 / 配送费 / 优惠） |
| 店铺详情页 | `ShopDetailActivity`：店铺名称、公告、优惠、配送信息 + 水果清单（首个店铺 14 种，可滚动）、底部购物车栏、购物车弹层、清空确认、起送价判断 |
| 菜品详情页 | `FruitDetailActivity`：商品大图、名称、价格、所属店铺、月销量、产地与详细描述，支持加入购物车与收藏 |
| 订单页面 | `OrderActivity`：收货地址、订单明细、商品小计 / 配送费 / 合计，提交订单写入数据库并生成支付二维码 |

## 选做模块

| 模块 | 说明 |
|---|---|
| 个人中心 | `ProfileActivity`：用户信息展示、登录 / 修改资料（跳转登录页）、订单数与收藏数、退出登录 |
| 搜索 | `SearchActivity`：全站商品摊平后按水果名 / 产地 / 店铺名搜索，支持热门关键字与键盘搜索键 |
| 收藏 | `FruitDetailActivity` 收藏按钮 + `FavoritesActivity` 收藏列表（长按取消收藏） |

## 其他页面

| 页面 | 说明 |
|---|---|
| 登录页 | `LoginActivity`：手机号 + 密码 + 昵称，校验后把登录状态写入 SharedPreferences；可「先随便逛逛」以游客身份进入；已登录时启动自动跳过 |
| 购物车页 | `CartActivity`：底部导航页签，按店铺分组展示 SQLite 中的购物车，支持＋/－、清空、按店铺去结算 |
| 我的订单 | `OrderListActivity`：从 SQLite 读取订单与明细，展示店铺、商品摘要、金额与状态 |

## 技术要求对照

| 要求 | 实现位置 |
|---|---|
| Activity 组织页面 | 10 个 Activity，公共基类 `BaseActivity` 统一处理状态栏 / 导航栏适配 |
| RecyclerView 展示列表 | 店铺列表、菜品列表、购物车弹层、订单明细、搜索结果、收藏、订单历史共 7 处 |
| Intent 跳转与数据传递 | 店铺对象、菜品对象均通过 `Serializable` 传递；底部导航用 `FLAG_ACTIVITY_REORDER_TO_FRONT` 复用页面 |
| SharedPreferences | `util/UserPrefs.java` 保存登录状态、游客状态、昵称、手机号 |
| SQLite 保存购物车 / 订单 | `util/DBHelper.java` + `CartDao` / `FavoriteDao` / `OrderDao`（cart、favorite、orders、order_item 四张表） |
| 解析本地 / 远程 JSON | `util/DataProvider.java` 异步线程 + Handler，先请求 Tomcat，失败回退 `assets/shop_list_data.json`；`util/JsonUtils.java` 用 org.json 解析 |
| 异常处理与加载提示 | 首页加载中 / 空数据 / 失败点击重试；购物车空态提示；搜索页无结果提示；订单提交失败提示；图片加载异常忽略并保持占位 |

## 运行

1. Android Studio 打开工程，等待 Gradle 同步
2. 直接运行（默认读取内置 `assets/shop_list_data.json`，无需联网；需 JDK 25，可用 Android Studio 自带 JBR）
3. 如需还原「访问服务器」的演示方式：把 `shop_list_data.json` 放到 Tomcat 的 `webapps/order/` 目录，并修改 `util/Const.java` 中的 `SERVER_URL`（模拟器访问本机为 `10.0.2.2:8080`）

## 目录结构

```
app/src/main/
├── java/com/example/myapplication/
│   ├── LoginActivity.java          登录页（启动入口）
│   ├── BaseActivity.java           所有页面基类（系统栏适配）
│   ├── MainActivity.java           主页 / 店铺页面
│   ├── CartActivity.java           购物车页（底部导航页签）
│   ├── ShopDetailActivity.java     店铺详情页（含购物车）
│   ├── FruitDetailActivity.java    菜品详情页（含收藏）
│   ├── OrderActivity.java          订单页面（提交 + 支付）
│   ├── SearchActivity.java         搜索
│   ├── ProfileActivity.java        个人中心（底部导航页签）
│   ├── OrderListActivity.java      我的订单
│   ├── FavoritesActivity.java      我的收藏
│   ├── adapter/                    ShopAdapter、FruitAdapter、CartAdapter、OrderAdapter、SearchAdapter、OrderRecordAdapter
│   ├── bean/                       Shop、Fruit、Order
│   └── util/                       DataProvider、JsonUtils、DBHelper、CartDao、FavoriteDao、OrderDao、UserPrefs、ImageUtils、PriceUtils、QRCodeUtils、BottomNav、Const
├── res/layout/                     各页面、列表项、底部导航与弹窗布局
├── res/drawable*/                  形状资源与图片素材（均为网络真实照片）
└── assets/shop_list_data.json      3 家店铺、23 个水果的 JSON 数据
```

## 图片素材

全部来自 Wikimedia Commons 等可自由使用的图库实拍照片（水果 / 果蔬摊位），未使用 AI 生成图片。

## 提交记录

| 提交 | 内容 |
|---|---|
| 搭建多店铺数据与本地存储 | 多店铺 JSON、实体扩展、SQLite 四张表与 DAO、SharedPreferences 用户信息 |
| 完成店铺列表页与店铺详情页 | 首页店铺列表、店铺详情与购物车（落库） |
| 完成菜品详情页 | 图文详情、加入购物车、收藏 |
| 订单页支持提交订单 | 订单写入 SQLite、支付二维码 |
| 增加个人中心、搜索与收藏功能 | 个人中心、搜索、收藏列表、订单历史 |
| 增加底部导航与购物车页面 | 主页 / 购物车 / 个人中心三个页签、跨店铺购物车页、系统栏适配 |
| 增加登录页 | 手机号密码登录、游客模式、启动自动跳过、修改资料复用登录页 |
| 美化首页并扩充水果 | 渐变头部与搜索、广告位、快捷入口、卡片式店铺列表；首个店铺扩充至 14 种水果 |
