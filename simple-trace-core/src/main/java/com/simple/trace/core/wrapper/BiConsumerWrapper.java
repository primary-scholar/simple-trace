package com.simple.trace.core.wrapper;

import com.simple.trace.core.context.TraceContextManager;
import com.simple.trace.core.context.TraceContextSnapshot;
import com.simple.trace.core.span.TraceSpan;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;

public class BiConsumerWrapper<T, U> implements BiConsumer<T, U> {

    private final BiConsumer<T, U> biConsumer;
    private final TraceContextSnapshot contextSnapshot;

    public BiConsumerWrapper(BiConsumer<T, U> biConsumer, TraceContextSnapshot contextSnapshot) {
        this.biConsumer = biConsumer;
        this.contextSnapshot = contextSnapshot;
    }

    @Override
    public void accept(T t, U u) {
        TraceSpan localSpan = TraceContextManager.createLocalSpan(StringUtils.EMPTY);
        TraceContextManager.continued(contextSnapshot);
        try {
            biConsumer.accept(t, u);
        } finally {
            TraceContextManager.stopSpan();
        }
    }

    @Override
    public BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) {
        TraceSpan localSpan = TraceContextManager.createLocalSpan(StringUtils.EMPTY);
        TraceContextManager.continued(contextSnapshot);
        try {
            return biConsumer.andThen(after);
        } finally {
            TraceContextManager.stopSpan();
        }
    }
}
