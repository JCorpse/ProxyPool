package com.jcorpse.proxypool.job;

import com.jcorpse.proxypool.domain.Proxy;
import com.jcorpse.proxypool.http.HttpManager;
import com.jcorpse.proxypool.repository.imp.ProxyRepositoryImp;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class ScheduledVerify {
    private HttpManager Manager = HttpManager.getInstance();
    private ThreadPoolExecutor VerifyThreadPool = new ThreadPoolExecutor(1, 10, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Autowired
    private ProxyRepositoryImp repository;

    @Scheduled(fixedDelay = 1800000L, initialDelay = 5000L)
    private void Verify() {
        log.info("Verify start");
        repository.findAll().flatMap(new Function<Proxy, Publisher<Proxy>>() {
            @Override
            public Publisher<Proxy> apply(Proxy proxy) {
                return new Publisher<Proxy>() {
                    @Override
                    public void subscribe(Subscriber<? super Proxy> subscriber) {
                        VerifyThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (Manager.checkProxy(proxy)) {
                                    log.info("{}:{} is ok", proxy.getIp(), proxy.getPort());
                                    proxy.setAvailable(true);
                                    proxy.setVerifytime(System.currentTimeMillis());
                                    repository.save(proxy).subscribe();
                                } else {
                                    log.info("{}:{} is dead", proxy.getIp(), proxy.getPort());
                                    repository.delete(proxy).subscribe();
                                }
                            }
                        });
                    }
                };
            }
        }).subscribe();
        log.info("Verify done");
    }
}
