package com.bolt.config;


import java.util.Objects;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/9
 * @Description: TODO
 */
public class BoltOption<T> {
    private final String name;
    private T defaultValue;
    private Class<? extends BoltOption > type;

    protected BoltOption(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = getClass();
    }

    public String name() {
        return this.name;
    }

    public Class<? extends BoltOption > type() {
        return this.type;
    }

    protected void setType(Class<? extends BoltOption > type) {
        this.type = type;
    }

    public T defaultValue() {
        return this.defaultValue;
    }

    public static <T> BoltOption<T> valueOf(String name) {
        return new BoltOption<T>(name, null);
    }

    public static <T> BoltOption<T> valueOf(String name, T defaultValue) {
        return new BoltOption<T>(name, defaultValue);
    }

    public static <T> BoltOption<T> valueOf(Class<? extends BoltOption > type, String name) {
        BoltOption<T> option = new BoltOption<>(name, null);
        option.setType(type);
        return option;
    }

    public static <T> BoltOption<T> valueOf(Class<? extends BoltOption > type, String name, T defaultValue) {
        BoltOption<T> option = new BoltOption<>(name, defaultValue);
        option.setType(type);
        return option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoltOption<?> that = (BoltOption<?>) o;
        return name == null ? Objects.equals(name, that.name) : name == null;
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }
}
