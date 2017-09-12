package com.amdocs.digital.ms.shoppingcart.checkout.providerstate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProviderStateTest {

    @Bean
    public DocDownloader getDocDownloader() {
        return new DocDownloader();
    }

}
