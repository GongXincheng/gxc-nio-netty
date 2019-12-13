package com.gxc.spring.component.mvc.handler;

import com.gxc.spring.annotation.PathVariable;
import com.gxc.spring.annotation.RequestMapping;
import com.gxc.spring.component.context.AbstractApplicationContext;
import com.gxc.spring.model.enums.RequestMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-13 15:39
 */
public abstract class AbstractRequestHandler implements RequestHandler {

    /**
     * 获取Controller的方法 并执行返回
     *
     * @param reqMethod reqMethod
     * @param uri       uri
     * @return Object
     * @throws Exception Exception
     */
    Object getHandlerMethod(String reqMethod, String uri) throws Exception {

        // 获取所有Controller的Map
        Map<String, Object> controllerMap = AbstractApplicationContext.getControllerMap();
        for (Map.Entry<String, Object> entry : controllerMap.entrySet()) {
            Object object = entry.getValue();

            // 获取该类所有的方法
            Method[] methods = object.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                    // 如果请求方式匹配，则继续执行，如果不匹配则跳过当前循环
                    RequestMethod requestMethod = requestMapping.method();
                    if (!Objects.equals(requestMethod, RequestMethod.codeOf(reqMethod.toUpperCase()))) {
                        continue;
                    }

                    // 获取requestMapping的uri
                    String value = requestMapping.value();
                    if (!value.startsWith("/")) {
                        value = "/".concat(value);
                    }


                    // 方法的入参是否含有 PathVariable 注解
                    if (hasPathVariable(method)) {
                        // 匹配 uri
                        if (!Objects.equals(value.substring(0, value.lastIndexOf("/") + 1),
                                uri.substring(0, uri.lastIndexOf("/") + 1))) {
                            continue;
                        }

                        String paramString = uri.substring(uri.lastIndexOf("/") + 1);

                        // TODO：牛逼！
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        Class<?> parameterType = parameterTypes[0];
                        Constructor<?> constructor = parameterType.getConstructor(paramString.getClass());
                        Object o = constructor.newInstance(paramString);

                        return method.invoke(object, o);
                    }
                }
            }

        }
        return null;
    }

    /**
     * Controller的方法的入参是否有 @PathVariable 注解.
     *
     * @param method Method
     * @return boolean
     */
    private boolean hasPathVariable(Method method) {
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            PathVariable annotation = parameter.getAnnotation(PathVariable.class);
            if (Objects.isNull(annotation)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String uri = "/user/1";
        String paramString = uri.substring(uri.lastIndexOf("/") + 1);
        System.out.println(paramString);
    }
}
