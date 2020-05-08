package com.bolt.common;


import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/7
 * @Description: TODO
 */
public class Url {
    private Map<String, Object> parameters;
    private String host;
    private int port;
    public static final String CONNECT_TIMEOUT = Constants.CONNECT_TIMEOUT_KEY;
    public static final String TIMEOUT = Constants.TIMEOUT_KEY;
    public static final String SERIALIZATION= Constants.SERIALIZATION_KEY;
    public static final String MAX_CONNECTION = Constants.MAX_CONNECTION;
    public static final String MAX_PENDING_ACQUIRES = Constants.MAX_PENDING_ACQUIRES;
    public static final String ACQUIRE_TIMEOUT = Constants.ACQUIRE_TIMEOUT;
    public static String ACQUIRE_TIMEOUT_ACTION = Constants.ACQUIRE_TIMEOUT_ACTION;
    public static String RELEASE_HEALTH_CHECK = Constants.RELEASE_HEALTH_CHECK;
    public static String LAST_RECENT_USED = Constants.CONNECTION_LAST_RECENT_USED;
    public static final String ASYNC = Constants.ASYNC_KEY;
    public static final String ONEWAY = Constants.RETURN_KEY;
    public static final String MAX_HEARTBEAT_COUNT = Constants.MAX_HEARTBEAT_COUNT;


    public static Url.UrlBuilder builder() {
        return new Url.UrlBuilder();
    }

    public Url(String host, int port) {
        this(host, port, new HashMap<String, Object>());
    }

    public Url(String host, int port, Map<String, Object> parameters) {
        this.host = host;
        this.port = port;
        this.parameters = parameters;
    }

    public <T> T getParameter(String key, T defaultValue) {
        Object value = parameters.get(key);
        if (value != null) {
            return (T) value;
        }

        return defaultValue;

    }

    public void addParameters(Map<String, Object> parameters) {
        if (parameters == null || parameters.size() == 0) {
            return;
        }
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            this.parameters.putIfAbsent(entry.getKey(), entry.getValue());
        }
    }

    public int getPort() {
        return this.port;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Url other = (Url) obj;
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        if (parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!parameters.equals(other.parameters)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getHost()).append(":").append(getPort());
        if (parameters != null && parameters.size() > 0) {
            boolean first = true;
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (first) {
                    builder.append("?");
                    first = false;
                } else {
                    builder.append("&");
                }
                builder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return builder.toString();
    }

    public static class UrlBuilder {
        private Map<String, Object> parameters;
        private String host;
        private int port;

        private UrlBuilder() {

        }

        public Url.UrlBuilder host(String host) {
            this.host = host;
            return this;
        }

        public Url.UrlBuilder port(int port) {
            this.port = port;
            return this;
        }

        public Url.UrlBuilder setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Url build() {
            Url url = new Url(this.host, this.port);
            url.addParameters(this.parameters);
            return url;
        }
    }
}
