package com.amdocs.digital.ms.shoppingcart.checkout.gateways.config;

import com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.implementation.MapResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.implementation.ResourceMappersGenericFactory;
import com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.interfaces.IMapResource;
import com.amdocs.msbase.injection.generics.utils.interfaces.IBaseGenericBeansFactory;

@Configuration
public class GatewayMappersConfig {
    @Bean
    public IBaseGenericBeansFactory<IMapResource<? extends Object>> resourcesMappersGenericFactory() {
        return new ResourceMappersGenericFactory();
    }

    @Bean
    public <T> IMapResource<T> IMapResource() {
        return new MapResource<T>();
    }

}
