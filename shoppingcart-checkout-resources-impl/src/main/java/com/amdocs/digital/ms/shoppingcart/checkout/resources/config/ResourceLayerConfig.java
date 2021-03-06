//This code was generated by MS-Builder
package com.amdocs.digital.ms.shoppingcart.checkout.resources.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.delegates.CreateCheckoutDelegate;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.implementation.CheckoutsApiImpl.ICreateCheckoutDelegate;

/**
 * Delegates configuration file.
 */
@Configuration
public class ResourceLayerConfig
{
    @Bean
    public ICreateCheckoutDelegate createCheckoutDelegate()
    {
        return new CreateCheckoutDelegate();
    }
} 