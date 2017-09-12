package com.amdocs.digital.ms.shoppingcart.checkout.couchbase.errorhandling.interfaces;

public interface IRepositoryInterceptor {

    public void handleAllErrors(Throwable ex);

}
