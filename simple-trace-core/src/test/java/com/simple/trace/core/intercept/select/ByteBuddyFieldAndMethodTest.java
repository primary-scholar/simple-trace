package com.simple.trace.core.intercept.select;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.DefaultMethodCall;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

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
    public void methodInterceptTest() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Object> unloaded = new ByteBuddy().subclass(Object.class).name("com.simple.test.ObjectSub")
                .method(ElementMatchers.named("toString").and(ElementMatchers.returns(String.class)))
                .intercept(FixedValue.value("hello")).make();
        //unloaded.saveIn(new File("a"));
        Class<?> loaded = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        Object newInstance = loaded.newInstance();
        System.out.println(newInstance.toString());
    }

    /**
     * Byte Buddy 以栈的形式组织覆写方法的规则。这意味着每当你注册一条用于覆写方法的新规则时，它将被压入栈顶，并且始终首先应用， 直到添加一条新规则
     * <p>
     * 因此下列中的三个 method 匹配规则依次入栈 从栈顶向下依次为
     * 1.ElementMatchers.named("foo").and(ElementMatchers.takesArguments(1))
     * 2.ElementMatchers.named("foo")
     * 3.ElementMatchers.isDeclaredBy(Foo.class)
     * 以Foo类为例 @see Foo
     * 类中的三个方法，则按照1，2，3 的顺序进行匹配；
     * bar() 方法，匹配时只有第三个规则 可以匹配；
     * foo() 方法，匹配时 第二个规则可以匹配上，则应用第二个规则；
     * foo(Object),匹配时，第一个规则可以匹配上，则应用第一个规则；
     * 依次输出 one！，Two!，Three!
     * 因此 你应该始终最后注册更具体的方法匹配器；
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Test
    public void methodSelectTest() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Foo> unloaded = new ByteBuddy().subclass(Foo.class)
                .method(ElementMatchers.isDeclaredBy(Foo.class)).intercept(FixedValue.value("One!"))
                .method(ElementMatchers.named("foo")).intercept(FixedValue.value("Two!"))
                .method(ElementMatchers.named("foo").and(ElementMatchers.takesArguments(1))).intercept(FixedValue.value("Three!"))
                .make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Foo> load = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Foo foo = load.getLoaded().newInstance();
        System.out.println(foo.bar());
        System.out.println(foo.foo());
        System.out.println(foo.foo(new Object()));
    }

    /**
     * 调用默认方法
     * DefaultMethodCall.prioritize(First.class)
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void defaultMethodTest() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Object> unloaded = new ByteBuddy(ClassFileVersion.JAVA_V8).subclass(Object.class)
                .implement(First.class)
                .implement(Second.class)
                .method(ElementMatchers.named("info"))
                .intercept(DefaultMethodCall.prioritize(First.class)).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Object> load = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);
        Class<?> loaded = load.getLoaded();
        Object newInstance = loaded.newInstance();
        String firstInfo = ((First) newInstance).info();
        System.out.println(firstInfo);
        String secondInfo = ((Second) newInstance).info();
        System.out.println(secondInfo);
    }


}
