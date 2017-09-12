package com.amdocs.digital.ms.shoppingcart.checkout.gateways.topics;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

import com.amdocs.msb.asyncmessaging.publish.ITopicPublish;

public interface ICheckoutResourcesPublish extends ITopicPublish {
    @Output("Checkout_Resources_publish")
    SubscribableChannel publish();
}
