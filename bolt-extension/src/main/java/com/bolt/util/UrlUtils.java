package com.bolt.util;

import com.bolt.common.Constants;
import com.bolt.common.Url;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/7
 * @Description: TODO
 */
public class UrlUtils {

    private final static String URL_COLON_SYMBOL = ":";
    private final static String URL_PARAM_STARTING_SYMBOL = "?";

    public static Url parseUrl(String address, Map<String, Object> parameterMap) {
        if (StringUtils.isBlank(address)) {
            throw new IllegalArgumentException("Illegal format address [" + address + "], it should not be blank! ");
        }
        String host = null;
        int port = 0;
        String[] urls = StringUtils.split(address, URL_COLON_SYMBOL);
        if (urls.length != 2) {
            throw new IllegalArgumentException("Illegal format address [" + address + "], Illegal colon position ");

        }
        host = urls[0];
        if (urls[1].endsWith(URL_PARAM_STARTING_SYMBOL)) {
            throw new IllegalArgumentException("Illegal format address [" + address + "], should be not end with [?] ");
        }
        urls = StringUtils.split(urls[1], URL_PARAM_STARTING_SYMBOL);
        if (urls.length > 2) {
            throw new IllegalArgumentException("Illegal format address [" + address + "], must have one [?]! ");
        }

        port = Integer.valueOf(urls[0]);
        Map<String, Object> parameters = new HashMap<>();
        ;
        if (urls.length == 2) {
            String params = urls[1];
            String[] parts = StringUtils.split(params, "&");
            for (String part : parts) {
                int i = part.indexOf("=");
                if (i >= 0) {
                    parameters.put(part.substring(0, i), part.substring(i + 1));

                }
            }
        }
        parameters.putAll(parameterMap);
        return new Url(host, port, parameters);
    }

    public static Url valueOf(Url url, Map<String, Object> parameters) {
        url.addParameters(parameters);
        return url;
    }

    public static boolean isAsync(Url url){
        return url.getParameter(Constants.ASYNC_KEY, false);
    }

    public static boolean isOneway(Url url){
        return url.getParameter(Constants.RETURN_KEY, false);
    }

    public static int getHeartbeat(Url url) {
        return url.getParameter(Constants.HEARTBEAT_KEY, Constants.DEFAULT_HEARTBEAT);
    }

    public static int getIdleTimeout(Url url) {
        int heartBeat = getHeartbeat(url);
        // idleTimeout should be at least more than six heartBeat because possible retries of client.
        int idleTimeout = url.getParameter(Constants.HEARTBEAT_TIMEOUT_KEY, heartBeat * 6);
        if (idleTimeout < heartBeat * 6) {
            throw new IllegalStateException("idleTimeout < heartbeatInterval * 6");
        }
        return idleTimeout;
    }

    public static int getTimeout(Url url) {
        return url.getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
    }
}
