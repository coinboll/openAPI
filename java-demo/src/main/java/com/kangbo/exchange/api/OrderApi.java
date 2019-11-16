package com.kangbo.exchange.api;

import com.kangbo.exchange.domain.OrdersParam;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApi {

    /**
     * 下单接口
     */
    @POST("/openapi/exchange/{pairCode}/orders")
    Call<Long> postOrder(@Path("pairCode") String pairCode, @Body OrdersParam body);

}
