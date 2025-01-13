package com.simple.trace.core.transform;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class ByteBuddyTransformDefineFieldAgent {
    private static final String INTERNAL_FIELD_NAME = "internalField";

    public static void premain(String agentArgs, Instrumentation instrumentation) {

        ResettableClassFileTransformer transformer = new AgentBuilder.Default().with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(ElementMatchers.isAnnotatedWith(CustomAnnotation.class))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                    System.out.println(builder);
                    System.out.println(typeDescription.getCanonicalName());
                    System.out.println(classLoader);
                    System.out.println(module);
                    System.out.println(protectionDomain);
                    return builder.defineField(INTERNAL_FIELD_NAME, Object.class, Opcodes.ACC_PUBLIC | Opcodes.ACC_VOLATILE);
                }).with(new AgentBuilder.Listener() {
                    @Override
                    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
                    }

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
                        System.out.println(typeDescription.getCanonicalName());
                        /*byte[] bytes = dynamicType.getBytes();
                        try {
                            Files.write(Paths.get(typeDescription.getCanonicalName() + ".class"), bytes);
                        } catch (IOException e) {
                            System.out.println("listener transformation error write file " + e);
                        }*/
                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                    }

                    @Override
                    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
                    }
                }).installOn(instrumentation);

    }
}
