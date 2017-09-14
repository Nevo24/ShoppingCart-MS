package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.Checkout;

import javax.servlet.http.HttpServletRequest;

public interface IPopulateHrefs {

    /**
     * Set all the href references in the checkout object
     */
    Checkout populateHrefs(Checkout checkout, HttpServletRequest httpRequest);

}