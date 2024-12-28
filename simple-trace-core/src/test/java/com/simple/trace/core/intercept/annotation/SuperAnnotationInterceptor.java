package com.simple.trace.core.intercept.annotation;

import net.bytebuddy.implementation.bind.annotation.Super;

import java.util.List;

public class SuperAnnotationInterceptor {
    public static List<String> log(String info, @Super DataBase superz) {
        System.out.println("before invoke");
        try {
            return superz.load(info + " (logged access)");
        } finally {
            System.out.println("after invoke");
        }
    }
}
