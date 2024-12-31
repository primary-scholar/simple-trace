package com.simple.trace.core.wrapper;

import com.simple.trace.core.context.TraceContextManager;
import com.simple.trace.core.context.TraceContextSnapshot;
import com.simple.trace.core.span.TraceSpan;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

public class ConsumerWrapper<T> implements Consumer<T> {

    private final Consumer<T> consumer;
    private final TraceContextSnapshot contextSnapshot;

    public ConsumerWrapper(Consumer<T> consumer, TraceContextSnapshot contextSnapshot) {
        this.consumer = consumer;
        this.contextSnapshot = contextSnapshot;
    }

    @Override
    public void accept(T t) {
        TraceSpan localSpan = TraceContextManager.createLocalSpan(StringUtils.EMPTY);
        TraceContextManager.continued(contextSnapshot);
        try {
            consumer.accept(t);
        } finally {
            TraceContextManager.stopSpan();
        }
    }
}
