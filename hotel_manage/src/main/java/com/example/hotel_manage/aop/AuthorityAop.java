package com.example.hotel_manage.aop;

import com.example.hotel_manage.Pojo.Result;
import com.example.hotel_manage.Utils.JwtUtils;
import com.fasterxml.jackson.core.ObjectCodec;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

//使用aop功能进行权限检查 拒绝越级操作
@Component
@Aspect
@Slf4j
public class AuthorityAop {
    @Autowired
    private HttpServletRequest request;
    @Around("@annotation(com.example.hotel_manage.anno.authority_anno.AdminAnno)")
    public Object AdminCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        String jwt= request.getHeader("token");
        Claims claims= JwtUtils.parseJWT(jwt);
        String type=claims.get("type").toString();
        if(!type.equals("admin")){
            return Result.error("操作失败，权限不足");
        }
        Object result= joinPoint.proceed();
        return result;
    }
    @Around("@annotation(com.example.hotel_manage.anno.authority_anno.ReceptionistAnno)")
    public Object receptionistCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        String jwt= request.getHeader("token");
        Claims claims= JwtUtils.parseJWT(jwt);
        String type=claims.get("type").toString();
        if(!Arrays.asList("admin", "receptionist").contains(type)){
            return Result.error("操作失败，权限不足");
        }
        Object result= joinPoint.proceed();
        return result;
    }
    @Around("@annotation(com.example.hotel_manage.anno.authority_anno.HousekeeperAnno)")
    public Object housekeeperCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        String jwt= request.getHeader("token");
        Claims claims= JwtUtils.parseJWT(jwt);
        String type=claims.get("type").toString();
        if(!Arrays.asList("admin", "housekeeper").contains(type)){
            return Result.error("操作失败，权限不足");
        }
        Object result= joinPoint.proceed();
        return result;
    }
    @Around("@annotation(com.example.hotel_manage.anno.authority_anno.AccountantAnno)")
    public Object accountantCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        String jwt= request.getHeader("token");
        Claims claims= JwtUtils.parseJWT(jwt);
        String type=claims.get("type").toString();
        if(!Arrays.asList("admin", "accountant").contains(type)){
            return Result.error("操作失败，权限不足");
        }
        Object result= joinPoint.proceed();
        return result;
    }
}
