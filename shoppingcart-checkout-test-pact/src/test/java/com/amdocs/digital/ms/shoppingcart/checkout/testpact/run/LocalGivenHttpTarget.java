package com.amdocs.digital.ms.shoppingcart.checkout.testpact.run;

import au.com.dius.pact.model.Interaction;
import au.com.dius.pact.model.RequestResponseInteraction;
import au.com.dius.pact.provider.junit.target.HttpTarget;

public class LocalGivenHttpTarget extends HttpTarget {
    ProviderState providerState;

    // TODO maybe add other constructor signatures
    public LocalGivenHttpTarget(ProviderState provState, final String protocol, final String host, final int port){
        super(protocol, host, port);
        providerState = provState;
    }

    @Override
    public void testInteraction(String consumerName, Interaction interaction) {
        String state = interaction.getProviderState();
        try {
            providerState.establishState( state);
        } catch (Exception e) {
            throw new IllegalStateException( "Could not establish provider state=" + state, e);
        }
        super.testInteraction(consumerName, interaction);
        // TODO download events here into a file named by the interaction.  Use separate files so is order independent
    }


}
