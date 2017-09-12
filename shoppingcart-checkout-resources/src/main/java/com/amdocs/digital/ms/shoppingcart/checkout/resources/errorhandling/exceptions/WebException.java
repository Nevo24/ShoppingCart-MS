package com.amdocs.digital.ms.shoppingcart.checkout.resources.errorhandling.exceptions;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class WebException extends RuntimeException {
	
	private final HttpStatus status;
    private final String code;
	private final String message;
	
    public WebException(final HttpStatus status, final String code, final String message){
		this.status = status;
		this.code = code;
		this.message = message;
	}
	
	public HttpStatus getStatus() {
		return status;
	}
	
    public String getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
