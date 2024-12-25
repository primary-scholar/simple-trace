package com.simple.trace.core.transform;

public class PlainMainClass {

    public static void main(String[] args) {
        Bar bar = new Bar();
        System.out.println(bar.print());
        Foo foo = new Foo();
        System.out.println(foo.print());
    }
}
