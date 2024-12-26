package com.simple.trace.core.loadclass;

import com.simple.trace.core.loadclass.model.Bar;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

import java.io.IOException;

public class ByteBuddyLoadClassTest {

    /**
     * 加载类
     * <p>
     * java 中所有的类都通过 ClassLoader(类加载器)加载；在 jdk 中有系统类加载器，应用程序类加载器(jdk版本不同，加载器种类不同)；
     * 不管那种加载器，其实都无法加载我们动态创建的class二进制文件；
     * <p>
     * 为了加载应用动态创建的class二进制文件，Byte Buddy 提供了几种开箱即用的类加载策略，通过不同的策略创建 不同的新的 classloader；
     * 每种策略都会创建一个新的 classloader；
     * ClassLoadingStrategy.Default.WRAPPER；创建一个新的，经过包装的 classloader
     * ClassLoadingStrategy.Default.CHILD_FIRST; 创建一个类似WRAPPER的但具有孩子优先语义的类加载器,子类加起优先加载一个类(不使用双亲委派中的父类加载器优先加载)
     * ClassLoadingStrategy.Default.INJECTION; 用反射注入一个动态类型
     * WRAPPER和CHILD_FIRST策略可以在所谓的manifest(清单)版本中使用，即使在类加载后，也会保留类的二进制格式；所以可以通过ClassLoader::getResourceAsStream方法访问
     * INJECTION 不可在manifest中使用，并且不可使用 ClassLoader::getResourceAsStream方法访问
     *
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void modifyFunctionReturnValue() throws InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<Bar> unloaded =
                new ByteBuddy().subclass(Bar.class).method(ElementMatchers.named("function")).intercept(FixedValue.value("none argument")).make();
        //unloaded.saveIn(new File("a"));
        Class<? extends Bar> aClass = unloaded.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        Bar newInstance = aClass.newInstance();
        String function = newInstance.function();
        System.out.println(function);
    }

}
