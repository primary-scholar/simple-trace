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
     * 委托方法的选择 MethodDelegation.to()
     * MethodDelegation 提供了多个注解 进行委托方法的匹配，
     * 1.@Argument;
     * 2.@AllArguments;
     * 3.@This;
     * 4.@Origin;
     * 1.默认使用 @argument 注解的行为进行方法的匹配(如果Target 中没有明确使用注解)
     * source：function(Object oo,Object os) ---> target: anyName(@Argument(0) Object oo,@Argument(1) Object os)
     * ByteBuddy 选择 target 中的方法时，会根据source function() 方法中给定的 参数类型，顺序和个数，匹配target中 参数类型，顺序和个数一致的方法；
     * 2.@AllArguments 使用该注解的参数，则要求 参数必须是数组，并且数组参数完全匹配；byte buddy 在每次调用时 都会复制一份参数到目标方法上；
     * 3.@This; 使用该注解，表明 委托代理的 target 是实例(不能是静态类)，并且是 target 当前实例；使用@This注解的一个典型原因是获取一个实例字段的访问权限
     * 4.@Origin;
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptMethodSelect() throws InstantiationException, IllegalAccessException, IOException {
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

    @Test
    public void interceptWithObject() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Source> unloaded = new ByteBuddy().subclass(Source.class)
                .method(ElementMatchers.named("hello")).intercept(MethodDelegation.to(new TargetSecond())).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Source> loaded = unloaded.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends Source> aClass = loaded.getLoaded();
        Source newInstance = aClass.newInstance();
        String world = newInstance.hello("world");
        System.out.println(world);
    }
}
