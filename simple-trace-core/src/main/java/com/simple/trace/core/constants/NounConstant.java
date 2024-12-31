package com.simple.trace.core.constants;

import org.apache.commons.lang3.math.NumberUtils;

public class NounConstant {

    public static final String TRACE_ID = "_TRACE_ID_";
    public static final String PARENT_SPAN_ID = "_PARENT_SPAN_ID_";
    public static final String SPAN_ID = "_SPAN_ID_";
    public static final String START_TIME = "_START_TIME_";
    public static final String COST = "_COST_";
    public static final String URI = "_URI_";
    public static final String QUERY = "_QUERY_";
    public static final String REQUEST = "_REQUEST_";
    public static final String RESPONSE = "_RESPONSE_";

    /**
     * 该字段值是由客户端请求接口时带过来的，约定参数key为requestId
     */
    public static final String REQUEST_ID = "requestId";
    public static final String CID = "cid";
    public static final String CID_P1 = "p1";

    public static final Integer DEFAULT_PARENT_SPAN_ID = NumberUtils.INTEGER_ZERO;
    public static final Integer DEFAULT_SPAN_ID = NumberUtils.INTEGER_ZERO;
}
