package com.per.msa_common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
    // 랜덤 문자열(숫자포함) 생성
    public static String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String currentDate(String pattern) {
        Date date = new Date();
        SimpleDateFormat thismonth;
        thismonth = new SimpleDateFormat(pattern);
        return thismonth.format(date);
    }

    public static boolean isEmpty(Object s) {
        if (s == null) {
            return true;
        }
        if ((s instanceof String) && (((String) s).trim().length() == 0)) {
            return true;
        }
        if (s instanceof Map) {
            return ((Map<?, ?>) s).isEmpty();
        }
        if (s instanceof List) {
            return ((List<?>) s).isEmpty();
        }
        if (s instanceof Object[]) {
            return (((Object[]) s).length == 0);
        }
        return false;
    }

    public static ArrayList<HashMap<String, Object>> jsonList(String data)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        String jsonData = data;
        list = mapper.readValue(jsonData, new TypeReference<ArrayList<HashMap<String, Object>>>() {
        });
        return list;
    }

    public static Map<String, Object> pagingSum(Map<String, Object> map, int listCount) {
        int currentPage = Integer.parseInt((String) map.get("currentPage")); // 현재 페이지
        int pagingSize = Integer.parseInt((String) map.get("pagingSize")); // 페이징 보여줄 갯수
        int rowCount = Integer.parseInt((String) map.get("rowCount")); // 리스트 가져올 갯수

        int startPage = ((currentPage - 1) / pagingSize * pagingSize) + 1; // 시작블럭숫자 (1~5페이지일경우 1, 6~10일경우 6)
        int endPage = ((currentPage - 1) / pagingSize * pagingSize) + pagingSize; // 끝 블럭 숫자 (1~5일 경우 5, 6~10일경우 10)
        int toEndPage = listCount == 0 ? 1 : ((listCount - 1) / rowCount) + 1;

        if (endPage > toEndPage) {
            endPage = toEndPage;
        }
        if (startPage < 1) {
            startPage = 1;
        }

        map.put("pagingSize", pagingSize); // 페이징 보여줄 갯수만큼 foreach 돌리고
        map.put("currentPage", currentPage); // 현재 선택한 페이지가 뭔지 active 해주고
        map.put("pageStart", (currentPage * rowCount) - rowCount); // 쿼리에서 사용할 변수
        map.put("toEndPage", toEndPage); // 계속 < > 버튼 누르지 못하게
        map.put("endPage", endPage);
        map.put("startPage", startPage);
        map.put("numberPagingUseYn", "Y");
        return map;
    }

    public static String getIp(HttpServletRequest request) throws Exception {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getBrowser() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        // 에이전트
        String agent = request.getHeader("User-Agent");
        // 브라우져 구분
        String browser = null;
        if (agent != null) {
            if (agent.indexOf("Trident") > -1 || agent.indexOf("MSIE") > -1) {
                browser = "MSIE";
            } else if (agent.indexOf("Safari") > -1) {
                if (agent.indexOf("Chrome") > -1) {
                    browser = "Chrome";
                } else {
                    browser = "Safari";
                }
            } else if (agent.indexOf("Opera") > -1) {
                browser = "Opera";
            } else if (agent.indexOf("iPhone") > -1 && agent.indexOf("Mobile") > -1) {
                browser = "iPhone";
            } else if (agent.indexOf("Android") > -1 && agent.indexOf("Mobile") > -1) {
                browser = "Android";
            } else {
                browser = "Etc";
            }
        }
        return browser;
    }
}
