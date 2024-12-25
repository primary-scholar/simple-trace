package com.simple.trace.core.select;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

public class ByteBuddyFieldAndMethodTest {

    /***
     * 方法选择
     *
     * method()：覆盖的方法，这里通过ElementMatchers进行方法选择
     * intercept()：决定方法的实现，该方法的参数为 一个Implementation类型的实现；
     *
     * 下面的case 即覆盖object.class 中方法名为toString()且返回值为String的方法，
     * 覆盖后的方法实现为：返回固定值 hello
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Test
    public void methodInterceptTest() throws InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<Object> unloaded = new ByteBuddy().subclass(Object.class).name("com.simple.test.ObjectSub")
                .method(ElementMatchers.named("toString").and(ElementMatchers.returns(String.class)))
                .intercept(FixedValue.value("hello")).make();
        Class<?> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        Object newInstance = loaded.newInstance();
        System.out.println(newInstance.toString());
    }

    @Test
    public void methodSelectTest() throws InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<Foo> unloaded = new ByteBuddy().subclass(Foo.class)
                .method(ElementMatchers.isDeclaredBy(Foo.class)).intercept(FixedValue.value("One!"))
                .method(ElementMatchers.named("foo")).intercept(FixedValue.value("Two!"))
                .method(ElementMatchers.named("foo").and(ElementMatchers.takesArguments(1))).intercept(FixedValue.value("Three!"))
                .make();
        DynamicType.Loaded<Foo> load = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);

        Foo foo = load.getLoaded().newInstance();
        System.out.println(foo.bar());
        System.out.println(foo.foo());
        System.out.println(foo.foo(new Object()));
    }

}
