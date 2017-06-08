package cn.omsfuk.samurai.framework.core.bean.impl;

import cn.omsfuk.samurai.framework.core.AspectUtil;
import cn.omsfuk.samurai.framework.core.InstanceFactory;
import cn.omsfuk.samurai.framework.core.annotation.Aspect;
import cn.omsfuk.samurai.framework.core.annotation.BeanScope;
import cn.omsfuk.samurai.framework.core.aop.GeneticAspect;
import cn.omsfuk.samurai.framework.core.aop.ProxyFactory;
import cn.omsfuk.samurai.framework.core.bean.*;
import cn.omsfuk.samurai.framework.core.exception.InstanceBeanException;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 默认的BeanContext。注解驱动
 * Created by omsfuk on 17-5-29.
 */
public class DefaultBeanContext extends AbstractBeanContext {

    private InstanceFactory instanceFactory = new InstanceFactory(this);

    private ProxyFactory proxyFactory = new ProxyFactory();

    private SingletonBeanFactory singletonBeanFactory;

    private ThreadLocal<RequestBeanFactory> requestBeanFactory;

    private PrototypeBeanFactory prototypeBeanFactory;

    public DefaultBeanContext(String... packages) {
        singletonBeanFactory = new SingletonBeanFactory(instanceFactory);
        requestBeanFactory = new ThreadLocal<>();
        prototypeBeanFactory = new PrototypeBeanFactory(instanceFactory);
        init(packages);
    }

    private void init(String... packages) {
        List<AbstractBean> beans = scanBean(packages);
        List<GeneticAspect> aspects = extractAspect(packages);
        weaveAspect(beans, aspects);
        registerBean(beans);
    }

    @Override
    public Object getBean(String beanId) {
        Object bean = null;
        if (((bean = getPrototypeBeanFactory().getBean(beanId)) == null)
                && ((bean = getRequestBeanFactory().getBean(beanId)) == null)
                && ((bean = getSingletonBeanFactory().getBean(beanId)) == null)) {
            return null;
        }
        return bean;
    }

    @Override
    public Object getBean(Class<?> beanClass) {
        Object bean = null;
        if ((bean = getBean(beanClass.getSimpleName())) == null
                && (bean = getSingletonBeanFactory().getBean(beanClass)) == null
                && (bean = getPrototypeBeanFactory().getBean(beanClass)) == null
                && (bean = getRequestBeanFactory().getBean(beanClass)) == null) {
            return null;
        }
        return bean;
    }

    @Override
    public void setBean(String name, Object configObject, Method method, BeanScope beanScope) {
        AbstractBean bean = new AbstractBean(name, configObject, method, beanScope);
        if (beanScope == BeanScope.singleton) {
            getSingletonBeanFactory().setBean(bean);
        } else if (beanScope == BeanScope.request) {
            getRequestBeanFactory().setBean(bean);
        } else if (beanScope == BeanScope.prototype) {
            getPrototypeBeanFactory().setBean(bean);
        }
    }

    @Override
    public void setBean(String name, Class<?> beanClass, BeanScope beanScope) {
        if (beanClass.getConstructors().length != 1) {
            throw new InstanceBeanException();
        }
        getSingletonBeanFactory().setBean(new AbstractBean(name, beanClass.getConstructors()[0], beanScope));
    }

    @Override
    public void setBean(String name, Object instance, BeanScope beanScope) {
        if (beanScope == BeanScope.singleton) {
            getSingletonBeanFactory().setBean(new AbstractBean(name, instance, BeanScope.singleton));
        } else if (beanScope == BeanScope.request) {
            getRequestBeanFactory().setBean(new AbstractBean(name, instance, BeanScope.request));
        }
        // TODO prototype没有提示
    }

    @Override
    public void removeRequestBeans() {
        requestBeanFactory.remove();
    }

    private List<GeneticAspect> extractAspect(String... packages) {
        List<GeneticAspect> aspects = new LinkedList<>();
        scanAspect(packages).stream().forEach(aspectClass -> {
            Object aspectObject = instanceFactory.getInstanceWithoutAutowiring(aspectClass);
            Aspect aspectAnno = aspectClass.getAnnotation(Aspect.class);
            int order = aspectAnno.order();
            Stream.of(aspectClass.getDeclaredMethods())
                    .filter(AspectUtil::isAspectMethod)
                    .forEach(method -> aspects.add(AspectUtil.getAspect(aspectObject, method, order)));
        });

        Collections.sort(aspects, (a, b) -> b.getOrder() - a.getOrder());
        return aspects;
    }

    private void registerBean(List<AbstractBean> beans) {
        beans.stream()
                .filter(bean -> bean.getBeanScope() == BeanScope.prototype)
                .forEach(bean ->getPrototypeBeanFactory().setBean(bean));
        beans.stream()
                .filter(bean -> bean.getBeanScope() == BeanScope.singleton)
                .forEach(bean -> getSingletonBeanFactory().setBean(bean));
        beans.stream()
                .filter(bean -> bean.getBeanScope() == BeanScope.prototype)
                .forEach(bean -> getRequestBeanFactory().setBean(bean));
    }

    private void weaveAspect(List<AbstractBean> beans, List<GeneticAspect> aspects) {
        beans.forEach(bean -> {
            Class<?> proxyClass = proxyFactory.weaveAspect(bean.getBeanType(), findMatchedAspect(bean.getBeanType(), aspects));
            bean.setConstructor(new BeanConstructor(proxyClass.getConstructors()[0]));
        });
    }

    private List<GeneticAspect> findMatchedAspect(Class<?> beanClass, List<GeneticAspect> aspects) {
        return aspects.stream()
                .filter(aspect -> isClassMatch(aspect.getWeavingClass(), beanClass.getName()) || beanClass.isAnnotationPresent(aspect.getWeavingAnnotation()))
                .collect(Collectors.toList());
    }

    private boolean isClassMatch(String pattern, String name) {
        return name.matches(pattern);
    }

    private SingletonBeanFactory getSingletonBeanFactory() {
        return singletonBeanFactory;
    }

    private PrototypeBeanFactory getPrototypeBeanFactory() {
        return prototypeBeanFactory;
    }

    private RequestBeanFactory getRequestBeanFactory() {
        RequestBeanFactory requestBeans =  requestBeanFactory.get();
        if (requestBeans == null) {
            requestBeans = new RequestBeanFactory(instanceFactory);
            requestBeanFactory.set(requestBeans);
        }
        return requestBeans;
    }
}
