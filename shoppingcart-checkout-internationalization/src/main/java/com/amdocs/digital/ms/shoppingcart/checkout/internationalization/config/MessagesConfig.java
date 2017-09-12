package com.amdocs.digital.ms.shoppingcart.checkout.internationalization.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;
import com.amdocs.digital.ms.shoppingcart.checkout.internationalization.Messages;

/**
 * Configuration required for Internationalization support.
 */
@Configuration
public class MessagesConfig {
	
	@Value("${com.amdocs.digital.ms.default-locale}")
	private String defaultLocaleString;
	
	/*
	 * Expecting a value with comma separated list of supported locale string in IETF BCP 47.
	 * Note that the IETF BCP 47 format uses "-" instead of "_". Example: en-US, ja-JP-u-ca-japanese
	 * Invalid locale string would be ignored.
	 */
	@Value("${com.amdocs.digital.ms.supported-locales}")
	private String supportedLocalsString;
	 
		
	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
		
		if (defaultLocaleString != null) {
			Locale defaultLocale = Locale.forLanguageTag(defaultLocaleString);
			resolver.setDefaultLocale(defaultLocale);
		}
		else {
			resolver.setDefaultLocale(Locale.ENGLISH);
		}
	 
	    resolver.setSupportedLocales(getSupportedLocales());
	    
	    return resolver;
	}
	
	/**
	 * Parses the supported locale string and creates a list of <code>java.util.Locale</code> objects.  
	 * @return
	 */
	private List<Locale> getSupportedLocales() {
		List<Locale> supportedLocals = new ArrayList<>();
		
		if (supportedLocalsString != null) {
			StringTokenizer tokenizer = new StringTokenizer(supportedLocalsString, ",");
			
			while(tokenizer.hasMoreTokens()) {
				String localString = tokenizer.nextToken();
				Locale locale = Locale.forLanguageTag(localString.trim());
				
				//Ignores empty locale object which gets generated for invalid (as per IETF BCP 47) locale token. 
				if (locale.getLanguage().length() != 0) {
					supportedLocals.add(locale);
				}
			}
		}
		
		return supportedLocals;
	}
	
	@Bean
	public IMessages getMessages() {
		return new Messages();
	}
}
