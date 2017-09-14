package com.amdocs.digital.ms.shoppingcart.checkout.resources.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.*;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.implementation.*;
@Configuration
public class ResourceMappersConfig
{
    @Bean
    public IMapCheckoutDTOToV1Checkout mapCheckoutDTOToV1Checkout()
    {
        return new MapCheckoutDTOToV1Checkout();
    }

    @Bean
    public IMapV1CheckoutToCheckoutDTO mapV1CheckoutToCheckoutDTO()
    {
        return new MapV1CheckoutToCheckoutDTO();
    }

    @Bean
    public IMapV1ShoppingCartRefToShoppingCartRefDTO mapV1ShoppingCartRefToShoppingCartRefDTO()
    {
        return new MapV1ShoppingCartRefToShoppingCartRefDTO();
    }

    @Bean
    public IMapV1ProductOrderRefToProductOrderRefDTO mapV1ProductOrderRefToProductOrderRefDTO()
    {
        return new MapV1ProductOrderRefToProductOrderRefDTO();
    }

    @Bean
    public  IPopulateHrefs populateHrefs()
    {
        return new PopulateHrefs();
    }
}