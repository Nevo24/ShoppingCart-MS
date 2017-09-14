package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.ProductOrderRef;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IProductOrderRefDTO;


public interface IMapV1ProductOrderRefToProductOrderRefDTO {

    IProductOrderRefDTO map(ProductOrderRef shoppingCartDto);

}
