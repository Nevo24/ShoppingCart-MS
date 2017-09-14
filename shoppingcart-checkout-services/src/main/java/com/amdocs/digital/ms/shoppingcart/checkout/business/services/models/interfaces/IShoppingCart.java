package com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.interfaces;

import com.amdocs.msbase.repository.dto.IBaseEntity;
public interface IShoppingCart extends IBaseEntity
{
    public String getId();
    public void setId(String id);
    // TODO Add other attributes needed for Product Order
}