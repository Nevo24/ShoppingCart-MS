package com.amdocs.digital.ms.shoppingcart.checkout.gateways.implementation;

import java.util.Locale;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import com.amdocs.digital.ms.shoppingcart.checkout.gateways.mappers.interfaces.IMapV1ShoppingCartToShoppingCart;
import com.amdocs.digital.ms.shoppingcart.checkout.business.gateways.interfaces.IGetShoppingCartService;
import com.amdocs.digital.ms.shoppingcart.checkout.business.services.models.interfaces.IShoppingCart;
import com.amdocs.digital.ms.shoppingcart.shoppingcart.ck.resources.interfaces.ShoppingCartApi;
import com.amdocs.digital.ms.shoppingcart.shoppingcart.ck.resources.models.V1ShoppingCart;
public class GetShoppingCartService implements IGetShoppingCartService
{
    @Inject
    ShoppingCartApi client;

    @Inject
    IMapV1ShoppingCartToShoppingCart responseMapper;

    private static final Logger logger = LoggerFactory.getLogger(GetShoppingCartService.class);

    @Override
    public IShoppingCart getShoppingCart(String shoppingCartId)
    {
        logger.debug("GetShoppingCart entry, shoppingCartId={}", shoppingCartId);

        Locale locale = LocaleContextHolder.getLocale();
        V1ShoppingCart response = client.getShoppingCart("channel", shoppingCartId, locale.toString());
        IShoppingCart shoppingCart = responseMapper.map(response);

        logger.debug("GetShoppingCart exit, shopping={}", shoppingCart);
        return shoppingCart;
    }
}