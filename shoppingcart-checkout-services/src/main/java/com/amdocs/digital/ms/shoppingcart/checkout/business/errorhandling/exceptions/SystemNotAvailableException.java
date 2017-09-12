package com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions;

import java.util.HashMap;
import java.util.Map;

import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;

@SuppressWarnings("serial")
public class SystemNotAvailableException extends AbstractApplicationException {
	
	private final String message;
	
    private final transient Map<String, Object> parameters;
    
    private static final String MSG_ID = "100012008";
	
	public static final String SYSTEM_NAME = "SYSTEM_NAME";
	
	public SystemNotAvailableException(final String systemName, final Throwable cause, final IMessages messages) {
		super(cause);
		this.parameters = initMap(systemName);
		this.message = messages.getMessage(MSG_ID, systemName);
	}

	private Map<String,Object> initMap(String systemName){
		Map<String,Object> map = new HashMap<>();
		map.put(SYSTEM_NAME, systemName);
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
