package com.bolt.common;

/**
 * @Author: wangxw
 * @DateTime: 2020/3/26
 * @Description: TODO
 */
public class ClassHelper {
    /**
     * @param clazz
     * @return
     * @see <a href="https://www.iteye.com/blog/tyrion-1958814">
     * @see <a href="https://blog.csdn.net/u010312474/article/details/91046318"></>
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            return cl;
        }
        if (clazz != null) {
            cl = clazz.getClassLoader();
            if (cl != null) {
                return cl;
            }
        }
        return ClassLoader.getSystemClassLoader();
    }
}
