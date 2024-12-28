package com.simple.trace.core.intercept;

public class TargetThird {

    public String intercept(String name) {
        return "Hello " + name + "!";
    }

    public String intercept(int i) {
        return Integer.toString(i);
    }

    public String intercept(Object o) {
        return o.toString();
    }
}
