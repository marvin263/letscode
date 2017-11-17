package com.syniverse.rti.csp.validator;


import java.io.FileNotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.regex.Matcher;

import javax.naming.Context;
import javax.naming.InitialContext;


public class Validator {
    private String jndiName = null;
    private String attrList = null;
    // all attribute rules
    private List<AttributeRule> attrRuleList = null;
    private AttributeValidator attributeValidator = null;
    private String billingID = "";
    private String delimiter = ",";
    private int attrListLength = 0;
    private boolean caseSensitive = true;
    // total time used 
    private long timeForRecordsExist = 0;
    private long timeStatmentIN = 0;
    private long timeStatmentOR = 0;
    private long timePreparedStatmentOR = 0;

    /*
     * constructor for loader
     */
    public Validator(String billingID) {
        this.billingID = billingID;
    }

    /*
     * constructor for GUI
     */
    public Validator(String jndiName, String billingID) throws Exception {
        this.jndiName = jndiName;

        // get DB connection from JNDI
        Context ctx = new InitialContext();
        Connection conn =
            ((javax.sql.DataSource)ctx.lookup(jndiName)).getConnection();

        initPattern(conn);

        if (billingID != null) {
            this.billingID = billingID;
            initAttributeList(conn, "GUI");
            initAttributeRule(conn);
        }

        conn.close();
    }

    /*
     * constructor for loader
     */
    public Validator(String jdbcURL, String jdbcUsername, String jdbcPassword,
                     String billingID, String attrStr) throws Exception {
        this.billingID = billingID;

        // load oracle driver
        Class.forName("oracle.jdbc.OracleDriver");
        
        // get DB connection
        Connection batchConn =
            DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

        initPattern(batchConn);
        
        if (attrStr == null || attrStr.trim().length() == 0) {
            initAttributeList(batchConn, "BATCH");
        } else {
            this.attrList = attrStr.toUpperCase();
        }
        
        initAttributeRule(batchConn);

        batchConn.close();
    }

    /*
     * initialize validate pattern
     */
    private void initPattern(Connection conn) throws Exception {
        String sql =
            "select keyid,keyvalue from app_config where config_type='VALIDATOR'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        String datePattern = "";
        String numericPattern = "";
        String integerPattern = "";
        String emailPattern = "";
        String textPattern = "";

        while (rs.next()) {
            if ("DATE_PATTERN".equalsIgnoreCase(rs.getString(1))) {
                datePattern = rs.getString(2).trim();
            } else if ("NUMERIC_PATTERN".equalsIgnoreCase(rs.getString(1))) {
                numericPattern = rs.getString(2).trim();
            } else if ("INTEGER_PATTERN".equalsIgnoreCase(rs.getString(1))) {
                integerPattern = rs.getString(2).trim();
            } else if ("EMAIL_PATTERN".equalsIgnoreCase(rs.getString(1))) {
                emailPattern = rs.getString(2).trim();
            } else if ("TEXT_PATTERN".equalsIgnoreCase(rs.getString(1))) {
                textPattern = rs.getString(2).trim();
            } else if ("CASE_SENSITIVE".equalsIgnoreCase(rs.getString(1))) {
                this.caseSensitive =
                        ("N".equalsIgnoreCase(rs.getString(2).trim()) ? false :
                         true);
            }
        }

        this.attributeValidator =
                new AttributeValidator(datePattern, numericPattern,
                                       integerPattern, emailPattern,
                                       textPattern);
        
        ValidateResult.initValidateResult(this.attributeValidator);

        rs.close();
        stmt.close();
    }
    
    /*
     * initialize attribute list
     */
    private void initAttributeList(Connection conn,
                                   String sourceType) throws Exception {
        String sql =
            "select ac.delimiter,al.attr_list " + "from attribute_config ac,attribute_list al " +
            "where ac.billing_id='" + this.billingID + "' and ac.type='" +
            sourceType +
            "' and ac.list_id=al.list_id and ac.version_num=al.version_num";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        rs.next();
        this.delimiter = rs.getString(1);
        this.attrList = rs.getString(2);

        rs.close();
        stmt.close();
    }

