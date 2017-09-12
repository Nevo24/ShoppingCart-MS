package com.amdocs.digital.ms.shoppingcart.checkout.gateways.services.interfaces;

public interface IResourceClassByResourceService {
    
    String getResourceTypeFullyQualifiedName (String resourceName);

    String getMappedResourceTypeFullyQualifiedName (String resourceName);

}
