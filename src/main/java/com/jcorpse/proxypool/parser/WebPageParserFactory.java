package com.jcorpse.proxypool.parser;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class WebPageParserFactory {
    private static Map<String, WebPageParser> ParserMap = new HashMap<>();

    public static WebPageParser getPageParser(Class classz) {
        String parserName = classz.getSimpleName();
        if (ParserMap.containsKey(parserName)) {
            return ParserMap.get(parserName);
        } else {
            try {
                WebPageParser parser = (WebPageParser) classz.getDeclaredConstructor().newInstance();
                ParserMap.put(parserName, parser);
                return parser;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
