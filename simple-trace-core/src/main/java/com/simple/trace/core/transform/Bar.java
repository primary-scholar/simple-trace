package com.simple.trace.core.transform;

@CustomAnnotation
public class Bar {

    public String print(){
        System.out.println("Bar print");
        return "Bar print";
    }
}
