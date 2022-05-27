package com.jcorpse.proxypool.repository.imp;

import com.jcorpse.proxypool.domain.Proxy;
import com.jcorpse.proxypool.repository.ProxyRepository;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class ProxyRepositoryImp implements ProxyRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public ProxyRepositoryImp(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Proxy> save(Proxy proxy) {
        return mongoTemplate.save(proxy);
    }

    @Override
    public Mono<DeleteResult> delete(Proxy proxy) {
        return mongoTemplate.remove(proxy);
    }

    @Override
    public Flux<Proxy> findAll() {
        return mongoTemplate.findAll(Proxy.class);
    }

    @Override
    public Flux<Proxy> findByCustom(Integer limit, boolean available, boolean anonymous, String country) {
        Query query = new Query();
        log.info("limit:{} available:{} anonymous:{} country:{}",limit, available, anonymous, country);


        if (available) {
            query.addCriteria(Criteria.where("available").is(available));
        }
        if (anonymous) {
            query.addCriteria(Criteria.where("anonymous").is(anonymous));
        }
        if (country != null && country.length() > 0) {
            query.addCriteria(Criteria.where("country").is(country));
        }
        if (limit != null) {
            query.limit(limit);
        }
        log.info("{}",query.toString());
        return mongoTemplate.find(query, Proxy.class);
    }
}
