package com.jcorpse.proxypool.http;

import com.jcorpse.proxypool.config.Constant;
import com.jcorpse.proxypool.domain.Proxy;
import com.jcorpse.proxypool.domain.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;


@Slf4j
public class HttpManager {
    private PoolingHttpClientConnectionManager HttpPoolManager = null;
    private CookieStore cookieStore = new BasicCookieStore();

    private HttpManager() {
        try {
            HttpPoolManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(SSLContextBuilder.create()
                                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                                    .build())
                            .build())
                    .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(3)).build())
                    .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                    .setConnPoolPolicy(PoolReusePolicy.LIFO)
                    .setConnectionTimeToLive(TimeValue.ofMinutes(1))
                    .build();
        } catch (Exception e) {
            log.error("HttpPoolManager init error: {}", e.getMessage());
        }
    }

    public static HttpManager getInstance() {
        return Holder.Instance;
    }

    private static class Holder {
        private static final HttpManager Instance = new HttpManager();
    }

    private CloseableHttpClient createHttpClient(long timeout, Proxy proxy) {
        RequestConfig.Builder RequestBuilder = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofSeconds(timeout))
                .setConnectTimeout(Timeout.ofSeconds(timeout));
        RequestConfig Request = RequestBuilder.build();

        HttpClientBuilder ClientBuilder = HttpClients.custom()
                .setUserAgent(Constant.USER_AGENT)
                .setDefaultRequestConfig(Request)
                .setConnectionManager(HttpPoolManager)
                .setDefaultCookieStore(cookieStore);

        if (proxy != null) {
            ClientBuilder.setProxy(proxy.getHttpHost());
        }

        return ClientBuilder.build();
    }

    public CloseableHttpResponse getResponse(String url) {
        return getResponse(url, 30000L);
    }

    public CloseableHttpResponse getResponse(String url, long timeout) {
        return getResponse(url, timeout, null);
    }

    public CloseableHttpResponse getResponse(String url, long timeout, Proxy proxy) {
        HttpGet httpGet = new HttpGet(url);
        return getResponse(httpGet, timeout, proxy);
    }

    public CloseableHttpResponse getResponse(HttpGet httpGet, long timeout, Proxy proxy) {
        CloseableHttpResponse response = null;
        try {
            if (proxy != null) {
                response = createHttpClient(timeout, proxy).execute(httpGet);
            } else {
                response = createHttpClient(timeout, null).execute(httpGet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public WebPage getPage(String url) {
        return getPage(url, "UTF-8");
    }

    public WebPage getPage(String url, String charset) {
        return getPage(url, charset, 30000L);
    }

    public WebPage getPage(String url, String charset, long timeout) {
        return getPage(url, charset, timeout, null);
    }

    public WebPage getPage(String url, String charset, long timeout, Proxy proxy) {
        WebPage page = new WebPage();
        CloseableHttpResponse response = getResponse(url, timeout, proxy);
        if (response != null) {
            page.setUrl(url);
            page.setCode(response.getCode());
            if (response.getCode() == 200) {
                try {
                    page.setBody(EntityUtils.toString(response.getEntity(), charset));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return page;
    }

    public boolean checkProxy(Proxy proxy) {
        CloseableHttpResponse response = getResponse("https://api.ipify.org/?format=json", 3000L, proxy);
        if (response.getCode() == 200) {
            return true;
        }
        return false;
    }

    public void setCookie(String name, String value, String Domain) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(Domain);
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
    }
}
