package com.simple.trace.core.span;


import com.simple.trace.core.constants.NounConstant;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class TraceSpan implements AbstractSpan {
    private static final Logger IO = LoggerFactory.getLogger("IO");
    @Getter
    protected Integer parentSpanId;
    @Getter
    protected Integer spanId;
    protected String spanName;
    @Getter
    protected Long startTime;
    @Getter
    protected Long endTime;
    @Getter
    protected Map<String, String> tags;

    public TraceSpan(Integer parentSpanId, Integer spanId, String spanName) {
        this.parentSpanId = parentSpanId;
        this.spanId = spanId;
        this.spanName = spanName;
        this.startTime = System.currentTimeMillis();
        this.tags = new HashMap<>();
    }

    public TraceSpan setSpanName(String spanName) {
        this.spanName = spanName;
        return this;
    }

    public TraceSpan start() {
        this.startTime = System.currentTimeMillis();
        return this;
    }

    public TraceSpan stop() {
        this.endTime = System.currentTimeMillis();
        return this;
    }

    public void setParentSpanId(Integer parentSpanId) {
        this.parentSpanId = parentSpanId;
        this.tags.put(NounConstant.PARENT_SPAN_ID, String.valueOf(parentSpanId));
    }

    public void setSpanId(Integer spanId) {
        this.spanId = spanId;
        this.tags.put(NounConstant.SPAN_ID, String.valueOf(spanId));
    }

    public void addTag(String key, String value) {
        this.tags.put(key, value);
    }

}
