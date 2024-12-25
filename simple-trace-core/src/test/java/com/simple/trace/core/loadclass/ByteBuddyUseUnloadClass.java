package com.simple.trace.core.loadclass;

import com.simple.trace.core.loadclass.model.Bar;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.pool.TypePool;
import org.junit.Test;

public class ByteBuddyUseUnloadClass {

    /**
     * 在使用类之前显式地重定义类，这里我们在jvm内置的类加载器加载之前重新定义了 com.simple.trace.core.loadclass.model.Bar 该类并加载；
     * <p>
     * 注意 这里使用 TypePool 来提供类描述时 不能使用 Bar 类的 Bar.class.getCanonicalName() 方式 获取全限定名，因为这样jvm会在我们
     * 重新定义类之前加载它；
     * 处理未加载的类时，我们还需要指定一个ClassFileLocator(类文件定位器)，来定位类的类文件
     *
     * @throws Exception
     */
    @Test
    public void useUnloadClassTest() throws Exception {
        TypePool typePool = TypePool.Default.ofSystemLoader();
        DynamicType.Unloaded<Object> unloaded = new ByteBuddy().redefine(typePool.describe("com.simple.trace.core" +
                        ".loadclass.model.Bar").resolve(),
                ClassFileLocator.ForClassLoader.ofSystemLoader()).defineField("fieldStr", String.class).make();
        Class<?> loaded =
                unloaded.load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
        System.out.println(loaded.getDeclaredField("fieldStr"));

    }
}
