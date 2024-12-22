package com.simple.trace.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

public class SimpleTraceAgent {
    private static Logger LOGGER = LoggerFactory.getLogger(SimpleTraceAgent.class);

    public static void premain(String agentArgs, Instrumentation inst) {

    }
}
