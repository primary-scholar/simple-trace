package com.simple.trace.core;


public class PrintSomething {
    public void printFirst() {
        System.out.println("class PrintSomething function printFirst");
    }

    public String printSecond(String something) {
        String format = String.format("class PrintSomething function printSecond argument %s", something);
        System.out.println(format);
        return format;
    }
}
