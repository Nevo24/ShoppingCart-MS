package com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.implementation;

import java.util.Map;

import javax.inject.Inject;

import com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.interfaces.IMapResource;
import com.amdocs.msbase.injection.generics.utils.implementation.BaseGenericBeansFactory;

public class ResourceMappersGenericFactory extends BaseGenericBeansFactory<IMapResource<?>> {

    @Inject
    Map<String, IMapResource<?>> mappers;


    @Override
    protected Map<String, IMapResource<?>> getBeans() {         
        return mappers;
    }
}
