package com.example.hotel_manage.Utils;


import com.example.hotel_manage.Pojo.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


public class HttpUtils {
    private static HttpURLConnection con;
    public Result get(String url1) throws IOException {
        URL url = new URL(url1);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        StringBuilder content = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 300 ? con.getInputStream() : con.getErrorStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        } finally {
            con.disconnect();
        }

        if (status >= 200 && status < 300) {
            return Result.success(content.toString());
        } else {
            return Result.error("HTTP error code: " + status);
        }
    }
    public Result get(String url1, Map<String, String> data) throws IOException {
        // 将 Map 转换为查询参数字符串
        StringBuilder queryParams = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (queryParams.length() != 0) queryParams.append('&');
            queryParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            queryParams.append('=');
            queryParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        // 构建带有查询参数的完整URL
        String fullUrl = url1 + "?" + queryParams.toString();

        URL url = new URL(fullUrl);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        StringBuilder content = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 300 ? con.getInputStream() : con.getErrorStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        } finally {
            con.disconnect();
        }

        if (status >= 200 && status < 300) {
            return Result.success(content.toString());
        } else {
            return Result.error("HTTP error code: " + status);
        }
    }
    public Result post(String url1, Map<String, String> data) throws IOException {
        URL url = new URL(url1);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        // 将 Map 转换为 URL 编码的表单数据
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        try (OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream())) {
            out.write(postData.toString());
            out.flush();
        }

        int status = con.getResponseCode();
        StringBuilder content = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 300 ? con.getInputStream() : con.getErrorStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        } finally {
            con.disconnect();
        }

        if (status >= 200 && status < 300) {
            return Result.success(content.toString());
        } else {
            return Result.error("HTTP error code: " + status);
        }
    }

}
