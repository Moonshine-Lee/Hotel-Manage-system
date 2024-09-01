package com.example.hotel_manage.Filter;

import com.example.hotel_manage.Pojo.Result;
import com.example.hotel_manage.Utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //获取请求url
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String url = req.getRequestURL().toString();
        log.info("请求url" + url);
        //登陆请求则放行
        if (url.contains("login")) {
            log.info("登陆放行");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }        // 设置CORS响应头
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.addHeader("Access-Control-Allow-Headers", "*");
        res.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN");
        res.setHeader("Access-Control-Allow-Credentials", "false");
        String jwt = req.getHeader("token");
        //查看是否为空  对于空消息，需要手动设置res的js，而不是依赖controller的自动json转化
        if (jwt == null) {
            Result notLogin = Result.error("NOT_LOGIN");
            //手动将对象转换为json
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(notLogin);
            res.getWriter().write(jsonStr);
            return;
        }
        if (jwt.isEmpty()) {
            log.info("token为空，返回未登录的信息");
            Result notLogin = Result.error("NOT_LOGIN");
            //手动将对象转换为json
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(notLogin);
            res.getWriter().write(jsonStr);
            return;
        }
        try {
            JwtUtils.parseJWT(jwt);
        } catch (Exception e) {
            //jwt解析失败
            e.printStackTrace();
            log.info("令牌无效");
            Result notLogin = Result.error("NOT_LOGIN");
            //手动将对象转换为json
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(notLogin);
            res.getWriter().write(jsonStr);
            return;
        }
        log.info("令牌合法，放行");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
