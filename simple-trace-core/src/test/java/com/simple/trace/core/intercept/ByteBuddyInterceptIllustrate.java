package com.simple.trace.core.intercept;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;

/**
 * ByteBuddy 进行委托时 是使用AmbiguityResolver解析器来根据规则进行 识别的，
 * ByteBuddy 有默认的解析器(用户也可以自定义),其解析规则如下：
 * 1.使用@BindingPriority注解给方法分配明确地优先级，优先级高的优先匹配；
 * 2.使用IgnoreForBinding注解的方法永远不会视为目标方法；
 * 3.方法名称：如果源方法和目标方法有一个相同的名称，则该目标方法优先于其它与源方法不同名的方法
 * 4.如果两个方法通过使用@Argument注解绑定源方法的相同参数，则有更确切的参数类型的方法将会被考虑
 * 5.如果一个目标方法比另一个目标方法参数多，则前一种方法优于后一种方法
 */
public class ByteBuddyInterceptIllustrate {

    /**
     * ByteBuddy 既可以 委托 静态方法；也可以委托实例方法，和构造器方法；
     * 1.静态方法：MethodDelegation.to(Target.class)；
     * 2.实例方法：MethodDelegation.to(new Target())；包括target的子类和object中的方法；
     * 3.构造器方法：MethodDelegation.toConstructor(Class)；拦截方法的任何调用将返回一个给定的目标类型的实例
     */
    public void interceptIllustrate() {

        /**
         * ByteBuddy 内置了几个注解可以和MethodDelegation一起使用；
         * 1.@Empty：注解，Byte Buddy会注入参数(parameter)类型的默认值。对于基本类型即为零值，对于引用类型值为null；
         * 2.@StubValue：注解，注解的参数将注入拦截方法的存根值。对于返回引用类型和void的方法，会注入null。对于返回基本类型的方法，会注入相等的0的包装类型
         * 3.@FieldValue：注解，在检测类的类层次结构中定位一个字段并且将字段值注入到注解的参数中。如果没有找到注解参数兼容的可见字段， 则目标方法不会被绑定
         * 4.@FieldProxy：使用此注解，Byte Buddy 会为给定字段注入一个accessor(访问器)。如果拦截的方法表示此类方法，
         * 被访问的字段可以通过名称显式地指定，也可以从getter或setter方法名称派生。在这个注解被使用之前，需要显式地安装和注册，类似于@Pipe注解
         * 5.@Morph：这个注解的工作方式与@SuperCall注解非常相似。然而，使用这个注解允许指定用于调用超类方法参数。
         * 注意，仅当你需要调用具有与原始调用不同参数的超类方法时，才应该使用此注解，因为使用@Morph注解需要对所有参数装箱和拆箱
         * 6.@SuperMethod
         * 7.@DefaultMethod
         */

        /**
         * 创建一个带有默认构造器的Object类型的子类，该构造器被定义为简单地调用它的直接父类构造器，Object类型默认的构造器
         */
        new ByteBuddy().subclass(Object.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS).make();

    }
}
