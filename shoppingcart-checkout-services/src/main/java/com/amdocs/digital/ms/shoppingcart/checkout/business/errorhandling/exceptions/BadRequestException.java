package com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;


public class BadRequestException extends AbstractApplicationException {
    private static final String HEADER_FIELD_NAME = "HEADER_FIELD_NAME";
    private static final long serialVersionUID = 1L;
    
    private static final String BAD_REQUEST_MSG = "100012003";
    
    private static final String BAD_REQUEST_FIELD_MSG = "100012004";

    private final String message;
    
    private final transient Map<String, Object> parameters;

    public BadRequestException(final String headerFieldName, final Throwable cause, final IMessages messages) {
        super(cause);
        
        this.parameters = initMap( headerFieldName);
        this.message    = messages.getMessage(headerFieldName == null ? BAD_REQUEST_MSG : BAD_REQUEST_FIELD_MSG, headerFieldName);
    }

    public BadRequestException(final String headerFieldName, final  IMessages messages) {
        this(headerFieldName, null, messages);
    }

    private Map<String, Object> initMap(final String headerFieldName) {
        final Map<String, Object> ret = new HashMap<>(1);
        ret.put(HEADER_FIELD_NAME, headerFieldName);
        return ret;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    @Override
    public String getErrorCode()
    {
        return parameters.get(HEADER_FIELD_NAME) == null ? BAD_REQUEST_MSG : BAD_REQUEST_FIELD_MSG;
    }
}
