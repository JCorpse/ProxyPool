package com.jcorpse.proxypool.parser;

import com.jcorpse.proxypool.domain.Proxy;

import java.util.List;

public interface WebPageParser {
    List<Proxy> parser(String html);
}
