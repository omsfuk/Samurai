package cn.omsfuk.smart.framework.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by omsfuk on 17-5-27.
 */

public class AopHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

//    static {
//        LOGGER.debug("init aspect...");
//        List<Pair<Integer, Object>> aspects = new LinkedList<>();
//
//        ClassHelper.getClassesByAnnotation(Aspect.class).stream().forEach(aspectClass -> {
//            Pair<Integer, Object> pair = null;
//            try {
//                pair = new Pair<>(aspectClass.getAnnotation(Order.class).value(), BeanHelper.satisfyFieldDependency(aspectClass.newInstance()));
//            } catch (InstantiationException | IllegalAccessException e) {
//                LOGGER.error("fail to init Aspect class");
//                throw new RuntimeException(e);
//            }
//            aspects.add(pair);
//        });
//
//        Collections.sort(aspects, Comparator.comparingInt(Pair::getKey));
//
//        List<Pair<Class<?>, ProxyChain>> proxys = convertToProxyChain(aspects);
//
//        BeanHelper.getAllBeans().forEach(bean -> {
//            BeanHelper.setBean(CgLibUtil.getProxy(bean.getClass(), proxys.stream()
//                    .filter((proxy) -> isClassMatch(proxy.getValue().getClassName(), bean.getClass().getName()))
//                    .map(Pair::getValue)
//                    .collect(Collectors.toList())));
//        });
//
////        BeanHelper.getAllBeans().stream().forEachOrdered(System.out::println);
////
////        BeanHelper.getBeanByAnnotation(Controller.class).forEach(controller -> {
////            BeanHelper.updateToProxyBean(controller.getClass(), CgLibUtil.getProxy(controller));
////        });
//
//    }

    private static boolean isClassMatch(String pattern, String name) {
        return name.matches(pattern);
    }

}
