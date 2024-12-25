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
     * 创建 java 代理
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
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
