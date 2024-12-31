package com.simple.trace.core.wrapper;

import com.simple.trace.core.context.TraceContextManager;
import com.simple.trace.core.context.TraceContextSnapshot;
import com.simple.trace.core.span.TraceSpan;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class FunctionWrapper<T, R> implements Function<T, R> {

    private final Function<T, R> function;
    private final TraceContextSnapshot contextSnapshot;

    public FunctionWrapper(Function<T, R> function, TraceContextSnapshot contextSnapshot) {
        this.function = function;
        this.contextSnapshot = contextSnapshot;
    }

    @Override
    public R apply(T t) {
        TraceSpan localSpan = TraceContextManager.createLocalSpan(StringUtils.EMPTY);
        TraceContextManager.continued(contextSnapshot);
        try {
            return function.apply(t);
        } finally {
            TraceContextManager.stopSpan();
        }
    }

    @Override
    public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        TraceSpan localSpan = TraceContextManager.createLocalSpan(StringUtils.EMPTY);
        TraceContextManager.continued(contextSnapshot);
        try {
            return function.compose(before);
        } finally {
            TraceContextManager.stopSpan();
        }

    }

    @Override
    public <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        TraceSpan localSpan = TraceContextManager.createLocalSpan(StringUtils.EMPTY);
        TraceContextManager.continued(contextSnapshot);
        try {
            return function.andThen(after);
        } finally {
            TraceContextManager.stopSpan();
        }
    }
}
