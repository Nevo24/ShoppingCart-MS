package com.amdocs.digital.ms.shoppingcart.checkout.autolog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Development notes:  Use IntelliJ's decompiler to look at .class files to see what aspectj added.  When you are editing
// aspect annotations, it give faster turn-around to do this than to run an augmented program.
// Safari has AspectJ in Action.  Which is useful.
// I think if you try to break the lines up, it will confuse aspectj.  Note that Shift+Alt+Y turns on linewrap in Eclipse
// Eclipse warns that advice is not applied.  I guess it just doesn't see the uses in other projects.

// This says for each type (aka class or interface) that matches the package pattern (btw ".." means is a wild card so
// that this annotation could be copied into different microservices without having to edit) and that doesn't have the
// NotLoggable annotation make an instance of AutoLogAspect.  We want an instance per type so we can have a Logger
// per type.
@Aspect("pertypewithin((!@NotLoggable com.amdocs.digital.ms..couchbase.*) || (!@NotLoggable com.amdocs.digital.ms..resources.delegates.*) || (!@NotLoggable com.amdocs.digital.ms..oracle.*) || (!@NotLoggable com.amdocs.digital.ms..business.services.implementation.*) || (!@NotLoggable com.amdocs.digital.ms..business.domain..implementation.*) || (!@NotLoggable com.amdocs.digital.ms..gateways.implementation.*))")
public class AutoLogAspect {
    // We have an AutoLogAspect per type, so this is like a static field in each matching type.
    private Logger logger;

    @Pointcut("staticinitialization(*)")
    public void staticIniter() {
        // This just give something so that the following @After can refer to it. So noop is ok.
    }

    // Initialize logger when target type is loaded
    @After("staticIniter()")
    public void init(JoinPoint.StaticPart joinPointStatic) {
        // I believe by SLF4J rules auto logger will be a child of the class's logger.
        // So enabling debug logging for the class will enable debug autologging
        logger = LoggerFactory.getLogger(joinPointStatic.getSignature().getDeclaringTypeName() + ".autolog");
    }

    // This defines the methods that should get debug logs.  Note it only applies to classes that have been accepted 
    // by the @Aspect annotation.  If this were not the case the type level filtering could be done here with
    // @Pointcut("execution( !@NotLoggable * (!@NotLoggable com.amdocs.digital.ms..resources.delegates.*).execute(..))").
    // This says for each method that doesn't have a NotLoggable annotation and that is in the given method.
    // Note that only the execute method is done for the resource delegates.
    @Pointcut("execution( !@NotLoggable * com.amdocs.digital.ms..resources.delegates.*.execute(..))")
    private void logDebug() {
        // Exists just to have a referent for @Before.  Noop is ok.
    }

    // Like the above but for the classes that should have log level trace not debug.  Note all methods here are logged
    // Added restriction so that hashCode, equals, toString and getters are not logged.
    // Consider adding annotation IsLoggable to add support for getters.  If done then *must* do something to avoid
    // recursing infinitely when logging foo in call to foo.getBar() !!
    @Pointcut( "!execution(* *.hashCode()) && !execution(* *.equals(Object)) && !execution(* *.toString()) && !execution(* *.get*()) && (execution( !@NotLoggable * com.amdocs.digital.ms..couchbase.*.*(..)) || execution( !@NotLoggable * com.amdocs.digital.ms..oracle.*.*(..)) || execution( !@NotLoggable * com.amdocs.digital.ms..business.services.implementation.*.*(..)) || execution( !@NotLoggable * com.amdocs.digital.ms..business.domain..implementation.*.*(..)) || execution( !@NotLoggable * com.amdocs.digital.ms..gateways.implementation.*.*(..)))")
    
    private void logTrace() {
        // Exists just to have a referent for @Before.  Noop is ok.
    }

    @Before("logDebug()")
    public void logDebugBefore(JoinPoint jp) {
        logBefore(jp, false);
    }

    @Before("logTrace()")
    public void logTraceBefore(JoinPoint jp) {
        logBefore(jp, true);
    }

    private void logBefore(JoinPoint jp, boolean isTrace) {
        if (!amLogging(isTrace)) {
            return;
        }
        StringBuilder buf = new StringBuilder(100);
        try {
            MethodSignature sig = (MethodSignature) jp.getSignature();
            Method meth = sig.getMethod();
            Annotation[][] paramAnnots = meth.getParameterAnnotations();
            Object[] actualArgs = jp.getArgs();
            AutoLogFormatter.create(0, buf, new HashSet<Object>()).addMethodCall(meth, jp.getThis(), paramAnnots, actualArgs);
            doLog(isTrace, buf);
        } catch (Exception e) {
            logger.error("Error in AutoLog enter. Partial output: " + buf, e);
        }
    }

    @AfterReturning(pointcut = "logDebug()", returning = "retVal")
    public void logDebugAfterReturn(JoinPoint jp, Object retVal) {
        logAfterReturn(jp, retVal, false);
    }

    @AfterReturning(pointcut = "logTrace()", returning = "retVal")
    public void logTraceAfterReturn(JoinPoint jp, Object retVal) {
        logAfterReturn(jp, retVal, true);
    }

    private void logAfterReturn(JoinPoint jp, Object retVal, boolean isTrace) {
        if (!amLogging(isTrace)) {
            return;
        }
        StringBuilder buf = new StringBuilder(100);
        try {
            MethodSignature sig = (MethodSignature) jp.getSignature();
            Method meth = sig.getMethod();
            Class<?> retType = meth.getReturnType();
            String methName = sig.getName();
            AutoLogFormatter.create(0, buf, new HashSet<Object>()).addMethodReturn(methName, retVal, retType);
            doLog(isTrace, buf);
        } catch (Exception e) {
            logger.error("Error in AutoLog enter return.  Partial output: " + buf, e);
        }
    }

    private boolean amLogging(boolean isTrace) {
        return (isTrace && logger.isTraceEnabled()) || logger.isDebugEnabled();
    }

    @SuppressWarnings("squid:S2629") // This is only called if logging requested
    private void doLog(boolean isTrace, StringBuilder buf) {
        if (isTrace) {
            logger.trace(buf.toString());
        } else {
            logger.debug(buf.toString());
        }
    }

}
