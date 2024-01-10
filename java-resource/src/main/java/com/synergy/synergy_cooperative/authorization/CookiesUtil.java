package com.synergy.synergy_cooperative.authorization;

import org.springframework.http.ResponseCookie;

public class CookiesUtil {

    private final String name;
    private final String token;

    public CookiesUtil(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public String getCookie(){
        return cookie().toString();
    }

    private ResponseCookie cookie(){
        return ResponseCookie
                .from(name, token)
                .secure(false)
                .httpOnly(true)
                .path("/")
                .sameSite("STRICT")
                .build();
    }

}
