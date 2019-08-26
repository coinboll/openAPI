Kangbo trading platform official API documentation
==================================================
[Kangbo]()Trading platform developer documentation([中文文档](./REST_API_CN.md))。

- [Introduction](#Introduction)
- [Start to use](#Start to use)
- [API interface encryption verification](#api interface encryption verification)
    - [Generate API Key](#Generate api-key)
    - [Propose request](#Propose request)
    - [Signature](#Signature)
    - [Select timestamp](#Select timestamp)
    - [Request interaction](#Request interaction)
        - [Request](#Request)
    - [Standard](#Standard)
        - [Timestamp](#Timestamp)
        - [Examples](#Examples)
        - [Digits](#Digits)
        - [Current limit](#Current limit)
                - [REST API](#rest-api)
- [Spot business API reference](#Spot business api reference)
    - [Cryptos market API](#Cryptos market api)
        - [1. Get the list of all pairs](#1-Get a list of all pairs)
        - [2. Get trading depth list of trading pairs](#2-Get trading depth list of trading pairs)
        - [3. Get pairs Ticker](#3-Get pairs ticker)
        - [4. Get trading history of trading pairs](#4-Get trading history of trading pairs)
        - [5. Get K-Line data](#5-Get K-Line data)
        - [6. Get server time](#6-Get server time)
    - [Crypto account API](#Crypto account api)
        - [1. Get account information](#1-Get account information)
        - [2. Orders](#2-Orders)
        - [3. Cancel all orders](#3-Cancel all orders)
        - [4. Cancel orders](#4-Cancel specific orders)
        - [5. Query all orders](#5-Query all orders])
        - [6. 通过id获取订单](#6-通过id获取订单)
        - [7. Get bills](#7-Get bills)
<!-- /TOC -->
# Introduction 
Welcome to use [Kangbo]()developer documentation.
This document provides an introduction of Kangbo crypto-crypto trading service API usage methods such as market inquiry, trading, and account management .
The market API is a public interface that provides market data of the crypto trading market; the trading and account API require identity authentication for functions such as order placing, order cancellation and account information query .
# Start to use   
REST，the abbreviation of Representational State Transfer，is a kind of popular  internet transfer architecture featured with clear and standard structure ,scalablity and easy operation .The advantages are as follows: 
+ In a RESTful architecture, each URL represents a kind of resource;
+ A layer of presentation of such resources between the client end and the server;
+ The client end operates on the server-side resources through four HTTP commands to realize  "presentation layer state conversion".
Developers are advised to use the REST API for market queries, crypto trading, and account management,etc.
# API interface encryption verification
## Generate API KEY
Before signing any request, you must generate  an API KEY via the kangbo website [User Center] - [API]. After generating the API KEY, you will get 3 pieces of information that you must remember:
* API Key
* Secret Key
* Passphrase
API Key and Secret Key will be generated randomly.Passphrase will be set by users.
## Propose request
All REST requests must contain the following title：
* ACCESS-KEY API Key as a string
* ACCESS-SIGN Use base64 encoding signature (refer to signature message)
* ACCESS-TIMESTAMP As the timestamp of your request
* ACCESS-PASSPHRASE Passphrase that you set when generating  the API KEY
* All requests should contain application/json type content and it should be valid JSON.
## Signature 
The request header of ACCESS-SIGN is gotten  by encrypting the string **timestamp + method + requestPath + "?" + queryString + body** (+ indicates string concatenation) with **HMAC SHA256** method and **BASE64** Coded output. Among them, the value of timestamp is the same as the ACCESS-TIMESTAMP request header.
* method refers to request method (POST/GET/PUT/DELETE)，all the letters have to be capitalized .
* requestPath refers to the request interface path
* queryString refers to the query string in the GET request
* Body is the string of the request body. If the request has no body (usually a GET request), the body can be omitted.
**For example: sign the following request parameters**
* Get depth information, taking LTC_BTC as an example
```java
Timestamp = 1540286290170 
Method = "GET"
requestPath = "/openapi/exchange/public/LTC_BTC/orderBook"
queryString= "?size=100"
```
Generate a string to be signed
```java
Message = '1540286290170GET/openapi/exchange/public/LTC_BTC/orderBook?size=100'  
```
* Place an order，taking LTC_BTC as an example
```java
Timestamp = 1540286476248 
Method = "POST"
requestPath = "/openapi/exchange/LTC_BTC/orders"
body = {"price":"1","side":"buy","source":"web","systemOrderType":"limit","volume":"1"}
```
Generate a string to be signed
```java
Message = '1540286476248POST/openapi/exchange/LTC_BTC/orders{"price":"1","side":"buy","source":"web","systemOrderType":"limit","volume":"1"}'  
```
Then, adding the private key parameter to the string to be signed so as to generate the final string to be signed
```java
hmac = hmac(secretkey, Message, SHA256)
```Base64 encoding for hmac before use
```java
Signature = base64.encode(hmac.digest())
```
## Request interaction  
Root URL for REST access：`https://www.kangbo.io`
### Request
All requests are based on the Https protocol, and the Content-Type in the request header information needs to be uniformly set to: 'application/json'.
**Request interaction description**
1、Request parameters: encapsulate parameter according to interface request parameters.
2、Submit request parameters: Submit the encapsulated request parameters to the server through POST/GET/DELETE.
3、Server response: The server first performs parameter security verification on the user request data, and returns the response data to the user in JSON format according to the business logic after verification.
4、Data processing: Processing server response data.
**Success**
The HTTP status code 200 indicates a successful response and may contain content. If the response contains content, it will be displayed in the corresponding return content.
**Common error code**
* 400 Bad Request – Invalid request format
* 401 Unauthorized – Invalid API Key
* 403 Forbidden – You do not have access to the requested resource 
* 404 Not Found – No request found
* 429 Too Many Requests – Restricted by the system because of too frequent requests.
* 500 Internal Server Error – We had a problem with our server 
If it fails, the Response body has an error description
## Standard specification
### Timestamp
All timestamps in the API are returned in microseconds unless otherwise specified.
The unit of ACCESS-TIMESTAMP in the request signature is seconds, allowing decimals to represent more precise time. The requested timestamp must be within 30 seconds of the API service time, otherwise the request will be considered expired and rejected. If there is a large deviation between the local server time and the API server time, then we recommend that you update the Http Header by querying the API server time.
### Examples
`1524801032573`
### Digits 
In order to maintain the integrity of the accuracy across platforms, decimal numbers are returned as strings. It is recommended that you also convert numbers to strings when invoking a request to avoid truncation and precision errors.
Integers (such as transaction numbers and order) are not quoted.
### Current limit
If the request is too frequent, the system will automatically limit the request and return 429 Too many requests status code in the Http Header.
##### REST API
* Public interface: We restrict the invocation of public interfaces by IP: at most 6 requests every 2 seconds.
* Private interface: We restrict the call of the private interface by user ID: at most 6 requests every 2 seconds.
* The special restrictions on some interfaces are noted on the corresponding  interfaces.
# Crypto-crypto trading(Spot)API reference
## Cryptos market API
### 1.Get the list of all pairs
**Request**
```http
    # Request
    GET /openapi/exchange/public/currencies
```
**Response**
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
**Return value description**  

|Return field | Field description|
| ----------|:-------:|
| baseIncrement | The minimum changing amount of transactions|
| baseSymbol    | Traded currency  |
| makerFeesRate | maker rate |
| maxPrice  | Trading price decimals places number |
| maxVolume | Minimum decimals places of trading amount |
| minTrade | Minimum order amount |
| online | Online or not |
| pairCode | the combination between Base and quote BTC_USD |
| quoteIncrement | Minimum trading unit |
| quotePrecision | Unit accuracy of pricing currency quantity  |
| quoteSymbol | Pricing currency |
| sort | Sort value |
| tickerFeesRate | ticker rate |

### 2. Get trading depth list of trading pairs
**Request**
```http
    # Request
    GET /openapi/exchange/public/{pairCode}/orderBook
```
**Response**
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
**Return value description**  

|Return field | Field description|
|--------| :-------: |
|asks| Asker's depth |
|bids| Bider's depth |

**Request parameters**  

| Parameter Name | Parameter Type | Required | Description |
| ------------- |----|----|----|
| pairCode | String | Yes | Trading pairs，example:LTC_BTC |

### 3. Get trading pairs ticker
Snapshot information of last trading price,bid 1 price, ask 1 price, 24h higest price, 24h lowest price, 24h open price and 24h trading volume.
**Request**
```http
    # Request
    GET /openapi/exchange/public/{pairCode}/ticker
```
**Response**
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
**Return value description**
 
|Return field | Field description|
|--------| :-------: |
|buy| last bid price  |
|change24| 24h change |
|changePercentage| change percentage  |
|changeRate24| 24h change rate |
|close| 24h close |
|createOn| create time|
|high| Highest trading price |
|high24| 24h highest trading price |
|last| last trading price |
|low| Lowest trading price |
|low24| 24h lowest trading price |
|open| 24h open |
|pairCode| trading pairs information  |
|quoteVolume| trade volume of pricing currency  |
|sell| last ask price |
|volume| trade volume of benchmark  currency |
    
**Request parameters**

|Parameter Name | Parameter Type | Required | Description|
|------|----|:---:|:---:|
|pairCode|String|Yes|trading pair，example:ETH_BTC|
    
### 4. Get trading history of trading pairs,can be queried with pagination .
Get trading history of the requested trading pairs,can be queried with pagination .
**Request**
```http
    # Request
    GET /openapi/exchange/{pairCode}/fulfillment
```
**Response**
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
**Return value description**

|Return field | Field description|
|--------|----|
| id |order id|
| pairCode |the combination between Base and quote BTC_USD|
| userId |user id|
| brokerId |broker id|
| side |side bid, ask|
| entrustPrice |order price|
| amount |order amount|
| dealAmount |trading amount|
| quoteAmount |benchmark currency amount , only used in bid orders with market price|
| dealQuoteAmount |trading amount of benchmark currency|
| systemOrderType |10:limit price 11:market price|
| status |0:unfilled 1:partially filled 2:filled 3:cancelling  -1:canceled |
| sourceInfo |order source web,api,Ios,android|
| createOn |create time|
| updateOn |modify time|
| symbol |currency|
| trunOver |trade volume   dealQuoteAmount * dealAmount|
| notStrike |unfilled amount|
| averagePrice |average trading price|
| openAmount |order amount|

**Request parameters**

|Parameter Name | Parameter Type | Required | Description|
|-----|:---:|----|----|
|pairCode|String|Yes|trading pair，example:LTC_BTC|
|startDate|Long|No|Start time，example:1524801032573|
|endDate|Long|No|end time，example:1524801032573|
|systemOrderType|Integer|No|10:limit price 11:market price|
|price|BigDecimal|No|Price|
|amount|BigDecimal|No|Amount|
|source|String|No|Trading pair，example:LTC_BTCweb,api,Ios,android|
|isHistory|Boolean|No|Whether to check historical data, the data of one week ago is historical data, default false|
|page|Integer|No|Page number|
|pageSize|Integer|No|terms number of per page|

**Description**
+ Trade side represents the maker's side in each filled orders, and  the maker refers to the trader who places the order on the order depth chart ,that is, the passive trading side.
+ Buy represents a dropping market, because the maker places the bid order, and his order is filled, resulting in price falls; On the contrary, sell represents the market going up, because the maker places the ask order, and the order is filled, indicating the rise of price.

### 5. Get K-Line data
**Request**
```http
    # Request
    GET  /openapi/exchange/public/{pairCode}/candles?interval=1min&start=start_time&end=end_time
```
**Response**
    
```javascript
    # Response
    {
        [ 1415398768, 0.32, 0.42, 0.36, 0.41, 12.3 ]
        ...
    }
```
**Return value description (in order)**  
    
|Return field | Field description|
|-----|----|
|1415398768|K line start timestamp|
|0.32|Lowest price|
|0.42|Highest price|
|0.36|Open price（the first transaction）|
|0.41|Close price（the last transaction ）|
|12.3|Trade volume （calculated with traded currency ）|
**Request parameters**
    
|Parameter Name | Parameter Type | Required | Description|
|-----|----|----|----|
|pairCode|String|Yes|Trading pair example:btc_usdt|
|interval|String|Yes|K line cycle type, such as1min/1hour/day/week/month|
|start|String|No|Start time based on ISO 8601 standard|
|end|String|No|End time based on ISO 8601 standard|

### 6. Get server time
The interface to get the time of the API server.

**Request**
```http
    # Request
    
    GET /openapi/exchange/public/time
```
**Response**
    
```javascript
    # Reponse
{
        "epoch": "1524801032.573"
        "iso": "2015-01-07T23:47:25.201Z",
        "timestamp": 1524801032573
    }
```
    
**Return value description**
    
|Return field | Field description|
|-----|----|
|epoch|Server time expressed in seconds as a timestamp|
|iso|Server time for ISO 8061 standard time string representation|
|timestamp|Server time expressed in milliseconds as a timestamp|

## Crypto account API
### 1. Get account information Get the balance list of crypto  account, check the balance of each currency and their freeze and availability status.
**Request**
```
    # Request
    GET /openapi/exchange/assets
```
**Response**
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
**Return value description**

|Return field | Field description|
|----|----|
|brokerId|broker id|
|symbol|currency|
|available|balance|
|hold|frozen|
|baseBTC|convert to BTC|
|withdrawLimit|Withdraw amount limit|

### 2. Orders
Provide limit orders and market orders.
**Request**
```
    # Request
    POST /openapi/exchange/{pairCode}/orders
```
**Response**
```javascript
    # Response
    10000
```   
**Return value description**
Order id

**Request parameters**

|Parameter Name | Parameter Type | Required | Description|
|:----:|:----:|:---:|----|
|pairCode|String|Yes|trading pair，example:BTC_USDT|
|side|String|Yes|bid is buy，ask is sell|
|systemOrderType|String|Yes|limit is limit order，market is market price |
|volume|String|No|Transmit parameters when there is limit order or ask order with market price, representing the traded currency amount.|
|price|String|No|Transmit parameters when there is limit order , representing the trading price.|
|quoteVolume|String|No|Transmit parameters when there is bid order with market price , representing the pricing currency amount.|

### 3. Cancel all orders
Cancel all unfilled orders of the target trading pair,  50 cancellations at most. The interface has no return value because the cancellations are conducted asynchronously.
**Request**
```
    # Request
    DELETE /openapi/exchange/{pairCode}/cancel-all
```
**Response**
```javascript
    # Response
    { ...}
```
**Request parameters**

|Parameter Name | Parameter Type | Required | Description|
|----|----| ----| ----|
|pairCode|String|Yes|trading pair， example:BTC_USDT|
Currently canceling 200 pending orders in batches.

### 4. Cancel specific orders
The specified order is cancelled according to the order id. The interface has no return value because the cancellations are conducted asynchronously.
**Request**
```http
    # Request
    DELETE /openapi/exchange/{pairCode}/orders/{id}
```
**Response**
```javascript
    # Response
    {...}
```
**Request parameters**

|Parameter Name | Parameter Type | Required | Description|
|---|----|----|----|
|code|String|Yes|trading pair，example:BTC_USDT|
|orderId|String|Yes|The ID of the unfilled order that needs to be canceled|

### 5. Query orders, support pagination.
Query orders by order status.
    
**Request**
```http   
    # Request
    GET /openapi/exchange/orders
```
**Response**
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
**Return value description**

|Return field | Field description|
|--------|----|
| id |Order id|
| pairCode |the combination between Base and quote BTC_USD|
| userId |User id|
| brokerId |Broker id|
| side |Side bid,ask|
| entrustPrice |order price|
| amount |order amount|
| dealAmount |trading amount|
| quoteAmount |benchmark currency amount  only used in bid order with market price|
| dealQuoteAmount |trading amount of benchmark currency|
| systemOrderType |10:limit price 11:market price|
| status |0:unfilled 1:partially filled 2:filled 3:cancelling  -1:canceled|
| sourceInfo |order source web,api,Ios,android|
| createOn |create time |
| updateOn |modify time |
| symbol |currency|
| trunOver |trade volume   dealQuoteAmount * dealAmount|
| notStrike |unfilled amount|
| averagePrice |average trading price|
| openAmount |order amount|

**Request parameters**

|Parameter Name | Parameter Type | Required | Description|
|---|----|----|----|
|pairCode|String|No|Trading pair，example:BTC_USDT|
|startDate|Long|No|Start time  milliseconds|
|endDate|Long|No|End time  milliseconds|
|price|BigDecimal|No|Order price|
|amount|BigDecimal|No|Order amount|
|systemOrderType|Integer|No|10:limit price  11:market price|
|source|String|No|Trading pair，example:LTC_BTCweb,api,Ios,android|
|page|Integer|No|Page number|
|pageSize|Integer|No|Terms number per page|

### 6. 通过id获取订单

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

### 7. Get bills, can be checked with pagination 
Get the crypto transaction bill.
**Request**
```http
    # Request
    GET /openapi/exchange/bills
```
**Response**
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
**Return value description**

|Return field | Field description|
|----|----|
|amount|change amount|
|balance|balance after change|
|createdDate|bill timestamp|
|details|details|
|orderId|corresponding order ID|
|code|corresponding trading pair of the order，example:BTC_USDT|
|id|bill ID|
|type|transaction type|

**Request parameters**  

|Parameter Name | Parameter Type | Required | Description|
|----|---|---|---|
|currencyCode|String|Yes|currency ，such as BTC|
|limit|Integer|No|Request return data volume, default / maximum value is 100|
  
[kangbo]: https://www.kangbo.io 
[English Docs]: https://github.com/kangbo/openAPI/blob/master/README_EN.md