    /*
     * initialize attribute rules
     */
    private void initAttributeRule(Connection conn) throws Exception {
        this.attrRuleList = new ArrayList<AttributeRule>();
        AttributeRule aRule = null;
        ResultSet rs = null;
        String sql =
            "select attr_type,attr_display_name,required,valid_length from attribute where billing_id=? and attr_column_name=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, this.billingID);
        String[] attrArray = this.attrList.split(",");

        for (String attr : attrArray) {
            if ("ACTION".equalsIgnoreCase(attr)) {
                continue;
            }

            aRule = new AttributeRule();
            aRule.setAttrColumnName(attr);

            pstmt.setString(2, attr);
            rs = pstmt.executeQuery();
            rs.next();
            
            aRule.setAttrType(AttributeType.valueOf(rs.getString(1).toUpperCase()));
            aRule.setAttrDisplayName(rs.getString(2));
            aRule.setRequired("Y".equals(rs.getString(3).toUpperCase()) ?
                              true : false);
            aRule.setValidLength(rs.getInt(4));
            
            this.attrRuleList.add(aRule);
        }
        this.attrListLength = this.attrRuleList.size();

        rs.close();
        pstmt.close();
    }

    /*
     * parse a row
     */
    private List<String> parseRow(String record) {
        List<String> list = new ArrayList<String>();

        if (attributeValidator.isBlankOrNull(record)) {
            return list;
        }

        int delimiterL = delimiter.length();
        int orgnL = record.length();
        int lastEnd = 0;

        while (lastEnd <= (orgnL - 1)) {
            int currentStart = record.indexOf(delimiter, lastEnd);
            if (currentStart == -1) {
                list.add(record.substring(lastEnd));
                break;
            }
            list.add(record.substring(lastEnd, currentStart));
            lastEnd = currentStart + delimiterL;
        }

        if (record.endsWith(delimiter)) {
            list.add("");
        }

        return list;
    }

