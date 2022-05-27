package com.jcorpse.proxypool.repository;

import com.jcorpse.proxypool.domain.Proxy;
import com.mongodb.client.result.DeleteResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ProxyRepository {

    Mono<Proxy> save(Proxy proxy);

    Mono<DeleteResult> delete(Proxy proxy);

    Flux<Proxy> findAll();

    Flux<Proxy> findByCustom(Integer limit, boolean available, boolean anonymous, String country);
}
