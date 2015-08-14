package com.github.hippoom.resthelper.spring;

import com.github.hippoom.resthelper.annotation.Command;
import com.github.hippoom.resthelper.annotation.PathVar;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class CommandMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final BeanFactory beanFactory;
    private final PathVariableMethodArgumentResolverWrapper pathVariableMethodArgumentResolver;
    private HandlerMethodArgumentResolver target;
    private List<HttpMessageConverter<?>> messageConverters;


    public CommandMethodArgumentResolver(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.pathVariableMethodArgumentResolver = new PathVariableMethodArgumentResolverWrapper();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Command.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return getTarget().resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }


    private Object resolvePathVariable(String name, MethodParameter parameter, NativeWebRequest request) {
        return pathVariableMethodArgumentResolver.resolvePathVariable(name, parameter, request);
    }

    private HandlerMethodArgumentResolver getTarget() {
        if (target == null) {
            target = new PathVarInjectableRequestBodyResolver(getMessageConverters());
        }
        return target;
    }

    /**
     * Cannot autowire {@link RequestMappingHandlerAdapter}.
     * Found workaround at
     * http://forum.spring.io/forum/spring-projects/web/121393-custom-handlermethodargumentresolver-that-uses-messageconverters
     */
    private List<HttpMessageConverter<?>> getMessageConverters() {
        if (messageConverters == null) {
            messageConverters = beanFactory.getBean(RequestMappingHandlerAdapter.class).getMessageConverters();
        }
        return messageConverters;
    }

    class PathVariableMethodArgumentResolverWrapper extends PathVariableMethodArgumentResolver {
        protected Object resolvePathVariable(String name, MethodParameter parameter, NativeWebRequest request) {
            try {
                return resolveName(name, parameter, request);
            } catch (Exception e) {
                e.printStackTrace();
                return null;//TODO throw exception indicating bad request
            }
        }
    }

    class PathVarInjectableRequestBodyResolver extends RequestResponseBodyMethodProcessor {

        public PathVarInjectableRequestBodyResolver(List<HttpMessageConverter<?>> messageConverters) {
            super(messageConverters);
        }

        @Override
        protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter methodParam,
                                                       Type paramType) throws IOException, HttpMediaTypeNotSupportedException {
            final Object arg = super.readWithMessageConverters(webRequest, methodParam, paramType);
            enrichWithPathVarialbes(webRequest, methodParam, arg);
            return arg;
        }

        private void enrichWithPathVarialbes(final NativeWebRequest webRequest, final MethodParameter methodParam, final Object arg) {
            ReflectionUtils.doWithFields(arg.getClass(), new ReflectionUtils.FieldCallback() {
                        @Override
                        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                            final PathVar pathVar = field.getDeclaredAnnotation(PathVar.class);
                            ReflectionUtils.makeAccessible(field);
                            ReflectionUtils.setField(field, arg, resolvePathVariable(pathVar.value(), methodParam, webRequest));
                        }
                    },
                    new ReflectionUtils.FieldFilter() {
                        @Override
                        public boolean matches(Field field) {
                            return field.isAnnotationPresent(PathVar.class);
                        }
                    }
            );
        }

    }

}
