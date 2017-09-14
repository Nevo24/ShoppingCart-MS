package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.implementation;

import javax.inject.Inject;
import javax.inject.Provider;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IMapV1CheckoutToCheckoutDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IMapV1ProductOrderRefToProductOrderRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IMapV1ShoppingCartRefToShoppingCartRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.Checkout;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.msbase.repository.key.services.interfaces.ISetKeyService;
import com.amdocs.msbase.resource.lock.interfaces.IOptimisticLocksFromRequest;

public class MapV1CheckoutToCheckoutDTO implements IMapV1CheckoutToCheckoutDTO
{
    @Inject
    private Provider<ICheckoutDTO> checkoutDTOProvider;
    @Inject
    private IMapV1ShoppingCartRefToShoppingCartRefDTO mapShoppingCartRef;
    @Inject
    IMapV1ProductOrderRefToProductOrderRefDTO mapProductOrderRef;
    @Inject
    ISetKeyService keyService;
    @Override
    public ICheckoutDTO map(Checkout checkout, IOptimisticLocksFromRequest optLocksFromRequest) {
        ICheckoutDTO checkoutDto = null;
        if(checkout != null)
        {
            checkoutDto = checkoutDTOProvider.get();
            // Set Key from Id
            keyService.setKey(checkoutDto, checkout.getId());
            // Get optimistic locking value from request
            optLocksFromRequest.setOptimisticLockingValue(checkoutDto);
            // Map other fields
            checkoutDto.setState(checkout.getState());
            // Map contained classes
            checkoutDto.setShoppingCartRef(mapShoppingCartRef.map(checkout.getShoppingCart()));
            checkoutDto.setProductOrderRef(mapProductOrderRef.map(checkout.getProductOrder()));
        }
        return checkoutDto;
    }
}