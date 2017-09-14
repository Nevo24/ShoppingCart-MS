package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.implementation;

import javax.inject.Inject;
import javax.inject.Provider;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IMapV1ProductOrderRefToProductOrderRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.ProductOrderRef;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IProductOrderRefDTO;

public class MapV1ProductOrderRefToProductOrderRefDTO implements IMapV1ProductOrderRefToProductOrderRefDTO {
    @Inject
    private Provider<IProductOrderRefDTO> productOrderRefDTOProvider;
    @Override
    public IProductOrderRefDTO map(ProductOrderRef productOrderDto)
    {
        IProductOrderRefDTO productOrderRef = null;
        if(productOrderDto != null)
        {
            productOrderRef = productOrderRefDTOProvider.get();
            productOrderRef.setId(productOrderDto.getId());
        }
        return productOrderRef;
    }
}