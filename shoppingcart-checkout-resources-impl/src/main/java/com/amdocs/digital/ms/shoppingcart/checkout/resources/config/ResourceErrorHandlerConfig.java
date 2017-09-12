package com.amdocs.digital.ms.shoppingcart.checkout.resources.config;

import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.errorhandling.implementation.ResourceErrorHandlerAspect;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.errorhandling.implementation.ResourceInterceptor;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.errorhandling.interfaces.IResourceInterceptor;

@Configuration
@EnableAspectJAutoProxy
public class ResourceErrorHandlerConfig {

    @Bean
    public IResourceInterceptor getResourceInterceptor() {
        return new ResourceInterceptor();
    }

    @Bean
    public ResourceErrorHandlerAspect getResourceErrorHandlerAspect() {
        return Aspects.aspectOf(ResourceErrorHandlerAspect.class);
    }

}