    /*
     * validate one line of record
     */
    public ValidateResult validateRecord(int lineNumber, String record) {
        ValidateResult result = new ValidateResult();
        result.setBillingID(this.billingID);
        result.setRecord(record);
        result.setLineNumber(lineNumber);

        List<String> attributes = parseRow(record);

        // check column count
        if (attributes.size() == 0) {
            result.addErrorMessage("Empty line.");

            return result;
        }
        
        if (this.attrListLength + 1 != attributes.size()) {
            result.addErrorMessage("Column count doesn't match.");

            return result;
        }

        // check if first column is 'A/U/D'
        if ("A".equals(attributes.get(0))) {
            result.setAction(ActionType.INSERT);
        } else if ("U".equals(attributes.get(0))) {
            result.setAction(ActionType.UPDATE);
        } else if ("D".equals(attributes.get(0))) {
            result.setAction(ActionType.DELETE);
        } else {
            result.addErrorMessage("The first column should be A/U/D.");

            return result;
        }
        attributes.remove(0);

        // initialize flag matrix
        String technologyType = "";
        String IMSI = "";
        String MIN = "";
        String MSISDN = "";
        String MDN = "";
        boolean hasIMSI = false;
        boolean hasMSISDN = false;
        AttributeRule aRule = null;
        String value = null;
        
        // loop for each attribute
        for (int i = 0; i < this.attrListLength; i++) {
            aRule = this.attrRuleList.get(i);
            value = attributes.get(i);

            // check if it is required
            if (attributeValidator.isBlankOrNull(value)) {
                if (aRule.isRequired()) {
                    result.addErrorMessage(aRule.getAttrDisplayName() +
                                           ":Value is required.");
                }

                continue;
            }

            // check length
            if (!attributeValidator.maxLength(value, aRule.getValidLength())) {
                result.addErrorMessage(aRule.getAttrDisplayName() +
                                       ":Value exceeds maximum length " +
                                       aRule.getValidLength() +
                                       ".");

                continue;
            }

            // check data type
            switch (aRule.getAttrType()) {
            case TEXT:
                if ("TECHNOLOGY_TYPE".equals(aRule.getAttrColumnName())) {
                    if (this.caseSensitive) {
                        if ("GSM".equals(value)) {
                            technologyType = "GSM";
                        } else if ("CDMA".equals(value)) {
                            technologyType = "CDMA";
                        } else {
                            result.addErrorMessage(aRule.getAttrDisplayName() +
                                                   ":Invalid value.");

                            continue;
                        }
                    } else {
                        if ("GSM".equalsIgnoreCase(value)) {
                            technologyType = "GSM";
                        } else if ("CDMA".equalsIgnoreCase(value)) {
                            technologyType = "CDMA";
                        } else {
                            result.addErrorMessage(aRule.getAttrDisplayName() +
                                                   ":Invalid value.");

                            continue;
                        }
                    }
                } else if ("IMSI".equals(aRule.getAttrColumnName())) {
                    hasIMSI = true;
                    if (attributeValidator.isInteger(value)) {
                        IMSI = value;
                    } else {
                        result.addErrorMessage(aRule.getAttrDisplayName() +
                                               ":Should be a number.");

                        continue;
                    }
                } else if ("MSISDN".equals(aRule.getAttrColumnName())) {
                    hasMSISDN = true;
                    if (attributeValidator.isInteger(value)) {
                        MSISDN = value;
                    } else {
                        result.addErrorMessage(aRule.getAttrDisplayName() +
                                               ":Should be a number.");

                        continue;
                    }
                } else if ("MIN".equals(aRule.getAttrColumnName())) {
                    if (attributeValidator.isInteger(value)) {
                        MIN = value;
                    } else {
                        result.addErrorMessage(aRule.getAttrDisplayName() +
                                               ":Should be a number.");

                        continue;
                    }
                } else if ("MDN".equals(aRule.getAttrColumnName())) {
                    if (attributeValidator.isInteger(value)) {
                        MDN = value;
                    } else {
                        result.addErrorMessage(aRule.getAttrDisplayName() +
                                               ":Should be a number.");

                        continue;
                    }
                } else if ("NOTIFICATION_PHONE".equals(aRule.getAttrColumnName())) {
                    if (!attributeValidator.isInteger(value)) {
                        result.addErrorMessage(aRule.getAttrDisplayName() +
                                               ":Should be a number.");

                        continue;
                    }
                } else if ("VOICE_SERVICE".equals(aRule.getAttrColumnName()) ||
                           "DATA_SERVICE".equals(aRule.getAttrColumnName()) ||
                           "SMS_SERVICE".equals(aRule.getAttrColumnName()) ||
                           "DO_NOT_SOLICIT".equals(aRule.getAttrColumnName())) {
                    if (this.caseSensitive) {
                        if (!"Y".equals(value) && !"N".equals(value)) {
                            result.addErrorMessage(aRule.getAttrDisplayName() +
                                                   ":Invalid value.");

                            continue;
                        }
                    } else {
                        if (!"Y".equalsIgnoreCase(value) &&
                            !"N".equalsIgnoreCase(value)) {
                            result.addErrorMessage(aRule.getAttrDisplayName() +
                                                   ":Invalid value.");

                            continue;
                        }
                    }
                } else {
                    if (!attributeValidator.isText(value)) {
                        result.addErrorMessage(aRule.getAttrDisplayName() +
                                               ":Invalid value.");
                        continue;
                    }
                }

                break;
            case NUMERIC:
                if (!attributeValidator.isInteger(value)) {
                    result.addErrorMessage(aRule.getAttrDisplayName() +
                                           ":Should be a number.");
                    continue;
                }

                break;
            case DATE:
                if (!attributeValidator.isDate(value)) {
                    result.addErrorMessage(aRule.getAttrDisplayName() +":Invalid date format.(Example:mm/dd/yyyy)");
                    continue;
                }

                break;
            case EMAIL:
                if (!attributeValidator.isEmail(value)) {
                    result.addErrorMessage(aRule.getAttrDisplayName() +":Invalid email.");
                    continue;
                }

                break;
            default:
                result.addErrorMessage(aRule.getAttrDisplayName() +
                                       ":Invalid attribute type.");

                continue;
            }
        }

        // check flag matrix
        if ("GSM".equals(technologyType)) {
            if (!hasIMSI && IMSI.length() == 0) {
                result.addErrorMessage("Please provide an IMSI.");
            } 
            
            if (!hasMSISDN && MSISDN.length() == 0) {
                result.addErrorMessage("Please provide a MSISDN.");
            }
            
            if (!result.isSuccess()) {
                return result;
            }

            result.setSubscriberKey(IMSI);
            result.setSubscriberKeySource("IMSI");
            result.setSubscriberNum(MSISDN);
        } else if ("CDMA".equals(technologyType)) {
            if (MIN.length() == 0 && MDN.length() == 0) {
                result.addErrorMessage("Please provide a MIN or MDN.");
                return result;
            }

            if (MIN.length() > 0) {
                result.setSubscriberKey(MIN);
                result.setSubscriberKeySource("MIN");
            } else {
                result.setSubscriberKey(MDN);
                result.setSubscriberKeySource("MDN");
            }

            result.setSubscriberNum(MDN.length() > 0 ? MDN : MIN);
        }

        return result;
    }

