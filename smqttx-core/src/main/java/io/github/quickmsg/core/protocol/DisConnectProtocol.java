package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.auth.AuthManager;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.DisConnectMessage;
import io.github.quickmsg.common.metric.CounterType;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.core.auth.HttpAuthManager;
import io.github.quickmsg.core.mqtt.MqttReceiveContext;
import reactor.netty.Connection;
import reactor.util.context.ContextView;

import java.util.Optional;

/**
 * @author luxurong
 */
public class DisConnectProtocol implements Protocol<DisConnectMessage> {


    @Override
    public void parseProtocol(DisConnectMessage message, MqttChannel mqttChannel, ContextView contextView) {
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        logManager.printInfo(mqttChannel, LogEvent.DISCONNECT, LogStatus.SUCCESS, JacksonUtil.bean2Json(message));
        receiveContext.getMetricManager().getMetricRegistry().getMetricCounter(CounterType.DIS_CONNECT_EVENT).increment();

        mqttChannel.getConnectCache().setWill(null);
        Connection connection;
        if (!(connection = mqttChannel.getConnection()).isDisposed()) {
            connection.dispose();
        }

        MqttReceiveContext mqttReceiveContext = (MqttReceiveContext) contextView.get(ReceiveContext.class);
        HttpAuthManager authManager = (HttpAuthManager) mqttReceiveContext.getAuthManager();
        authManager.disConnect(Optional.of(message.getClientId()).orElse(""))
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        // todo
                    }else {
                        // todo
                    }
                });
    }

    @Override
    public Class<DisConnectMessage> getClassType() {
        return DisConnectMessage.class;
    }


}
