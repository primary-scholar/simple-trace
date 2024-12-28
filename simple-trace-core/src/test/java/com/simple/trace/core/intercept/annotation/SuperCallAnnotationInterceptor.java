package com.simple.trace.core.intercept.annotation;

import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.List;
import java.util.concurrent.Callable;

public class SuperCallAnnotationInterceptor {
    public static List<String> log(@SuperCall Callable<List<String>> callable) {
        System.out.println("before invoke");
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("after invoke");
        }
    }
}
