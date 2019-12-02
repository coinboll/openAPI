康波交易平台官方API文档
==================================================
[康波]()交易平台开发者文档([English Docs](./REST_API_EN.md))。
<!-- TOC -->
- [介绍](#介绍)
- [开始使用](#开始使用)
- [API接口加密验证](#api接口加密验证)
    - [生成API Key](#生成api-key)
    - [发起请求](#发起请求)
    - [签名](#签名)
    - [选择时间戳](#选择时间戳)
    - [请求交互](#请求交互)
        - [请求](#请求)
    - [标准规范](#标准规范)
        - [时间戳](#时间戳)
        - [例子](#例子)
        - [数字](#数字)
        - [限流](#限流)
                - [REST API](#rest-api)
- [现货(Spot)业务API参考](#现货spot业务api参考)
    - [币币行情API](#币币行情api)
        - [1. 获取所有币对列表](#1-获取所有币对列表)
        - [2. 获取币对交易深度列表](#2-获取币对交易深度列表)
        - [3. 获取币对Ticker](#3-获取币对ticker)
        - [4. 获取币对历史成交记录](#4-获取币对历史成交记录)
        - [5. 获取K线数据](#5-获取k线数据)
        - [6. 获取服务器时间](#6-获取服务器时间)
		- [7. 获取所有币对Ticker](#7-获取所有币对Ticker)
    - [币币账户API](#币币账户api)
        - [1. 获取账户信息](#1-获取账户信息)
        - [2. 交易委托](#2-交易委托)
        - [3. 撤销所有委托](#3-撤销所有委托)
        - [4. 按订单撤销委托](#4-按订单撤销委托)
        - [5. 查询所有订单](#5-查询所有订单)
        - [6. 通过id获取订单](#6-通过id获取订单)
        - [7. 获取账单](#7-获取账单)
<!-- /TOC -->
# 介绍
欢迎使用[康波]()开发者文档。
本文档提供了康波交易平台的币币交易(Spot)业务的行情查询、交易、账户管理等API使用方法的介绍。
行情API是公开接口，提供币币交易市场的行情数据；交易和账户API需要身份验证，提供下单、撤单、查询订单和帐户信息等功能。
# 开始使用    
REST，即Representational State Transfer的缩写，是一种流行的互联网传输架构。它具有结构清晰、符合标准、易于理解、扩展方便的，正得到越来越多网站的采用。其优点如下：
+ 在RESTful架构中，每一个URL代表一种资源；
+ 客户端和服务器之间，传递这种资源的某种表现层；
+ 客户端通过四个HTTP指令，对服务器端资源进行操作，实现“表现层状态转化”。
建议开发者使用REST API进行行情查询、币币交易和账户管理等操作。
# API接口加密验证
## 生成API KEY
在对任何请求进行签名之前，您必须通过 kangbo网站【用户中心】-【API】创建一个API KEY。 创建API KEY后，您将获得3个必须记住的信息：
* API Key
* Secret Key
* Passphrase
API Key和Secret Key将随机生成，Passphrase由用户自己设定。
## 发起请求
所有REST请求都必须包含以下标题：
* ACCESS-KEY API Key作为一个字符串
* ACCESS-SIGN 使用base64编码签名（请参阅签名消息）
* ACCESS-TIMESTAMP 作为您的请求的时间戳
* ACCESS-PASSPHRASE 您在创建API KEY时设置的Passphrase
* 所有请求都应该含有application/json类型内容，并且是有效的JSON
## 签名
ACCESS-SIGN的请求头是对 **timestamp + method + requestPath + "?" + queryString + body** 字符串(+表示字符串连接)使用**HMAC SHA256**方法加密，通过**BASE64**编码输出而得到的。其中，timestamp的值与ACCESS-TIMESTAMP请求头相同。
* method是请求方法(POST/GET/PUT/DELETE)，字母全部大写
* requestPath是请求接口路径
* queryString是GET请求中的查询字符串
* body是指请求主体的字符串，如果请求没有主体(通常为GET请求)，则body可省略
**例如：对于如下的请求参数进行签名**
* 获取深度信息，以LTC_BTC为例
```java
Timestamp = 1540286290170 
Method = "GET"
requestPath = "/openapi/exchange/public/LTC_BTC/orderBook"
queryString= "?size=100"
```
生成待签名的字符串
```java
Message = '1540286290170GET/openapi/exchange/public/LTC_BTC/orderBook?size=100'  
```
* 下单，以LTC_BTC为例
```java
Timestamp = 1540286476248 
Method = "POST"
requestPath = "/openapi/exchange/LTC_BTC/orders"
body = {"price":"1","side":"buy","source":"web","systemOrderType":"limit","volume":"1"}
```
生成待签名的字符串
```java
Message = '1540286476248POST/openapi/exchange/LTC_BTC/orders{"price":"1","side":"buy","source":"web","systemOrderType":"limit","volume":"1"}'  
```
然后，将待签名字符串添加私钥参数生成最终待签名字符串
```java
hmac = hmac(secretkey, Message, SHA256)
```
在使用前需要对于hmac进行base64编码
```java
Signature = base64.encode(hmac.digest())
```
代码示例:[Java](https://github.com/coinboll/openAPI/tree/master/java-demo)
## 请求交互  
REST访问的根URL：`https://www.kangbo.io`
### 请求
所有请求基于Https协议，请求头信息中Content-Type需要统一设置为:'application/json’。
**请求交互说明**
1、请求参数：根据接口请求参数规定进行参数封装。
2、提交请求参数：将封装好的请求参数通过POST/GET/DELETE等方式提交至服务器。
3、服务器响应：服务器首先对用户请求数据进行参数安全校验，通过校验后根据业务逻辑将响应数据以JSON格式返回给用户。
4、数据处理：对服务器响应数据进行处理。
**成功**
HTTP状态码200表示成功响应，并可能包含内容。如果响应含有内容，则将显示在相应的返回内容里面。
**常见错误码**
* 400 Bad Request – Invalid request forma 请求格式无效
* 401 Unauthorized – Invalid API Key 无效的API Key
* 403 Forbidden – You do not have access to the requested resource 请求无权限
* 404 Not Found – 没有找到请求
* 429 Too Many Requests – 请求太频繁被系统限流
* 500 Internal Server Error – We had a problem with our server 服务器内部错误
如果失败，Response body带有错误描述信息
## 标准规范
### 时间戳
除非另外指定，API中的所有时间戳均以微秒为单位返回。
请求签名中的ACCESS-TIMESTAMP的单位是秒，允许用小数表示更精确的时间。请求的时间戳必须在API服务时间的30秒内，否则请求将被视为过期并被拒绝。如果本地服务器时间和API服务器时间之间存在较大的偏差，那么我们建议您使用通过查询API服务器时间来更新Http Header。
### 例子
`1524801032573`
### 数字
为了保持跨平台时精度的完整性，十进制数字作为字符串返回。建议您在发起请求时也将数字转换为字符串以避免截断和精度错误。 
整数（如交易编号和顺序）不加引号。
### 限流
如果请求过于频繁系统将自动限制请求，并在Http Header中返回429 too many requests状态码。
##### REST API
* 公共接口：我们通过IP限制公共接口的调用：每2秒最多6个请求。
* 私人接口：我们通过用户ID限制私人接口的调用：每2秒最多6个请求。
* 某些接口的特殊限制在具体的接口上注明
# 币币交易(Spot)API参考
## 币币行情API
### 1. 获取所有币对列表
**请求**
```http
    # Request
    GET /openapi/exchange/public/currencies
```
**响应**
```javascript
    # Response
    [{
    	"baseIncrement": 0,
    	"baseSymbol": "BTC",
    	"makerFeesRate": "0",
    	"maxPrice": 4,
    	"maxVolume": 4,
    	"minTrade": 0.00001000,
    	"online": 0,
    	"pairCode": "BTC_USDT",
    	"quoteIncrement": 0,
    	"quotePrecision": 0,
    	"quoteSymbol": "USDT",
    	"sort": 1,
    	"tickerFeesRate": "0"
    }, {
    	"baseIncrement": 0,
    	"baseSymbol": "ETH",
    	"makerFeesRate": "0",
    	"maxPrice": 4,
    	"maxVolume": 4,
    	"minTrade": 0.01000000,
    	"online": 0,
    	"pairCode": "ETH_USDT",
    	"quoteIncrement": 0,
    	"quotePrecision": 0,
    	"quoteSymbol": "USDT",
    	"sort": 2,
    	"tickerFeesRate": "0"
    },...]
```
**返回值说明**  

|返回字段 | 字段说明|
| ----------|:-------:|
| baseIncrement | 交易数量最小交易变动单位 |
| baseSymbol    | 交易货币 |
| makerFeesRate | maker 费率 |
| maxPrice  | 交易价格小数位数 |
| maxVolume | 交易数量小数位数 |
| minTrade | 最小委托量 |
| online | 是否上线 |
| pairCode | 是Base和quote之间的组合 BTC_USD |
| quoteIncrement | 最小交易单位 |
| quotePrecision | 计价货币数量单位精度 |
| quoteSymbol | 计价货币 |
| sort | 排序值 |
| tickerFeesRate | ticker 费率 |

### 2. 获取币对交易深度列表
**请求**
```http
    # Request
    GET /openapi/exchange/public/{pairCode}/orderBook
```
**响应**
```javascript
    # Response
    {
        "asks":[
            [
                "10463.3399",
                "0.0025"
            ],
            ...
        ],
        "bids":[
            [
                "7300.2456",
                "0.0022"
            ],
            ...
        ]
    }
```
**返回值说明**  

|返回字段|字段说明|
|--------| :-------: |
|asks| 卖方深度 |
|bids| 买方深度 |

**请求参数**  

| 参数名 | 参数类型  | 必填 | 描述 |
| ------------- |----|----|----|
| pairCode | String | 是 | 币对，如LTC_BTC |

### 3. 获取币对Ticker
最新成交价、买一价、卖一价、24h最高价、24h最低价、24h开盘价和24h成交量的快照信息。
**请求**
```http
    # Request
    GET /openapi/exchange/public/{pairCode}/ticker
```
**响应**
```javascript
    # Response
    {
    	"buy": "9512.70000000",
    	"change24": "19.60000000",
    	"changePercentage": "",
    	"changeRate24": "0.0020000000000000",
    	"close": "",
    	"createOn": 1564404929000,
    	"high": "9729.50000000",
    	"high24": "9729.50000000",
    	"last": "9525.20000000",
    	"low": "9171.80000000",
    	"low24": "9171.80000000",
    	"open": "9505.60000000",
    	"pairCode": "BTC_USDT",
    	"quoteVolume": "39140860.67498000",
    	"sell": "9515.60000000",
    	"volume": "4101.34040000"
    }
```
**返回值说明**
 
|返回字段|字段说明|
|--------| :-------: |
|buy| 最新买入价 |
|change24| 24小时变化值 |
|changePercentage| 变化百分比 |
|changeRate24| 24小时涨跌比例 |
|close| 24小时 close |
|createOn| 创建时间 |
|high| 最高成交价 |
|high24| 24小时最高成交价 |
|last| 最新成交价 |
|low| 最低成交价 |
|low24| 24小时最低成交价 |
|open| 24小时 open |
|pairCode| 币对信息 |
|quoteVolume| 计价币的成交量 |
|sell| 最新卖出价 |
|volume| 基准币的成交量 |
    
**请求参数**

|参数名|参数类型|必填|描述|
|------|----|:---:|:---:|
|pairCode|String|是|币对，如ETH_BTC|
    
### 4. 获取币对历史成交记录，支持分页查询
获取所请求交易对的历史成交信息，该请求支持分页。
**请求**
```http
    # Request
    GET /openapi/exchange/{pairCode}/fulfillment
```
**响应**
```javascript
    # Response
    [
        [
            	"id": 1524801032573,
				"pairCode": "BTC_USDT",
				"userId": 1001,		
				"brokerId": 10000,		
				"side": "buy",
				"entrustPrice": "1",
				"amount": "1",
				"dealAmount": "1",
				"quoteAmount": "1",
				"dealQuoteAmount": "1",
				"systemOrderType": "limit",
				"status": 0,
				"sourceInfo": "web",
				"createOn": 1524801032573,
				"updateOn": 1524801032573,
				"symbol": "BTC",
				"trunOver": "1",
				"notStrike": "0",
				"averagePrice": "1",
				"openAmount": "1"
        ],
        ...
    ]
```
**返回值说明**

|返回字段|字段说明|
|--------|----|
| id |订单id|
| pairCode |是Base和quote之间的组合 BTC_USD|
| userId |用户id|
| brokerId |券商id|
| side |方向 买、卖|
| entrustPrice |下单价格|
| amount |下单数量|
| dealAmount |成交数量|
| quoteAmount |基准币数量  只有在市价买的情况下会用到|
| dealQuoteAmount |基准币已成交数量|
| systemOrderType |10:限价 11:市价|
| status |0:未成交 1:部分成交 2:完全成交 3:撤单中 -1:已撤单|
| sourceInfo |下单来源 web,api,Ios,android|
| createOn |创建时间|
| updateOn |修改时间|
| symbol |币种|
| trunOver |成交金额  dealQuoteAmount * dealAmount|
| notStrike |尚未成交的数量|
| averagePrice |平均成交价|
| openAmount |下单数量|

**请求参数**

|参数名|参数类型|必填|描述|
|-----|:---:|----|----|
|pairCode|String|是|币对，如LTC_BTC|
|startDate|Long|否|开始时间，如1524801032573|
|endDate|Long|否|结束时间，如1524801032573|
|systemOrderType|Integer|否|10:限价 11:市价|
|price|BigDecimal|否|价格|
|amount|BigDecimal|否|数量|
|source|String|否|币对，如LTC_BTCweb,api,Ios,android|
|isHistory|Boolean|否|是否查历史数据、一周前成交的数据是历史数据、默认false|
|page|Integer|否|第几页|
|pageSize|Integer|否|每页条数|

**解释说明**
+ 交易方向 side 表示每一笔成交订单中 maker 下单方向,maker 是指将订单挂在订单深度列表上的交易用户，即被动成交方。
+ buy 代表行情下跌，因为 maker 是买单，maker 的买单被成交，所以价格下跌；相反的情况下，sell代表行情上涨，因为此时maker是卖单，卖单被成交，表示上涨。

### 5. 获取K线数据
**请求**
```http
    # Request
    GET  /openapi/exchange/public/{pairCode}/candles?interval=1min&start=start_time&end=end_time
```
**响应**
    
```javascript
    # Response
    {
        [ 1415398768000, 0.32, 0.42, 0.36, 0.41, 12.3 ]
        ...
    }
```
**返回值说明（按顺序）**  
    
|返回字段|字段说明|
|-----|----|
|1415398768000|K线开始时间戳,单位ms|
|0.32|最低价|
|0.42|最高价|
|0.36|开盘价（第一笔交易）|
|0.41|收盘价（最后一笔交易）|
|12.3|交易量（按交易币统计）|
**请求参数**
    
|参数名|参数类型|必填|描述|
|-----|----|----|----|
|pairCode|String|是|币对如btc_usdt|
|interval|String|是|K线周期类型如1min/1hour/day/week/month|
|start|String|否|开始时间（unix时间戳,单位ms）|
|end|String|否|结束时间（unix时间戳,单位ms）|

### 6. 获取服务器时间
获取API服务器的时间的接口。
**请求**
```http
    # Request
    
    GET /openapi/exchange/public/time
```
**响应**
    
```javascript
    # Reponse
{
        "epoch": "1524801032.573"
        "iso": "2015-01-07T23:47:25.201Z",
        "timestamp": 1524801032573
    }
```
    
**返回值说明**
    
|返回字段|字段说明|
|-----|----|
|epoch|以秒为时间戳形式表达的服务器时间|
|iso|为ISO 8061标准的时间字符串表达的服务器时间|
|timestamp|以毫秒为时间戳形式表达的服务器时间|

### 7. 获取所有币对Ticker
**请求**
```http
	# Request
	GET /openapi/exchange/public/currenciesTicker
```
**响应**
```javascript
[
    {
        "currencyPairDTO":{
            "baseIncrement":0,
            "baseSymbol":"BTC",
            "id":0,
            "lastPrice":0,
            "makerFeesRate":"-0.001",
            "maxPrice":4,
            "maxVolume":4,
            "minTrade":0.000001,
            "online":0,
            "pairCode":"BTC_USDT",
            "quoteIncrement":0,
            "quotePrecision":0,
            "quoteSymbol":"USDT",
            "sort":1,
            "tickerFeesRate":"-0.001"
        },
        "tickerDTO":{
            "buy":"7971.1087",
            "change24":"0E-8",
            "changePercentage":"",
            "changeRate24":"0.0000",
            "close":"",
            "createOn":1571631472964,
            "high":"8010.0039000000000000",
            "high24":"8010.0039000000000000",
            "last":"8010.0039",
            "low":"8010.0039000000000000",
            "low24":"8010.0039000000000000",
            "open":"8010.00390000",
            "pairCode":"BTC_USDT",
            "quoteVolume":"0E-16",
            "sell":"8011.0039",
            "volume":"0"
        }
    },
    {
        "currencyPairDTO":{
            "baseIncrement":0,
            "baseSymbol":"ETH",
            "id":0,
            "lastPrice":0,
            "makerFeesRate":"-0.001",
            "maxPrice":4,
            "maxVolume":4,
            "minTrade":0.001,
            "online":0,
            "pairCode":"ETH_USDT",
            "quoteIncrement":0,
            "quotePrecision":0,
            "quoteSymbol":"USDT",
            "sort":2,
            "tickerFeesRate":"-0.001"
        },
        "tickerDTO":{
            "buy":"173.68390000",
            "change24":"0E-8",
            "changePercentage":"",
            "changeRate24":"0E-16",
            "close":"",
            "createOn":1571631456000,
            "high":"173.86230000",
            "high24":"173.86230000",
            "last":"173.86230000",
            "low":"173.86230000",
            "low24":"173.86230000",
            "open":"173.86230000",
            "pairCode":"ETH_USDT",
            "quoteVolume":"0E-8",
            "sell":"174.04560000",
            "volume":"0E-8"
        }
    },...
]
```
**返回值说明**  

|返回字段 | 字段说明|
| ----------|:-------:|
|currencyPairDTO|见[获取所有币对列表](#1-获取所有币对列表)-返回值说明|
|tickerDTO|见[获取币对Ticker](#3-获取币对Ticker)-返回值说明|

## 币币账户API
### 1. 获取账户信息
获取币币交易账户余额列表，查询各币种的余额，冻结和可用情况。
**请求**
```
    # Request
    GET /openapi/exchange/assets
```
**响应**
```
    # Response
    [
        {
            "brokerId":10000,
            "symbol":"BTC",
            "available":"1",
            "hold":"0",
            "baseBTC":1,
            "withdrawLimit":"1",
        },
        ...
    ]
```
**返回值说明**

|返回字段|字段说明|
|----|----|
|brokerId|券商id|
|symbol|币种|
|available|余额|
|hold|冻结|
|baseBTC|折合BTC|
|withdrawLimit|提币限额|

### 2. 交易委托
提供限价和市价两种订单类型。
**请求**
```
    # Request
    POST /openapi/exchange/{pairCode}/orders
```
**响应**
```javascript
    # Response
    10000
```   
**返回值说明**
订单id

**请求参数**

|参数名| 参数类型 |必填|描述|
|:----:|:----:|:---:|----|
|pairCode|String|是|币对，如BTC_USDT|
|side|String|是|买入为buy，卖出为sell|
|systemOrderType|String|是|限价委托为limit，市价委托为market|
|volume|String|否|限价委托以及市价卖出时传递，代表交易币的数量|
|price|String|否|限价委托时传递，代表交易价格|
|quoteVolume|String|否|市价买入时传递，代表计价币的数量|

### 3. 撤销所有委托
撤销目标币对下所有未成交委托，最多撤销50条。由于是异步撤单，所以该接口没有返回值。
**请求**
```
    # Request
    DELETE /openapi/exchange/{pairCode}/cancel-all
```
**响应**
```javascript
    # Response
    { ...}
```
**请求参数**

|参数名|参数类型|必填|描述|
|----|----| ----| ----|
|pairCode|String|是|币对， 如BTC_USDT|
目前批量撤销200个挂单

### 4. 按订单撤销委托
按照订单id撤销指定订单。由于是异步撤单，所以该接口没有返回值。
**请求**
```http
    # Request
    DELETE /openapi/exchange/{pairCode}/orders/{id}
```
**响应**
```javascript
    # Response
```
**请求参数**

|参数名|参数类型|必填|描述|
|---|----|----|----|
|pairCode|String|是|币对，如BTC_USDT|
|id|Long|是|需要撤销的未成交委托的id|

### 4. 按订单批量撤销委托
按照订单id列表撤销订单。由于是异步撤单，所以该接口没有返回值。
**请求**
```http
    # Request
    DELETE /openapi/exchange/{pairCode}/orders
```
**响应**
```javascript
    # Response
    空
```
**请求参数**

|参数名|参数类型|必填|描述|
|---|----|----|----|
|pairCode|String|是|币对，如BTC_USDT|

**Request body**
```json
    #需要撤销的未成交委托的所有id
    [1,2,3]
```

### 6. 查询所有订单
    
**请求**
```http   
    # Request
    GET /openapi/exchange/orders
```
**响应**
```javascript
    # Response
    [
        {
            	"id": 1524801032573,
				"pairCode": "BTC_USDT",
				"userId": 1001,		
				"brokerId": 10000,		
				"side": "buy",
				"entrustPrice": "1",
				"amount": "1",
				"dealAmount": "1",
				"quoteAmount": "1",
				"dealQuoteAmount": "1",
				"systemOrderType": "limit",
				"status": 0,
				"sourceInfo": "web",
				"createOn": 1524801032573,
				"updateOn": 1524801032573,
				"symbol": "BTC",
				"trunOver": "1",
				"notStrike": "0",
				"averagePrice": "1",
				"openAmount": "1"
        },
        ...
    ]
```
**返回值说明**

|返回字段|字段说明|
|--------|----|
| id |订单id|
| pairCode |是Base和quote之间的组合 BTC_USD|
| userId |用户id|
| brokerId |券商id|
| side |方向 买、卖|
| entrustPrice |下单价格|
| amount |下单数量|
| dealAmount |成交数量|
| quoteAmount |基准币数量  只有在市价买的情况下会用到|
| dealQuoteAmount |基准币已成交数量|
| systemOrderType |10:限价 11:市价|
| status |0:未成交 1:部分成交 2:完全成交 3:撤单中 -1:已撤单|
| sourceInfo |下单来源 web,api,Ios,android|
| createOn |创建时间|
| updateOn |修改时间|
| symbol |币种|
| trunOver |成交金额  dealQuoteAmount * dealAmount|
| notStrike |尚未成交的数量|
| averagePrice |平均成交价|
| openAmount |下单数量|

**请求参数**

|参数名 | 参数类型 | 必填 | 描述 |
|---|----|----|----|
|pairCode|String|否|币对，如BTC_USDT|
|startDate|Long|否|开始时间 毫秒|
|endDate|Long|否|结束时间 毫秒|
|price|BigDecimal|否|下单价格|
|amount|BigDecimal|否|下单数量|
|systemOrderType|Integer|否|10:限价 11:市价|
|source|String|否|币对，如LTC_BTCweb,api,Ios,android|
|page|Integer|否|第几页|
|pageSize|Integer|否|每页条数|

### 7. 通过id获取订单

**请求**
```http   
    # Request
    GET /openapi/exchange/{pairCode}/orders/{id}
```
**响应**
```javascript
    # Response
    {
    	"id": 1524801032573,
    	"pairCode": "BTC_USDT",
    	"userId": 1001,
    	"brokerId": 10000,
    	"side": "buy",
    	"entrustPrice": "1",
    	"amount": "1",
    	"dealAmount": "1",
    	"quoteAmount": "1",
    	"dealQuoteAmount": "1",
    	"systemOrderType": "limit",
    	"status": 0,
    	"sourceInfo": "web",
    	"createOn": 1524801032573,
    	"updateOn": 1524801032573,
    	"symbol": "BTC",
    	"trunOver": "1",
    	"notStrike": "0",
    	"averagePrice": "1",
    	"openAmount": "1"
    }
```
**返回值说明**

|返回字段|字段说明|
|--------|----|
| id |订单id|
| pairCode |是Base和quote之间的组合 BTC_USD|
| userId |用户id|
| brokerId |券商id|
| side |方向 买、卖|
| entrustPrice |下单价格|
| amount |下单数量|
| dealAmount |成交数量|
| quoteAmount |基准币数量  只有在市价买的情况下会用到|
| dealQuoteAmount |基准币已成交数量|
| systemOrderType |10:限价 11:市价|
| status |0:未成交 1:部分成交 2:完全成交 3:撤单中 -1:已撤单|
| sourceInfo |下单来源 web,api,Ios,android|
| createOn |创建时间|
| updateOn |修改时间|
| symbol |币种|
| trunOver |成交金额  dealQuoteAmount * dealAmount|
| notStrike |尚未成交的数量|
| averagePrice |平均成交价|
| openAmount |下单数量|

**请求参数**

|参数名 | 参数类型 | 必填 | 描述 |
|---|----|----|----|
|pairCode|String|是|币对，如BTC_USDT|
|id|Long|是|订单id|

### 8. 获取账单

**请求**
```http
    # Request
    GET /openapi/exchange/bills
```
**响应**
```javascript
    # Response
    {
    	"code": 0,
    	"data": {
    		"bills": [{
    				"amount": 1.6,
    				"assets": "51.44",
    				"brokerId": 0,
    				"createOn": 1552636850000,
    				"fee": -0.16,
    				"id": 0,
    				"makerTaker": 0,
    				"referId": 0,
    				"symbol": "",
    				"tradeNo": "",
    				"type": 7,
    				"updateOn": 0,
    				"userId": 0
    			},
    			...
    		],
    		"paginate": {
    			"page": 1,
    			"pageSize": 10,
    			"total": 0
    		}
    	},
    	"msg": "success"
    }
```
**返回值说明**

|返回字段 | 字段说明 |
|----|----|
|amount|变动数量|
|balance|变动后余额|
|createdDate|账单时间戳|
|details|详情|
|orderId|对应订单ID|
|code|订单对应币对，如BTC_USDT|
|id|账单ID|
|type|交易类型|

**请求参数**  

|参数名|参数类型|必填|描述|
|----|---|---|---|
|currencyCode|String|是|币种，如BTC|
|limit|Integer|否|请求返回数据量，默认/最大值为100|
  
[kangbo]: https://www.kangbo.io 
[English Docs]: https://github.com/kangbo/openAPI/blob/master/README_EN.md
