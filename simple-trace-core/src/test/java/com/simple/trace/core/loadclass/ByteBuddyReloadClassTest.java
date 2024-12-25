package com.simple.trace.core.loadclass;

import com.simple.trace.core.loadclass.model.Bar;
import com.simple.trace.core.loadclass.model.Foo;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import org.junit.Test;

import java.lang.instrument.Instrumentation;

public class ByteBuddyReloadClassTest {

    /**
     * <p>
     * 重新加载类
     * byte buddy可以利用 Java 虚拟机的HotSwap功能， 即使在类被加载之后，也可以重新定义类并加载它；
     * 但是这里 Java 的 HotSwap 功能有巨大的限制，HotSwap的当前实现要求重定义的类在重定义前后应用相同的类模式。
     * 即当重新加载类时，不允许添加方法或字段
     * 同时 类重定义不适用于具有显式的类初始化程序的方法(类中的静态块)的类， 因为该初始化程序也需要复制到额外的方法中
     *
     * @throws Exception
     */
    @Test
    public void reloadClassTest() throws Exception {
        Instrumentation install = ByteBuddyAgent.install();
        Foo foo = new Foo(); // 加载并初始化 Foo 类
        DynamicType.Unloaded<Foo> unloaded = new ByteBuddy().redefine(Foo.class).name(Bar.class.getName()).make();
        //unloaded.saveIn(new File("a"));
        DynamicType.Loaded<Foo> load = unloaded.load(Foo.class.getClassLoader(),
                ClassReloadingStrategy.fromInstalledAgent());
        Class<? extends Foo> loaded = load.getLoaded();
        Foo newInstance = loaded.newInstance();
        System.out.println(newInstance.function());
    }

    /**
     * 这里可能有点 违反直觉，Byte Buddy 重新定义的是 Bar 类，重命名成了 Foo 的名称；
     * 再次调用 Foo 类的对象方法，实际调用的却是 bar类中 的方法
     * 这里 添加 jvm 参数 -XX:+EnableDynamicAgentLoading 后可避免 warning 信息
     *
     * @throws Exception
     */
    @Test
    public void reloadClassTestContrast() throws Exception {
        Instrumentation install = ByteBuddyAgent.install();
        Foo foo = new Foo(); // 加载并初始化 Foo 类
        /**
         * 重新定义 bar 类，把 bar 类 重命名为 foo 类的名字
         */
        DynamicType.Unloaded<Bar> unloaded = new ByteBuddy().redefine(Bar.class).name(Foo.class.getName()).make();
        //unloaded.saveIn(new File("a"));
        unloaded.load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        // 这里 再次调用foo的方法，实际上调用的却是 bar 类的方法；
        System.out.println(foo.function());
    }
}
