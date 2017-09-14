package com.amdocs.digital.ms.shoppingcart.checkout.business.services.implementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.services.interfaces.ICreateCheckoutApplicationService;

public class CreateCheckoutApplicationService implements ICreateCheckoutApplicationService
{


    private static final Logger logger = LoggerFactory.getLogger(CreateCheckoutApplicationService.class);
    public ICheckoutDTO create(String shoppingCartId) {
        logger.debug("CreateCheckoutApplicationService entry, shoppingCartId={}", shoppingCartId );

        // TODO Implement stub service
        ICheckoutDTO checkout = null;

        // Save Checkout
        logger.debug("CreateCheckoutApplicationService exit, checkout={}", checkout );
        return checkout;
    }
}