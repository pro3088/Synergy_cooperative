package com.synergy.synergy_cooperative.authorization.utils;

import org.springframework.http.ResponseCookie;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookiesUtil {

    private String name = null;
    private String token = null;

    public CookiesUtil() {
    }

    public CookiesUtil(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public String getCookie() {
        return cookie().toString();
    }

    public String clear(){
        return clearCookie().toString();
    }

    public String encodeCookieValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public String decodeCookieValue(String encodedValue) {
        return URLDecoder.decode(encodedValue, StandardCharsets.UTF_8);
    }

    private ResponseCookie cookie() {
        return ResponseCookie
                .from(name, encodeCookieValue(token))
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(1800)
                .sameSite("NONE")
                .build();
    }

    private ResponseCookie clearCookie(){
        return ResponseCookie.from(name, "")
                .maxAge(0)
                .path("/")
                .build();
    }
}
