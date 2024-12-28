package com.simple.trace.core.intercept.annotation;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Pipe;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * MethodDelegation 提供了多个注解 进行委托方法的匹配，
 * 1.@Argument;
 * 2.@AllArguments;
 * 3.@This;
 * 4.@Origin;
 * 5.@SuperCall
 * 6.@Super
 * 7.@RuntimeType
 * 8.@Pipe
 * 1.默认使用 @argument 注解的行为进行方法的匹配(如果Target 中没有明确使用注解)
 * source：function(Object oo,Object os) ---> target: anyName(@Argument(0) Object oo,@Argument(1) Object os)
 * ByteBuddy 选择 target 中的方法时，会根据source function() 方法中给定的 参数类型，顺序和个数，匹配target中 参数类型，顺序和个数一致的方法；
 * 2.@AllArguments 使用该注解的参数，则要求 参数必须是数组，并且数组参数完全匹配；byte buddy 在每次调用时 都会复制一份参数到目标方法上；
 * 3.@This; 使用该注解，表明 委托代理的 target 是实例(不能是静态类)，并且是 target 当前实例；使用@This注解的一个典型原因是获取一个实例字段的访问权限
 * 4.@Origin;
 * 5.@SuperCall 在intercept中调用目标类的方法
 * 6.@Super 通过注入目标对象。然后调用目标对象的方法
 * 7.@RuntimeType 注解是以放弃类型安全检查,进行方法匹配 @see interceptRuntimeType()
 * 8.@Pipe 可以将一个拦截的方法调用转发到另一个对象
 */
public class ByteBuddyAnnotationTest {

    /**
     * SuperCall 注解，调用目标对象的 方法，这里会调用到(DataBase)中的方法；
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptSuperCall() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<DataBase> unloaded = new ByteBuddy().subclass(DataBase.class).method(ElementMatchers.named("load"))
                .intercept(MethodDelegation.to(SuperCallAnnotationInterceptor.class)).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<DataBase> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends DataBase> aClass = loaded.getLoaded();
        DataBase newInstance = aClass.newInstance();
        List<String> info = newInstance.load("world");
        System.out.println(info);
    }

    /**
     * Super 通过注入目标对象。然后调用目标对象的方法
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptSuper() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<DataBase> unloaded = new ByteBuddy().subclass(DataBase.class).method(ElementMatchers.named("load"))
                .intercept(MethodDelegation.to(SuperAnnotationInterceptor.class)).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<DataBase> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends DataBase> aClass = loaded.getLoaded();
        DataBase newInstance = aClass.newInstance();
        List<String> info = newInstance.load("world");
        System.out.println(info);
    }

    /**
     * RuntimeType Byte Buddy 允许给方法和方法参数添加@RuntimeType注解，来终止严格类型检查以支持运行时类型转换；
     * 这样可以使用interceptor中的一个方法去 委托Foo.class 中两个方法签名不同的两个方法；
     * <p>
     * RuntimeType 注解是以放弃类型安全检查，如果你将不兼容的类型混淆，会导致ClassCastException(类转换异常)
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptRuntimeType() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Foo> unloaded = new ByteBuddy().subclass(Foo.class).method(ElementMatchers.named("info"))
                .intercept(MethodDelegation.to(RuntimeTypeAnnotationInterceptor.class)).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Foo> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends Foo> aClass = loaded.getLoaded();
        Foo newInstance = aClass.newInstance();
        String hello = newInstance.info("hello");
        System.out.println(hello);
        Integer info = newInstance.info(10);
        System.out.println(info);
    }

    /**
     * 通过使用@Pipe注解，你可以将一个拦截的方法调用转发到另一个对象,因为@Pipe 注解没有与MethodDelegation一起预先注册，所以这里需要手动注册
     * MethodDelegation.withDefaultConfiguration().withBinders(Pipe.Binder.install(Function.class))
     * .to(new PipeAnnotationInterceptor(new DataBase()))
     * <p>
     * 这里 我们只转发了我们本地创建的实例的调用,通过子类化一个类型来拦截一个方法的优势在于，这种方法允许增强一个存在的实例。
     * 此外，你通常会在实例级别注册拦截器，而不是在类级别注册一个静态拦截器; new PipeAnnotationInterceptor(new DataBase())
     * <p>
     * 这里是委托给了实例对象 new PipeAnnotationInterceptor(new DataBase())
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void interceptPipe() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<DataBase> unloaded = new ByteBuddy().subclass(DataBase.class).method(ElementMatchers.named("load"))
                .intercept(MethodDelegation.withDefaultConfiguration().withBinders(Pipe.Binder.install(Function.class))
                        .to(new PipeAnnotationInterceptor(new DataBase()))).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<DataBase> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Class<? extends DataBase> aClass = loaded.getLoaded();
        DataBase newInstance = aClass.newInstance();
        List<String> world = newInstance.load("world");
        System.out.println(world);
    }
}
