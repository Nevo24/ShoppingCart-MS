package com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.interfaces;

import com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.interfaces.IShoppingCart;
import com.amdocs.digital.ms.shoppingcart.shoppingcart.ck.resources.models.V1ShoppingCart;

public interface IMapV1ShoppingCartToShoppingCart
{
    IShoppingCart map(V1ShoppingCart shoppingCart);
}