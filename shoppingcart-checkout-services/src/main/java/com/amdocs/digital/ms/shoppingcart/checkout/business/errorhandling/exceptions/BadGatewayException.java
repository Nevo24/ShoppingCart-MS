package com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;

@SuppressWarnings("serial")
public class BadGatewayException extends AbstractApplicationException {
	
	private final String message;
	
    private final transient Map<String, Object> parameters;
    
    public static final String SERVICE_NAME = "SERVICE_NAME";
    
    public static final String GATEWAY_CODE = "GATEWAY_CODE";
    public static final String GATEWAY_STATUS = "GATEWAY_STATUS";
    public static final String GATEWAY_ERR_MSG = "GATEWAY_ERR_MSG";

    private static final String MSG_ID = "100012012";

	public BadGatewayException(final String serviceName, final String gatewayCode, final String gatewayErrMsg, 
        final HttpStatus gatewayStatus, final Throwable cause, final IMessages messages) {
		super(cause);
		
		this.parameters = initMap(serviceName, gatewayCode, gatewayErrMsg, gatewayStatus);
        this.message    = messages.getMessage(MSG_ID, serviceName, gatewayErrMsg);
	}

    private Map<String, Object> initMap(final String serviceName, final String gatewayCode, final String gatewayErrMsg,
            final HttpStatus gatewayStatus) {
		final Map<String, Object> ret = new HashMap<>();
		ret.put(SERVICE_NAME, serviceName);
		ret.put(GATEWAY_CODE, gatewayCode);
		ret.put(GATEWAY_ERR_MSG, gatewayErrMsg);
        ret.put(GATEWAY_STATUS, gatewayStatus);
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
        String errorCode = (String) parameters.get(GATEWAY_CODE);
        return errorCode != null ? errorCode : MSG_ID;
    }

}
