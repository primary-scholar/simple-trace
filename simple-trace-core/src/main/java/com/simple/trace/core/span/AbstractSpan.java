package com.simple.trace.core.span;

public interface AbstractSpan {

    /**
     * 是否是 entrySpan 节点
     *
     * @return Boolean
     */
    Boolean isEntry();

    /**
     * 是否是 exitSpan 节点
     *
     * @return Boolean
     */
    Boolean isExit();
}
