package com.simple.trace.core.plugin.interceptor;

import java.lang.reflect.Method;

public interface InstanceMethodsAroundInterceptor {

    public void beforeMethod(Object target, Method method, Object[] args, Object result);

    public Object afterMethod(Object target, Method method, Object[] args, Object result);

    public void handleException(Object target, Method method, Object[] args, Throwable throwable);
}
