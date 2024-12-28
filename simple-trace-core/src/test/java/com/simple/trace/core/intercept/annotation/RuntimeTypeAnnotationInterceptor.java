package com.simple.trace.core.intercept.annotation;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class RuntimeTypeAnnotationInterceptor {

    @RuntimeType
    public static Object intercept(@RuntimeType Object object) {
        System.out.println("it was intercept");
        return object;
    }
}