    /*
     * check if a record exists.
     */
    public boolean isRecordExist(String subscriberKey) throws Exception {
        Context ctx = new InitialContext();
        Connection conn =
            ((javax.sql.DataSource)ctx.lookup(jndiName)).getConnection();

        String sql =
            "select count(billing_id) from subscriber where billing_id='" +
            this.billingID + "' and subscriber_key='" + subscriberKey + "'";

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        stmt.close();
        conn.close();

        return count > 0 ? true : false;
    }

    /*
     * check if records exist using PreparedStatement and IN
     */
    public boolean[] areRecordsExist(Connection conn, PreparedStatement pstmt,
                                     String[] subscriberKeys) throws Exception {
        long start = System.currentTimeMillis();

        int keysLength = subscriberKeys.length;
        boolean[] results = new boolean[keysLength];

        Set keys = new HashSet<String>();
        ResultSet rs = null;
        Statement stmt = null;
        int batchSize = 1000;
        int batchSize2 = batchSize + 2;

        int loopTimes = (int)Math.ceil(keysLength * 1.0 / batchSize);
        pstmt.setString(1, this.billingID);

        for (int i = 0; i < loopTimes; i++) {
            if (i < loopTimes - 1) {
                for (int j = 2, k = i * batchSize; j < batchSize2; j++, k++) {
                    pstmt.setString(j, subscriberKeys[k]);
                }

                rs = pstmt.executeQuery();
            } else {
                StringBuilder sb = new StringBuilder();
                String beginStr =
                    "select subscriber_key from subscriber where billing_id='" +
                    this.billingID + "' and subscriber_key in (";
                String endStr = ")";

                stmt = conn.createStatement();
                sb.setLength(0);
                sb.append(beginStr);

                for (int j = 0, k = i * batchSize;
                     j < batchSize && k < keysLength; j++, k++) {
                    if (j > 0) {
                        sb.append(",");
                    }

                    sb.append("'");
                    sb.append(subscriberKeys[k]);
                    sb.append("'");
                }

                sb.append(endStr);

                rs = stmt.executeQuery(sb.toString());
            }

            while (rs.next()) {
                keys.add(rs.getString(1));
            }
        }

        rs.close();
        if (stmt != null) {
            stmt.close();
        }

        for (int i = 0; i < keysLength; i++) {
            if (keys.contains(subscriberKeys[i])) {
                results[i] = true;
            } else {
                results[i] = false;
            }
        }

        this.timeForRecordsExist += (System.currentTimeMillis() - start);

        return results;
    }

    /*
     * check if records exist using Statement and IN
     */
    public boolean[] areRecordsExist(Connection conn,
                                     String[] subscriberKeys) throws Exception {
        long start = System.currentTimeMillis();

        int keysLength = subscriberKeys.length;
        boolean[] results = new boolean[keysLength];

        String beginStr =
            "select subscriber_key from subscriber where billing_id='" +
            this.billingID + "' and subscriber_key in (";
        String endStr = ")";

        StringBuilder sb = new StringBuilder();

        Set keys = new HashSet<String>();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        int batchSize = 1000;

        int loopTimes = (int)Math.ceil(keysLength * 1.0 / batchSize);

        for (int i = 0; i < loopTimes; i++) {
            sb.setLength(0);
            sb.append(beginStr);

            for (int j = 0, k = i * batchSize; j < batchSize && k < keysLength;
                 j++, k++) {
                if (j > 0) {
                    sb.append(",");
                }

                sb.append("'");
                sb.append(subscriberKeys[k]);
                sb.append("'");
            }

            sb.append(endStr);

            rs = stmt.executeQuery(sb.toString());

            while (rs.next()) {
                keys.add(rs.getString(1));
            }
        }

        rs.close();
        stmt.close();

        for (int i = 0; i < keysLength; i++) {
            if (keys.contains(subscriberKeys[i])) {
                results[i] = true;
            } else {
                results[i] = false;
            }
        }

        this.timeStatmentIN += (System.currentTimeMillis() - start);

        return results;
    }

