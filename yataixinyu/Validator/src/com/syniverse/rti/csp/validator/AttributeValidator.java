package com.syniverse.rti.csp.validator;

import java.sql.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AttributeValidator {
    private SimpleDateFormat datePattern = null;
    private Pattern numericPattern = null;
    private Pattern integerPattern = null;
    private Pattern emailPattern = null;
    private Pattern textPattern = null;

    public AttributeValidator(String datePattern, String numericPattern,
                              String integerPattern, String emailPattern,
                              String textPattern) {
        this.datePattern = new SimpleDateFormat(datePattern);
        this.datePattern.setLenient(false);
        this.numericPattern = Pattern.compile(numericPattern);
        this.integerPattern = Pattern.compile(integerPattern);
        this.emailPattern = Pattern.compile(emailPattern);
        this.textPattern = Pattern.compile(textPattern);
    }

    /*
     * check if the value is blank or null
     */
    public boolean isBlankOrNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * check the max length of the value
     */
    public boolean maxLength(String value, int length) {
        if (value.length() > length) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * check if the value is valid number
     */
    public boolean isNumber(String value) {
        Matcher mat = numericPattern.matcher(value);
        return mat.find();
    }

    /*
     * check if the value is valid text
     */
    public boolean isText(String value) {
        Matcher mat = textPattern.matcher(value);
        return mat.find();
    }

    /*
     * check if the value is valid integer
     */
    public boolean isInteger(String value) {
        Matcher mat = integerPattern.matcher(value);

        return mat.find();
    }

    /*
     * check if the value is valid date
     */
    public boolean isDate(String value) {
        try {
            datePattern.parse(value);
        } catch (ParseException e) {
            return false;
        }
        
        return true;
    }

    /*
     * check if the value is valid email
     */
    public boolean isEmail(String value) {
        Matcher mat = emailPattern.matcher(value);

        return mat.find();
    }

    /*
     * convert value to date
     */
    public Date convertToDate(String value) {
        Date date = null;

        try {
            date = new Date(datePattern.parse(value).getTime());
        } catch (ParseException e) {
            date = null;
        }
        
        return date;
    }

    /*
     * convert value to integer
     */
    public Integer convertToInteger(String value) {
        Integer num = null;

        try {
            num = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            num = null;
        }
        
        return num;
    }
}
