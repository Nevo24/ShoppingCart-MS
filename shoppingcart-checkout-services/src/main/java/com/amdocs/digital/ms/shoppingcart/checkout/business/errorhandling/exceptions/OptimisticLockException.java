package com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions;

import java.util.HashMap;
import java.util.Map;

import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;

@SuppressWarnings("serial")
public class OptimisticLockException extends AbstractApplicationException {
	
	private final String message;
	
    private final transient Map<String, Object> parameters;
    private static final String MSG_ID = "100012010";
	
    public OptimisticLockException(final String entityKey, final Throwable cause, final IMessages messages) {
		super(cause);
		this.parameters = initMap(entityKey);
		this.message = messages.getMessage(MSG_ID, entityKey);
	}
	
	private Map<String,Object> initMap(String entityKey){
		Map<String,Object> map = new HashMap<>();
		map.put(EntityNotFoundException.ENTITY_KEY, entityKey);
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
