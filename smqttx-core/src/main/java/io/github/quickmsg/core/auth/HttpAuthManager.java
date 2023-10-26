package io.github.quickmsg.core.auth;

import io.github.quickmsg.common.auth.AuthManager;
import io.github.quickmsg.common.config.AuthConfig;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author luxurong
 */
public class HttpAuthManager implements AuthManager {

    private final AuthConfig authConfig;

    private final HttpClient client;

    public HttpAuthManager(AuthConfig authConfig) {
        this.authConfig = authConfig;
        AuthConfig.HttpAuthConfig httpAuthConfig = authConfig.getHttp();
        this.client = HttpClient.create().host(httpAuthConfig.getHost()).port(httpAuthConfig.getPort())
                .headers(headers -> {
                    headers.add(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json;utf-8");
                    Optional.ofNullable(httpAuthConfig.getHeaders())
                            .ifPresent(addHeaders -> addHeaders.forEach(headers::add));
                });
    }
    @Override
    public Mono<Boolean> auth(String userName, byte[] passwordInBytes, String clientIdentifier) {
        AuthConfig.HttpAuthConfig httpAuthConfig = authConfig.getHttp();
        Map<String, String> params = new HashMap<>();
        params.put("clientIdentifier", clientIdentifier);
        params.put("username", userName);
        params.put("password", new String(passwordInBytes, StandardCharsets.UTF_8));
        params.putAll(httpAuthConfig.getParams());
        return client.post().uri(httpAuthConfig.getPath())
                .send(ByteBufFlux.fromString(Mono.just(JacksonUtil.map2Json(params))))
                .response()
                .map(response -> HttpResponseStatus.OK.code() == response.status().code())
        ;
    }

    @Override
    public Mono<Boolean> auth(Map<String, Object> options, String clientIdentifier) {
        AuthConfig.HttpAuthConfig httpAuthConfig = authConfig.getHttp();
        Map<String, String> params = new HashMap<>();
        try {
            params.put("clientIdentifier", clientIdentifier);
            params.put("username", (String) options.getOrDefault("username",""));
            params.put("password", (String) options.getOrDefault("password",""));
            params.put("deviceType", (String) options.getOrDefault("deviceType",""));
            params.put("productIdentification",(String) options.getOrDefault("productIdentification",""));
            params.putAll(httpAuthConfig.getParams());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return client.post().uri(httpAuthConfig.getPath())
                .send(ByteBufFlux.fromString(Mono.just(JacksonUtil.map2Json(params))))
                .response()
                .map(response -> HttpResponseStatus.OK.code() == response.status().code())
                ;
    }

    public Mono<Boolean> disConnect(String clientIdentifier) {
        AuthConfig.HttpAuthConfig httpAuthConfig = authConfig.getHttp();
        Map<String, String> params = new HashMap<>();
        params.put("clientIdentifier", clientIdentifier);
        return client.post().uri(httpAuthConfig.getDisconnectPath())
                .send(ByteBufFlux.fromString(Mono.just(JacksonUtil.map2Json(params))))
                .response()
                .map(response->HttpResponseStatus.OK.code() == response.status().code());
    }

}
