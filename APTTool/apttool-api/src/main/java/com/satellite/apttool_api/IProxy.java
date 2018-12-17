package com.satellite.apttool_api;

import android.view.View;

public interface IProxy<T> {
    /**
     *
     * @param target
     *           注解所在的类
     * @param root
     *           类的根view
     */
    public void inject(final T target, View root);
}
