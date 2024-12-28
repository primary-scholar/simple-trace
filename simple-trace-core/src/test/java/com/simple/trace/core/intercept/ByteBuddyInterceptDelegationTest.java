package com.simple.trace.core.intercept;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

import java.io.File;
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
        DynamicType.Loaded<Source> loaded = unloaded.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends Source> aClass = loaded.getLoaded();
        Source newInstance = aClass.newInstance();
        String world = newInstance.hello("world");
        System.out.println(world);
    }

    /**
     * 委托 静态类 MethodDelegation.to(TargetSecond.class)
     * TargetSeond.class 中定义的所有方法名都是 intercept ,因此 Byte Buddy 不需要目标方法被命名为与原方法名称一样
     * 1. 因此 Byte Buddy 不需要目标方法被命名为与原方法名称一样
     * 2. intercept(int i) 因为参数不匹配，所以不会选择
     * 3. intercept(Object) 可以匹配，但是 Byte buddy 会优先选择参数类型更明确的 方法 进行匹配；
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptWithStaticClassAgain() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Source> unloaded = new ByteBuddy().subclass(Source.class)
                .method(ElementMatchers.named("hello")).intercept(MethodDelegation.to(TargetSecond.class)).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Source> loaded = unloaded.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends Source> aClass = loaded.getLoaded();
        Source newInstance = aClass.newInstance();
        String world = newInstance.hello("world");
        System.out.println(world);
    }


    /**
     * 委托实例对象 MethodDelegation.to(new TargetThird())) TargetThird 类中是非静态方法，否者这会委托到 Object.toString()
     * 这时 方法委托选择和 静态类一致；
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptWithObject() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Source> unloaded = new ByteBuddy().subclass(Source.class)
                .method(ElementMatchers.named("hello")).intercept(MethodDelegation.to(new TargetThird())).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Source> loaded = unloaded.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends Source> aClass = loaded.getLoaded();
        Source newInstance = aClass.newInstance();
        String world = newInstance.hello("world");
        System.out.println(world);
    }
}
