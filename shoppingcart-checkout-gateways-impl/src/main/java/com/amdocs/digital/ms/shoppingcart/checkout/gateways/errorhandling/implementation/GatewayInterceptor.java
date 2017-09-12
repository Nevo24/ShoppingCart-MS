package com.amdocs.digital.ms.shoppingcart.checkout.gateways.errorhandling.implementation;

import java.util.Map;

import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions.BadGatewayException;
import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;
import com.amdocs.digital.ms.shoppingcart.checkout.gateways.errorhandling.interfaces.IGatewayInterceptor;

public class GatewayInterceptor implements IGatewayInterceptor {

    @Inject
    private IMessages messages;

    @Override
    public void handleGetterErrors(JoinPoint joinPoint, Throwable ex) {
        String serviceName = joinPoint.getSignature().getName().replace("get", "");
        feignExceptionToErrorResponseMapper(ex, serviceName);
        throw new BadGatewayException(serviceName, null, "", null, ex, messages);
    }

    @Override
    public void handleCreateErrors(JoinPoint joinPoint, Throwable ex) {
        String serviceName = joinPoint.getSignature().getName().replace("create", "");
        feignExceptionToErrorResponseMapper(ex, serviceName);
        throw new BadGatewayException(serviceName, null, "", null, ex, messages);

    }

    
    private void feignExceptionToErrorResponseMapper(Throwable ex, String serviceName) {
        //Throwable cause = ex;
        
        //if( ex instanceof HystrixRuntimeException){
        //    cause = ex.getCause();
        //}
        
        //if( cause instanceof ShoppingCartExternalClientException) 
        //{
        //    ShoppingCartExternalClientException feignEx = (ShoppingCartExternalClientException) cause;
        //    Map<String, Object> errorResponseMap = feignEx.getErrorResponseMap();
        //    
        //    throw new BadGatewayException(serviceName, (String)errorResponseMap.get("code"),
        //            (String)errorResponseMap.get("message"), feignEx.getStatus(), cause, messages);
        //} 
    }
}
