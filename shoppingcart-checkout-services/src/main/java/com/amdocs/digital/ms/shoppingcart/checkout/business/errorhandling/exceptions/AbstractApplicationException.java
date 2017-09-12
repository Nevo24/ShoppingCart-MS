package com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions;

import java.util.Map;

@SuppressWarnings("serial")
public abstract class AbstractApplicationException extends RuntimeException {
	
    private final Throwable initialCause;
    
    public AbstractApplicationException(final Throwable cause) {
        super(cause);
        this.initialCause = initialCause(cause);
    }
    
    private Throwable initialCause(final Throwable cause) {
        Throwable retCause = null;
        if (cause != null) {
            if (cause instanceof AbstractApplicationException) {
                retCause = ((AbstractApplicationException)cause).getInitialCause();
            }
            if (retCause == null) {
                retCause = cause;
            }
        }
        return retCause;
    }
    
    public Throwable getInitialCause() {
        return initialCause;
    }
    
    @Override
    public abstract String getMessage();
    
    public abstract Map<String,Object> getParameters();
    
    public abstract String getErrorCode();
}
