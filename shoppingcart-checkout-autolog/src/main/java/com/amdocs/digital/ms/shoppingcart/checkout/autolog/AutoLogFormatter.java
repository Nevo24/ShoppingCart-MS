package com.amdocs.digital.ms.shoppingcart.checkout.autolog;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.springframework.core.annotation.AnnotationUtils;
@java.lang.SuppressWarnings({"squid:CommentedOutCodeLine", "squid:S1172"}) // This class allows customizing formatting using currently unused parameters and commented out code
public class AutoLogFormatter {
    private static final String ITERABLE_SEPARATOR = ",";
    private static final String MAP_SEPARATOR = ",";
    private static final String PARAM_SEPARATOR = ",";
    private static final String PII_MASK = "MaskPII";


    // If someone decides that we don't want this all on one line, indent could be added to an indenting subclass
    // and used to track the current amount of indentation.
    // private int indent;
    private StringBuilder buf;
    private Set<Object> visitedObjs;

    /**
     * Creates a formatter.
     * @param indent if newlines are added this is the starting indentation
     * @param buf all output is added to this
     * @param visitedObjs Used to avoid looping on cyclic data.  Only the first occurrence of an object is output the
     * set of objects seen.  Visited objects will be added to this.
     */
    public AutoLogFormatter(int indent, StringBuilder inBuf, Set<Object> inVisitedObjs) {
		buf = inBuf;
		visitedObjs = inVisitedObjs;
    }

    // Maybe autowire this from config, so different implementations could be used.
    public static AutoLogFormatter create( int indent, StringBuilder buf,  Set<Object> visitedObjs) {
		return new AutoLogFormatter( indent, buf, visitedObjs);
    }

    public boolean isPiiMeth( Method meth) {
		return AnnotationUtils.findAnnotation( meth, PII.class) != null;
    }

    /**
     * @param bean an object to be recursively added to the buf
     */
    @SuppressWarnings( "squid:S3776") // splitting up would not make this clearer
    public void addObject( Object bean) throws Exception {
		addBeforeValue();
		if ( bean == null) {
		    addNull();
		    addAfterValue();
		    return;
		}
		Class<? extends Object> cls = bean.getClass();
		Method toStrMeth = cls.getMethod("toString");
		if ( cls.isArray()) {
		    addIterable( Arrays.asList((Object[]) bean));
		} else if ( Iterable.class.isAssignableFrom( cls)) {
		    addIterable((Iterable<?>) bean);
		} else if ( Map.class.isAssignableFrom( cls)) {
		    Map<?,?> mapBean = (Map<?,?>) bean;
		    addBeforeMap();
		    for( Map.Entry<?,?> elem: mapBean.entrySet()) {
			addBeforeMapKey();
			addObject( elem.getKey());
			addBeforeMapValue();
			addObject( elem.getValue());
			addAfterMapValue();
		    }
		    addAfterMap( mapBean.size() == 0);
		} else if ( !toStrMeth.getDeclaringClass().equals(Object.class)
		    // Printing the proxy via reflection has lots of crud.  Uncomment if you want to see it:
		    // && !Proxy.class.isAssignableFrom(toStrMeth.getDeclaringClass())
		    ) {
		    buf.append( bean.toString());
		} else { // Use reflection on getters
		    PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(cls, Object.class).getPropertyDescriptors();
		    addBeforeObjectByReflection();
		    addBeanId( cls, bean);
		    if ( visitedObjs.add( bean)) {
			boolean noProps = true;
			addBeforeProps();
			for( PropertyDescriptor propDesc: propertyDescriptors) {
			    Method meth = propDesc.getReadMethod();
			    if ( meth != null) {
				try {
				    addBeforePropName();
				    buf.append( propDesc.getName());
				    addAfterPropName();
				    addObject( isPiiMeth( meth) ? PII_MASK : meth.invoke( bean));
				    addAfterPropValue();
				    noProps = false;
				} catch ( Exception e) { // NOSONAR e in output not logged.
				    buf.append( "{{");
				    addBeanId( cls, bean);
				    buf.append( "." + meth + "had error: " + e.toString() + "}}");
				}
			    }
			}
			addAfterProps( noProps);
		    }
		    addAfterObjectByReflection();
		}
		addAfterValue();
    }

    private void addBeforeProps() { 
    	// Do nothing
    }

    /**
     * If Overridden also override addAfterPropValue
     */
    private void addAfterProps(boolean noProps) { 
    	// Do nothing
    }

