package com.syniverse.rti.csp.validator;

import java.io.File;

import java.sql.Date;

import java.text.ParseException;

public class ValidateResult {
    private static AttributeValidator attributeValidator = null;
    private int lineNumber = 0;
    private String record = "";
    private StringBuilder errorMessage = new StringBuilder();
    private ActionType action;
    private String billingID = "";
    private String subscriberKey = "";
    private String subscriberKeySource = "";
    private String subscriberNum = "";


    /*
     * Initialize validate result
     */
    public static void initValidateResult(AttributeValidator attributeValidator) {
        ValidateResult.attributeValidator = attributeValidator;
    }
    
    /*
     * convert value to date
     */
    public Date convertToDate(String value) {
        return ValidateResult.attributeValidator.convertToDate(value);
    }

    /*
     * convert value to integer
     */
    public Integer convertToInteger(String value) {
        return ValidateResult.attributeValidator.convertToInteger(value);
    }
    
    /*
     * Getter
     */
    public boolean isSuccess() {
        if (errorMessage.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Setter
     */
    public void setRecord(String record) {
        this.record = record;
    }

    /*
     * Getter
     */
    public String getRecord() {
        return record;
    }

    /*
     * Setter
     */
    public void addErrorMessage(String errorMessage) {
        if (this.errorMessage.length() > 0) {
            this.errorMessage.append(";");
        }
        this.errorMessage.append(errorMessage);
    }

    /*
     * Getter
     */
    public String getErrorMessage() {
        return errorMessage.toString();
    }

    @Override
    public String toString() {
        return "----------------------------------------------------------------\nSuccess: " +
            isSuccess() + "\nRecord: " + record + "\nLineNumber: " +
            lineNumber + "\nError: " + errorMessage.toString() + "\nAction: " +
            action + "\nBillingID: " + billingID + "\nSubscriberKey: " +
            subscriberKey + "\nSubscriberSource: " + subscriberKeySource +
            "\nSubscriberNum: " + subscriberNum +
            "\n----------------------------------------------------------------";
    }

    /*
     * Setter
     */
    public void setBillingID(String billingID) {
        this.billingID = billingID;
    }

    /*
     * Getter
     */
    public String getBillingID() {
        return billingID;
    }

    /*
     * Setter
     */
    public void setSubscriberKey(String subscriberKey) {
        this.subscriberKey = subscriberKey;
    }

    /*
     * Getter
     */
    public String getSubscriberKey() {
        return subscriberKey;
    }

    /*
     * Setter
     */
    public void setAction(ActionType action) {
        this.action = action;
    }

    /*
     * Getter
     */
    public ActionType getAction() {
        return action;
    }

    /*
     * Setter
     */
    public void setSubscriberNum(String subscriberNum) {
        this.subscriberNum = subscriberNum;
    }

    /*
     * Getter
     */
    public String getSubscriberNum() {
        return subscriberNum;
    }

    /*
     * Setter
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /*
     * Getter
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /*
     * Setter
     */
    public void setSubscriberKeySource(String subscriberKeySource) {
        this.subscriberKeySource = subscriberKeySource;
    }

    /*
     * Getter
     */
    public String getSubscriberKeySource() {
        return subscriberKeySource;
    }
}
