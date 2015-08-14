package com.github.hippoom.resthelper.sample;


import com.github.hippoom.resthelper.spring.CommandMethodArgumentResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@EnableWebMvc
@Configuration
public class ServletContext extends WebMvcConfigurerAdapter {

    @Autowired
    private BeanFactory beanFactory;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CommandMethodArgumentResolver(beanFactory));
        return;
    }
}