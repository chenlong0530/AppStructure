package com.app.library.net;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @ClassName: UrlParams
 * @Description: URL参数类
 * @Author: hexh 350098988@qq.com
 * @Date: 2014-9-21
 *
 */
public class UrlParams extends LinkedHashMap<String, String> {

    private static final String MARK = "?";
    private static final String ADD = "&";
    private static final String EQUAL = "=";

    public UrlParams() {
    }

    public UrlParams(Map<String, String> map) {
        putAll(map);
    }

    public String generateParams() {
        StringBuffer sb = new StringBuffer();
        boolean firstParam = true;
        for (Entry<String, String> entry : entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (firstParam) {
                sb.append(MARK);
                firstParam = false;
            }
            else {
                sb.append(ADD);
            }

            String key = entry.getKey();
            sb.append(key);
            sb.append(EQUAL);
            sb.append(value);
        }
        return sb.toString();
    }

    public String toString() {
        return generateParams();
    }

    public UrlParams add(String key, String value) {
        put(key, value);
        return this;
    }

    public void clearAll() {
        clear();
    }

    public UrlParams clear(String key) {
        remove(key);
        return this;
    }
}