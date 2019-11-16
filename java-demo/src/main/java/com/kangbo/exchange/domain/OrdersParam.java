package com.kangbo.exchange.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * 下单param
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrdersParam {
    /**
     * 买卖方向 1:buy,2:sell
     */
    @NonNull
    private String side;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 新版（数量）
     */
    private BigDecimal volume;

    /**
     * 新版（基准币数量  只有在市价买的情况下会用到）
     */
    private BigDecimal quoteVolume;

    /**
     * 订单类型 limit:0限价单 market:1 市价单
     */
    @NonNull
    private String systemOrderType;
}
