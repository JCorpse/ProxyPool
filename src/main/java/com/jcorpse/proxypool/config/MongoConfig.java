package com.jcorpse.proxypool.config;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.jcorpse.proxypool.repository")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Override
    protected MongoClient createReactiveMongoClient(MongoClientSettings settings) {
        return MongoClients.create(MongoClientSettings.builder().applyConnectionString(new ConnectionString("mongodb://192.168.56.102")).build());
    }
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    protected String getDatabaseName() {
        return "proxypool";
    }
}
