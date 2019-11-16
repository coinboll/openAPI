package com.kangbo.common.domain;

import com.kangbo.common.enums.Content;
import com.kangbo.common.enums.SupportedLocaleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientParameter {
    /**
     * 用户 api key，必填
     */
    private String apiKey;
    /**
     * 用户密钥，必填
     */
    private String secretKey;
    /**
     * 用户 passphrase，必填
     */
    private String passphrase;
    /**
     * 服务 url，非必填 默认
     */
    private String baseUrl;
    /**
     * 链接超时时间，非必填 默认 30s
     */
    private Long   timeout;
    /**
     * 语言环境
     */
    private String locale;

    public ClientParameter(String apiKey, String secretKey, String baseUrl, String passphrase) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.passphrase = passphrase;
        Validate.notNull(this.apiKey, "apiKey is null");
        Validate.notNull(this.secretKey, "secretKey is null");
        Validate.notNull(this.passphrase, "passphrase is null");
        Validate.notNull(this.baseUrl, "baseUrl is null");
        this.setBaseUrl(StringUtils.defaultIfBlank(this.getBaseUrl(), Content.BASE_URL));
        this.setTimeout(ObjectUtils.defaultIfNull(this.getTimeout(), Content.TIME_OUT));
        this.setLocale(ObjectUtils.defaultIfNull(this.getLocale(), SupportedLocaleEnum.EN_US.getName()));
    }
}
