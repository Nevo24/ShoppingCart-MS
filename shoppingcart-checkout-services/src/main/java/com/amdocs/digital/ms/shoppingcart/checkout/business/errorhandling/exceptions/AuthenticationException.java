package com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions;

import java.util.Map;

import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;

@SuppressWarnings("serial")
public class AuthenticationException extends AbstractApplicationException{

    private final String message;
    private final transient Map<String, Object> parameters;

    private static final String MSG_ID = "100012006";

	public AuthenticationException(final Throwable cause, final IMessages messages) {
		super(cause);
		this.message = messages.getMessage(MSG_ID);
		this.parameters = null;
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
