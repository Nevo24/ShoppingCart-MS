package com.amdocs.digital.ms.shoppingcart.checkout.resources.errorhandling.implementation;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.amdocs.digital.ms.shoppingcart.checkout.resources.errorhandling.exceptions.WebException;
import com.amdocs.digital.ms.shoppingcart.checkout.resources.models.ErrorResponse;

/**
 * 
 * Maps an {@link AbstractApplicationException} to a {@link ErrorResponse}
 *
 */
@ControllerAdvice
public class ExceptionToErrorResponseMapper extends ResponseEntityExceptionHandler {

	/**
	 * Maps an exception to a {@link ErrorResponse}. If the exception is not
	 * of type {@link AbstractApplicationException} then the error response will
	 * default to Internal Server Error
	 * 
	 * @param req The Request 
	 * @param exception The exception that is being mapped to an ErrorResponse
	 * @return an ErrorResponse object
	 */
	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> handleException(HttpServletRequest req, Exception exception) {
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = "Internal Server Error";
        String code = "";
		
		WebException webEx = null;
		if( exception instanceof WebException){
			webEx = (WebException) exception;
			status = webEx.getStatus();
			message = webEx.getMessage();
            code = webEx.getCode();
		}
		
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setLink(req.getPathInfo());
        errorResponse.setCode(code);
		errorResponse.setMessage(message);
		 
		return new ResponseEntity<ErrorResponse>(errorResponse, status);
	}

}
