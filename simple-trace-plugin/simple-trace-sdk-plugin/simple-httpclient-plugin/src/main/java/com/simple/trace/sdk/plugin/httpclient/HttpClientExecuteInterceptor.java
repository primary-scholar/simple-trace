package com.simple.trace.sdk.plugin.httpclient;


import com.alibaba.fastjson.JSONObject;
import com.simple.trace.core.constants.HttpNounConstant;
import com.simple.trace.core.constants.NounConstant;
import com.simple.trace.core.context.TraceContextCarrier;
import com.simple.trace.core.context.TraceContextManager;
import com.simple.trace.core.span.TraceSpan;
import com.simple.trace.core.util.RequestParamResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClientExecuteInterceptor {
    private static final Logger IO = LoggerFactory.getLogger("IO");

    public void invokeBefore(HttpUriRequest request) {
        try {
            TraceContextCarrier traceContextCarrier = new TraceContextCarrier();
            TraceSpan exitSpan = TraceContextManager.createExitSpan(StringUtils.EMPTY, traceContextCarrier, StringUtils.EMPTY);
            if (request instanceof HttpGet) {
                HttpGet httpGet = (HttpGet) request;
                extractCarrier(httpGet, traceContextCarrier);
                fillSpanTag(httpGet, exitSpan);
            }
            if (request instanceof HttpPost) {
                HttpPost httpPost = (HttpPost) request;
                HttpEntityEnclosingRequestBase copy = (HttpEntityEnclosingRequestBase) httpPost.clone();
                HttpEntity entity = copy.getEntity();
                extractCarrier(httpPost, traceContextCarrier);
                //todo 需要需改参数
                fillSpanTag(httpPost, StringUtils.EMPTY, exitSpan);
            }
        } catch (Exception e) {
            IO.error("", e);
        }
    }

    public void invokeAfter(HttpUriRequest request) {
        try {
            TraceSpan traceSpan = TraceContextManager.activeSpan();
            if (request instanceof HttpGet) {
                HttpGet httpGet = (HttpGet) request;
                //todo 需要需改参数
                fillSpanTag(httpGet, StringUtils.EMPTY, traceSpan);
            }
            TraceContextManager.stopSpan();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void extractCarrier(HttpGet httpGet, TraceContextCarrier carrier) {
        Map<String, String> tags = carrier.tags();
        for (Map.Entry<String, String> next : tags.entrySet()) {
            httpGet.setHeader(next.getKey(), next.getValue());
        }
    }

    private void extractCarrier(HttpPost httpPost, TraceContextCarrier carrier) {
        Map<String, String> tags = carrier.tags();
        for (Map.Entry<String, String> next : tags.entrySet()) {
            httpPost.setHeader(next.getKey(), next.getValue());
        }
    }

    private String getRequest(String uri) {
        String param = StringUtils.EMPTY;
        try {
            param = URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            IO.error("", e);
        }
        return param;
    }

    private String getRequestPath(String uri) {
        String path = StringUtils.EMPTY;
        String request = getRequest(uri);
        int httpSlash = request.indexOf(HttpNounConstant.HTTP_SLASH);
        if (httpSlash > NumberUtils.INTEGER_MINUS_ONE) {
            path = request.substring(httpSlash + HttpNounConstant.HTTP_SLASH.length());
        }
        int slashIndex = path.indexOf(HttpNounConstant.SLASH);
        int questionIndex = path.indexOf(HttpNounConstant.QUESTION);
        if (slashIndex > NumberUtils.INTEGER_MINUS_ONE) {
            if (questionIndex > NumberUtils.INTEGER_MINUS_ONE) {
                path = path.substring(slashIndex + HttpNounConstant.SLASH.length(), questionIndex);
            }
        }
        return path;
    }

    private String getRequestQuery(String uri) {
        String query = StringUtils.EMPTY;
        String request = getRequest(uri);
        int questionIndex = request.indexOf(HttpNounConstant.QUESTION);
        if (questionIndex > NumberUtils.INTEGER_MINUS_ONE) {
            query = request.substring(questionIndex + HttpNounConstant.QUESTION.length());
        }
        return query;
    }

    private void fillSpanTag(HttpGet httpGet, TraceSpan span) {
        Map<String, String> tags = span.getTags();
        String string = httpGet.getURI().toString();
        if (StringUtils.isEmpty(tags.get(NounConstant.URI))) {
            span.addTag(NounConstant.URI, getRequestPath(string));
        }
        if (StringUtils.isEmpty(tags.get(NounConstant.QUERY))) {
            span.addTag(NounConstant.QUERY, getRequestQuery(string));
        }
        if (StringUtils.isEmpty(tags.get(NounConstant.REQUEST))) {
            span.addTag(NounConstant.REQUEST, getRequestQuery(string));
        }
        String tagCid = tags.get(NounConstant.CID);
        if (StringUtils.isEmpty(tagCid) || NumberUtils.toLong(tagCid) <= NumberUtils.LONG_ZERO) {
            Map<String, Object> params = RequestParamResolver.decodeParams(getRequest(httpGet.getURI().toString()));
            span.addTag(NounConstant.CID, params.getOrDefault(NounConstant.CID, NumberUtils.LONG_ZERO).toString());
        }
    }

    private void fillSpanTag(HttpGet httpGet, String result, TraceSpan span) {
        Map<String, String> tags = span.getTags();
        fillSpanTag(httpGet, span);
        if (StringUtils.isEmpty(tags.get(NounConstant.RESPONSE))) {
            span.addTag(NounConstant.RESPONSE, result);
        }
    }

    private void fillSpanTag(HttpPost httpPost, String request, TraceSpan span) {
        Map<String, String> tags = span.getTags();
        String uri = httpPost.getURI().toString();
        if (StringUtils.isEmpty(tags.get(NounConstant.URI))) {
            span.addTag(NounConstant.URI, getRequestPath(uri));
        }
        if (StringUtils.isEmpty(tags.get(NounConstant.QUERY))) {
            span.addTag(NounConstant.QUERY, getRequestQuery(uri));
        }
        if (StringUtils.isEmpty(tags.get(NounConstant.REQUEST))) {
            span.addTag(NounConstant.REQUEST, request);
        }
        String tagCid = tags.get(NounConstant.CID);
        if (StringUtils.isEmpty(tagCid) || NumberUtils.toLong(tagCid) <= NumberUtils.LONG_ZERO) {
            JSONObject parsed = JSONObject.parseObject(request);
            RequestParamResolver.fillCidParam(parsed);
            span.addTag(NounConstant.CID, parsed.getOrDefault(NounConstant.CID, NumberUtils.LONG_ZERO).toString());
        }
    }

    private void fillSpanTag(HttpPost httpPost, String request, TraceSpan span, String response) {
        Map<String, String> tags = span.getTags();
        fillSpanTag(httpPost, request, span);
        if (StringUtils.isEmpty(tags.get(NounConstant.RESPONSE))) {
            span.addTag(NounConstant.RESPONSE, response);
        }
    }
}
