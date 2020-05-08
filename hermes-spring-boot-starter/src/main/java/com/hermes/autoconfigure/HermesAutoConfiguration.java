package com.hermes.autoconfigure;

import com.bolt.config.BoltClientOption;
import com.bolt.config.BoltGenericOption;
import com.bolt.config.BoltRemotingOption;
import com.bolt.config.BoltServerOption;
import com.bolt.transport.BoltClient;
import com.bolt.transport.BoltServer;
import com.bolt.transport.Client;
import com.bolt.transport.Server;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({HermesProperties.class})
public class HermesAutoConfiguration {

    private HermesProperties properties;

    public HermesAutoConfiguration(HermesProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "hermes.client", name = "enabled", havingValue = "true")
    public Client client() {
        HermesProperties.Client pc = properties.getClient();
        BoltClient client = new BoltClient();
        client.option(BoltClientOption.HOST, pc.getHost())
                .option(BoltClientOption.PORT, pc.getPort())
                .option(BoltGenericOption.TCP_NODELAY, properties.isTcpNodelay())
                .option(BoltGenericOption.TCP_SO_REUSEADDR, properties.isTcpSoReuseaddr())
                .option(BoltGenericOption.NETTY_BUFFER_HIGH_WATER_MARK, properties.getNettyBufferHighWatermark())
                .option(BoltGenericOption.NETTY_BUFFER_LOW_WATER_MARK, properties.getNettyBufferLowerWatermark())
                .option(BoltGenericOption.NETTY_IO_RATIO, properties.getNettyIORatio())
                .option(BoltGenericOption.TCP_SO_KEEPALIVE, properties.isTcpSoKeepalive())
                .option(BoltClientOption.HEARTBEATINTERVAL, pc.getHeartbeatInterval())
                .option(BoltClientOption.CONNECT_TIMEOUT, pc.getConnectTimeout())
                .option(BoltClientOption.MAX_CONNECTION, pc.getMaxConnection())
                .option(BoltClientOption.ACQUIRE_TIMEOUT, pc.getAcquireTimeout())
                .option(BoltClientOption.ACQUIRE_TIMEOUT_ACTION, pc.getAcquireTimeoutAction())
                .option(BoltClientOption.RELEASE_HEALTH_CHECK, pc.isHealthCheck())
                .option(BoltClientOption.CONNECTION_LAST_RECENT_USED, pc.isLastRecentUsed())
                .option(BoltRemotingOption.SERIALIZATION, properties.getSerialization());
        client.startUp();
        return client;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "hermes.server", name = "enabled", havingValue = "true")
    public Server server() {
        HermesProperties.Server ps = properties.getServer();
        BoltServer server = new BoltServer();
        server.option(BoltServerOption.PORT, ps.getPort())
                .option(BoltGenericOption.TCP_NODELAY, properties.isTcpNodelay())
                .option(BoltGenericOption.TCP_SO_REUSEADDR, properties.isTcpSoReuseaddr())
                .option(BoltGenericOption.NETTY_BUFFER_HIGH_WATER_MARK, properties.getNettyBufferHighWatermark())
                .option(BoltGenericOption.NETTY_BUFFER_LOW_WATER_MARK, properties.getNettyBufferLowerWatermark())
                .option(BoltGenericOption.NETTY_IO_RATIO, properties.getNettyIORatio())
                .option(BoltGenericOption.TCP_SO_KEEPALIVE, properties.isTcpSoKeepalive())
                .option(BoltGenericOption.IO_THREADS, properties.getNettyServerIoThread())
                .option(BoltRemotingOption.SERIALIZATION, properties.getSerialization());
        server.startUp();
        return server;
    }

}
