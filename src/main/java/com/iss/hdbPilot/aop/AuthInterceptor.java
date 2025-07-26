package com.iss.hdbPilot.aop;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iss.hdbPilot.annotation.AuthCheck;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.service.UserService;

@Component
@Aspect
public class AuthInterceptor {
    
    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint proceedingJoinPoint,AuthCheck authCheck) throws Throwable{
        String mustRole = authCheck.mustRole();

        //在 非 Controller 的上下文 中手动获取当前线程绑定的 HttpServletRequest 对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();

        User currUser = userService.getCurrentUser(request);

        //如果没有权限要求，放行
        if(mustRole.isEmpty()){
            return proceedingJoinPoint.proceed();
        }

        //如果要求是管理员而当前用户不是管理员，抛出异常
        String userRole = currUser.getUserRole();
        if(mustRole.equals("admin") && !mustRole.equals(userRole)){
            throw new RuntimeException("No permission");
        }

        return proceedingJoinPoint.proceed();
    }
    
}
