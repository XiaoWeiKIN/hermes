package com.bolt.util;

import org.springframework.lang.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
public class ObjectUtils {

    public static void isNotNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String toString(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    public static String toString(String msg, Throwable e) {
        StringWriter w = new StringWriter();
        w.write(msg + "\n");
        PrintWriter p = new PrintWriter(w);
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }
}
