package com.simple.trace.core.context;

import com.simple.trace.core.constants.NounConstant;
import com.simple.trace.core.span.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TraceContext {
    private static final Logger IO = LoggerFactory.getLogger("IO");
    @Getter
    private final TraceSegment traceSegment;
    private final List<TraceSpan> activeSpans = new LinkedList<>();

    public TraceContext() {
        this.traceSegment = new TraceSegment();
    }

    public TraceSpan createEntrySpan(String operationName) {
        TraceSpan entrySpan;
        TraceSpan parentSpan = peek();
        if (Objects.nonNull(parentSpan) && parentSpan.isEntry()) {
            parentSpan.setSpanName(operationName);
            entrySpan = parentSpan;
            entrySpan.start();
        } else {
            Integer parentSpanId = Objects.isNull(parentSpan) ? NounConstant.DEFAULT_PARENT_SPAN_ID :
                    parentSpan.getParentSpanId();
            entrySpan = new EntrySpan(parentSpanId++, getTraceSegment().getSpanIdAndPlus(), operationName);
            entrySpan.start();
            push(entrySpan);
        }
        fillLogMdcInfo();
        return entrySpan;
    }

    public void extract(TraceContextCarrier carrier, TraceSpan span) {
        getTraceSegment().modifyGlobalTraceId(carrier.getTraceId());
        span.setParentSpanId(carrier.getParentSpanId());
        span.setSpanId(carrier.getSpanId());
        fillLogMdcInfo();
    }

    public TraceSpan createExitSpan(String operationName, String peer) {
        TraceSpan exitSpan;
        TraceSpan parentSpan = peek();
        if (Objects.nonNull(parentSpan) && parentSpan.isExit()) {
            exitSpan = parentSpan;
        } else {
            Integer parentSpanId = Objects.isNull(parentSpan) ? NounConstant.DEFAULT_PARENT_SPAN_ID :
                    parentSpan.getParentSpanId();
            exitSpan = new ExitSpan(parentSpanId++, getTraceSegment().getSpanIdAndPlus(), operationName);
            push(exitSpan);
        }
        exitSpan.start();
        fillLogMdcInfo();
        return exitSpan;
    }

    public void inject(TraceContextCarrier carrier, TraceSpan span) {
        carrier.setTraceId(getTraceSegment().getGlobalTraceId());
        carrier.setParentSpanId(span.getParentSpanId());
        carrier.setSpanId(span.getSpanId());
        fillLogMdcInfo();
    }

    public TraceSpan createLocalSpan(String operationName) {
        TraceSpan localSpan;
        TraceSpan parentSpan = peek();
        Integer parentSpanId = Objects.isNull(parentSpan) ? NounConstant.DEFAULT_PARENT_SPAN_ID :
                parentSpan.getParentSpanId();
        localSpan = new LocalSpan(parentSpanId++, getTraceSegment().getSpanIdAndPlus(), operationName);
        localSpan.start();
        return push(localSpan);
    }

    public TraceContextSnapshot capture() {
        TraceSegment traceSegment = getTraceSegment();
        return new TraceContextSnapshot(traceSegment.getGlobalTraceId(), traceSegment.getTraceSegmentId(),
                traceSegment.getSpanIdAndPlus());
    }

    public void continued(TraceContextSnapshot contextSnapshot) {
        if (contextSnapshot.isValid()) {
            TraceSegment traceSegment = getTraceSegment();
            traceSegment.modifyGlobalTraceId(contextSnapshot.getGlobalTraceId());
            traceSegment.setCurrentIdIndex(contextSnapshot.getSpanId());
            TraceSpan traceSpan = activeSpan();
            traceSpan.setSpanId(contextSnapshot.getSpanId());
        }
    }

    public TraceSpan activeSpan() {
        TraceSpan span = peek();
        if (Objects.isNull(span)) {
            throw new IllegalStateException("No active span");
        }
        return span;
    }

    public Boolean stopSpan(TraceSpan span) {
        TraceSpan lastSpan = peek();
        if (lastSpan == span) {
            pop();
        }
        finish(lastSpan);
        return activeSpans.isEmpty();
    }

    private TraceSpan push(TraceSpan span) {
        activeSpans.add(span);
        return span;
    }

    private TraceSpan pop() {
        return activeSpans.remove(activeSpans.size() - 1);
    }

    private TraceSpan peek() {
        if (activeSpans.isEmpty()) {
            return null;
        }
        return activeSpans.get(activeSpans.size() - 1);
    }

    private void finish(TraceSpan span) {
        if (Objects.isNull(span) || span instanceof LocalSpan) {
            return;
        }
        span.stop();
        SpanLogInfo spanLogInfo = new SpanLogInfo(span, getTraceSegment());
        MDC.put(NounConstant.TRACE_ID, spanLogInfo.getTraceId());
        MDC.put(NounConstant.CID, String.valueOf(spanLogInfo.getCid()));
        MDC.put(NounConstant.PARENT_SPAN_ID, String.valueOf(spanLogInfo.getParentSpanId()));
        MDC.put(NounConstant.SPAN_ID, String.valueOf(spanLogInfo.getSpanId()));
        MDC.put(NounConstant.URI, spanLogInfo.getRemoteInterface());
        MDC.put(NounConstant.START_TIME, String.valueOf(spanLogInfo.getStarTime()));
        MDC.put(NounConstant.COST, String.valueOf(spanLogInfo.getCost()));
        MDC.put(NounConstant.QUERY, spanLogInfo.getQuery());
        MDC.put(NounConstant.REQUEST, spanLogInfo.getRequest());
        MDC.put(NounConstant.RESPONSE, spanLogInfo.getResponse());
        IO.info("");
        MDC.remove(NounConstant.TRACE_ID);
        MDC.remove(NounConstant.CID);
        MDC.remove(NounConstant.PARENT_SPAN_ID);
        MDC.remove(NounConstant.SPAN_ID);
        MDC.remove(NounConstant.URI);
        MDC.remove(NounConstant.START_TIME);
        MDC.remove(NounConstant.COST);
        MDC.remove(NounConstant.QUERY);
        MDC.remove(NounConstant.REQUEST);
        MDC.remove(NounConstant.RESPONSE);
    }

    private void fillLogMdcInfo() {
        MDC.put(NounConstant.TRACE_ID, getTraceSegment().getGlobalTraceId().getId());
    }

}
