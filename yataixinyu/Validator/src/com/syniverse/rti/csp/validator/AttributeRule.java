package com.syniverse.rti.csp.validator;

import java.util.Set;

public class AttributeRule {
    private String attrColumnName = null;
    private String attrDisplayName = null;
    private AttributeType attrType = null;
    private boolean required = false;
    private int validLength;

    /*
     * Setter
     */
    public void setAttrType(AttributeType attrType) {
        this.attrType = attrType;
    }

    /*
     * Getter
     */
    public AttributeType getAttrType() {
        return attrType;
    }

    /*
     * Setter
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /*
     * Getter
     */
    public boolean isRequired() {
        return required;
    }

    /*
     * Setter
     */
    public void setValidLength(int validLength) {
        this.validLength = validLength;
    }

    /*
     * Getter
     */
    public int getValidLength() {
        return validLength;
    }

    /*
     * Setter
     */
    public void setAttrColumnName(String attrColumnName) {
        this.attrColumnName = attrColumnName;
    }

    /*
     * Getter
     */
    public String getAttrColumnName() {
        return attrColumnName;
    }

    /*
     * Setter
     */
    public void setAttrDisplayName(String attrDisplayName) {
        this.attrDisplayName = attrDisplayName;
    }

    /*
     * Getter
     */
    public String getAttrDisplayName() {
        return attrDisplayName;
    }
}
