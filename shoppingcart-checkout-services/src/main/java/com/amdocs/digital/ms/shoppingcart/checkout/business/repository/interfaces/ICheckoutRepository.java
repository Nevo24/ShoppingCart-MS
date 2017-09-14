package com.amdocs.digital.ms.shoppingcart.checkout.business.repository.interfaces;

import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.msbase.repository.utils.IdGeneratorHolder;

public interface ICheckoutRepository  extends IdGeneratorHolder
{
    public ICheckoutDTO createCheckout(ICheckoutDTO checkoutDTO);
    public ICheckoutDTO updateCheckout(ICheckoutDTO checkoutDTO);
    public ICheckoutDTO getCheckout(String checkoutId);
}