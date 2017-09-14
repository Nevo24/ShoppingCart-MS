package com.amdocs.digital.ms.shoppingcart.checkout.couchbase.dto;

import java.io.Serializable;
import java.util.Date;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.ICheckoutDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IProductOrderRefDTO;
import com.amdocs.digital.ms.shoppingcart.checkout.business.repository.dto.interfaces.IShoppingCartRefDTO;
import com.amdocs.msbase.persistence.couchbase.repository.dto.KeyCouchbaseDTO;
import com.amdocs.msbase.repository.key.implementation.SimpleKey;
import com.amdocs.msbase.repository.key.interfaces.IKey;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class CheckoutDTO extends KeyCouchbaseDTO<IKey> implements ICheckoutDTO, Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    private String state = null;
    private IShoppingCartRefDTO shoppingCartRefDTO = null;
    private IProductOrderRefDTO productOrderRefDTO = null;
    // Control fields
    private String modelVersion;
    private String createdBy;
    private Date createdOn;
    private String lastModifiedBy;
    private Date lastModifiedOn;

    @Override
    @JsonDeserialize(as=SimpleKey.class)
    public void setKey(IKey key) {
        this.key = key;
    }
    @Override
    public String getState() {
        return this.state;
    }
    @Override
    public void setState(String state) {
        this.state = state;
    }
    @Override
    public IShoppingCartRefDTO getShoppingCartRef() {
        return this.shoppingCartRefDTO;
    }
    @Override
    @JsonDeserialize(as=ShoppingCartRefDTO.class)
    public void setShoppingCartRef(IShoppingCartRefDTO shoppingCartRef) {
        this.shoppingCartRefDTO = shoppingCartRef;
    }
    @Override
    public IProductOrderRefDTO getProductOrderRef() {
        return this.productOrderRefDTO;
    }
    @Override
    @JsonDeserialize(as=ProductOrderRefDTO.class)
    public void setProductOrderRef(IProductOrderRefDTO productOrderRef) {
        this.productOrderRefDTO = productOrderRef;
    }

    @Override
    public String getModelVersion() {
        return modelVersion;
    }
    @Override
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    @Override
    public String getCreatedBy() {
        return createdBy;
    }
    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    @Override
    public Date getCreatedOn() {
        return createdOn;
    }
    @Override
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    @Override
    public Date getLastModifiedOn() {
        return lastModifiedOn;
    }
    @Override
    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class CheckoutDTO {\n");
        sb.append("  " + super.toString()).append("\n");
        sb.append("  key: ").append(getKey().toString()).append("\n");
        sb.append("  state: ").append(state).append("\n");
        sb.append("  shoppingCartRef: ").append(shoppingCartRefDTO).append("\n");
        sb.append("  productOrderRef: ").append(productOrderRefDTO).append("\n");
        sb.append("  modelVersion: ").append(modelVersion).append("\n");
        sb.append("  createdOn: ").append(createdOn).append("\n");
        sb.append("  createdBy: ").append(createdBy).append("\n");
        sb.append("  lastModifiedOn: ").append(lastModifiedOn).append("\n");
        sb.append("  lastModifiedBy: ").append(lastModifiedBy).append("\n");
        sb.append("  optimisticLockingValue: ").append(getOptimisticLockingValue()).append("\n");
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