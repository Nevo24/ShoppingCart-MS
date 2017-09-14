package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.implementation;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IMapCheckoutDTOToV1Checkout;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IProductOrderRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IShoppingCartRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.Checkout;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.ProductOrderRef;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.ShoppingCartRef;
import com.amdocs.msbase.resource.lock.interfaces.IOptimisticLocksForResponse;

public class MapCheckoutDTOToV1Checkout implements IMapCheckoutDTOToV1Checkout {
    @Override
    public Checkout map(ICheckoutDTO checkoutDto, IOptimisticLocksForResponse locksForResponse) {
        Checkout checkout = null;
        if(checkoutDto != null)
        {
            checkout = new Checkout();
            // Set Id from Key
            checkout.setId(checkoutDto.getKey().toString());
            if ( locksForResponse != null) {
                // Set optimistic lock value for response
                locksForResponse.add( checkoutDto);
            }
            // Map other fields
            checkout.setState(checkoutDto.getState());
            // Map contained classes
            checkout.setShoppingCart(mapShoppingCartRef(checkoutDto.getShoppingCartRef()));
            checkout.setProductOrder(mapProductOrderRef(checkoutDto.getProductOrderRef()));
        }
        return checkout;
    }
    private ShoppingCartRef mapShoppingCartRef(IShoppingCartRefDTO shoppingCartDto)
    {
        ShoppingCartRef shoppingCartRef = null;
        if(shoppingCartDto != null)
        {
            shoppingCartRef = new ShoppingCartRef();
            shoppingCartRef.setId(shoppingCartDto.getId());
        }
        return shoppingCartRef;
    }
    private ProductOrderRef mapProductOrderRef(IProductOrderRefDTO productOrderDto)
    {
        ProductOrderRef productOrderRef = null;
        if(productOrderDto != null)
        {
            productOrderRef = new ProductOrderRef();
            productOrderRef.setId(productOrderDto.getId());
        }
        return productOrderRef;
    }
}