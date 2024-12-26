package com.simple.trace.core.intercept;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

import java.io.IOException;

public class ByteBuddyInterceptDelegationTest {

    /**
     * 委托 静态类 MethodDelegation.to(Target.class)
     * MethodDelegation 实现识别Target类的任何方法调用并且在这些方法中找出一个最匹配的，
     * 因为Target类型只定义了一个静态方法，其中方法的参数、返回类型和名称与方法Source#hello(String) 的相同，很容易识别
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptWithStaticClass() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Source> unloaded = new ByteBuddy().subclass(Source.class)
                .method(ElementMatchers.named("hello")).intercept(MethodDelegation.to(Target.class)).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Source> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends Source> aClass = loaded.getLoaded();
        Source newInstance = aClass.newInstance();
        String world = newInstance.hello("world");
        System.out.println(world);
    }

    /**
     * 委托 静态类 MethodDelegation.to(TargetSecond.class)
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptWithStaticClassAgain() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Source> unloaded = new ByteBuddy().subclass(Source.class)
                .method(ElementMatchers.named("hello")).intercept(MethodDelegation.to(TargetSecond.class)).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Source> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends Source> aClass = loaded.getLoaded();
        Source newInstance = aClass.newInstance();
        String world = newInstance.hello("world");
        System.out.println(world);
    }

    @Test
    public void interceptWithObject() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Source> unloaded = new ByteBuddy().subclass(Source.class)
                .method(ElementMatchers.named("hello")).intercept(MethodDelegation.to(new TargetSecond())).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Source> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends Source> aClass = loaded.getLoaded();
        Source newInstance = aClass.newInstance();
        String world = newInstance.hello("world");
        System.out.println(world);
    }
}
