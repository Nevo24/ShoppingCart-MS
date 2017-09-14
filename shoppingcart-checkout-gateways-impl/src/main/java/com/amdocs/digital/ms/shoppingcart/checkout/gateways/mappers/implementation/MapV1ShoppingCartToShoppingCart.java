package com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.implementation;

import javax.inject.Inject;
import javax.inject.Provider;

import com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.interfaces.IShoppingCart;
import com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.interfaces.IMapV1ShoppingCartToShoppingCart;
import com.amdocs.digital.ms.shoppingcart.shoppingcart.ck.resources.models.V1ShoppingCart;

public class MapV1ShoppingCartToShoppingCart implements IMapV1ShoppingCartToShoppingCart
{
    @Inject
    private Provider<IShoppingCart> shoppingCartProvider;

    @Override
    public IShoppingCart map(V1ShoppingCart shoppingCart)
    {
        if( shoppingCart == null)
        {
            return null;
        }
        IShoppingCart cartDto = shoppingCartProvider.get();
        cartDto.setId(shoppingCart.getId());
        // TODO: add other attributes needed for Product Order
        return cartDto;
    }
}