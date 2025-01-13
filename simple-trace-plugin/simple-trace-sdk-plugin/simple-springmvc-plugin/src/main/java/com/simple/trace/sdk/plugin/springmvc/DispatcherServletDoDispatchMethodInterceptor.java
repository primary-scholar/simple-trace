package com.simple.trace.sdk.plugin.springmvc;

import com.simple.trace.core.plugin.interceptor.InstanceMethodsAroundInterceptor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

public class DispatcherServletDoDispatchMethodInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(Object target, Method method, Object[] args, Object result) {
        if (Objects.isNull(args) || args.length != NumberUtils.INTEGER_TWO) {
            return;
        }
        HttpServletRequest request = ((HttpServletRequest) args[0]);
        HttpServletResponse response = ((HttpServletResponse) args[1]);

    }

    @Override
    public Object afterMethod(Object target, Method method, Object[] args, Object result) {
        return null;
    }

    @Override
    public void handleException(Object target, Method method, Object[] args, Throwable throwable) {

    }
}
