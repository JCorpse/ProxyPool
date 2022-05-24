package com.jcorpse.proxypool.job;

import com.jcorpse.proxypool.config.Constant;
import com.jcorpse.proxypool.domain.Proxy;
import com.jcorpse.proxypool.domain.WebPage;
import com.jcorpse.proxypool.http.HttpManager;
import com.jcorpse.proxypool.parser.WebPageParser;
import com.jcorpse.proxypool.parser.WebPageParserFactory;
import com.jcorpse.proxypool.repository.ProxyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class ScheduledCrawler {
    private HttpManager Manager = HttpManager.getInstance();

    @Autowired
    private ProxyRepository repository;

    //    @Scheduled(fixedDelay = 600000L, initialDelay = 5000L)
    @Scheduled(fixedDelay = 10000L, initialDelay = 0L)
    private void Crawler() {
        log.info("Crawler start");
        Iterator<String> siteI = Constant.SITE_MAP.keySet().iterator();
        while (siteI.hasNext()) {
            String url = siteI.next();
            WebPage page = Manager.getPage(url);
            if (page.getCode() == 200) {
                WebPageParser parser = WebPageParserFactory.getPageParser(Constant.SITE_MAP.get(url));
                List<Proxy> proxyList = parser.parser(page.getBody());
                for (Proxy proxy : proxyList) {
                    log.info("{}:{}", proxy.getIp(), proxy.getPort());
                }
            }
            log.info("siet:{} done", url);
        }
    }

}
