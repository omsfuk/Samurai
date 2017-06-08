package cn.omsfuk.samurai.framework.mvc.aspect;

import cn.omsfuk.samurai.framework.core.annotation.Around;
import cn.omsfuk.samurai.framework.core.aop.Invocation;
import cn.omsfuk.samurai.framework.core.aop.ProxyChain;
import cn.omsfuk.samurai.framework.core.bean.BeanContextManager;
import cn.omsfuk.samurai.framework.core.bean.BeanContext;
import cn.omsfuk.samurai.framework.core.annotation.Aspect;
import cn.omsfuk.samurai.framework.core.annotation.Controller;
import cn.omsfuk.samurai.framework.core.annotation.Order;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Controller参数注入切面。负责解析Request中的参数，并注入Controller。并且包括需要从BeanContext自动注入的参数
 * Created by omsfuk on 17-5-30.
 */

@Aspect
@Order(100000)
public class ParameterAspect {
    @Around(value = "", anno = Controller.class)
    public Object around(Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();

        BeanContext beanContext = BeanContextManager.get();
        HttpServletRequest request = (HttpServletRequest) beanContext.getBean("HttpServletRequest");

        LocalVariableTableParameterNameDiscoverer u =
                new LocalVariableTableParameterNameDiscoverer();

        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] paramName = u.getParameterNames(method);
        Object[] params = new Object[paramName.length];
        for (int i = 0; i < paramName.length; i++) {
            if (String.class.isAssignableFrom(parameterTypes[i])) {
                params[i] = request.getParameter(paramName[i]);
            } else if (Integer.class.isAssignableFrom(parameterTypes[i]) || int.class.isAssignableFrom(parameterTypes[i])) {
                params[i] = Integer.parseInt(request.getParameter(paramName[i]));
            } else if (Double.class.isAssignableFrom(parameterTypes[i]) || double.class.isAssignableFrom(parameterTypes[i])) {
                params[i] = Double.parseDouble(request.getParameter(paramName[i]));
            } else if (Boolean.class.isAssignableFrom(parameterTypes[i]) || boolean.class.isAssignableFrom(parameterTypes[i])) {
                params[i] = Boolean.parseBoolean(request.getParameter(paramName[i]));
            }
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (params[i] == null) {
                params[i] = beanContext.getBean(parameterTypes[i]);
            }
        }

        return proxyChain.doProxyChain(invocation);
    }
}
