package com.jcorpse.proxypool.repository;

import com.jcorpse.proxypool.domain.Proxy;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProxyRepository extends ReactiveMongoRepository<Proxy, String> {
}
