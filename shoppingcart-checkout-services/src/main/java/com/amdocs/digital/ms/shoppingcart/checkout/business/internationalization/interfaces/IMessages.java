package com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces;

public interface IMessages {
	
	/**
	 * Returns the localized message for the given key.
	 * 
	 * @param key
	 * @param params
	 * @return
	 */
	public String getMessage(String key, String... params);

}
