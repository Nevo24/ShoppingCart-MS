package com.amdocs.digital.ms.shoppingcart.checkout.autolog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotate getter methods, or method parameters with this to indicate that the
 * values contain personally identifiable information that should be masked.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface PII {
}