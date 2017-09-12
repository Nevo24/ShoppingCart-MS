package com.amdocs.digital.ms.shoppingcart.checkout.testpact.run;

import org.apache.http.HttpRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.TargetRequestFilter;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;

@RunWith(PactRunner.class)
@Provider("Checkout")
@PactBroker(protocol = "${pactBrokerProtocol}", host = "${pactBrokerHost}", port = "${pactBrokerPort}")
public class RunPactTest {
    static ProviderState providerState;

    @TestTarget
    public final Target target = new LocalGivenHttpTarget( providerState,
           "http",
            ProviderState.requireProperty("provider.host"),
            Integer.parseInt( ProviderState.requireProperty("provider.port")));

    @BeforeClass
    public static void setUpService() {
        providerState = new ProviderState();
    }

    @Before
    public void before() {
        // Rest data
        // Mock dependent service responses
        // ...
    }

    @TargetRequestFilter
    public void exampleRequestFilter(HttpRequest request) {
		request.addHeader(HttpHeaders.AUTHORIZATION,
            "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImdpUFkxeHZYb0taTVN3eDcvV1dHSUpQQjByTSJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImV4cCI6MTQ4OTA4MTA3OTc5OTB9.K-Bp_9lAW2W9PjmXGdGwrEID-dvamTmZwwndcQvO8mJJgW_PIo76_BYO-Ncstb_vPnxdG2Re9X_kEjve-i5cymdIkoYczPlryg-QxgLa1ZUt0-7FkLhWbGxghNLxk2k5vdcS07OwOG6wdDhoEvv_49h05p4FKG2Re5dskJIXRKzvmBYddqJrBDJsfYT0UGB94oVKnOQtI7mEB4Q1XpKz5NqYMN_HWZqEF5MHBBLbsIxCpHD6zOeJNppl7BSywFyJMRp-eSBwKlsR3eSX_jMDuM13Eaf3h3yd2pKDIxtValh822xaL9GnDq-YeAxmQrEg8o6tN_r1WRcoS8-cgqw3Ng");
    }

    // TODO (manual customization instruction)
    // 1. Go under *-test-providerstate/approved and find the provider state data directories
    // 2. Replace "v1-provider-state-1", "v1-provider-state-2" wit the directory names you find in #1
    // 3. Do not touch "emptyDb". This is a reserved provider state for empty database.
    @State({"emptyDb", "v1-provider-state-1", "v1-provider-state-2"})
    public void dummyProviderStateMethodToAvoidMissingProviderStateError() {
        // Provider State is set in the LocalGivenHttpTaget class
    }
}