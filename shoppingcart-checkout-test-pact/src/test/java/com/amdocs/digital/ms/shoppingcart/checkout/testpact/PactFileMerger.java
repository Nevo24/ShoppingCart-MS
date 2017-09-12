package com.amdocs.digital.ms.shoppingcart.checkout.testpact;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;

public class PactFileMerger 
{
    public static String PACKAGE_OF_PACTS_TO_MERGE;
    public static Set<Object> pactGens = new HashSet<Object>();
    public static void init() throws InstantiationException, IllegalAccessException
    {
        Reflections reflections = new Reflections(PACKAGE_OF_PACTS_TO_MERGE,
                new SubTypesScanner(false));
        Set<Class<? extends Object>> classes = reflections.getSubTypesOf(Object.class);
        for( Class<? extends Object> cls: classes)
        {
            pactGens.add(cls.newInstance());
        }
    }

    public RequestResponsePact createFragment(PactDslWithProvider builder) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestResponsePact retVal = null;
        for( Object pactGen : pactGens)
        {
            Class<? extends Object> cls = pactGen.getClass();
            Method createFragment = cls.getMethod( "createFragment", PactDslWithProvider.class);
            retVal = (RequestResponsePact) createFragment.invoke( pactGen, builder);
        }
        return retVal;
     }

    public void runTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        for( Object pactGen : pactGens)
        {
            Class<? extends Object> cls = pactGen.getClass();
            Method runTest;
            try {
                runTest = cls.getMethod( "runTest");
                runTest.invoke(pactGen);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}