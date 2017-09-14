package com.amdocs.digital.ms.shoppingcart.checkout.gateways.config;

import com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.implementation.MapV1ShoppingCartToShoppingCart;
import com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.interfaces.IMapV1ShoppingCartToShoppingCart;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayMappersConfig {
    @Bean
    public IMapV1ShoppingCartToShoppingCart mapV1ShoppingCartToShoppingCart()
    {
        return new MapV1ShoppingCartToShoppingCart();
    }

}
