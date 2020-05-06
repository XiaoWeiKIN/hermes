package com.bolt.transport;

import com.bolt.common.enums.ConnectionEventType;
import com.bolt.reomoting.Connection;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/1
 * @Description: TODO
 */
public class ConnectionEventListener {
    private ConcurrentHashMap<ConnectionEventType, List<ConnectionEventProcessor>> processors = new ConcurrentHashMap<ConnectionEventType, List<ConnectionEventProcessor>>();

    public void onEvent(ConnectionEventType type,Connection connection) {
        List<ConnectionEventProcessor> processorList = this.processors.get(type);
        if (processorList != null) {
            for (ConnectionEventProcessor processor : processorList) {
                processor.onEvent(connection);
            }
        }
    }

    public void addConnectionEventProcessor(ConnectionEventType type,
                                            ConnectionEventProcessor processor) {
        List<ConnectionEventProcessor> processorList = this.processors.get(type);
        if (processorList == null) {
            this.processors.putIfAbsent(type, new ArrayList<ConnectionEventProcessor>(1));
            processorList = this.processors.get(type);
        }
        processorList.add(processor);
    }
}
