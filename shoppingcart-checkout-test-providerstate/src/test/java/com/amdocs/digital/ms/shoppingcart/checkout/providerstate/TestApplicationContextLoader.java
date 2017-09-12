package com.amdocs.digital.ms.shoppingcart.checkout.providerstate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;

import com.amdocs.msbase.injection.AmdocsEmbeddedWebApplicationContext;

public class TestApplicationContextLoader extends SpringBootContextLoader {

    @Override
    public ApplicationContext loadContext(final MergedContextConfiguration config) throws Exception {
        return super.loadContext(config);
    }

    @Override
    protected SpringApplication getSpringApplication() {
        SpringApplication springApplication = new SpringApplication();
        springApplication.setApplicationContextClass(AmdocsEmbeddedWebApplicationContext.class);
        return springApplication;
    }
}