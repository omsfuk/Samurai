package cn.omsfuk.smart.framework.core.impl;

import cn.omsfuk.smart.framework.core.ProxyChain;
import cn.omsfuk.smart.framework.core.annotation.*;
import cn.omsfuk.smart.framework.helper.AnnotationHelper;
import cn.omsfuk.smart.framework.helper.CgLibUtil;
import cn.omsfuk.smart.framework.helper.ClassHelper;
import cn.omsfuk.smart.framework.core.BeanContext;
import cn.omsfuk.smart.framework.core.exception.BeanConflictException;
import cn.omsfuk.smart.framework.core.exception.InstanceBeanException;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-29.
 */
public class DefaultBeanContext implements BeanContext {

    private static ThreadLocal<DefaultBeanContext> defaultBeanContextThreadLocal = new ThreadLocal<>();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBeanContext.class);

    private SingletonBeanMap singletonBeanMap;

    private ThreadLocal<RequestBeanMap> requestBeanMap;

    private PrototypeBeanMap prototypeBeanMap;

    public DefaultBeanContext(String packageName) {
        singletonBeanMap = new SingletonBeanMap();
        requestBeanMap = new ThreadLocal<>();
        prototypeBeanMap = new PrototypeBeanMap();

        Map<String, Class<?>> beanMap = resolveBean(scannerBeans(packageName));
        List<ProxyChain> proxys = getProxys(packageName);
        fillAspect(beanMap, proxys);
    }

    private void fillAspect(Map<String, Class<?>> beanMap, List<ProxyChain> proxys) {
        beanMap.forEach((key, value) -> {
            BeanScope beanScope = BeanScope.singleton;
            if (value.isAnnotationPresent(Scope.class)) {
                beanScope = value.getAnnotation(Scope.class).value();
            }
            Class proxyClass = CgLibUtil.getProxy(value, proxys.stream()
                    .filter(proxy -> isClassMatch(proxy.getClassName(), value.getName()) || value.isAnnotationPresent(proxy.getAnnotation()))
                    .collect(Collectors.toList()));
            beanMap.put(key, proxyClass);
            setBean(key, proxyClass, beanScope);
        });
    }

    public static void set(DefaultBeanContext beanCtx) {
        defaultBeanContextThreadLocal.set(beanCtx);
    }

    public static BeanContext get() {
        return defaultBeanContextThreadLocal.get();
    }

    public static void remove() {
        defaultBeanContextThreadLocal.remove();
    }

    @Override
    public Object getBean(String beanId) {
        Object bean = null;
        if (((bean = getPrototypeBeanMap().get(beanId)) == null)
                && ((bean = getRequestBeanMap().get(beanId)) == null)
                && ((bean = getSingletonBeanMap().get(beanId)) == null)) {
            return null;
        }
        return bean;
    }

    @Override
    public Object getBean(Class<?> beanClass) {
        Object bean = null;
        if ((bean = getBean(beanClass.getSimpleName())) != null) {
            return bean;
        }

        Optional<Object> ans = getSingletonBeanMap().getBeanCollection().stream().filter(bean0 -> beanClass.isInstance(bean0)).findAny();
        if(ans.isPresent()) {
            return ans.get();
        }
        ans = getRequestBeanMap().getBeanCollection().stream().filter(bean0 -> beanClass.isInstance(bean0)).findAny();
        if(ans.isPresent()) {
            return ans.get();
        }

        Optional<Class<?>> optional = getPrototypeBeanMap().getBeanClassCollection().stream().filter(beanCls -> beanClass.isAssignableFrom(beanCls)).findAny();
        if (optional.isPresent()) {
            return getInstance(optional.get());
        }

        return null;
    }

    @Override
    public void setBean(String name, Class<?> beanClass, BeanScope beanScope) {
        if (beanScope == BeanScope.singleton) {
            getSingletonBeanMap().put(name, beanClass);
        } else if (beanScope == BeanScope.request) {
            getRequestBeanMap().put(name, beanClass);
        } else if (beanScope == BeanScope.prototype) {
            getPrototypeBeanMap().put(name, beanClass);
        }
    }

    @Override
    public void setBean(String name, Object obj, BeanScope beanScope) {
        if (beanScope == BeanScope.prototype) {
            return ;
        }
        if (beanScope == BeanScope.singleton) {
            getSingletonBeanMap().put(name, obj);
        }
        if (beanScope == BeanScope.request) {
            getRequestBeanMap().put(name, obj);
        }
    }

    @Override
    public void removeRequestBeans() {
        requestBeanMap.remove();
    }

    private static boolean isClassMatch(String pattern, String name) {
        return name.matches(pattern);
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

        ClassHelper.getClassesByAnnotation(packageName, Aspect.class).stream().forEach(aspectClass -> {
            Pair<Integer, Object> pair = null;
            pair = new Pair<>(aspectClass.getAnnotation(Order.class).value(), getInstance(aspectClass));
            aspects.add(pair);
        });


        Collections.sort(aspects, Comparator.comparingInt(Pair::getKey));

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
                            proxyChain.setBefore(() -> {try {
                                method.invoke(aspect.getValue(), null);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }});
                            proxyList.add(proxyChain);
                        }

                        if (method.isAnnotationPresent(After.class)) {
                            After after = method.getAnnotation(After.class);
                            // TODO 异常处理
                            ProxyChain proxyChain = new ProxyChain(after.value(), after.method(), after.anno());
                            proxyChain.setAfter(() -> {try {
                                method.invoke(aspect.getValue(), null);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }});
                            proxyList.add(proxyChain);
                        }
                        if (method.isAnnotationPresent(Around.class)) {
                            Around around = method.getAnnotation(Around.class);
                            // TODO 异常处理
                            ProxyChain proxyChain = new ProxyChain(around.value(), around.method(), around.anno());
                            proxyChain.setAround((method0, args, proxyChain1) -> {
                                try {
                                    return method.invoke(aspect.getValue(), new Object[] {method0, args, proxyChain1});
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                                return null;
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

    private List<Class<?>> scannerBeans(String packageName) {
        List<Class<? extends Annotation>> annotations = new ArrayList<>();
        annotations.add(Controller.class);
        annotations.add(Repository.class);
        annotations.add(Service.class);
        annotations.add(Component.class);

        return ClassHelper.getClassesByAnnotation(packageName, annotations);
    }

    public SingletonBeanMap getSingletonBeanMap() {
        return singletonBeanMap;
    }

    public PrototypeBeanMap getPrototypeBeanMap() {
        return prototypeBeanMap;
    }

    public RequestBeanMap getRequestBeanMap() {
        RequestBeanMap requestBeans =  requestBeanMap.get();
        if (requestBeans == null) {
            requestBeans = new RequestBeanMap();
            requestBeanMap.set(requestBeans);
        }
        return requestBeans;
    }

    private Object satisfyFieldDenpendencies(Object bean) {
        Class<?> beanClass = AnnotationHelper.getOriginClass(bean.getClass());
        Stream.of(beanClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> {
                    String beanType = field.getAnnotation(Inject.class).value();
                    try {
                        if ("".equals(beanType)) {
                            field.set(bean, getBean(field.getType()));
                        } else {
                            field.set(bean, getBean(beanType));
                        }
                    } catch (IllegalAccessException e) {
                        // TODO 异常处理
                        e.printStackTrace();
                    }
                });
        return bean;
    }

    private Object getInstance(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getConstructors();
        if (constructors.length != 1) {
            InstanceBeanException instanceBeanException = new InstanceBeanException();
            LOGGER.error("more than one construction [" + beanClass.getName() + "]", instanceBeanException);
            throw instanceBeanException;
        }

        Constructor<?> constructor = constructors[0];
        Object[] params = new Object[constructor.getParameterCount()];
        Class<?>[] paramTypes = constructor.getParameterTypes();
        String beanId = beanClass.getSimpleName();
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            params[i] = getBean(paramTypes[i]);
        }

        Object instance = null;
        try {
            instance = constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            InstanceBeanException instanceBeanException = new InstanceBeanException(e);
            LOGGER.error("constructor params can't be satisfied [" + beanClass.getName() + "]", instanceBeanException);
            throw instanceBeanException;
        }
        
        return satisfyFieldDenpendencies(instance);
    }

    private class SingletonBeanMap {
        /**
         * 设为ConcurrentHashMap。为了保证效率，且一般不会遇到并发的情况，所以设为ConcurrentHashMap
         */
        private Map<String, Class<?>> singletonBeanClassMap = new ConcurrentHashMap<>();

        /**
         * 缓存对象。用来延时加载Singleton级别的对象
         */
        private Map<String, Object> cacheBean = new ConcurrentHashMap<>();

        public Object get(String key) {
            Object instance = null;
            if (!cacheBean.containsKey(key)) {
                if (singletonBeanClassMap.containsKey(key)) {
                    cacheBean.put(key, getInstance(singletonBeanClassMap.get(key)));
                } else {
                    return null;
                }
            }

            return cacheBean.get(key);
        }
        
        public void put(String key, Object value) {
            if (cacheBean.containsKey(key)) {
                // TODO 异常处理
                throw new BeanConflictException();
            }
            cacheBean.put(key, value);
        }

        public void put(String key, Class<?> value) {
            if (singletonBeanClassMap.containsKey(key)) {
                BeanConflictException exception = new BeanConflictException();
                LOGGER.error("bean already exist [" + key + "]", exception);
                throw exception;
            }
            singletonBeanClassMap.put(key, value);
        }

        public void update(String key, Class<?> value) {
            singletonBeanMap.put(key, value);
            cacheBean.remove(key);
        }

        public Collection<Object> getBeanCollection() {
            return cacheBean.values();
        }
    }

    private class PrototypeBeanMap {
        /**
         * 设为ConcurrentHashMap。为了保证效率，且一般不会遇到并发的情况，所以设为ConcurrentHashMap
         */
        private Map<String, Class<?>> prototypeBeanClassMap = new ConcurrentHashMap<>();

        public Object get(String key) {
            if (!prototypeBeanClassMap.containsKey(key)) {
                return null;
            }

            return satisfyFieldDenpendencies(getInstance(prototypeBeanClassMap.get(key)));
        }

        public void put(String key, Class<?> value) {
            if (prototypeBeanClassMap.containsKey(key)) {
                BeanConflictException exception = new BeanConflictException();
                LOGGER.error("bean already exist [" + key + "]", exception);
                throw exception;
            }
            prototypeBeanClassMap.put(key, value);
        }

        public void update(String key, Class<?> value) {
            prototypeBeanMap.put(key, value);
        }

        public Collection<Class<?>> getBeanClassCollection() {
            return prototypeBeanClassMap.values();
        }
    }

    private class RequestBeanMap extends SingletonBeanMap {

    }
}
