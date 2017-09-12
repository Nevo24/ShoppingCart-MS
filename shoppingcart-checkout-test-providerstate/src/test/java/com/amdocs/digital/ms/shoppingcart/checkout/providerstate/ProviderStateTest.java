package com.amdocs.digital.ms.shoppingcart.checkout.providerstate;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

//import com.amdocs.digital.ms.shoppingcart.checkout.ck.resources.interfaces.CheckoutApi;
//import com.amdocs.digital.ms.shoppingcart.checkout.ck.resources.models.Checkout;
//import com.amdocs.digital.ms.shoppingcart.checkout.ck.resources.models.ShoppingCartID;
//import com.amdocs.digital.ms.shoppingcart.shoppingcart.ck.resources.models.ShoppingCart;

/**
 * This test class creates provider state. One state per test. Note one state could depend on its
 * prior state. This occurs when the current test does not flush the db contents prior to adding new
 * state. This only works if the tests are run in order. So the test methods should have a prefix
 * which can be used to order them.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.amdocs.msbase.app.Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(loader = TestApplicationContextLoader.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProviderStateTest {

    @Inject
    private DocDownloader downloader;

//    @Inject
//    CheckoutApi client;

    @Test
    public void A_CreateCheckout() throws URISyntaxException {
//        downloader.flush();
//        testCreateCheckout();
//        downloader.downloadAndDiffDocs("checkout0Exists");  
    }

    public void testCreateCheckout() throws URISyntaxException {
        //Implement your test here    
//        Checkout co = client.createCheckout(shoppingCartID(), null);
//        assertThat(co.getId(), startsWith("Checkout_"));
    }

//    private ShoppingCartID shoppingCartID() {
//        return new ShoppingCartID().id("ShoppingCart_000000000");
//    }

}
