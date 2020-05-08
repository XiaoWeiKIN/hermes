package com.hermes.processor;

import com.bolt.protocol.processor.AbstractUserProcessorAdapter;
import com.hermes.api.SimpleRequestBody;
import com.hermes.api.SimpleResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/8
 * @Description: TODO
 */
public class SimpleUerProcessor extends AbstractUserProcessorAdapter<SimpleRequestBody> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleUerProcessor.class);

    @Override
    public Object handleRequest(SimpleRequestBody request) throws Exception {
        logger.info("Server Recv: " + request.toString());
        return new SimpleResponseBody(request.toString());
    }

}
