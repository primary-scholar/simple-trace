package com.simple.trace.core.transform;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * 如需通过命令 java -javaagent:simple-trace-core-1.0-SNAPSHOT.jar -jar simple-trace-core-1.0-SNAPSHOT.jar 验证
 * javaagent 的代理功能，可按如下步骤进行操作(simple-trace-core-1.0-SNAPSHOT.jar 是执行maven 打包后的jar包，该jar包中
 * 即含有main方法，也含有premain 方法)
 * <p>
 * 1.把当前包(test路径下的)中的 类
 *
 * @see Bar
 * @see ByteBuddyTransformAgent
 * @see CustomAnnotation
 * @see Foo
 * @see PlainMainClass
 * 五个类复制到(java 路径下)
 * 2.按照 pom.xml 文件中maven-shade-plugin 插件中的配置 执行 打包命令 mvn clean package -DskipTests
 * 之后 就可通过 java -javaagent:(agent.jar) main.jar 进行代理验证了
 */
public class ByteBuddyTransformAgent {

    /**
     * 创建 java 代理 (这里需要使用插件打包,打成含有manifest清单的agent.jar 包)
     * <p>
     * 使用 Byte buddy的 transform 代理功能可以拦截java应用中进行的任何类的加载活动；
     * 下面这个case 使用 AgentBuilder，对所有带有CustomAnnotation注解的类的 print 方法进行代理，
     * transform的结果 即对符合条件的类的print方法返回固定值；
     * 通过java -javaagent:(agent.jar) -jar main.jar 验证 可输出
     * Bar print
     * Bar print
     * it was intercepted
     * 这种结果
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        /**
         * 这里 typeDescription 即带有 CustomAnnotation 注解的 com.simple.trace(.transform).core.Foo 类；
         * 依据测试 这里的参数依次为：
         * builder：net.bytebuddy.dynamic.scaffold.inline.RebaseDynamicTypeBuilder@43fce89a
         * typeDescription：class com.simple.trace.core.Foo
         * classLoader：jdk.internal.loader.ClassLoaders$AppClassLoader@4e0e2f2a
         * module：unnamed module @2530c12
         * protectionDomain：ProtectionDomain  (file:/Users/mimu/github/simple-trace/simple-trace-core/target/simple-trace-core-1.0-SNAPSHOT.jar <no signer certificates>)
         *  jdk.internal.loader.ClassLoaders$AppClassLoader@4e0e2f2a
         *  <no principals>
         *  java.security.Permissions@63376bed (
         *  ("java.lang.RuntimePermission" "exitVM")
         *  ("java.io.FilePermission" "/Users/mimu/github/simple-trace/simple-trace-core/target/simple-trace-core-1.0-SNAPSHOT.jar" "read")
         * )
         */
        new AgentBuilder.Default().type(ElementMatchers.isAnnotatedWith(CustomAnnotation.class)).transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
            /*System.out.println(builder);
            System.out.println(typeDescription);
            System.out.println(classLoader);
            System.out.println(module);
            System.out.println(protectionDomain);*/
            return builder.method(ElementMatchers.named("print")).intercept(FixedValue.value("it was intercepted"));
        }).installOn(instrumentation);
    }

}
