package com.simple.trace.core;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

public class PrintSomethingInterceptorS {

    @RuntimeType
    public Object intercept(@This Object thiz, @Origin Method method, @AllArguments Object[] args) throws Exception {
        System.out.println(thiz);
        System.out.println(method);
        System.out.println(args);
        return thiz;
    }
}
