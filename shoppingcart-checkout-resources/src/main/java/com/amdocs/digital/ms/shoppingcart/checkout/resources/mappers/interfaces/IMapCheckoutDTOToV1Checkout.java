package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.Checkout;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.msbase.resource.lock.interfaces.IOptimisticLocksForResponse;

public interface IMapCheckoutDTOToV1Checkout {
    public Checkout map(ICheckoutDTO checkoutDto, IOptimisticLocksForResponse outOptLocks);
}