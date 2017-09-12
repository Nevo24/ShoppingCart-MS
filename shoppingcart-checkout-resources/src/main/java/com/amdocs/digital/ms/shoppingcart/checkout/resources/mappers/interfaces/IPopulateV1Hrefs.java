package com.amdocs.digital.ms.shoppingcart.checkout.resources;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.Checkout;

import javax.servlet.http.HttpServletRequest;
public interface IPopulateV1Hrefs {
    /**
     * Set all the href references in the checkout object
     */
    Checkout populateHrefs(Checkout checkout, HttpServletRequest httpRequest);
}