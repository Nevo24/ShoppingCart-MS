package com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.implementation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IPopulateHrefs;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.*;

public class PopulateHrefs implements IPopulateHrefs {

//    @Value("${services.checkout.url}")
//    private String checkoutURL;
//    private static final String GET_CHECKOUT_RESOURCE = "//checkouts/{checkoutId}";
//
//    @Value("${services.shoppingCart.url}")
//    private String shoppingCartURL;
//    private static final String GET_SHOPPINGCART_RESOURCE = "//shopping-carts/{shoppingCartId}?salesChannel={salesChannel}";
//
//    @Value("${services.productOrder.url}")
//    private String productOrderURL;
//    private static final String GET_PRODUCTORDER_RESOURCE = "//product-orders/{productOrderId}?salesChannel={salesChannel}";
//
//    /**
//     * Set all the href references in the checkout object
//     */
    @Override
    public Checkout populateHrefs(Checkout checkout, HttpServletRequest httpRequest) {
//
//        // Set Checkout href
//        Map<String, Object> hrefUriValues = new HashMap<>();
//        hrefUriValues.put("checkoutId", checkout.getId());
//        String checkoutHref = buildHref(checkoutURL, GET_CHECKOUT_RESOURCE, hrefUriValues, httpRequest);
//        checkout.setHref(checkoutHref);
//
//        // Set ShoppingCart href
//        hrefUriValues.clear();
//        hrefUriValues.put("shoppingCartId", checkout.getShoppingCart().getId());
//        hrefUriValues.put("salesChannel", "channel");
//        String shoppingCartHref = buildHref(shoppingCartURL, GET_SHOPPINGCART_RESOURCE, hrefUriValues, httpRequest);
//        checkout.getShoppingCart().setHref(shoppingCartHref);
//
//        // Set ProductOrder href
//        hrefUriValues.clear();
//        hrefUriValues.put("productOrderId", checkout.getProductOrder().getId());
//        hrefUriValues.put("salesChannel", "channel");
//        String productOrderHref = buildHref(productOrderURL, GET_PRODUCTORDER_RESOURCE, hrefUriValues, httpRequest);
//        checkout.getProductOrder().setHref(productOrderHref);
//
        return checkout;
    }
//
//    private String buildHref(String url, String resource, Map<String, ?> hrefUriValues, HttpServletRequest httpRequest) {
//        String httpUrl = url + resource;
//        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromHttpUrl(httpUrl);
//        return uriComponents.buildAndExpand(hrefUriValues).toUriString();
//    }
}
