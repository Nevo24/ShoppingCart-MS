package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.implementation;

import javax.inject.Inject;
import javax.inject.Provider;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IMapV1ShoppingCartRefToShoppingCartRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IShoppingCartRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.ShoppingCartRef;

public class MapV1ShoppingCartRefToShoppingCartRefDTO implements IMapV1ShoppingCartRefToShoppingCartRefDTO {
    @Inject
    private Provider<IShoppingCartRefDTO> shoppingCartRefDTOProvider;
    @Override
    public IShoppingCartRefDTO map(ShoppingCartRef shoppingCartDto)
    {
        IShoppingCartRefDTO shoppingCartRef = null;
        if(shoppingCartDto != null)
        {
            shoppingCartRef = shoppingCartRefDTOProvider.get();
            shoppingCartRef.setId(shoppingCartDto.getId());
        }
        return shoppingCartRef;
    }
}