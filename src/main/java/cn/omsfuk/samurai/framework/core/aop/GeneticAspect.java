package cn.omsfuk.samurai.framework.core.aop;

import java.lang.annotation.Annotation;
import java.util.function.BiFunction;

/**
 * 一个切面。包括要拦截的类名称的描述，方法的描述，注解的描述以及切面优先级，前置，后置和环绕动作
 * Created by omsfuk on 17-6-4.
 */
public class GeneticAspect {

    /**
     * 要拦截的类的名称的正则表达式模式
     */
    private String weavingClass;

    /**
     * 要拦截的方法的正则表达式模式
     */
    private String weavingMethod;

    /**
     * 优先级
     */
    private int order;

    /**
     * 要拦截的注解
     */
    private Class<? extends Annotation> weavingAnnotation;

    private Runnable before;

    private Runnable after;

    private BiFunction<Invocation, ProxyChain, Object> around;

    public GeneticAspect(String weavingClass, String weavingMethod, Class<? extends Annotation> weavingAnnotation, int order) {
        this.weavingClass = weavingClass;
        this.weavingMethod = weavingMethod;
        this.weavingAnnotation = weavingAnnotation;
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public String getWeavingClass() {
        return weavingClass;
    }

    public Class<? extends Annotation> getWeavingAnnotation() {
        return weavingAnnotation;
    }

    public Runnable getBefore() {
        return before;
    }

    public Runnable getAfter() {
        return after;
    }

    public BiFunction<Invocation, ProxyChain, Object> getAround() {
        return around;
    }

    public void setBefore(Runnable before) {
        this.before = before;
    }

    public void setAfter(Runnable after) {
        this.after = after;
    }

    public void setAround(BiFunction<Invocation, ProxyChain, Object> around) {
        this.around = around;
    }

}