    /*
     * check if records exist using Statement and OR
     */
    public boolean[] areRecordsExistOR(Connection conn,
                                       String[] subscriberKeys) throws Exception {
        long start = System.currentTimeMillis();

        int keysLength = subscriberKeys.length;
        boolean[] results = new boolean[keysLength];

        Set keys = new HashSet<String>();

        StringBuilder sb = new StringBuilder();
        sb.append("select subscriber_key from subscriber where billing_id='" +
                  this.billingID + "' and (");

        for (int i = 0; i < keysLength; i++) {
            if (i > 0) {
                sb.append(" or ");
            }

            sb.append("subscriber_key='");
            sb.append(subscriberKeys[i]);
            sb.append("'");
        }

        sb.append(")");

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sb.toString());

        while (rs.next()) {
            keys.add(rs.getString(1));
        }

        rs.close();
        stmt.close();

        for (int i = 0; i < keysLength; i++) {
            if (keys.contains(subscriberKeys[i])) {
                results[i] = true;
            } else {
                results[i] = false;
            }
        }

        this.timeStatmentOR += (System.currentTimeMillis() - start);

        return results;
    }

    /*
     * check if records exist using PreparedStatement and OR
     */
    public boolean[] areRecordsExistOR(Connection conn,
                                       PreparedStatement pstmt, int batchSize,
                                       String[] subscriberKeys) throws Exception {
        long start = System.currentTimeMillis();

        int keysLength = subscriberKeys.length;
        boolean[] results = new boolean[keysLength];

        Set keys = new HashSet<String>();
        ResultSet rs = null;
        Statement stmt = null;

        int loopTimes = (int)Math.ceil(keysLength * 1.0 / batchSize);
        pstmt.setString(1, this.billingID);

        int batchSize2 = batchSize + 2;
        for (int i = 0; i < loopTimes; i++) {
            if (i < loopTimes - 1) {
                for (int j = 2, k = i * batchSize; j < batchSize2; j++, k++) {
                    pstmt.setString(j, subscriberKeys[k]);
                }

                rs = pstmt.executeQuery();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("select subscriber_key from subscriber where billing_id='" +
                          this.billingID + "' and (");

                for (int j = 0; j < keysLength; j++) {
                    if (j > 0) {
                        sb.append(" or ");
                    }

                    sb.append("subscriber_key='");
                    sb.append(subscriberKeys[j]);
                    sb.append("'");
                }

                sb.append(")");

                stmt = conn.createStatement();
                rs = stmt.executeQuery(sb.toString());
            }

            while (rs.next()) {
                keys.add(rs.getString(1));
            }
        }

        rs.close();
        if (stmt != null) {
            stmt.close();
        }

        for (int i = 0; i < keysLength; i++) {
            if (keys.contains(subscriberKeys[i])) {
                results[i] = true;
            } else {
                results[i] = false;
            }
        }

        this.timePreparedStatmentOR += (System.currentTimeMillis() - start);

        return results;
    }
    
    /*
     * check if the value is valid text
     */
    public ValidateResult isText(String attrName, String value) {
        ValidateResult result = new ValidateResult();

        if (!attributeValidator.isText(value)) {
            result.addErrorMessage(attrName + ":Invalid value.");
        }

        return result;
    }

    /*
     * check if the value is valid integer
     */
    public ValidateResult isInteger(String attrName, String value) {
        ValidateResult result = new ValidateResult();

        if (!attributeValidator.isInteger(value)) {
            result.addErrorMessage(attrName + ":Should be a number.");
        }

        return result;
    }

    /*
     * check if the value is valid date
     */
    public ValidateResult isDate(String attrName, String value) {
        ValidateResult result = new ValidateResult();

        if (!attributeValidator.isDate(value)) {
            result.addErrorMessage(attrName + ":Invalid date format.(Example:mm/dd/yyyy)");
        }

        return result;
    }

    /*
     * check if the value is valid email
     */
    public ValidateResult isEmail(String attrName, String value) {
        ValidateResult result = new ValidateResult();

        if (!attributeValidator.isEmail(value)) {
            result.addErrorMessage(attrName + ":Invalid email.");
        }

        return result;
    }
    
    /*
     * check if the value has delimiter
     */
    public ValidateResult hasDelimiter(String attrName, String value) {
        ValidateResult result = new ValidateResult();

        if (value.indexOf(this.delimiter) >= 0) {
            result.addErrorMessage(attrName + ":Invalid value.");
        }

        return result;
    }

    /*
     * Getter
     */
    public long getStatmentIN() {
        return timeStatmentIN;
    }

    /*
     * Getter
     */
    public long getStatmentOR() {
        return timeStatmentOR;
    }

    /*
     * Getter
     */
    public long getPreparedStatmentOR() {
        return timePreparedStatmentOR;
    }

    /*
     * Getter
     */
    public long getTimeForRecordsExist() {
        return timeForRecordsExist;
    }


