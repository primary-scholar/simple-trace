package com.simple.trace.core.context;

import com.simple.trace.core.id.DistributedId;
import lombok.Getter;

import java.util.Objects;


@Getter
public class TraceContextSnapshot {
    private final DistributedId globalTraceId;
    private final String traceSegmentId;
    private final Integer spanId;

    public TraceContextSnapshot(DistributedId globalTraceId, String traceSegmentId, Integer spanId) {
        this.globalTraceId = globalTraceId;
        this.traceSegmentId = traceSegmentId;
        this.spanId = spanId;
    }

    public Boolean isValid() {
        return Objects.nonNull(globalTraceId) && Objects.nonNull(traceSegmentId) && Objects.nonNull(spanId);
    }

    public Boolean isFromCurrent() {
        TraceContext traceContext = TraceContextManager.get();
        return Objects.nonNull(traceSegmentId) && traceSegmentId.equals(traceContext.getTraceSegment().getTraceSegmentId());
    }

}
