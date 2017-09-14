package com.amdocs.digital.ms.shoppingcart.checkout.resources.delegates;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.services.interfaces.ICreateCheckoutApplicationService;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.implementation.CheckoutsApiImpl.CreateCheckoutParameters;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.implementation.CheckoutsApiImpl.ICreateCheckoutDelegate;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IMapCheckoutDTOToV1Checkout;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.mappers.interfaces.IPopulateHrefs;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.Checkout;
import com.amdocs.msbase.resource.lock.interfaces.IOptimisticLocksForResponse;

public class CreateCheckoutDelegate implements ICreateCheckoutDelegate {

    @Inject
    private IMapCheckoutDTOToV1Checkout responseMapper;
    @Inject
    private ICreateCheckoutApplicationService applicationService;
    @Inject
    private IPopulateHrefs populateHrefs;
    @Inject
    private Provider<IOptimisticLocksForResponse> optLocksForResponseProvider;


    private static final Logger logger = LoggerFactory.getLogger(CreateCheckoutDelegate.class);
    @Override
    public ResponseEntity<Checkout> execute(CreateCheckoutParameters parameters) {

        MDC.put("focusId", "shoppingCartId=" + parameters.getShoppingCartID().getId());
        logger.debug("CreateCheckoutDelegate entry" );
        // Create Checkout for Shopping Cart Id
        ICheckoutDTO checkoutDTO  = applicationService.create(parameters.getShoppingCartID().getId());
        // Map response
        IOptimisticLocksForResponse optLocksForResponse = optLocksForResponseProvider.get();
        Checkout response = responseMapper.map(checkoutDTO,optLocksForResponse );

        MDC.put("focusId", "checkoutId=" + response.getId());

        // Update response header and body
        HttpHeaders headers = optLocksForResponse.addETagToHeaders(null);
        populateHrefs.populateHrefs(response, parameters.getHttpServletRequest());

        logger.debug("CreateCheckoutDelegate exit, Checkout={}", response );
        // TODO it was : logger.debug("CreateCheckoutDelegate exit, V1Checkout={}", response );

        return new ResponseEntity<Checkout>(response, headers, HttpStatus.OK);
    }
}
