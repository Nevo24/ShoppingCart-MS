package com.amdocs.digital.ms.shoppingcart.checkout.resources.errorhandling.implementation;

import javax.inject.Inject;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.errorhandling.interfaces.IResourceInterceptor;

@Aspect
public class ResourceErrorHandlerAspect {
	
	@Inject
	IResourceInterceptor resourceInterceptor;
	
	@AfterThrowing(pointcut="execution(* com.amdocs.digital.ms.shoppingcart.checkout.resources..execute(..))", throwing="ex")
	public void handleAllErrors(Throwable ex) {
		resourceInterceptor.handleAllErrors(ex);
	}

}