//        public static void main(String[] args) throws FileNotFoundException,
//                                                      Exception {
    //        Pattern textPattern = Pattern.compile("^[\\w.]*$");
    //
    //        Matcher mat =
    //            textPattern.matcher("aabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_.");
    //
    //        System.out.println(mat.find());


    //        System.out.println("initializing");

    //        Validator v =
    //            new Validator("jdbc:oracle:thin:@//hw-sls-rac.syniverse.com:1521/CSPD",
    //                          "cspski", "cspski", "99999",
    //                          "TECHNOLOGY_TYPE,IMSI,MSISDN,MIN,MDN,SUBSCRIBER_NAME,SUBSCRIBER_IDENTIFIER,SUBSCRIBER_TYPE,SUBSCRIBER_GROUP,AUTHORIZED_USER_ID,NOTIFICATION_EMAIL,NOTIFICATION_PHONE,USAGE_VALUE,INTERNAL_SCORING,FINANCIAL_STANDING,SERVICE_START_DATE,VOICE_SERVICE,DATA_SERVICE,SMS_SERVICE,HOME_SERVICE_MARKET,DEVICE_TYPE,DEVICE_CATEGORY,EXTERNAL_SOURCE_ID,DO_NOT_SOLICIT,DO_NOT_SOLICIT_DATE,ACCOUNT_TYPE,PAYMENT_METHOD,MESSAGE_LANGUAGE");

    //        Validator v =
    //            new Validator("jdbc:oracle:thin:@10.28.120.252:1521:CSPD",
    //                          "cspapp01", "cspapp01", "99999",
    //                          "TECHNOLOGY_TYPE,IMSI,MSISDN,MIN,MDN,SUBSCRIBER_NAME,SUBSCRIBER_IDENTIFIER,SUBSCRIBER_TYPE,SUBSCRIBER_GROUP,AUTHORIZED_USER_ID,NOTIFICATION_EMAIL,NOTIFICATION_PHONE,USAGE_VALUE,INTERNAL_SCORING,FINANCIAL_STANDING,SERVICE_START_DATE,VOICE_SERVICE,DATA_SERVICE,SMS_SERVICE,HOME_SERVICE_MARKET,DEVICE_TYPE,DEVICE_CATEGORY,EXTERNAL_SOURCE_ID,DO_NOT_SOLICIT,DO_NOT_SOLICIT_DATE,ACCOUNT_TYPE,PAYMENT_METHOD,MESSAGE_LANGUAGE");
    //
    //
    //        String str = "A,GSM,32181,32181,,,,,,,,,,,,,,N,N,N,,,,,N,,,,";
    //
    //        ValidateResult r = v.validateRecord(0, str);
    //
    //        System.out.println(r);

    // Validator v = new Validator("jdbc/CSPD", "99999");

