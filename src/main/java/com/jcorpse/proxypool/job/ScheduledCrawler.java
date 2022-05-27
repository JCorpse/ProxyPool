package com.jcorpse.proxypool.job;

import com.jcorpse.proxypool.config.Constant;
import com.jcorpse.proxypool.domain.Proxy;
import com.jcorpse.proxypool.domain.WebPage;
import com.jcorpse.proxypool.http.HttpManager;
import com.jcorpse.proxypool.parser.WebPageParser;
import com.jcorpse.proxypool.parser.WebPageParserFactory;
import com.jcorpse.proxypool.repository.imp.ProxyRepositoryImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class ScheduledCrawler {
    private HttpManager Manager = HttpManager.getInstance();

    @Autowired
    private ProxyRepositoryImp repository;

    @Scheduled(fixedDelay = 3000000L, initialDelay = 0L)
    private void Crawler() {
        log.info("Crawler start");
        Hooks.onErrorDropped(throwable -> {
            if (!(throwable instanceof DuplicateKeyException)) {
                log.error(throwable.getMessage());
            }
        });
        Iterator<String> siteI = Constant.SITE_MAP.keySet().iterator();
        while (siteI.hasNext()) {
            String url = siteI.next();
            WebPage page = Manager.getPage(url);
            if (page.getCode() == 200) {
                WebPageParser parser = WebPageParserFactory.getPageParser(Constant.SITE_MAP.get(url));
                List<Proxy> proxyList = parser.parser(page.getBody());
                for (Proxy proxy : proxyList) {
                    try {
                        repository.save(proxy).block();
                    } catch (DuplicateKeyException e) {
                        log.info("{}:{} is exist", proxy.getIp(), proxy.getPort());
                    }
                }
            }
            log.info("siet:{} done", url);
        }
    }

}
