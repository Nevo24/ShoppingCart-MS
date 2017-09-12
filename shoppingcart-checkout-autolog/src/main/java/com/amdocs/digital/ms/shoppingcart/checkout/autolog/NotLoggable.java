package com.amdocs.digital.ms.shoppingcart.checkout.autolog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotate method to indicate method should not be logged by automatic logging. 
 * Annotate type to indicate no method in this type should not be logged by automatic logging.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface NotLoggable {
}