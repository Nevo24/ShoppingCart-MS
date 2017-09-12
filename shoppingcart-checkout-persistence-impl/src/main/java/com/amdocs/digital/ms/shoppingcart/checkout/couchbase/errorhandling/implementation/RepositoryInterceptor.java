package com.amdocs.digital.ms.shoppingcart.checkout.couchbase.errorhandling.implementation;

import javax.inject.Inject;

import com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions.AuthenticationException;
import com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions.EntityKeyAlreadyExistsException;
import com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions.EntityNotFoundException;
import com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions.OptimisticLockException;
import com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions.SystemNotAvailableException;
import com.amdocs.digital.ms.shoppingcart.checkout.business.errorhandling.exceptions.UnknownException;
import com.amdocs.digital.ms.shoppingcart.checkout.business.internationalization.interfaces.IMessages;
import com.amdocs.digital.ms.shoppingcart.checkout.couchbase.errorhandling.interfaces.IRepositoryInterceptor;
import com.amdocs.msbase.persistence.couchbase.CouchbaseAggregateException;
import com.amdocs.msbase.persistence.couchbase.CouchbaseAggregateException.Cause;
import com.couchbase.client.core.ServiceNotAvailableException;
import com.couchbase.client.java.error.BucketDoesNotExistException;
import com.couchbase.client.java.error.CASMismatchException;
import com.couchbase.client.java.error.CannotRetryException;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.couchbase.client.java.error.InvalidPasswordException;
import com.couchbase.client.java.error.TemporaryLockFailureException;

public class RepositoryInterceptor implements IRepositoryInterceptor {

    private static final Integer PRIMARY_CAUSE_INDEX = 0;
    private static final String COUCHBASE = "Couchbase";
    private static final String CHECKOUT = "Checkout";

    @Inject
    private IMessages messages;

    /**
     * Maps {@link CouchbaseAggregateException} to an appropriate 
     * {@link AbstractApplicationException} based on the cause.
     */
    public void handleAllErrors(Throwable ex){

        String key = null;

        if(ex instanceof CouchbaseAggregateException){

            CouchbaseAggregateException aggEx = (CouchbaseAggregateException) ex;
            Cause primaryCause = aggEx.getCauses().get(PRIMARY_CAUSE_INDEX);

            key = primaryCause.getKey();

            Throwable initialError = primaryCause.getError();
            if( initialError instanceof CannotRetryException){
                initialError = initialError.getCause();
            }

            ex = initialError;
        }

        if(ex instanceof BucketDoesNotExistException || ex instanceof ServiceNotAvailableException) {
            throw new SystemNotAvailableException(COUCHBASE, ex, messages);
        }
        else if(ex instanceof InvalidPasswordException) {
            throw new AuthenticationException(ex, messages);
        }
        else if(ex instanceof DocumentAlreadyExistsException) {
            throw new EntityKeyAlreadyExistsException(key, "", ex, messages);
        }
        else if(ex instanceof DocumentDoesNotExistException) {
            // TODO derive entity name from context in a localizable way, or hard-code entity name in exception message if only one for service
            throw new EntityNotFoundException(CHECKOUT, key, ex, messages);
        }
        else if(ex instanceof CASMismatchException || ex instanceof TemporaryLockFailureException) {
            throw new OptimisticLockException(key, ex, messages);
        }
        else {
            throw new UnknownException(ex, messages);
        }
    }

}
