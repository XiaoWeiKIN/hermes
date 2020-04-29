package com.bolt.serialization;

import com.bolt.common.Constants;
import com.bolt.common.Url;
import com.bolt.common.exception.SerializationException;
import com.bolt.common.extension.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;


/**
 * @Author: wangxw
 * @DateTime: 2020/3/27
 * @Description: TODO
 */
public class SerializationManager {

    private static final Logger logger = LoggerFactory.getLogger(SerializationManager.class);

    private static Serialization[] ID_SERIALIZER_LIST = new Serialization[5];

    static {
        Set<String> extensions = ExtensionLoader.getExtensionLoader(Serialization.class).getSupportedExtensions();
        for (String name : extensions) {
            Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(name);
            byte contentTypeId = serialization.getContentTypeId();
            if (ID_SERIALIZER_LIST[contentTypeId] != null) {
                logger.error("Serialization extension " + serialization.getClass().getName()
                        + " has duplicate id to Serialization extension "
                        + ID_SERIALIZER_LIST[contentTypeId].getClass().getName()
                        + ", ignore this Serializer extension");
                continue;
            }

            addSerialiation(contentTypeId, serialization);
        }

    }

    private SerializationManager() {

    }

    public static Serialization getSerialization(Url url) {
        return ExtensionLoader.getExtensionLoader(Serialization.class)
                .getExtension(url.getParameter(Constants.SERIALIZATION_KEY,
                        Constants.DEFAULT_REMOTING_SERIALIZATION));
    }

    public static Serialization getSerializationById(Byte id) throws IOException {
        final String error = "Unexpected serializer id:" + id + " received from network, please check if the peer send the right id.";
        return Optional.ofNullable(ID_SERIALIZER_LIST[id])
                .orElseThrow(() -> new SerializationException(error));
    }

    private static void addSerialiation(int idx, Serialization serialization) {
        if (ID_SERIALIZER_LIST.length <= idx) {
            Serialization[] newSerializers = new Serialization[idx + 5];
            System.arraycopy(ID_SERIALIZER_LIST, 0, newSerializers, 0, ID_SERIALIZER_LIST.length);
            ID_SERIALIZER_LIST = newSerializers;
        }
        ID_SERIALIZER_LIST[idx] = serialization;
    }

}
