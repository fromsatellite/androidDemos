package com.satellite.apttool_api;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;

public class ProxyTool {

    //生成代理类的后缀名
    public static final String SUFFIX = "$$Proxy";

    // Activity
    @UiThread
    public static void bind(@NonNull Activity target){
        View sourceView = target.getWindow().getDecorView();
        createBinding(target, sourceView);
    }

    // View
    @UiThread
    public static void bind(@NonNull View target){
        createBinding(target, target);
    }

    // Fragment
    @UiThread
    public static void bind(@NonNull Object target, @NonNull View source){
        createBinding(target, source);
    }

    public static void createBinding(@NonNull Object target, @NonNull View root) {
        // 生成类名+后缀名的代理类，并通过反射执行注入操作
        try {
            Class<?> targetClass = target.getClass();
            Class<?> proxyClass = Class.forName(targetClass.getName() + SUFFIX);
            IProxy proxy = (IProxy) proxyClass.newInstance();
            proxy.inject(target, root);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }






















}
