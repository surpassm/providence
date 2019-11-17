package com.ysy.oath.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * @author mc
 * Create date 2019/9/24 17:13
 * Version 1.0
 * Description
 * <p>
 * 最后在启动类中加入注解 @ServletComponentScan
 */

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static Set<String> notAllowedKeyWords = new HashSet<>(16);

    static {
        String key = "and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+";
        String[] keyStr = key.split("\\|");
        Collections.addAll(notAllowedKeyWords, keyStr);
    }

    private String currentUrl;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param servletRequest The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
        currentUrl = servletRequest.getRequestURI();
    }

    /**
     * 覆盖getParameter方法，将参数名和参数值都做xss过滤。顶顶顶顶顶顶顶烦烦烦对当代顶顶顶顶
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取
     * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
     */
    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return cleanXss(value);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXss(values[i]);
        }
        return encodedValues;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> values = super.getParameterMap();
        if (values == null) {
            return null;
        }
        Map<String, String[]> result = new HashMap<>(16);
        for (String key : values.keySet()) {
            String encodedKey = cleanXss(key);
            int count = values.get(key).length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++) {
                encodedValues[i] = cleanXss(values.get(key)[i]);
            }
            result.put(encodedKey, encodedValues);
        }
        return result;
    }

    /**
     * 覆盖getHeader方法，将参数名和参数值都做xss过滤。
     * 如果需要获得原始的值，则通过super.getHeaders(name)来获取
     * getHeaderNames 也可能需要覆盖
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXss(value);
    }

    /**
     * 防止XSS注入
     *
     * @param values 值
     * @return 清理后
     */
    private String cleanXss(String values) {
        // You'll need to remove the spaces from the html entities below
        String value = values.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        value = cleanSqlKeyWords(value);
        return value;
    }

    /**
     * 防止sql注入
     *
     * @param value 值
     * @return 清理后
     */
    private String cleanSqlKeyWords(String value) {
        String paramValue = value;
        for (String keyword : notAllowedKeyWords) {
            if (paramValue.length() > keyword.length() + 4
                    && (paramValue.contains(" " + keyword) || paramValue.contains(keyword + " ") || paramValue.contains(" " + keyword + " "))) {
                String replacedString = "INVALID";
                paramValue = StringUtils.replace(paramValue, keyword, replacedString);
                log.error(this.currentUrl + "已被过滤，因为参数中包含不允许sql的关键词(" + keyword
                        + ")" + ";参数：" + value + ";过滤后的参数：" + paramValue);
            }
        }
        return paramValue;
    }
}
