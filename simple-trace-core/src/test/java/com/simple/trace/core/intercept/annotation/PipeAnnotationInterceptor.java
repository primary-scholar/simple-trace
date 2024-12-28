package com.simple.trace.core.intercept.annotation;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.Pipe;

import java.util.List;
import java.util.function.Function;

public class PipeAnnotationInterceptor {
    private final DataBase dataBase;

    public PipeAnnotationInterceptor(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public List<String> log(@Pipe Function<DataBase, List<String>> pipe) {
        System.out.println("before invoker");
        try {
            return pipe.apply(dataBase);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("after invoker");
        }
    }
}
