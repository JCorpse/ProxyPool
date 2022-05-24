package com.jcorpse.proxypool.domain;

import lombok.Data;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;

@Document(collection = "pool")
@Data
@CompoundIndex(name = "ip_port", unique = true, def = "{'ip':1,'port':1}")
public class Proxy implements Serializable {

    @Serial
    private static final long serialVersionUID = 6178822625094919923L;

    @Id
    private String id;
    @Field("ip")
    private String ip;
    @Field("port")
    private int port;
    private String type;
    private boolean available;
    private boolean anonymous;
    private String country;
    private long verifytime;

    public HttpHost getHttpHost() {
        return new HttpHost(ip, port);
    }
}
