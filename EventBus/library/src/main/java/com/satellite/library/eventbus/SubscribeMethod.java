package com.satellite.library.eventbus;

import java.lang.reflect.Method;

public class SubscribeMethod {

    // 订阅回调方法
    private Method method;

    // 订阅方法回调的线程模式
    private ThreadMode threadMode;

    // 订阅方法的参数类型
    private Class<?> eventType;

    public SubscribeMethod(Method method, ThreadMode threadMode, Class<?> clazz) {
        this.threadMode = threadMode;
        this.method = method;
        this.eventType = clazz;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
