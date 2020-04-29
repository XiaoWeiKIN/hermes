package com.bolt.common.serialize;

import com.bolt.common.extension.ExtensionLoader;
import com.bolt.serialization.ObjectInput;
import com.bolt.serialization.ObjectOutput;
import com.bolt.serialization.Serialization;
import com.bolt.serialization.hessian2.Hessian2ObjectOutput;
import com.bolt.serialization.hessian2.Hessian2Serialization;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/24
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class SerializationTest {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Test
    public void test_se() throws Exception{
        String str = "zhan";
        Data data = new Data();
        data.setName(str);
        Hessian2Serialization hessian2Serialization = new Hessian2Serialization();
        ObjectOutput objectOutput = hessian2Serialization.serialize(byteArrayOutputStream);
        objectOutput.writeUTF(str);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput objectInput = hessian2Serialization.deserialize(byteArrayInputStream);
        Assert.assertEquals(str, objectInput.readUTF());
        Assert.assertEquals(data.toString(), objectInput.readObject(Data.class).toString());


    }

    @lombok.Data
    public static class  Data implements Serializable {
        String name;
    }
}
