package com.simple.trace.core.intercept.delegation;

public class TargetSecond {
    public static String intercept(String name) {
        return "Hello " + name + "!";
    }

    public static String intercept(int i) {
        return Integer.toString(i);
    }

    public static String intercept(Object o) {
        return o.toString();
    }
}
