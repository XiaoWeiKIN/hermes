/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bolt.common.enums;

import lombok.AllArgsConstructor;

/**
 * Status of the response.
 *
 * @author jiangping
 * @version $Id: ResponseStatus.java, v 0.1 2015-9-28 PM3:08:12 tao Exp $
 */
@AllArgsConstructor
public enum ResponseStatus {

    SUCCESS((byte) 101),
    SERVER_EXCEPTION((byte) 102),
    SERVER_THREADPOOL_BUSY((byte) 103),
    NO_PROCESSOR((byte) 104),
    CLIENT_TIMEOUT((byte) 105),
    SERVER_TIMEOUT((byte) 106),
    CLIENT_SEND_ERROR((byte) 107),
    CODEC_EXCEPTION((byte) 108),
    CONNECTION_CLOSED((byte) 109),
    UNKNOWN((byte) 111);

    private byte value;

    public byte value() {
        return this.value;
    }

    public static ResponseStatus toEnum(byte value) {
        for (ResponseStatus status : values()) {
            if (value == status.value()) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status value ," + value);
    }
//
//    public byte getValue() {
//        switch (this) {
//            case SUCCESS:
//                return 0x0000;
//            case ERROR:
//                return 0x0001;
//            case SERVER_EXCEPTION:
//                return 0x0002;
//            case UNKNOWN:
//                return 0x0003;
//            case SERVER_THREADPOOL_BUSY:
//                return 0x0004;
//            case ERROR_COMM:
//                return 0x0005;
//            case NO_PROCESSOR:
//                return 0x0006;
//            case CLIENT_TIMEOUT:
//                return 0x0007;
//            case CLIENT_SEND_ERROR:
//                return 0x0008;
//            case CODEC_EXCEPTION:
//                return 0x0009;
//            case CONNECTION_CLOSED:
//                return 0x0010;
//        }
//        throw new IllegalArgumentException("Unknown status," + this);
//    }
//
//    /**
//     * Convert to ResponseStatus.
//     *
//     * @param value
//     * @return
//     */
//    public static ResponseStatus valueOf(byte value) {
//        switch (value) {
//            case 10:
//                return SUCCESS;
//            case 11:
//                return SERVER_EXCEPTION;
//            case 12:
//                return SERVER_THREADPOOL_BUSY;
//            case 13:
//                return NO_PROCESSOR;
//            case 14:
//                return CLIENT_TIMEOUT;
//            case 15:
//                return SERVER_TIMEOUT;
//            case 16:
//                return CODEC_EXCEPTION;
//            case 17:
//                return CLIENT_SEND_ERROR;
//            case 18:
//                return CONNECTION_CLOSED;
//            case 19:
//                return UNKNOWN;
//
//        }
//    }
}
