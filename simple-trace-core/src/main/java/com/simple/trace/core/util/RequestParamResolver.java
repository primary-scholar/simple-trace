package com.simple.trace.core.util;

import com.simple.trace.core.constants.NounConstant;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RequestParamResolver {

    public static Map<String, Object> decodeParams(String s) {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String name = null;
        int pos = 0; // Beginning of the unprocessed region
        int i;       // End of the unprocessed region
        char c;  // Current character
        for (i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c == '=' && name == null) {
                if (pos != i) {
                    name = decodeComponent(s.substring(pos, i), Charset.defaultCharset());
                }
                pos = i + 1;
                // http://www.w3.org/TR/html401/appendix/notes.html#h-B.2.2
            } else if (c == '&' || c == ';') {
                if (name == null && pos != i) {
                    // We haven't seen an `=' so far but moved forward.
                    // Must be a param of the form '&a&' so add it with
                    // an empty value.
                    addParam(params, decodeComponent(s.substring(pos, i), Charset.defaultCharset()), null);
                } else if (name != null) {
                    addParam(params, name, decodeComponent(s.substring(pos, i), Charset.defaultCharset()));
                    name = null;
                }
                pos = i + 1;
            }
        }

        if (pos != i) {  // Are there characters we haven't dealt with?
            if (name == null) {     // Yes and we haven't seen any `='.
                addParam(params, decodeComponent(s.substring(pos, i), Charset.defaultCharset()), null);
            } else {                // Yes and this must be the last value.
                addParam(params, name, decodeComponent(s.substring(pos, i), Charset.defaultCharset()));
            }
        } else if (name != null) {  // Have we seen a name without value?
            addParam(params, name, "");
        }
        return params;
    }

    public static void fillCidParam(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            return;
        }
        Object cid = params.get(NounConstant.CID);
        if (Objects.nonNull(cid) && NumberUtils.toLong(String.valueOf(cid)) > NumberUtils.LONG_ZERO) {
            return;
        }
        Object P1 = params.get(NounConstant.CID_P1);
        if (Objects.nonNull(P1)) {
            String p1Str = String.valueOf(P1);
            if (StringUtils.isNotEmpty(p1Str)) {
                try {
                    p1Str = URLDecoder.decode(p1Str, StandardCharsets.UTF_8.name());
                    String p1 = new String(Base64.getDecoder().decode(p1Str), StandardCharsets.UTF_8);
                    params.put(NounConstant.CID, NumberUtils.toLong(p1, NumberUtils.LONG_ZERO));
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
    }

    private static boolean addParam(Map<String, Object> params, String name, Object value) {
        params.put(name, value);
        fillCidParam(params);
        return true;
    }

    public static String decodeComponent(final String s, final Charset charset) {
        if (s == null) {
            return "";
        }
        final int size = s.length();
        boolean modified = false;
        for (int i = 0; i < size; i++) {
            final char c = s.charAt(i);
            if (c == '%' || c == '+') {
                modified = true;
                break;
            }
        }
        if (!modified) {
            return s;
        }
        final byte[] buf = new byte[size];
        int pos = 0;  // position in `buf'.
        for (int i = 0; i < size; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    buf[pos++] = ' ';  // "+" -> " "
                    break;
                case '%':
                    if (i == size - 1) {
                        throw new IllegalArgumentException("unterminated escape" + " sequence at end of string: " + s);
                    }
                    c = s.charAt(++i);
                    if (c == '%') {
                        buf[pos++] = '%';  // "%%" -> "%"
                        break;
                    }
                    if (i == size - 1) {
                        throw new IllegalArgumentException("partial escape" + " sequence at end of string: " + s);
                    }
                    c = decodeHexNibble(c);
                    final char c2 = decodeHexNibble(s.charAt(++i));
                    if (c == Character.MAX_VALUE || c2 == Character.MAX_VALUE) {
                        throw new IllegalArgumentException("invalid escape sequence `%" + s.charAt(i - 1) + s.charAt(i) + "' at index " + (i - 2) + " of: " + s);
                    }
                    c = (char) (c * 16 + c2);
                    // Fall through.
                default:
                    buf[pos++] = (byte) c;
                    break;
            }
        }
        return new String(buf, 0, pos, charset);
    }

    private static char decodeHexNibble(final char c) {
        if ('0' <= c && c <= '9') {
            return (char) (c - '0');
        } else if ('a' <= c && c <= 'f') {
            return (char) (c - 'a' + 10);
        } else if ('A' <= c && c <= 'F') {
            return (char) (c - 'A' + 10);
        } else {
            return Character.MAX_VALUE;
        }
    }

}
