package com.liufeng.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

public class JsonUtils {
    private static ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

    public static <T> T getObjectFromStr(String str, Class<T> clazz) {
        try {
            return mapper.readValue(str, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getStrFromObject(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static String turnListToString(List<String> list){
        if (list == null || list.isEmpty()){
            return null;
        } else {
            return StringUtils.join(list);
        }
    }

    public static String turnListToRepalceString(List<String> list){
        return turnListToString(list) == null ? null : turnListToString(list).replace("[","").replace("]","");
    }
}
