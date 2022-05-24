package com.jcorpse.proxypool.parser.site;

import com.jcorpse.proxypool.domain.Proxy;
import com.jcorpse.proxypool.parser.WebPageParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FreeProxyListParser implements WebPageParser {

    @Override
    public List<Proxy> parser(String html) {
        List<Proxy> proxyList = new ArrayList<Proxy>();
        Document doc = Jsoup.parse(html);
        Elements proxyListElements = doc.select("table.table.table-striped.table-bordered > tbody > tr");
        for (Element proxyElement : proxyListElements) {
            Elements proxyData = proxyElement.select("td");
            Proxy proxy = new Proxy();
            proxy.setIp(proxyData.get(0).text());
            proxy.setPort(Integer.valueOf(proxyData.get(1).text()));
            proxy.setCountry(proxyData.get(2).text());
            proxy.setAnonymous(getAnonymous(proxyData.get(4).text()));
            proxy.setType(getType(proxyData.get(5).text()));
            proxy.setAvailable(false);
            proxy.setVerifytime(System.currentTimeMillis());
            proxyList.add(proxy);
        }
        return proxyList;
    }

    private boolean getAnonymous(String Anonymity) {
        boolean anonymous = switch (Anonymity) {
            case "elite proxy", "anonymous" -> true;
            default -> false;
        };
        return anonymous;
    }

    private String getType(String Https) {
        String type = switch (Https) {
            case "yes" -> "http/https";
            default -> "http";
        };
        return type;
    }
}