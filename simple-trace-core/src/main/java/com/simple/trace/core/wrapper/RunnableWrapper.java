package com.simple.trace.core.wrapper;

import com.simple.trace.core.context.TraceContextManager;
import com.simple.trace.core.context.TraceContextSnapshot;
import com.simple.trace.core.span.TraceSpan;
import org.apache.commons.lang3.StringUtils;

public class RunnableWrapper implements Runnable {
    private final Runnable runnable;
    private final TraceContextSnapshot contextSnapshot;

    public RunnableWrapper(Runnable runnable, TraceContextSnapshot contextSnapshot) {
        this.runnable = runnable;
        this.contextSnapshot = contextSnapshot;
    }

    @Override
    public void run() {
        TraceSpan localSpan = TraceContextManager.createLocalSpan(StringUtils.EMPTY);
        TraceContextManager.continued(contextSnapshot);
        try {
            runnable.run();
        } finally {
            TraceContextManager.stopSpan();
        }
    }
}
