package com.amdocs.digital.ms.shoppingcart.checkout.couchbase.dto;

import java.io.Serializable;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IShoppingCartRefDTO;

public class ShoppingCartRefDTO implements IShoppingCartRefDTO, Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    private String id = null;
    @Override
    public String getId() {
        return this.id;
    }
    @Override
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class ShoppingCartRefDTO {\n");
        sb.append("  " + super.toString()).append("\n");
        sb.append("  id: ").append(id).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
    @Override
    public Object clone()
    {
        try {
            return super.clone();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}