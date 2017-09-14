package com.amdocs.digital.ms.shoppingcart.checkout.business.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.implementation.ShoppingCart;
import com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.interfaces.IShoppingCart;
@Configuration
public class BusinessModelsConfig {
    @Bean
    @Scope("prototype")
    public IShoppingCart getShoppingCart() {
        return new ShoppingCart();
    }
}