    private void addAfterObjectByReflection() {
		buf.append("}");
    }

    private void addAfterPropName() {
		buf.append( " = ");
    }

    /**
     * If Overridden also override addAfterProps
     */
    private void addAfterPropValue() {
		buf.append( ";");
    }

    private void addBeforePropName() {
		buf.append( " ");
    }

    private StringBuilder addBeforeObjectByReflection() {
		return buf.append("{");
    }

    private void addBeforeValue() {
		buf.append( " ");
    }

    private void addAfterValue() { 
    	// do nothing
    }

    private void addBeforeMapValue() {
		buf.append( " :: ");
    }

    private void addBeforeMapKey() {
		buf.append( " ");
    }

    public void addBeforeIterable() {
		buf.append( "[");
    }

    private void addBeforeIterableElem() {
		buf.append( " ");
    }

    /**
     * If Overridden also override addAfterIterable
     */
    private void addAfterIterableElem() {
		buf.append(ITERABLE_SEPARATOR);
    }

    /**
     * If Overridden also override addAfterIterableElem
     */
    public void addAfterIterable( boolean wasEmpty) {
		addAfterGroupWithSeparator(wasEmpty, ITERABLE_SEPARATOR);
		buf.append("]");
    }

    public void addAfterGroupWithSeparator(boolean wasEmpty, String separator) {
		if ( !wasEmpty) {
		    buf.setLength(buf.length() - separator.length()); // remove trailing separator
		}
    }

    private void addBeforeMap() {
		buf.append( "<");
    }

    private void addAfterMapValue() {
		buf.append( MAP_SEPARATOR);
    }

    private void addAfterMap( boolean wasEmpty) {
		addAfterGroupWithSeparator(wasEmpty, MAP_SEPARATOR);
		buf.append(">");
    }

    private void addNull() {
		buf.append("*null*");
    }

    public void addBeanId( Class<?> cls, Object bean) {
		buf.append( cls.getSimpleName());
		buf.append( "-");
		buf.append( System.identityHashCode(bean));
		buf.append( ":");
    }

    public void addIterable(Iterable<?> iter) throws Exception {
		addBeforeIterable();
		boolean wasEmpty = true;
		for( Object elem: iter) {
		    addBeforeIterableElem();
		    addObject( elem);
		    addAfterIterableElem();
		    wasEmpty = false;
		}
		addAfterIterable( wasEmpty);
    }

    public void addMethodCall(Method method, Object theThis, Annotation[][] paramAnnots, Object[] actualArgs) throws Exception {
		addMethodStart(method);
	
		if ( theThis != null) {
		    addBeforeMapKey();
		    addObject( theThis);
		}
		addBeforeParams();
		for (int a = 0; a < actualArgs.length; a++) {
		    boolean doLogParam = true;
		    for (Annotation annot : paramAnnots[a]) {
			Class<? extends Annotation> annotType = annot.annotationType();
			if (annotType.equals(PII.class)) {
			    doLogParam = false;
			}
		    }
		    if ( doLogParam) {
			// As it stands an object will only be recursively expanded via reflection once per method.
			// This means that if an object, X, is recursively expanded in the 1st parameter at most a reference
			// to X will be present in the 2nd parameter.  If instead we want the 1st occurrence of an object to
			// be expanded in *each* parameter, add     visitedObjs.clear();     here.
			addObject( actualArgs[a]);
			addAfterParam();
		    } else {
			buf.append( PII_MASK + MAP_SEPARATOR);
		    }
		}
		addAfterParams( actualArgs.length == 0);
    }

    private void addBeforeParams() {
		buf.append("(");
    }

    /**
     * If Overridden also override addAfterParams
     */
    private void addAfterParam() {
		buf.append( PARAM_SEPARATOR);
    }

    /**
     * If Overridden also override addAfterParam
     */
    private void addAfterParams(boolean wasEmpty) {
		addAfterGroupWithSeparator( wasEmpty, PARAM_SEPARATOR);
		buf.append(") AutoLog enter end");
    }

    private void addMethodStart(Method method) {
		buf.append( "AutoLog enter start: " +  method.getClass().getName() + "." + method.getName());
    }

    void addMethodReturn(String methodName, Object retVal, Class<?> retType) throws Exception {
		if ( retType.equals(Void.TYPE)) {
		    buf.append( "AutoLog return: " +  methodName);
		} else {
		    buf.append( "AutoLog return start: " +  methodName);
		    addObject( retVal);
		    buf.append( " AutoLog return end: " +  methodName);
		}
    }
}
