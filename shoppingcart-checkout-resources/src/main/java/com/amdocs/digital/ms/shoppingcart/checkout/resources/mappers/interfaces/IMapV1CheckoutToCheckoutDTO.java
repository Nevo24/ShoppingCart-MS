package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.Checkout;
import com.amdocs.msbase.resource.lock.interfaces.IOptimisticLocksFromRequest;
public interface IMapV1CheckoutToCheckoutDTO {
    public ICheckoutDTO map(Checkout v1checkout, IOptimisticLocksFromRequest optLocksFromRequest);
}