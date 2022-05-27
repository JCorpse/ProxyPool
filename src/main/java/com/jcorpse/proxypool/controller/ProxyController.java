package com.jcorpse.proxypool.controller;

import com.jcorpse.proxypool.domain.Proxy;
import com.jcorpse.proxypool.repository.imp.ProxyRepositoryImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.constraints.AssertTrue;


@Slf4j
@Validated
@RestController()
@RequestMapping(value = "/api/proxy")
public class ProxyController {
    @Autowired
    private ProxyRepositoryImp repository;

    @GetMapping("/")
    private Flux<Proxy> Proxys(@RequestParam(required = false, name = "limit") Integer limit,
                               @RequestParam(required = false, name = "available") boolean available,
                               @RequestParam(required = false, name = "anonymous") boolean anonymous,
                               @RequestParam(required = false, name = "country") String country) {
        return repository.findByCustom(limit, available, anonymous, country);
    }
}
