package com.satellite.library.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EventBus {

    public static final String TAG = EventBus.class.getSimpleName();

    private static volatile EventBus instance = null;
    private Handler handler;
    private Map<Object, List<SubscribeMethod>> methodMap;

    private EventBus (){
        handler = new Handler(Looper.getMainLooper());
        methodMap = new HashMap<>();
    }

    public static EventBus getDefault (){
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public void register (Object subscriber) {
        Class<?> clazz = subscriber.getClass();
        HashSet<String> eventTypesFound = new HashSet();
        List<SubscribeMethod> subscribeMethods = methodMap.get(subscriber);
        if (subscribeMethods == null) {
            subscribeMethods = new ArrayList<>();
        }

        StringBuilder methodKeyBuilder = new StringBuilder();
        while (clazz != null) {
            String className = clazz.getName();
            if (className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("android.")) {
                break;
            }

            Method[] methods = clazz.getMethods();
            int length = methods.length;
            for (int i = 0; i < length; i++) {
                Method method = methods[i];
                String methodName = method.getName();
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if (annotation == null) {
                    continue;
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
//                continue;
                    throw new AndroidRuntimeException(clazz + ": Method " + methodName
                            + "() can only has one parameter!");
                }
                ThreadMode threadMode = annotation.threadMode();
                Class<?> eventType = parameterTypes[0];

                methodKeyBuilder.setLength(0);
                methodKeyBuilder.append(methodName);
                methodKeyBuilder.append('>').append(eventType.getName());
                String methodKey = methodKeyBuilder.toString();
                if (eventTypesFound.add(methodKey)) {
                    SubscribeMethod subscribeMethod = new SubscribeMethod(method, threadMode, eventType);
                    subscribeMethods.add(subscribeMethod);
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (subscribeMethods.isEmpty()) {
            throw new AndroidRuntimeException("Subscriber " + clazz + " has no Subscribe methods called ");
        } else {
            synchronized (methodMap) {
                methodMap.put(subscriber, subscribeMethods);
            }

        }

    }

    public void unRegister (Object subscriber) {
        List<SubscribeMethod> subscribeMethods = this.methodMap.get(subscriber);
        if (subscribeMethods != null) {
            synchronized (methodMap) {
                this.methodMap.remove(subscriber);
            }
        } else {
            Log.w(TAG, "Subscriber to unregister was not registered before: " + subscriber.getClass());
        }
    }

    public void post (Object event){
        Class<? extends Object> eventClass = event.getClass();
        try {
            for (Map.Entry<Object, List<SubscribeMethod>> entry : methodMap.entrySet()) {
                Object subscriber = entry.getKey();
                List<SubscribeMethod> subscribeMethods = entry.getValue();
                for (SubscribeMethod method : subscribeMethods) {
                    Class<?> eventType = method.getEventType();
                    if (eventClass != eventType) {
                        continue;
                    }
                    Method execute = method.getMethod();
                    invoke(subscriber, execute, event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invoke(Object subscriber, Method execute, Object args) {
        try {
            execute.invoke(subscriber, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
