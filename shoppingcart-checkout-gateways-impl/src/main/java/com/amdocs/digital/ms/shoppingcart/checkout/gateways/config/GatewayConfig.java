package com.amdocs.digital.ms.shoppingcart.checkout.gateways.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amdocs.digital.ms.shoppingcart.checkout.gateways.implementation.GetShoppingCartService;
import com.amdocs.digital.ms.shoppingcart.checkout.business.gateways.interfaces.IGetShoppingCartService;

@Configuration
public class GatewayConfig
{
    @Bean
    public IGetShoppingCartService getShoppingCartService()
    {
        return new GetShoppingCartService();
    }
}