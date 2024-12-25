package com.simple.trace.core.transform;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ByteBuddyTransformAgent {

    /**
     * 创建 java 代理
     *
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        new AgentBuilder.Default().type(ElementMatchers.isAnnotatedWith(CustomAnnotation.class)).transform(new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                System.out.println(builder);
                System.out.println(typeDescription);
                System.out.println(classLoader);
                System.out.println(module);
                System.out.println(protectionDomain);
                return builder.method(ElementMatchers.named("print")).intercept(FixedValue.value("intercepted"));
            }
        }).installOn(instrumentation);
    }

}
