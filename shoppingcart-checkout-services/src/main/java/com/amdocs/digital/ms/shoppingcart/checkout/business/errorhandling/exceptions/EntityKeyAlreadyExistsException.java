package com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions;

import java.util.HashMap;
import java.util.Map;

import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;

@SuppressWarnings("serial")
public class EntityKeyAlreadyExistsException extends AbstractApplicationException {
	
	private final String message;
	
    private final transient Map<String, Object> parameters;
    
    private static final String MSG_ID = "100012009";
	
	private static final String USER_MSG = "USER_MSG";
	
	public EntityKeyAlreadyExistsException(final String entityKey, final String userMsg, final Throwable cause, 
																					     final IMessages messages) {
		super(cause);
		this.parameters = initMap(entityKey, userMsg);
		this.message = messages.getMessage(MSG_ID, entityKey, userMsg);
	}
	
	public EntityKeyAlreadyExistsException(final String entityKey, final String userMsg, final IMessages messages) {
		this(entityKey, userMsg, null, messages);
	}
	
	private Map<String,Object> initMap(String entityKey, String userMsg){
		Map<String,Object> map = new HashMap<>();
		map.put(EntityNotFoundException.ENTITY_KEY, entityKey);
		map.put(USER_MSG, userMsg);
		return map;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}

    @Override
    public String getErrorCode()
    {
        return MSG_ID;
    }
    
}
