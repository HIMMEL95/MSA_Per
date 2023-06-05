package com.per.msa_common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONFactory {
    public static void main(String[] args) {

    }

    private StringBuilder buff = new StringBuilder();

    public StringBuilder addKey(String key, boolean appendable) throws Exception {
        if (appendable) {
            this.buff.append(",");
        }

        this.buff.append("\"").append(key).append("\":");
        return this.buff;
    }

    public StringBuilder addValueJsonData(String value) throws Exception {
        if (value == null) {
            this.buff.append("\"").append("\"");
            return this.buff;
        } else {
            this.buff.append(value);
            return this.buff;
        }
    }

    public StringBuilder addValue(String value) throws Exception {
        if (value == null) {
            this.buff.append("\"").append("\"");
            return this.buff;
        } else {
            this.buff.append("\"").append(value).append("\"");
            return this.buff;
        }
    }

    public StringBuilder addValueInteger(String value) throws Exception {

        if (value == null) {
            this.buff.append("\"").append("\"");
            return this.buff;
        } else {
            this.buff.append(value);
            return this.buff;
        }
    }

    public StringBuilder addEncodedValue(Object value, boolean appendable) throws Exception {
        String encodedValue = String.valueOf(value);
        return this.addValue(encodedValue);
    }

    public StringBuilder addPrefix(boolean appendable) throws Exception {
        if (appendable) {
            this.buff.append(",");
        }

        this.buff.append("{");
        return this.buff;
    }

    public StringBuilder addPostfix() throws Exception {
        this.buff.append("}");
        return this.buff;
    }

    public StringBuilder addPrefixArray(boolean appendable) throws Exception {
        if (appendable) {
            this.buff.append(",");
        }

        this.buff.append("[");
        return this.buff;
    }

    public StringBuilder addPostfixArray() throws Exception {
        this.buff.append("]");
        return this.buff;
    }

    public String toString() {
        return this.buff.toString();
    }

    public StringBuilder addException(String key, String value) throws Exception {

        // "Level:Critical“

        this.buff.append("");
        this.buff.append("\"").append(key).append(":").append(value).append("\"");
        return this.buff;
    }

    public StringBuilder addException(String key) throws Exception {

        // "Level:Critical“

        this.buff.append("");
        this.buff.append("\"").append(key).append("\"");
        return this.buff;
    }

    public static JSONObject stringToJson(String resultString) throws Exception {

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(resultString);
        JSONObject jsonObj = (JSONObject) obj;

        return jsonObj;
    }

    public static JSONObject getJsonStringFromMap(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            jsonObject.put(key, value);
        }

        return jsonObject;
    }

    public static JSONArray getJsonArrayFromList(List<Map<String, Object>> list) {
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> map : list) {
            jsonArray.add(getJsonStringFromMap(map));
        }

        return jsonArray;
    }

    public static String getJsonStringFromList(List<Map<String, Object>> list) {
        JSONArray jsonArray = getJsonArrayFromList(list);
        return jsonArray.toJSONString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJsonObject(JSONObject jsonObj) {
        Map<String, Object> map = null;

        try {

            map = new ObjectMapper().readValue(jsonObj.toJSONString(), Map.class);

        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * JsonArray를 List<Map<String, String>>으로 변환한다.
     *
     * @param jsonArray JSONArray.
     * @return List<Map<String, Object>>.
     */
    public static List<Map<String, Object>> getListMapFromJsonArray(JSONArray jsonArray) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (jsonArray != null) {
            int jsonSize = jsonArray.size();
            for (int i = 0; i < jsonSize; i++) {
                Map<String, Object> map = JSONFactory.getMapFromJsonObject((JSONObject) jsonArray.get(i));
                list.add(map);
            }
        }

        return list;
    }

    /**
     * jsonString 을 List<Map<String, Object>> 로 변환한다.
     * 
     * @param jsonString
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getListMapFromJsonString(String jsonString) throws Exception {
        List<Map<String, Object>> listMap = new ObjectMapper().readValue(jsonString,
                new TypeReference<List<Map<String, Object>>>() {
                });

        return listMap;
    }
}
