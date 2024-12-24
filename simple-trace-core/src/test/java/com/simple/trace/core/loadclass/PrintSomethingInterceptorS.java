package com.simple.trace.core.loadclass;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

public class PrintSomethingInterceptorS {

    // todo 这里的注解 需要后续补充 并添加 说明
    @RuntimeType
    public Object intercept(@This Object thiz, @Origin Method method, @AllArguments Object[] args) throws Exception {
        System.out.println(thiz);
        System.out.println(method);
        System.out.println(args);
        return "hello";
    }
}
