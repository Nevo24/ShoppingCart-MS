package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces;


import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.ShoppingCartRef;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IShoppingCartRefDTO;


public interface IMapV1ShoppingCartRefToShoppingCartRefDTO {
    IShoppingCartRefDTO map(ShoppingCartRef shoppingCartDto);
}