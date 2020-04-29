package com.bolt.reomoting;

import com.bolt.common.exception.RemotingException;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/24
 * @Description: TODO
 */
public interface ResponseFuture {

    /**
     * get result.
     *
     * @return result.
     */
    <T> T get() throws RemotingException;

    /**
     * get result with the specified timeout.
     *
     * @param timeoutInMillis timeout.
     * @return result.
     */
    <T> T get(int timeoutInMillis) throws RemotingException;

    /**
     * set callback.
     *
     * @param callback
     */
    void setCallback(ResponseCallback callback);

    /**
     * check is done.
     *
     * @return done or not.
     */
    boolean isDone();
}
