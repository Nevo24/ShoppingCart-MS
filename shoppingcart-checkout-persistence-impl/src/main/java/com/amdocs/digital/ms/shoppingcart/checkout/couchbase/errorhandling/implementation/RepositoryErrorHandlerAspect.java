package com.amdocs.digital.ms.shoppingcart.checkout.couchbase.errorhandling.implementation;

import javax.inject.Inject;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import com.amdocs.digital.ms.shoppingcart.checkout.couchbase.errorhandling.interfaces.IRepositoryInterceptor;

@Aspect
public class RepositoryErrorHandlerAspect{

    @Inject
    IRepositoryInterceptor persistenceInterceptor;

    @AfterThrowing(pointcut="execution(* com.amdocs.digital.ms.shoppingcart.checkout.couchbase.CheckoutRepository.*(..))", throwing="ex")
    public void handleAllErrors(Exception ex) {
        persistenceInterceptor.handleAllErrors(ex);
    }

}