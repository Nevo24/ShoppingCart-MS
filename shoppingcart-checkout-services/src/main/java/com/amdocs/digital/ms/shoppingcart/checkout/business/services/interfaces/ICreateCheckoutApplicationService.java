package com.amdocs.digital.ms.shoppingcart.checkout.business.services.interfaces;

import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;

public interface ICreateCheckoutApplicationService {
    public ICheckoutDTO create(String shoppingCartId);
}