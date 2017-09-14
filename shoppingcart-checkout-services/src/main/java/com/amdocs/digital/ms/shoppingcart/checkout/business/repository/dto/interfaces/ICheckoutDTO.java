package com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces;
import com.amdocs.msbase.repository.dto.IBaseDTO;
import com.amdocs.msbase.repository.dto.IControlFieldsDTO;
import com.amdocs.msbase.repository.key.interfaces.IKey;
public interface ICheckoutDTO extends IBaseDTO<IKey>, IControlFieldsDTO
{
    public String getState();
    public void setState(String state);
    public IShoppingCartRefDTO getShoppingCartRef();
    public void setShoppingCartRef(IShoppingCartRefDTO shoppingCartRef);
    public IProductOrderRefDTO getProductOrderRef();
    public void setProductOrderRef(IProductOrderRefDTO productOrderRef);
}