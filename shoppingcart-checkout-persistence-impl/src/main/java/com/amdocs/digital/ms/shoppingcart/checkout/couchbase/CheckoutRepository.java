package com.amdocs.digital.ms.shoppingcart.checkout.couchbase;

import javax.inject.Inject;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.interfaces.ICheckoutRepository;
import com.amdocs.digital.ms.shoppingcart.checkout.couchbase.dto.CheckoutDTO;
import com.amdocs.msbase.persistence.couchbase.ICouchbaseDTOProvider;
import com.amdocs.msbase.persistence.couchbase.repository.dto.IBaseCouchbaseDTO;
import com.amdocs.msbase.repository.utils.IObjectIDGenerator;

public class CheckoutRepository implements ICheckoutRepository
{
    @Inject
    ICouchbaseDTOProvider provider;


    @Override
    public ICheckoutDTO createCheckout( ICheckoutDTO newCheckout )
    {
        return (CheckoutDTO) provider.insert( (CheckoutDTO)newCheckout);
    }

    @Override
    public ICheckoutDTO updateCheckout( ICheckoutDTO checkout)
    {
        provider.update((CheckoutDTO) checkout);
        return (CheckoutDTO) provider.update((CheckoutDTO) checkout);
    }
    @Override
    public ICheckoutDTO getCheckout( String checkoutId)
    {
        IBaseCouchbaseDTO checkout = provider.get(checkoutId, CheckoutDTO.class);
        return (ICheckoutDTO) checkout;
    }
    @Override
    public IObjectIDGenerator getIdGenerator()
    {
        return provider.getIdGenerator();
    }
}