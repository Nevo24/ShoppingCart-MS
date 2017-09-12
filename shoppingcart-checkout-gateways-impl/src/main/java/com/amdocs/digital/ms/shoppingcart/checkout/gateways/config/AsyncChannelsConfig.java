package com.amdocs.digital.ms.shoppingcart.checkout.gateways.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.amdocs.digital.ms.shoppingcart.checkout.gateways.topics.ICheckoutPublish;
import com.amdocs.digital.ms.shoppingcart.checkout.gateways.topics.ICheckoutResourcesPublish;
import com.amdocs.msb.asyncmessaging.config.MsbAsyncMessagingConfig;
import com.amdocs.msb.asyncmessaging.message.resource.intf.IResourceBaseMessage;
import com.amdocs.msb.asyncmessaging.subscribe.IHandleMessage;

@Configuration
@ConditionalOnProperty("spring.cloud.stream.enabled")
@Import({MsbAsyncMessagingConfig.class})
@EnableBinding({ICheckoutPublish.class, ICheckoutResourcesPublish.class})
public class AsyncChannelsConfig {
}
