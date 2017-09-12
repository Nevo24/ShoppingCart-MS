package com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.interfaces;

import com.amdocs.msb.asyncmessaging.message.resource.intf.IResourceBaseMessage;
import com.amdocs.msbase.repository.dto.IBaseEntity;

@SuppressWarnings("squid:S2326") // Implementing classes should declare a param type.  This prods them to do so.
public interface IMapResource<T> {
    public IBaseEntity mapResource(IResourceBaseMessage msg);
}
