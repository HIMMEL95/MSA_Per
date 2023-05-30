package com.util.msa_frontend.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;

public class CookieUtils {
    private static Logger log = LoggerFactory.getLogger(CookieUtils.class);

    public static String getCookie(String key, HttpServletRequest req) {
        String result = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    result = cookie.getValue();
                }
            }
        }
        log.info(result);
        return result;
    }

    public static void setCookie(String key, String value, HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from(key, value)
                .path("/")
                .sameSite("strict")
                .httpOnly(true)
                .secure(false)
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
    }

    public static void removeCookie(String key, HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from(key, null)
                .path("/")
                .sameSite("strict")
                .httpOnly(true)
                .secure(false)
                .maxAge(0)
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
    }
}
