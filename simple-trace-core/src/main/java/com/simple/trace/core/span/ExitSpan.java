package com.simple.trace.core.span;


public class ExitSpan extends TraceSpan {
    public ExitSpan(Integer parentSpanId, Integer spanId, String spanName) {
        super(parentSpanId, spanId, spanName);
    }

    @Override
    public Boolean isEntry() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean isExit() {
        return Boolean.TRUE;
    }
}
