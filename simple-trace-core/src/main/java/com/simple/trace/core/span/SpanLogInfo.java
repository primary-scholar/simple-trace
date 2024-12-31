package com.simple.trace.core.span;

import com.simple.trace.core.constants.NounConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
public class SpanLogInfo implements Serializable {
    private String traceId;
    private Integer parentSpanId;
    private Integer spanId;
    private Long starTime;
    private Long cid;
    private Integer cost;
    private String remoteInterface;
    private String query;
    private String request;
    private String response;

    public SpanLogInfo(TraceSpan span, TraceSegment traceSegment) {
        if (Objects.isNull(span)) {
            return;
        }
        Map<String, String> tags = span.getTags();
        this.traceId = traceSegment.getGlobalTraceId().getId();
        this.parentSpanId = span.getParentSpanId();
        this.spanId = span.getSpanId();
        this.starTime = span.getStartTime();
        this.cid = NumberUtils.toLong(tags.get(NounConstant.CID));
        this.cost = Math.toIntExact(span.getEndTime() - span.getStartTime());
        this.remoteInterface = tags.get(NounConstant.URI);
        this.query = tags.get(NounConstant.QUERY);
        this.request = tags.get(NounConstant.REQUEST);
        this.response = tags.get(NounConstant.RESPONSE);
    }
}
