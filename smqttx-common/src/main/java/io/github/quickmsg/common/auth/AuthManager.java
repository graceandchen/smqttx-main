package io.github.quickmsg.common.auth;


import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author luxurong
 */
public interface AuthManager {

    /**
     * 认证接口
     *
     * @param userName        用户名称
     * @param passwordInBytes 密钥
     * @param clientIdentifier 设备标志
     * @return 布尔
     */
    Mono<Boolean> auth(String userName, byte[] passwordInBytes, String clientIdentifier);

    /**
     * 认证接口2
     *
     * @param options          选项
     * @param clientIdentifier 客户端标识符
     * @return {@link Mono}<{@link Boolean}>
     */
    Mono<Boolean> auth(Map<String,Object> options, String clientIdentifier);

}
