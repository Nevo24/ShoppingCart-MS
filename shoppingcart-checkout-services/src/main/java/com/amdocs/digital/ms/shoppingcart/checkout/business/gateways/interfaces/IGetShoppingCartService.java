package com.amdocs.digital.ms.shoppingcart.checkout.business.gateways.interfaces;

import com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.interfaces.IShoppingCart;

public interface IGetShoppingCartService
{
    IShoppingCart getShoppingCart( String shoppingCartId );
}