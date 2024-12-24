package com.simple.trace.core;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class ByteBuddyMakeClassTest {

    @Test
    public void modifyFunctionReturnValue() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<PrintSomething> unloaded = new ByteBuddy().subclass(PrintSomething.class)
                .method(ElementMatchers.named("printFirst")).intercept(FixedValue.value("none argument"))
                .method(ElementMatchers.named("printSecond")).intercept(FixedValue.value("one argument")).make();
        unloaded.saveIn(new File("a"));
        Class<? extends PrintSomething> aClass = unloaded.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        PrintSomething newInstance = aClass.newInstance();
        newInstance.printFirst();
        String s = newInstance.printSecond(null);
        System.out.println(s);
    }

    @Test
    public void interceptWithStaticClass() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<PrintSomething> unloaded =
                new ByteBuddy().subclass(PrintSomething.class).method(ElementMatchers.named("printSecond"))
                .intercept(MethodDelegation.to(PrintSomethingInterceptor.class)).make();
        unloaded.saveIn(new File("a"));
        DynamicType.Loaded<PrintSomething> loaded = unloaded.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends PrintSomething> aClass = loaded.getLoaded();
        PrintSomething newInstance = aClass.newInstance();
        newInstance.printFirst();
        newInstance.printSecond("argument");
    }

    @Test
    public void interceptWithObject() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<PrintSomething> unloaded =
                new ByteBuddy().subclass(PrintSomething.class).method(ElementMatchers.named("printSecond"))
                        .intercept(MethodDelegation.to(new PrintSomethingInterceptorS())).make();
        unloaded.saveIn(new File("a"));
        DynamicType.Loaded<PrintSomething> loaded = unloaded.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends PrintSomething> aClass = loaded.getLoaded();
        PrintSomething newInstance = aClass.newInstance();
        newInstance.printFirst();
        newInstance.printSecond("argument");
    }
}
