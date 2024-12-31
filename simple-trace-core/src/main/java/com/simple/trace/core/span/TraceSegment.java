package com.simple.trace.core.span;

import com.simple.trace.core.id.DistributedId;
import com.simple.trace.core.id.GlobalIdGenerator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class TraceSegment {
    @Getter
    private DistributedId globalTraceId;
    @Getter
    private final String traceSegmentId;
    @Setter
    private Integer currentIdIndex;
    private final List<TraceSpan> spanList = new LinkedList<>();

    public TraceSegment() {
        this.traceSegmentId = GlobalIdGenerator.generate();
        this.globalTraceId = new DistributedId();
        this.currentIdIndex = 0;
    }

    public void modifyGlobalTraceId(DistributedId distributedId) {
        if (Objects.isNull(distributedId) || StringUtils.isEmpty(distributedId.getId())) {
            throw new RuntimeException("invalid globalTraceId");

        }
        this.globalTraceId = distributedId;
    }

    public Integer getSpanIdAndPlus() {
        Integer oldSpanId = currentIdIndex;
        this.currentIdIndex += 1;
        return oldSpanId;
    }

    public void addTraceSpan(TraceSpan traceSpan) {
        this.spanList.add(traceSpan);
    }

}