// System.out.println("Start");
//            Class.forName("oracle.jdbc.OracleDriver");
//                    Connection conn =
//                        DriverManager.getConnection("jdbc:oracle:thin:@10.28.120.252:1521:CSPD",
//                                                    "cspapp01", "cspapp01");
//            Connection conn =
//                DriverManager.getConnection("jdbc:oracle:thin:@//hw-sls-rac.syniverse.com:1521/CSPT",
//                                            "cspski", "cspski");
    
//            System.out.println("got connection");
            
//            Validator v = new Validator("jdbc:oracle:thin:@//hw-sls-rac.syniverse.com:1521/CSPT",
//                                            "cspski", "cspski","02075",null);
//
//    
//            String ss =
//                "A,GSM,12sdf,,,,,,,,,,,,,,,,,,N,N,N,,,,,N,,,,";
//            ValidateResult r = v.validateRecord(1, ss);
//    
//            System.out.println(r);
//    

    //        int max = 5060;
    //        String[] a = new String[max];
    //        for (int i = 0; i < max; i++) {
    //            a[i] = String.valueOf(i);
    //        }
    //
    //
    //        StringBuilder sb = new StringBuilder();
    //        sb.append("select subscriber_key from subscriber where billing_id=? and subscriber_key in (");
    //        for (int j = 0; j < 1000; j++) {
    //            if (j > 0) {
    //                sb.append(",");
    //            }
    //
    //            sb.append("?");
    //        }
    //        sb.append(")");
    //
    //        //
    //        long start = System.currentTimeMillis();
    //
    //        boolean[] res = v.areRecordsExist(conn, a);
    //
    //        System.out.println((System.currentTimeMillis() - start) +
    //                           " ms - statement IN");
    //
    //        //
    //        long start2 = System.currentTimeMillis();
    //
    //        boolean[] res2 =
    //            v.areRecordsExist(conn, conn.prepareStatement(sb.toString()), a);
    //
    //        System.out.println((System.currentTimeMillis() - start2) +
    //                           " ms - prepared IN");
    //
    //        //
    //        long start4 = System.currentTimeMillis();
    //
    //        boolean[] res4 = v.areRecordsExistOR(conn, a);
    //
    //        System.out.println((System.currentTimeMillis() - start4) +
    //                           " ms - statement OR");
    //
    //        //
    //        sb.setLength(0);
    //        sb.append("select subscriber_key from subscriber where billing_id=? and (");
    //
    //        for (int j = 0; j < 1000; j++) {
    //            if (j > 0) {
    //                sb.append(" or ");
    //            }
    //
    //            sb.append("subscriber_key=?");
    //        }
    //        sb.append(")");
    //
    //        long start3 = System.currentTimeMillis();
    //
    //        boolean[] res3 =
    //            v.areRecordsExistOR(conn, conn.prepareStatement(sb.toString()),
    //                                1000, a);
    //
    //        System.out.println((System.currentTimeMillis() - start3) +
    //                           " ms - prepared OR");
    //
    //        System.out.println("--------------");
    //        System.out.println("S IN: " + v.getStatmentIN());
    //        System.out.println("P IN: " + v.getPreparedStatmentIN());
    //        System.out.println("S OR: " + v.getStatmentOR());
    //        System.out.println("P OR: " + v.getPreparedStatmentOR());

    //        String line = null;
    //        ValidateResult result = null;
    //
    //        BufferedReader reader =
    //            new BufferedReader(new FileReader(new File("E:\\sample_data\\02075_0818201301010000")));
    //
    //        // skip header
    //        reader.readLine();
    //        int i = 0;
    //        while ((line = reader.readLine()) != null) {
    //            i++;
    //            try {
    //                result = v.validateRecord(i, line);
    //            } catch (Exception e) {
    //                System.out.println(e);
    //            }
    //            if (!result.isSuccess()) {
    //                System.out.println("Invalid " + i + ": " +
    //                                   result.getErrorMessage());
    //            }
    //        }
    //        reader.close();
    //
    //        v.close();
    //
    //        System.out.println("complete");
  //      }

}
