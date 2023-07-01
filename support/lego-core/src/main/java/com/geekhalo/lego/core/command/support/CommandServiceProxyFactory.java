package com.geekhalo.lego.core.command.support;

import com.geekhalo.lego.core.command.CommandRepository;
import com.geekhalo.lego.core.command.CommandServiceMethodLostException;
import com.geekhalo.lego.core.command.NoCommandService;
import com.geekhalo.lego.core.command.support.method.CreateServiceMethodInvokerFactory;
import com.geekhalo.lego.core.command.support.method.SyncServiceMethodInvokerFactory;
import com.geekhalo.lego.core.command.support.method.UpdateServiceMethodInvokerFactory;
import com.geekhalo.lego.core.support.intercepter.MethodDispatcherInterceptor;
import com.geekhalo.lego.core.support.invoker.ServiceMethodInvoker;
import com.geekhalo.lego.core.support.invoker.ServiceMethodInvokerFactory;
import com.geekhalo.lego.core.support.invoker.TargetBasedServiceMethodInvokerFactory;
import com.geekhalo.lego.core.support.proxy.DefaultProxyObject;
import com.geekhalo.lego.core.support.proxy.ProxyObject;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.transaction.interceptor.TransactionalProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by taoli on 2022/9/25.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@Setter(AccessLevel.PRIVATE)
@Slf4j
public class CommandServiceProxyFactory implements BeanClassLoaderAware {
    private ClassLoader classLoader;

    @Autowired
    private ApplicationContext applicationContext;


    public <T> T createCommandService(Class commandService){
        CommandServiceMetadata metadata = CommandServiceMetadata.fromCommandService(commandService);

        Object target = createTargetService(metadata);
        ProxyFactory result = new ProxyFactory();

        // 设置代理的目标对象
        result.setTarget(target);
        // 设置代理类实现的 接口
        result.setInterfaces(metadata.getCommandServiceClass(), ProxyObject.class, TransactionalProxy.class);

        // 收集所有的方法，初始化时进行校验
        Set<Method> methods = Sets.newHashSet(ReflectionUtils.getAllDeclaredMethods(commandService));
        methods.addAll(Sets.newHashSet(ReflectionUtils.getAllDeclaredMethods(ProxyObject.class)));


        // 对 default 方法进行拦截和转发
        if (DefaultMethodInvokingMethodInterceptor.hasDefaultMethods(commandService)) {
            result.addAdvice(new DefaultMethodInvokingMethodInterceptor());
            Set<Method> methodsForRemove = methods.stream()
                            .filter(Method::isDefault)
                            .collect(Collectors.toSet());
            // 移除默认方法
            Set<Method> all = methodsForRemove.stream()
                    .flatMap(method -> MethodUtils.getOverrideHierarchy(method, ClassUtils.Interfaces.INCLUDE).stream())
                    .collect(Collectors.toSet());
            methods.removeAll(all);
        }

        // 对所有的实现进行封装，基于拦截器进行请求转发
        // 1. target 对象封装
        result.addAdvice(createTargetDispatcherInterceptor(target, methods));

        // 添加 ProxyObject 提供 Meta信息
        result.addAdvice(createTargetDispatcherInterceptor(new DefaultProxyObject(metadata.getCommandServiceClass()), methods));

        // 2. 自定义实现类封装
        List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(metadata.getCommandServiceClass());
        for (Class itfCls : allInterfaces){
            if (itfCls.getAnnotation(NoCommandService.class) != null){
                continue;
            }
            String clsName = itfCls.getSimpleName();
            String beanName = Character.toLowerCase(clsName.charAt(0)) + clsName.substring(1, clsName.length()) + "Impl";
            Object bean = applicationContext.getBean(beanName, itfCls);
            if (bean != null){
                result.addAdvice(createTargetDispatcherInterceptor(bean, methods));
            }
        }

        // 3. 自动解析方法封装
        Class repositoryClass = metadata.getRepositoryClass();

        CommandRepository repository = (CommandRepository) this.applicationContext.getBean(repositoryClass);

        MethodDispatcherInterceptor syncMethodDispatcherInterceptor = createSyncMethodDispatcherInterceptor(methods, repository, metadata);
        log.info("Auto Register SyncMethod for {} : {}", commandService, syncMethodDispatcherInterceptor);
        result.addAdvice(syncMethodDispatcherInterceptor);


        MethodDispatcherInterceptor createMethodDispatcher = createCreateMethodDispatcherInterceptor(methods, repository, metadata);
        log.info("Auto Register CreateMethod for {} : {}", commandService, createMethodDispatcher);
        result.addAdvice(createMethodDispatcher);


        MethodDispatcherInterceptor updateMethodDispatcher = createUpdateMethodDispatcherInterceptor(methods, repository, metadata);
        log.info("Auto Register UpdateMethod for {} : {}", commandService, updateMethodDispatcher);
        result.addAdvice(updateMethodDispatcher);


        if (CollectionUtils.isNotEmpty(methods)){
            log.error("Method Lost for {}, lost method is {}", commandService, methods);
            throw new CommandServiceMethodLostException(methods);
        }

        T proxy = (T) result.getProxy(classLoader);
        return proxy;
    }

