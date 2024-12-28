package com.simple.trace.core.intercept.annotation;

import java.util.Arrays;
import java.util.List;

public class DataBase {
    public List<String> load(String info) {
        return Arrays.asList(String.format("bar %s", info), String.format("foo %s", info));
    }
}
