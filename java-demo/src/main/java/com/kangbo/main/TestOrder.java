package com.kangbo.main;

import com.kangbo.client.ApiClient;
import com.kangbo.common.domain.ClientParameter;
import com.kangbo.exchange.api.OrderApi;
import com.kangbo.exchange.domain.OrdersParam;

import java.io.IOException;
import java.math.BigDecimal;

public class TestOrder {
    public static void main(String[] args) throws IOException {
        ApiClient apiClient = new ApiClient(new ClientParameter(
                "apiKey",
                "secreKey",
                "https://www.kangbo.io",
                "passphrase"
        ));
        Long orderId = apiClient.create(OrderApi.class)
                             .postOrder(
                                     "BTC_USDT",
                                     OrdersParam.builder()
                                                .side("buy")
                                                .systemOrderType("limit")
                                                .price(new BigDecimal(1000))
                                                .volume(new BigDecimal(0.1))
                                                .build()
                             ).execute().body();
        System.out.println(orderId);
    }
}
