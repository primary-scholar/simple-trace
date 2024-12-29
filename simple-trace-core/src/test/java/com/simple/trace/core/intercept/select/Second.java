package com.simple.trace.core.intercept.select;

public interface Second {
    default String info() {
        return "second info";
    }
}
