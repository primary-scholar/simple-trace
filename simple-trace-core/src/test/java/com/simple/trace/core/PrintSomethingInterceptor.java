package com.simple.trace.core;

public class PrintSomethingInterceptor {

    public static String printFirstInterceptOne(String somthing) {
        String format = String.format("PrintSomethingInterceptor:printFirstInterceptOne %s", somthing);
        System.out.println(format);
        return somthing;
    }

    public static Integer printFirstInterceptSecond(Integer value) {
        String format = String.format("PrintSomethingInterceptor:printFirstInterceptSecond %s", value.toString());
        System.out.println(format);
        return value;
    }

}
