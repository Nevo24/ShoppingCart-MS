package com.amdocs.digital.ms.shoppingcart.checkout.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amdocs.digital.ms.shoppingcart.checkout.business.services.implementation.*;
import com.amdocs.digital.ms.shoppingcart.checkout.business.services.interfaces.*;

@Configuration
public class ApplicationServicesConfig
{
    @Bean
    public ICreateCheckoutApplicationService createCheckoutApplicationService()
    {
        return new CreateCheckoutApplicationService();
    }
}