    private MethodDispatcherInterceptor createCreateMethodDispatcherInterceptor(Set<Method> methods, CommandRepository repository, CommandServiceMetadata metadata) {
        MethodDispatcherInterceptor methodDispatcher = new MethodDispatcherInterceptor();

        CreateServiceMethodInvokerFactory methodInvokerFactory = new CreateServiceMethodInvokerFactory(metadata.getDomainClass());
        methodInvokerFactory.setCommandRepository(repository);
        this.applicationContext.getAutowireCapableBeanFactory().autowireBean(methodInvokerFactory);
        methodInvokerFactory.init();

        registerMethodInvokers(methods, methodDispatcher, methodInvokerFactory);
        return methodDispatcher;
    }

    private MethodDispatcherInterceptor createUpdateMethodDispatcherInterceptor(Set<Method> methods, CommandRepository repository, CommandServiceMetadata metadata) {
        MethodDispatcherInterceptor methodDispatcher = new MethodDispatcherInterceptor();

        UpdateServiceMethodInvokerFactory methodInvokerFactory = new UpdateServiceMethodInvokerFactory(metadata.getDomainClass());
        methodInvokerFactory.setCommandRepository(repository);
        this.applicationContext.getAutowireCapableBeanFactory().autowireBean(methodInvokerFactory);
        methodInvokerFactory.init();

        registerMethodInvokers(methods, methodDispatcher, methodInvokerFactory);
        return methodDispatcher;
    }

    private MethodDispatcherInterceptor createSyncMethodDispatcherInterceptor(Set<Method> methods, CommandRepository repository, CommandServiceMetadata metadata) {
        MethodDispatcherInterceptor methodDispatcher = new MethodDispatcherInterceptor();

        SyncServiceMethodInvokerFactory methodInvokerFactory = new SyncServiceMethodInvokerFactory(metadata.getDomainClass());
        methodInvokerFactory.setCommandRepository(repository);
        this.applicationContext.getAutowireCapableBeanFactory().autowireBean(methodInvokerFactory);
        methodInvokerFactory.init();


        registerMethodInvokers(methods, methodDispatcher, methodInvokerFactory);
        return methodDispatcher;
    }

    private MethodDispatcherInterceptor createTargetDispatcherInterceptor(Object target, Set<Method> methods){
        MethodDispatcherInterceptor targetMethodDispatcher = new MethodDispatcherInterceptor();
        TargetBasedServiceMethodInvokerFactory targetBasedQueryServiceMethodFactory = new TargetBasedServiceMethodInvokerFactory(target);

        registerMethodInvokers(methods, targetMethodDispatcher, targetBasedQueryServiceMethodFactory);

        return targetMethodDispatcher;
    }

    private void registerMethodInvokers(Set<Method> methods,
                                        MethodDispatcherInterceptor targetMethodDispatcher,
                                        ServiceMethodInvokerFactory serviceMethodInvokerFactory) {
        Set<Method> methodsForRemove = Sets.newHashSet();
        for (Method callMethod : methods){
            if (methodsForRemove.contains(callMethod)){
                continue;
            }
            ServiceMethodInvoker exeMethod = serviceMethodInvokerFactory.createForMethod(callMethod);
            if (exeMethod != null){
                Set<Method> overrideHierarchy = MethodUtils.getOverrideHierarchy(callMethod, ClassUtils.Interfaces.INCLUDE);
                for (Method hMethod : overrideHierarchy) {
                    targetMethodDispatcher.register(hMethod, exeMethod);
                    methodsForRemove.add(hMethod);
                }
            }
        }
        methods.removeAll(methodsForRemove);
    }

    private Object createTargetService(CommandServiceMetadata metadata) {
        return new Object();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
