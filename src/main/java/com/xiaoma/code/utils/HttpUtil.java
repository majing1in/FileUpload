package com.xiaoma.code.utils;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Author Administrator
 * @Date 2022/7/6 10:11:45
 */
public class HttpUtil {

    public static final String QUESTION_MARK = "?";

    public static final String CONNECTION_SYNBOL = "&";

    public static final String EQUAL_SIGN_SYNBOL = "=";

    public static final String HTTP = "http";

    public static final String HTTPS = "https";

    public static final String POST = "POST";

    public static final String GET = "GET";

    public static String doGet(String path, Map<String, Object> params) {
        return doGet(path, params, null);
    }

    public static String doGet(String path, Map<String, Object> params, Map<String, String> headers) {
        try {
            String values = mapToStr(params);
            path = path + QUESTION_MARK + values;
            URL url = new URL(path);
            HttpURLConnection connection = getHttpUrlConnection(url, headers, GET);
            return getHttpResult(connection, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doPost(String path, String json, Map<String, String> headers) {
        try {
            URL url = new URL(path);
            headers.put("Content-Type", "application/json;charset=utf-8");
            HttpURLConnection connection = getHttpUrlConnection(url, headers, POST);
            return getHttpResult(connection, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doPost(String path, Map<String, Object> params, Map<String, String> headers) {
        try {
            URL url = new URL(path);
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            HttpURLConnection connection = getHttpUrlConnection(url, headers, POST);
            return getHttpResult(connection, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection getHttpUrlConnection(URL url, Map<String, String> headers, String requestMethod) throws IOException {
        // 打开URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 请求方式
        connection.setRequestMethod(requestMethod);
        // 设置通用请求头
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(connection::setRequestProperty);
        }
        // 设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
        // 最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet
        // post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内
        connection.setDoOutput(Boolean.TRUE);
        connection.setDoInput(Boolean.TRUE);
        return connection;
    }

    @SuppressWarnings("unchecked")
    private static <T> String getHttpResult(HttpURLConnection connection, T params) throws IOException {
        if (POST.equals(connection.getRequestMethod())) {
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            if (params instanceof String) {
                String value = (String) params;
                writer.write(value);
            } else if (params instanceof Map) {
                Map<String, Object> value = (Map<String, Object>) params;
                writer.write(mapToStr(value));
            } else {
                throw new IllegalArgumentException("参数错误");
            }
            // 缓冲数据
            writer.flush();
            writer.close();
        }
        // 获取URLConnection对象对应的输入流
        InputStream inputStream = connection.getInputStream();
        // 构造一个字符流缓存,接口返回结果 编码为utf-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            result.append(str);
        }
        inputStream.close();
        // 断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断
        // 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息，写上disconnect后正常如果正在被其他线程使用就不切断一些
        connection.disconnect();
        return result.toString();
    }

    private static String mapToStr(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey());
            if (entry.getValue() != null) {
                stringBuilder.append(EQUAL_SIGN_SYNBOL);
                if (entry.getValue() instanceof String) {
                    stringBuilder.append(entry.getValue());
                } else {
                    stringBuilder.append(JSON.toJSONString(entry.getValue()));
                }
            }
            stringBuilder.append(CONNECTION_SYNBOL);
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

}
