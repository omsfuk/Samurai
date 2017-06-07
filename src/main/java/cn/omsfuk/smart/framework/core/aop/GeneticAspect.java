package cn.omsfuk.smart.framework.core.aop;

import java.lang.annotation.Annotation;
import java.util.function.BiFunction;

/**
 * Created by omsfuk on 17-6-4.
 */
public class GeneticAspect {

    private String weavingClass;

    private String weavingMethod;

    private int order;

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
