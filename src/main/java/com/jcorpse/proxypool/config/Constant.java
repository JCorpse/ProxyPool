package com.jcorpse.proxypool.config;

import com.jcorpse.proxypool.parser.site.FreeProxyListParser;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36 OPR/79.0.4143.72";

    public static final Map<String, Class> SITE_MAP = new HashMap<>();

    static {
        SITE_MAP.put("https://free-proxy-list.net/", FreeProxyListParser.class);
    }
}
