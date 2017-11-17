package com.tntrip;

/**
 * Created by libing2 on 2017/3/2.
 */
public class SchemaVO {
    private String schemaDesc;
    private String indexName;
    private String mappingName;
    private String createSchemaJson;
    private int customerId;
    private String customerName;

    public String getSchemaDesc() {
        return schemaDesc;
    }

    public void setSchemaDesc(String schemaDesc) {
        this.schemaDesc = schemaDesc;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getMappingName() {
        return mappingName;
    }

    public void setMappingName(String mappingName) {
        this.mappingName = mappingName;
    }

    public String getCreateSchemaJson() {
        return createSchemaJson;
    }

    public void setCreateSchemaJson(String createSchemaJson) {
        this.createSchemaJson = createSchemaJson;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
