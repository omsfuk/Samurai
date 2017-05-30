package cn.omsfuk.demo.aspect;

import cn.omsfuk.demo.controller.MainController;
import cn.omsfuk.smart.framework.aop.ProxyChain;
import cn.omsfuk.smart.framework.aop.annotation.After;
import cn.omsfuk.smart.framework.aop.annotation.Around;
import cn.omsfuk.smart.framework.aop.annotation.Aspect;
import cn.omsfuk.smart.framework.aop.annotation.Order;
import cn.omsfuk.smart.framework.ioc.DefaultBeanContext;
import cn.omsfuk.smart.framework.ioc.annotation.Controller;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-27.
 */


@Aspect
@Order(10)
public class AspectB {

    @Around(value = "", anno = Controller.class)
    public Object around(Method method, Object[] args, ProxyChain proxyChain) {
        HttpServletRequest request = (HttpServletRequest) DefaultBeanContext.get().getBean("HttpServletRequest");

        LocalVariableTableParameterNameDiscoverer u =
                new LocalVariableTableParameterNameDiscoverer();

        Parameter[] parameters = method.getParameters();
        String[] paramName = u.getParameterNames(method);
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < paramName.length; i++) {
            if (String.class.isAssignableFrom(parameters[i].getType())) {
                params[i] = request.getParameter(paramName[i]);
            } else if (Integer.class.isAssignableFrom(parameters[i].getType()) || int.class.isAssignableFrom(parameters[i].getType())) {
                params[i] = Integer.parseInt(request.getParameter(paramName[i]));
            } else if (Double.class.isAssignableFrom(parameters[i].getType()) || double.class.isAssignableFrom(parameters[i].getType())) {
                params[i] = Double.parseDouble(request.getParameter(paramName[i]));
            } else if (Boolean.class.isAssignableFrom(parameters[i].getType()) || boolean.class.isAssignableFrom(parameters[i].getType())) {
                params[i] = Boolean.parseBoolean(request.getParameter(paramName[i]));
            }
        }

        return proxyChain.doProxyChain(method, params);
    }
}
