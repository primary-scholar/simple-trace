package com.simple.trace.core.intercept.delegation;

public class Target {
    public static String hello(String name) {
        return "Hello " + name + "!";
    }
}
