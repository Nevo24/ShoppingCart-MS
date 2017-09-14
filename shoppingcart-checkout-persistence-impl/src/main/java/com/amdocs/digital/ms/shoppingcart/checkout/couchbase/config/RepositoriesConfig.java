package com.amdocs.digital.ms.shoppingcart.checkout.couchbase.config;

import com.amdocs.digital.ms.shoppingcart.checkout.couchbase.dto.ProductOrderRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.couchbase.dto.ShoppingCartRefDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IProductOrderRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IShoppingCartRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.interfaces.ICheckoutRepository;
import com.amdocs.digital.ms.shoppingcart.checkout.couchbase.CheckoutRepository;
import com.amdocs.digital.ms.shoppingcart.checkout.couchbase.dto.CheckoutDTO;
import com.amdocs.msbase.persistence.couchbase.CouchbaseDTOProvider;
import com.amdocs.msbase.persistence.couchbase.ICouchbaseDTOProvider;

@Configuration
public class RepositoriesConfig
{
    @Bean
    public ICheckoutRepository checkoutRepository()
    {
        return new CheckoutRepository();
    }

    @Bean
    @Scope("prototype")
    public ICheckoutDTO checkoutDTO()
    {
        return new CheckoutDTO();
    }

    @Bean
    @Scope("prototype")
    public IShoppingCartRefDTO shoppingCartRefDTO()
    {
        return new ShoppingCartRefDTO();
    }

    @Bean
    @Scope("prototype")
    public IProductOrderRefDTO productOrderRefDTO()
    {
        return new ProductOrderRefDTO();
    }

    // ------------------------------------------- Couchbase config
    @Bean
    public ICouchbaseDTOProvider provider() throws Exception {
        return new CouchbaseDTOProvider("cbProvider");
    }
}