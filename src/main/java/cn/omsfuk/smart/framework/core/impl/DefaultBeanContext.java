package cn.omsfuk.smart.framework.core.impl;

import cn.omsfuk.smart.framework.core.AbstractBean;
import cn.omsfuk.smart.framework.core.BeanContext;
import cn.omsfuk.smart.framework.core.InstanceFactory;
import cn.omsfuk.smart.framework.core.ProxyChain;
import cn.omsfuk.smart.framework.core.annotation.*;
import cn.omsfuk.smart.framework.core.bean.PrototypeBeanFactory;
import cn.omsfuk.smart.framework.core.bean.RequestBeanFactory;
import cn.omsfuk.smart.framework.core.bean.SingletonBeanFactory;
import cn.omsfuk.smart.framework.core.exception.InstanceBeanException;
import cn.omsfuk.smart.framework.helper.ClassHelper;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-29.
 */
public class DefaultBeanContext extends AbstractBeanContext {

    private static final String MVC_ASPECT = "cn.omsfuk.smart.framework.mvc.aspect";

    private static final String TX_ASPECT = "cn.omsfuk.smart.framework.tx.aspect";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBeanContext.class);

    private SingletonBeanFactory singletonBeanFactory;

    private ThreadLocal<RequestBeanFactory> requestBeanFactory;

    private PrototypeBeanFactory prototypeBeanFactory;

    private InstanceFactory instanceFactory = new InstanceFactory(this);

    public DefaultBeanContext(String packageName) {
        singletonBeanFactory = new SingletonBeanFactory(instanceFactory);
        requestBeanFactory = new ThreadLocal<>();
        prototypeBeanFactory = new PrototypeBeanFactory(instanceFactory);

        List<Class<? extends Annotation>> annoList = new ArrayList<>();
        annoList.add(Controller.class);
        annoList.add(Service.class);
        annoList.add(Component.class);
        Map<String, Class<?>> beanMap = resolveBean(scanBeanByAnnotation(packageName, annoList));

        List<ProxyChain> proxys = getProxys(packageName);
        fillAspect(beanMap, proxys);
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
    public void setBean(String name, Constructor constructor, BeanScope beanScope) {
        if (beanScope == BeanScope.singleton) {
            getSingletonBeanFactory().setBean(name, constructor);
        } else if (beanScope == BeanScope.request) {
            getRequestBeanFactory().setBean(name, constructor);
        } else if (beanScope == BeanScope.prototype) {
            getPrototypeBeanFactory().setBean(name, constructor);
        }
    }

    @Override
    public void setBean(String name, Object configObject, Method method, BeanScope beanScope) {
        if (beanScope == BeanScope.singleton) {
            getSingletonBeanFactory().setBean(name, configObject, method);
        } else if (beanScope == BeanScope.request) {
            getRequestBeanFactory().setBean(name, configObject, method);
        } else if (beanScope == BeanScope.prototype) {
            getPrototypeBeanFactory().setBean(name, configObject, method);
        }
    }

    public void setBean(String name, Class<?> beanClass, BeanScope beanScope) {
        System.out.println("beanClass" + beanClass);
        if (beanClass.getConstructors().length != 1) {
            throw new InstanceBeanException();
        }
        setBean(name, beanClass.getConstructors()[0], beanScope);
    }

    @Override
    public void setBean(String name, Object instance, BeanScope beanScope) {
        if (beanScope == BeanScope.singleton) {
            getSingletonBeanFactory().setBean(name, instance);
        } else if (beanScope == BeanScope.request) {
            getRequestBeanFactory().setBean(name, instance);
        }
        // TODO prototype没有提示
    }

    @Override
    public void removeRequestBeans() {
        requestBeanFactory.remove();
    }

    private Map<String, Class<?>> resolveBean(List<Class<?>> beanClasses) {
        Map<String, Class<?>> beanMap = new ConcurrentHashMap<>();
        beanClasses.forEach(beanClass -> {
            beanMap.put(getBeanId(beanClass), beanClass);
        });
        return beanMap;
    }

    private List<ProxyChain>  getProxys(String packageName) {
        List<Pair<Integer, Object>> aspects = new LinkedList<>();

        ClassHelper.getClassByAnnotation(packageName, Aspect.class).stream().forEach(aspectClass -> {
            Pair<Integer, Object> pair = null;
            pair = new Pair<>(aspectClass.getAnnotation(Order.class).value(), getInstance(aspectClass));
            aspects.add(pair);
        });

        ClassHelper.getClassByAnnotation(MVC_ASPECT, Aspect.class).stream().forEach(aspectClass -> {
            Pair<Integer, Object> pair = null;
            pair = new Pair<>(aspectClass.getAnnotation(Order.class).value(), getInstance(aspectClass));
            aspects.add(pair);
        });



        Collections.sort(aspects, (a, b) -> b.getKey() - a.getKey());

        return convertToProxyChain(aspects);
    }

    private List<ProxyChain> convertToProxyChain(List<Pair<Integer, Object>> aspects) {
        return aspects.stream()
                .flatMap(aspect -> {
                    List<ProxyChain> proxyList = new LinkedList<>();
                    Stream.of(aspect.getValue().getClass().getDeclaredMethods()).forEach(method -> {
                        if ((!method.isAnnotationPresent(Before.class)) && (!method.isAnnotationPresent(After.class)) && (!method.isAnnotationPresent(Around.class))) {
                            return ;
                        }

                        if (method.isAnnotationPresent(Before.class)) {
                            Before before = method.getAnnotation(Before.class);
                            // TODO 异常处理
                            ProxyChain proxyChain = new ProxyChain(before.value(), before.method(), before.anno());
                            proxyChain.setBefore(() -> {
                                try {
                                    method.invoke(aspect.getValue(), null);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            proxyList.add(proxyChain);
                        }

                        if (method.isAnnotationPresent(After.class)) {
                            After after = method.getAnnotation(After.class);
                            // TODO 异常处理
                            ProxyChain proxyChain = new ProxyChain(after.value(), after.method(), after.anno());
                            proxyChain.setAfter(() -> {try {
                                method.invoke(aspect.getValue(), null);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }});
                            proxyList.add(proxyChain);
                        }
                        if (method.isAnnotationPresent(Around.class)) {
                            Around around = method.getAnnotation(Around.class);
                            // TODO 异常处理
                            ProxyChain proxyChain = new ProxyChain(around.value(), around.method(), around.anno());
                            proxyChain.setAround((method0, args, proxyChain1) -> {
                                try {
                                    method.setAccessible(true);
                                    return method.invoke(aspect.getValue(), method0, args, proxyChain1);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }

                            });
                            proxyList.add(proxyChain);
                        }
                    });

                    return proxyList.stream();
                })
                .collect(Collectors.toList());
    }

    private String getBeanId(Class<?> beanClass) {
        String beanId = "";
        if (beanClass.isAnnotationPresent(Component.class) && !(beanId = beanClass.getAnnotation(Component.class).value()).equals("")) {
            return beanId;
        }
        if (beanClass.isAnnotationPresent(Controller.class) && !(beanId = beanClass.getAnnotation(Controller.class).value()).equals("")) {
            return beanId;
        }
        if (beanClass.isAnnotationPresent(Repository.class) && !(beanId = beanClass.getAnnotation(Repository.class).value()).equals("")) {
            return beanId;
        }
        if (beanClass.isAnnotationPresent(Service.class) && !(beanId = beanClass.getAnnotation(Service.class).value()).equals("")) {
            return beanId;
        }
        return beanClass.getSimpleName();
    }

    public SingletonBeanFactory getSingletonBeanFactory() {
        return singletonBeanFactory;
    }

    public PrototypeBeanFactory getPrototypeBeanFactory() {
        return prototypeBeanFactory;
    }

    public RequestBeanFactory getRequestBeanFactory() {
        RequestBeanFactory requestBeans =  requestBeanFactory.get();
        if (requestBeans == null) {
            requestBeans = new RequestBeanFactory(instanceFactory);
            requestBeanFactory.set(requestBeans);
        }
        return requestBeans;
    }
}
