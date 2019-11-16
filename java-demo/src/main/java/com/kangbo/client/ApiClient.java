package com.kangbo.client;

import com.kangbo.common.domain.ClientParameter;
import com.kangbo.common.enums.HttpHeader;
import com.kangbo.common.utils.SignatureUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private final Retrofit        retrofit;
    /**
     * 超时时间
     */
    private final ClientParameter parameter;

    public ApiClient(final ClientParameter parameter) {
        this.parameter = parameter;
        this.retrofit = new Retrofit.Builder()
                .baseUrl(parameter.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(this.httpClient())
                .build();
    }

    public <T> T create(final Class<T> clazz) {
        return this.retrofit.create(clazz);
    }

    private OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new SignInterceptor(this.parameter))
                .addInterceptor(new HttpStatusInterceptor())
                .connectTimeout(this.parameter.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(this.parameter.getTimeout(), TimeUnit.SECONDS)
                .build();
    }

    /**
     * 签名过滤器
     */
    private class SignInterceptor implements Interceptor {

        private final ClientParameter clientParameter;

        public SignInterceptor(final ClientParameter clientParameter) {
            this.clientParameter = clientParameter;
        }

        @Override
        public Response intercept(final Chain chain) {
            final Request original = chain.request();
            final String timestamp = BigDecimal.valueOf(System.currentTimeMillis())
                                               .divide(BigDecimal.valueOf(1000))
                                               .toString();
            final String contentType = "application/json";
            try {
                final String sign = SignatureUtils.generate(
                        timestamp,
                        original.method().toUpperCase(),
                        original.url().url().getPath(),
                        original.url().encodedQuery(),
                        original.body() == null ? "" : this.bodyToString(original),
                        this.clientParameter.getSecretKey()
                );

                final String localFormat = "locale=%s";
                final Request.Builder requestBuilder = original.newBuilder()
                                                               .addHeader(
                                                                       HttpHeader.ACCESS_KEY,
                                                                       this.clientParameter.getApiKey()
                                                               )
                                                               .addHeader(
                                                                       HttpHeader.ACCESS_PASSPHRASE,
                                                                       this.clientParameter.getPassphrase()
                                                               )
                                                               .addHeader(HttpHeader.ACCESS_SIGN, sign)
                                                               .addHeader(HttpHeader.CONTENT_TYPE, contentType)
                                                               .addHeader(
                                                                       HttpHeader.COOKIE,
                                                                       String.format(
                                                                               localFormat,
                                                                               this.clientParameter.getLocale()
                                                                       )
                                                               )
                                                               .addHeader(
                                                                       HttpHeader.X_LOCALE,
                                                                       this.clientParameter.getLocale()
                                                               )
                                                               .addHeader(HttpHeader.ACCESS_TIMESTAMP, timestamp);

                final Request request = requestBuilder.build();
                return chain.proceed(request);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        private String bodyToString(final Request request) {
            try {
                final Request copy = request.newBuilder().build();
                final Buffer buffer = new Buffer();
                copy.body().writeTo(buffer);
                return buffer.readUtf8();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * http 请求返回状态过滤器
     */
    private class HttpStatusInterceptor implements Interceptor {
        @Override
        public Response intercept(final Chain chain) throws IOException {
            final Response response = chain.proceed(chain.request());
            if (response.isSuccessful()) {
                return response;
            }
            throw new RuntimeException(" call " + chain.request().url().url().getPath()
                                       + " failed, status : " + response.code() + ", body : " + response.body()
                                                                                                        .string());
        }
    }
}
