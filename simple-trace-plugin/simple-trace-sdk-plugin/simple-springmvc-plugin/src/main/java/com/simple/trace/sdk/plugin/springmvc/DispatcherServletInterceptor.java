package com.simple.trace.sdk.plugin.springmvc;

import com.alibaba.fastjson.JSONObject;
import com.simple.trace.core.constants.NounConstant;
import com.simple.trace.core.context.TraceContextCarrier;
import com.simple.trace.core.context.TraceContextManager;
import com.simple.trace.core.span.TraceSpan;
import com.simple.trace.core.util.RequestParamResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DispatcherServletInterceptor {
    private static final Logger IO = LoggerFactory.getLogger("IO");

    public void invokeBefore(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!(request instanceof ContentCachingRequestWrapper)) {
                request = new ContentCachingRequestWrapper(request);
            }
            if (!(response instanceof ContentCachingResponseWrapper)) {
                response = new ContentCachingResponseWrapper(response);
            }
            request.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            TraceContextCarrier traceContextCarrier = new TraceContextCarrier();
            fillCarrier(request, traceContextCarrier);
            TraceSpan entrySpan = TraceContextManager.createEntrySpan(StringUtils.EMPTY, traceContextCarrier);
            fillSpanTag(request, entrySpan);
        } catch (Exception e) {
            IO.error("", e);
        }

    }

    public void invokeAfter(HttpServletRequest request, HttpServletResponse response) {
        try {
            TraceSpan traceSpan = TraceContextManager.activeSpan();
            fillSpanTag(request, response, traceSpan);
            TraceContextManager.stopSpan();
        } catch (Exception e) {
            IO.error("", e);
        }
    }

    private String getQueryString(HttpServletRequest request) {
        String queryString = StringUtils.EMPTY;
        try {
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
                queryString = URLDecoder.decode(requestWrapper.getQueryString(), requestWrapper.getCharacterEncoding());
            } else {
                queryString = URLDecoder.decode(request.getQueryString(), request.getCharacterEncoding());
            }
        } catch (UnsupportedEncodingException e) {
            IO.error("", e);
        }
        return queryString;
    }

    private Map<String, Object> getParameter(HttpServletRequest request) {
        String queryString = getQueryString(request);
        if (HttpMethod.GET.equals(HttpMethod.resolve(request.getMethod()))) {
            return RequestParamResolver.decodeParams(queryString);
        }
        if (HttpMethod.POST.equals(HttpMethod.resolve(request.getMethod()))) {
            Map<String, Object> urlParam = RequestParamResolver.decodeParams(queryString);
            String postQuery = getRequest(request);
            JSONObject postQueryParam = JSONObject.parseObject(postQuery);
            if (Objects.isNull(postQueryParam)) {
                postQueryParam = new JSONObject();
            }
            postQueryParam.putAll(urlParam);
            RequestParamResolver.fillCidParam(postQueryParam);
            return Objects.isNull(postQueryParam) ? Collections.emptyMap() : postQueryParam;
        }
        return Collections.emptyMap();
    }

    private String getRequest(HttpServletRequest request) {
        String param = StringUtils.EMPTY;
        try {
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
                if (HttpMethod.GET.equals(HttpMethod.resolve(request.getMethod()))) {
                    param = URLDecoder.decode(requestWrapper.getQueryString(), requestWrapper.getCharacterEncoding());
                }
                if (HttpMethod.POST.equals(HttpMethod.resolve(request.getMethod()))) {
                    String encoding = requestWrapper.getCharacterEncoding();
                    byte[] content = requestWrapper.getContentAsByteArray();
                    param = new String(content, encoding);
                }
            } else if (request instanceof StandardMultipartHttpServletRequest) {
                StandardMultipartHttpServletRequest multipartRequest = (StandardMultipartHttpServletRequest) request;
                Map<String, String[]> parameterMap = multipartRequest.getParameterMap();
                HashMap<String, Object> parameter = new HashMap<>();
                for (Map.Entry<String, String[]> keyValue : parameterMap.entrySet()) {
                    parameter.put(keyValue.getKey(), keyValue.getValue()[0]);
                }
                param = JSONObject.toJSONString(parameter);
            }
        } catch (UnsupportedEncodingException e) {
            IO.error("", e);
        }
        return param;
    }

    private String getResponse(HttpServletResponse response) {
        try {
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) response;
                String responseStr;
                String encoding = responseWrapper.getCharacterEncoding();
                byte[] content = responseWrapper.getContentAsByteArray();
                responseStr = new String(content, encoding);
                return responseStr;
            }
        } catch (UnsupportedEncodingException e) {
            IO.error("", e);
        }
        return StringUtils.EMPTY;
    }

    private void fillCarrier(HttpServletRequest request, TraceContextCarrier carrier) {
        Map<String, String> tags = carrier.emptyTags();
        tags.replaceAll((k, v) -> request.getHeader(k));
        Map<String, Object> parameter = getParameter(request);
        if (Objects.isNull(carrier.getTraceId())) {
            String traceId = tags.get(NounConstant.TRACE_ID);
            if (StringUtils.isEmpty(traceId)) {
                Object requestId = parameter.get(NounConstant.REQUEST_ID);
                traceId = Objects.isNull(requestId) ? StringUtils.EMPTY : requestId.toString();
            }
            carrier.setTraceId(TraceContextManager.createDistributedId(traceId));
        }
        if (Objects.isNull(carrier.getParentSpanId())) {
            String parentSpanId = tags.get(NounConstant.PARENT_SPAN_ID);
            if (StringUtils.isEmpty(parentSpanId)) {
                parentSpanId = request.getHeader(NounConstant.PARENT_SPAN_ID);
                if (StringUtils.isEmpty(parentSpanId)) {
                    parentSpanId = String.valueOf(NounConstant.DEFAULT_PARENT_SPAN_ID);
                }
            }
            carrier.setParentSpanId(NumberUtils.toInt(parentSpanId, NounConstant.DEFAULT_PARENT_SPAN_ID));
        }
        if (Objects.isNull(carrier.getSpanId())) {
            String spanId = tags.get(NounConstant.SPAN_ID);
            if (StringUtils.isEmpty(spanId)) {
                spanId = request.getHeader(NounConstant.SPAN_ID);
                if (StringUtils.isEmpty(spanId)) {
                    spanId = String.valueOf(NounConstant.DEFAULT_SPAN_ID);
                }
            }
            carrier.setSpanId(NumberUtils.toInt(spanId, NounConstant.DEFAULT_SPAN_ID));
        }

    }

    private void fillSpanTag(HttpServletRequest request, TraceSpan span) {
        Map<String, Object> parameter = getParameter(request);
        Map<String, String> tags = span.getTags();
        if (StringUtils.isEmpty(tags.get(NounConstant.URI))) {
            span.addTag(NounConstant.URI, request.getRequestURI());
        }
        if (StringUtils.isEmpty(tags.get(NounConstant.QUERY))) {
            span.addTag(NounConstant.QUERY, getQueryString(request));
        }
        if (StringUtils.isEmpty(tags.get(NounConstant.REQUEST))) {
            span.addTag(NounConstant.REQUEST, getRequest(request));
        }
        String tagCid = tags.get(NounConstant.CID);
        if (StringUtils.isEmpty(tagCid) || NumberUtils.toLong(tagCid) <= NumberUtils.LONG_ZERO) {
            span.addTag(NounConstant.CID, parameter.getOrDefault(NounConstant.CID, NumberUtils.LONG_ZERO).toString());
        }
    }

    private void fillSpanTag(HttpServletRequest request, HttpServletResponse response, TraceSpan span) {
        fillSpanTag(request, span);
        Map<String, String> tags = span.getTags();
        if (StringUtils.isEmpty(tags.get(NounConstant.RESPONSE))) {
            span.addTag(NounConstant.RESPONSE, getResponse(response));
        }
    }
}
