package com.hermes.autoconfigure;

import com.bolt.common.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hermes")
@Data
public class HermesProperties {
    private Client client = new Client();
    private Server server = new Server();

    private String serialization = "hessian2";
    private boolean tcpNodelay = true;
    private boolean tcpSoReuseaddr = true;
    private boolean tcpSoKeepalive = false;
    private int nettyIORatio = 70;
    private boolean nettyBufferPooled = true;
    private int nettyBufferHighWatermark = 64 * 1024;
    private int nettyBufferLowerWatermark = 32 * 1024;
    private int nettyServerIoThread = Constants.DEFAULT_IO_THREADS;

    @Data
    public static class Client {
        private boolean enabled = false;
        private String host = "127.0.0.1";
        private int port = 8091;
        private int connectTimeout = 3000;
        private int timeout =3000;
        private int heartbeatInterval = 15 * 1000;
        private int maxConnection = 1;
        private int maxPendingAcquires = Integer.MAX_VALUE;
        private long acquireTimeout = 3000;
        private String acquireTimeoutAction = "new";
        private boolean lastRecentUsed = false;
        private boolean healthCheck = true;

    }

    @Data
    public static class Server {
        private boolean enabled = false;
        private int port = 8091;
    }

}
