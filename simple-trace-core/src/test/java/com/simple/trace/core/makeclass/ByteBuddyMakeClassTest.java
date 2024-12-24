package com.simple.trace.core.makeclass;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.Test;

import java.io.IOException;


public class ByteBuddyMakeClassTest {

    /**
     * bytebuddy 默认配置提供了一个NamingStrategy（命名策略）， 它可以根据动态类的超类名称随机生成一个名称。
     * 并且和超类 在同一个包下，但是超类 Object 除外(因为jdk不允许自定义的类在 java.lang 包下) 此时的包默认为
     * net.bytebuddy.renamed.java.lang
     *
     * @throws IOException
     */
    @Test
    public void createClass() throws IOException {
        DynamicType.Unloaded<Object> unloaded = new ByteBuddy().with(new NamingStrategy.SuffixingRandom("suffix")).subclass(Object.class).make();
        // 可以通过 saveIn 方法保存class二进制到文件中
        //unloaded.saveIn(new File("a"));
        /**
         * bytebuddy 提供了三种方式创建对象；
         * subclass 通过子类的方式；
         * redefine 重新定义一个类；Byte Buddy 允许通过添加字段和方法或者替换已存在的方法实现来修改已存在的类。
         * 但是，如果方法的实现被另一个实现所替换，之前的实现就会丢失
         * rebase 通过变基的方式定义一个类；Byte Buddy 会保留所有被变基类的方法实现。Byte Buddy 会用兼容的签名复制所有方法的
         * 实现为一个私有的重命名过的方法， 而不像类重定义时丢弃覆写的方法
         */
        new ByteBuddy().subclass(Foo.class).make();
        new ByteBuddy().redefine(Foo.class).make();
        /**
         * class Foo {
         *   public String bar() { return "foo" + bar$original(); }
         *   private String bar$original() { return "bar"; }
         * }
         */
        new ByteBuddy().rebase(Foo.class).make();
    }

}
