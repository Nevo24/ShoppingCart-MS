package com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.implementation;

import com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.interfaces.IShoppingCart;

public class ShoppingCart implements IShoppingCart {
    private String id = null;
    // TODO: add other attributes needed for Product Order

    @Override
    public String getId() {
        return this.id;
    }
    @Override
    public void setId(String id) {
        this.id = id;
    }
}