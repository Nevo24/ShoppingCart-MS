package com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;


@SuppressWarnings("serial")
public class EntityNotFoundException extends AbstractApplicationException {
	
	private final String message;
	
    private final transient Map<String, Object> parameters;
    
    private static final String MSG_ID = "100012006";
	
	public static final String ENTITY_NAME = "ENTITY_NAME";
	
    public static final String ENTITY_KEY = "ENTITY_KEY";
     
    public EntityNotFoundException(final String entityName, final String entityKey, final Throwable cause, 
    																				final IMessages messages) {
        super(cause);
        this.parameters = initMap(entityName, entityKey);
        this.message = messages.getMessage(MSG_ID, entityName, entityKey);
    }
     
    public EntityNotFoundException(final String entityName, final String entityKey, final IMessages messages) {
        this(entityName, entityKey, null, messages);
    }
     
    private Map<String, Object> initMap(final String entityName, final String entityKey) {
        final Map<String, Object> ret = new HashMap<>();
        ret.put(EntityNotFoundException.ENTITY_KEY, entityKey);
        ret.put(EntityNotFoundException.ENTITY_NAME, entityName);
        return ret;
    }

	public String getMessage() {
		return message;
	}
    
	public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    @Override
    public String getErrorCode()
    {
        return MSG_ID;
    }
}
