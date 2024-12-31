package com.simple.trace.core.span;


public class EntrySpan extends TraceSpan {
    public EntrySpan(Integer parentSpanId, Integer spanId, String spanName) {
        super(parentSpanId, spanId, spanName);
    }

    @Override
    public Boolean isEntry() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isExit() {
        return Boolean.FALSE;
    }
}
