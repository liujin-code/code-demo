package com.geekhalo.lego.core.loader.support;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.Map;

import static com.geekhalo.lego.core.utils.BeanUtil.getPropertyName;
import static com.geekhalo.lego.core.utils.BeanUtil.isGetter;


public class LazyLoaderInterceptor implements InvocationHandler, MethodInterceptor {
    private final Map<String, PropertyLazyLoader> lazyLoaderCache;
    private final Object target;

    public LazyLoaderInterceptor(Map<String, PropertyLazyLoader> lazyLoaderCache, Object target) {
        this.target = target;
        this.lazyLoaderCache = lazyLoaderCache;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation instanceof ProxyMethodInvocation){
            ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) methodInvocation;
            return invoke(proxyMethodInvocation.getProxy(), proxyMethodInvocation.getMethod(), proxyMethodInvocation.getArguments());
        }
        return invoke(methodInvocation.getThis(), methodInvocation.getMethod(), methodInvocation.getArguments());
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (isGetter(method)) {
            String propertyName = getPropertyName(method);
            PropertyLazyLoader propertyLazyLoader = this.lazyLoaderCache.get(propertyName);

            if (propertyLazyLoader != null) {
                Object data = method.invoke(target, objects);
                if (data != null) {
                    return data;
                }

                data = propertyLazyLoader.loadData(o);

                if (data != null) {
                    FieldUtils.writeField(target, propertyName, data, true);
                }
                return data;
            }else {
                method.invoke(target, objects);
            }
        }

        return method.invoke(target, objects);
    }
}
