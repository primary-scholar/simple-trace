package com.simple.trace.core.transform;

@CustomAnnotation
public class Foo {

    public String print() {
        System.out.println("foo print");
        return "foo print";
    }
}
