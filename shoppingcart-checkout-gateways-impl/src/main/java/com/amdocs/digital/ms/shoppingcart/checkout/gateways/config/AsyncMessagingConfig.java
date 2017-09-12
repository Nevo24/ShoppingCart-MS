package com.amdocs.digital.ms.shoppingcart.checkout.gateways.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amdocs.digital.ms.shoppingcart.checkout.gateways.services.implementation.ResourceClassByResourceService;
import com.amdocs.digital.ms.shoppingcart.checkout.gateways.services.interfaces.IResourceClassByResourceService;

@Configuration
public class AsyncMessagingConfig {
    @Bean
    public IResourceClassByResourceService resourceClassByResourceService() {
        return new ResourceClassByResourceService();
    }
}
