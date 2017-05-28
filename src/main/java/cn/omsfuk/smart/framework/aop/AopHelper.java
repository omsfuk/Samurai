package cn.omsfuk.smart.framework.aop;

import cn.omsfuk.smart.framework.aop.annotation.*;
import cn.omsfuk.smart.framework.helper.CgLibUtil;
import cn.omsfuk.smart.framework.helper.ClassHelper;
import cn.omsfuk.smart.framework.ioc.BeanHelper;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-27.
 */

public class AopHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    static {
        LOGGER.debug("init aspect...");
        List<Pair<Integer, Object>> aspects = new LinkedList<>();

        ClassHelper.getClassesByAnnotation(Aspect.class).stream().forEach(aspectClass -> {
            Pair<Integer, Object> pair = null;
            try {
                pair = new Pair<>(aspectClass.getAnnotation(Order.class).value(), BeanHelper.satisfyFieldDependency(aspectClass.newInstance()));
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("fail to init Aspect class");
                throw new RuntimeException(e);
            }
            aspects.add(pair);
        });

        Collections.sort(aspects, Comparator.comparingInt(Pair::getKey));

        List<Pair<Class<?>, ProxyChain>> proxys = convertToProxyChain(aspects);

        BeanHelper.getAllBeans().forEach(bean -> {
            BeanHelper.setBean(CgLibUtil.getProxy(bean.getClass(), proxys.stream()
                    .filter((proxy) -> isClassMatch(proxy.getValue().getClassName(), bean.getClass().getName()))
                    .map(Pair::getValue)
                    .collect(Collectors.toList())));
        });

//        BeanHelper.getAllBeans().stream().forEachOrdered(System.out::println);
//
//        BeanHelper.getBeanByAnnotation(Controller.class).forEach(controller -> {
//            BeanHelper.updateToProxyBean(controller.getClass(), CgLibUtil.getProxy(controller));
//        });

    }

    private static boolean isClassMatch(String pattern, String name) {
        System.out.println("pattern : [" + pattern + "] name [" + name + "]");
        return name.matches(pattern);
    }

    private static List<Pair<Class<?>, ProxyChain>> convertToProxyChain(List<Pair<Integer, Object>> aspects) {
        return aspects.stream()
                .flatMap(aspect -> {
                    List<Pair<Class<?>, ProxyChain>> proxyList = new LinkedList<>();
                    Stream.of(aspect.getValue().getClass().getDeclaredMethods()).forEach(method -> {
                        if((!method.isAnnotationPresent(Before.class)) && (!method.isAnnotationPresent(After.class)) && (!method.isAnnotationPresent(Around.class))) {
                            return ;
                        }

                        if(method.isAnnotationPresent(Before.class)) {
                            Before before = method.getAnnotation(Before.class);
                            // TODO 异常处理
                            ProxyChain proxyChain = new ProxyChain(before.value(), before.method());
                            proxyChain.setBefore(() -> {try {
                                method.invoke(aspect.getValue(), null);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }});
                            proxyList.add(new Pair(aspect.getValue().getClass(), proxyChain));
                        }

                        if (method.isAnnotationPresent(After.class)) {
                            After after = method.getAnnotation(After.class);
                            // TODO 异常处理
                            ProxyChain proxyChain = new ProxyChain(after.value(), after.method());
                            proxyChain.setAfter(() -> {try {
                                method.invoke(aspect.getValue(), null);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }});
                            proxyList.add(new Pair(aspect.getValue().getClass(), proxyChain));
                        }
                        if(method.isAnnotationPresent(Around.class)) {
                            Around around = method.getAnnotation(Around.class);
                            // TODO 异常处理
                            ProxyChain proxyChain = new ProxyChain(around.value(), around.method());
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
                            proxyList.add(new Pair(aspect.getValue().getClass(), proxyChain));
                        }
                    });

                    return proxyList.stream();
                })
                .collect(Collectors.toList());
    }

}
