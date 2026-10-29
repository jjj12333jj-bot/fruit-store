# 水果商城（Fruit Store）

Android 期末项目，以教材综合项目「网上订餐」为原型，主题改为水果商城。App 名称、图标、配色、商品数据与界面布局均为自定义。

## 页面结构

```
店铺页面（首页）
  └─ 店铺详情页（店铺信息 + 菜品列表 + 购物车）
        └─ 菜品详情页（图片 / 名称 / 价格 / 描述，加入购物车、收藏）
              └─ 订单页面（确认订单 → 提交 → 扫码支付）
搜索页 / 个人中心 / 我的订单 / 我的收藏
```

## 必做模块

| 模块 | 说明 |
|---|---|
| 店铺页面 | `MainActivity`：搜索入口、个人中心入口、广告位、店铺列表（评分 / 月售 / 距离 / 起送价 / 配送费 / 优惠） |
| 店铺详情页 | `ShopDetailActivity`：店铺名称、公告、优惠、配送信息 + 菜品列表、底部购物车栏、购物车弹层、清空确认、起送价判断 |
| 菜品详情页 | `FruitDetailActivity`：商品大图、名称、价格、所属店铺、月销量、产地与详细描述，支持加入购物车与收藏 |
| 订单页面 | `OrderActivity`：收货地址、订单明细、商品小计 / 配送费 / 合计，提交订单写入数据库并生成支付二维码 |

## 选做模块

| 模块 | 说明 |
|---|---|
| 个人中心 | `ProfileActivity`：用户信息展示、登录 / 修改资料（昵称 + 手机号校验）、订单数与收藏数、退出登录 |
| 搜索 | `SearchActivity`：全站商品摊平后按水果名 / 产地 / 店铺名搜索，支持热门关键字与键盘搜索键 |
| 收藏 | `FruitDetailActivity` 收藏按钮 + `FavoritesActivity` 收藏列表（长按取消收藏） |

另附「我的订单」（`OrderListActivity`）作为个人中心的下级页面。

## 技术要求对照

| 要求 | 实现位置 |
|---|---|
| Activity 组织页面 | 8 个 Activity，见 `app/src/main/java/com/example/myapplication/` |
| RecyclerView 展示列表 | 店铺列表、菜品列表、购物车、订单明细、搜索结果、收藏、订单历史共 7 处 |
| Intent 跳转与数据传递 | 店铺对象、菜品对象均通过 `Serializable` 传递；订单页接收店铺 id / 名称 / 配送费 |
| SharedPreferences | `util/UserPrefs.java` 保存登录状态、昵称、手机号 |
| SQLite 保存购物车 / 订单 | `util/DBHelper.java` + `CartDao` / `FavoriteDao` / `OrderDao`（cart、favorite、orders、order_item 四张表） |
| 解析本地 / 远程 JSON | `util/DataProvider.java` 异步线程 + Handler，先请求 Tomcat，失败回退 `assets/shop_list_data.json`；`util/JsonUtils.java` 用 org.json 解析 |
| 异常处理与加载提示 | 首页加载中 / 空数据 / 失败点击重试；搜索页加载提示与无结果提示；订单提交失败提示；图片加载异常忽略并保持占位 |

## 运行

1. Android Studio 打开工程，等待 Gradle 同步
2. 直接运行（默认读取内置 `assets/shop_list_data.json`，无需联网；需 JDK 25，可用 Android Studio 自带 JBR）
3. 如需还原「访问服务器」的演示方式：把 `shop_list_data.json` 放到 Tomcat 的 `webapps/order/` 目录，并修改 `util/Const.java` 中的 `SERVER_URL`（模拟器访问本机为 `10.0.2.2:8080`）

## 目录结构

```
app/src/main/
├── java/com/example/myapplication/
│   ├── MainActivity.java           店铺页面（首页）
│   ├── ShopDetailActivity.java     店铺详情页（含购物车）
│   ├── FruitDetailActivity.java    菜品详情页（含收藏）
│   ├── OrderActivity.java          订单页面（提交 + 支付）
│   ├── SearchActivity.java         搜索
│   ├── ProfileActivity.java        个人中心
│   ├── OrderListActivity.java      我的订单
│   ├── FavoritesActivity.java      我的收藏
│   ├── adapter/                    ShopAdapter、FruitAdapter、CartAdapter、OrderAdapter、SearchAdapter、OrderRecordAdapter
│   ├── bean/                       Shop、Fruit、Order
│   └── util/                       DataProvider、JsonUtils、DBHelper、CartDao、FavoriteDao、OrderDao、UserPrefs、ImageUtils、PriceUtils、QRCodeUtils、Const
├── res/layout/                     各页面、列表项与弹窗布局
├── res/drawable*/                  形状资源与图片素材（均为网络真实照片）
└── assets/shop_list_data.json      3 家店铺、15 个水果的 JSON 数据
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
