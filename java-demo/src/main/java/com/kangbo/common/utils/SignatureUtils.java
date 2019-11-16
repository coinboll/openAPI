package com.kangbo.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

public class SignatureUtils {
    public static String generate(
            final String timestamp, String method, final String requestPath,
            String queryString, String body, final String secretKey
    ) throws UnsupportedEncodingException {
        method = method.toUpperCase();
        body = StringUtils.defaultIfBlank(body, StringUtils.EMPTY);
        queryString = StringUtils.isBlank(queryString) ? "" : "?" + queryString;
        final String preHash = timestamp + method + requestPath + queryString + body;
        return Base64.encodeBase64String(new HmacUtils(
                HmacAlgorithms.HMAC_SHA_256,
                secretKey.getBytes("UTF-8")
        ).hmac(preHash.getBytes("UTF-8")));
    }
}
