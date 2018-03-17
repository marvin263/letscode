package com.tntrip;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by libing2 on 2016/12/13.
 */
public class GenerateMappingByExcel {
    public static final int FIELD_NAME = 0;
    public static final int FIELD_TYPE = 1;
    public static final int FIELD_AVGLEN = 2;
    public static final int FIELD_MAXLEN = 3;
    public static final int FIELD_MUST_ANALYZE = 4;
    public static final int FIELD_DESC = 5;

    private static class SqlRowValue {
        private String fieldName;
        private String fieldType;
        private String avgLen;
        private String maxLen;
        private boolean mustAnalyze;
        private String description;
    }

    
    public static void main(String[] args) throws Exception {
        String fullPath = "D:/fast/gitworkspace/mob-java-esservice/esservice-site/src/main/resources/es_mapping_schema/order_info@mall_order.xlsx";
        String indexName = "mall_order";
        String mappingName = "order_info";
        FileInputStream fis = new FileInputStream(fullPath);
        // Use an InputStream, needs more memory
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIter = sheet.rowIterator();
        List<SqlRowValue> list = new ArrayList<>();

        int rowIndex = 0;
        while (rowIter.hasNext()) {
            rowIndex++;
            System.out.println("Processing row[" + rowIndex + "] ...");
            if (rowIndex == 0) {
                rowIter.next();
                continue;
            }
            SqlRowValue srv = new SqlRowValue();
            list.add(srv);
            traversalOneRow(rowIndex, rowIter.next(), srv);
        }
        fis.close();

        String strMappingJson = createMapping(indexName, mappingName, list);

        String mappingJsonAndPostParam = strMappingJson + "\n\n\n" + createPostParam(strMappingJson);

        try (FileWriter fw = new FileWriter(new File(fullPath).getAbsolutePath() + ".json");
             BufferedWriter bw = new BufferedWriter(fw);
        ) {
            bw.write(mappingJsonAndPostParam);
        }

    }

    private static String createPostParam(String strMappingJson) {
        SchemaVO s = new SchemaVO();
        s.setSchemaDesc("社区系统帖子");
        s.setIndexName("community_post");
        s.setMappingName("postinfo");

        s.setCreateSchemaJson(strMappingJson);

        s.setCustomerId(8699);
        s.setCustomerName("李兵2");
        return JSON.toJSONString(s, SerializerFeature.PrettyFormat);
    }

    private static void traversalOneRow(int rowIndex, Row oneRow, SqlRowValue srv) {
        Iterator<Cell> cellIterator = oneRow.cellIterator();
        int col = 0;
        while (cellIterator.hasNext()) {
            Cell aCell = cellIterator.next();
            String rawV = "";
            CellType cellTypeEnum = aCell.getCellTypeEnum();
            switch (cellTypeEnum) {
                case NUMERIC:
                    double d = aCell.getNumericCellValue();
                    rawV = Integer.toString(Double.valueOf(d).intValue());
                    break;
                case STRING:
                    rawV = aCell.getStringCellValue();
                    break;
            }
            String actualV = trim2Empty(rawV);
            System.out.println("    Processing cell[" + rowIndex + "][" + col + "] ...");
            switch (col) {
                case FIELD_NAME:
                    srv.fieldName = actualV;
                    break;
                case FIELD_TYPE:
                    srv.fieldType = actualV;
                    break;
                case FIELD_AVGLEN:
                    srv.avgLen = actualV;
                    break;
                case FIELD_MAXLEN:
                    srv.maxLen = actualV;
                    break;
                case FIELD_DESC:
                    srv.description = actualV;
                    break;
                case FIELD_MUST_ANALYZE:
                    srv.mustAnalyze = str2boolean(actualV);
                    break;
            }
            col++;
        }
    }

    private static String createMapping(String indexName, String mappingName, List<SqlRowValue> list) {
        Map<String, Object> finalMap = new LinkedHashMap<>();
        finalMap.put("indexName", indexName);
        finalMap.put("mappingName", mappingName);
        LinkedHashMap<String, Object> mappingsValueMap = new LinkedHashMap<>();
        finalMap.put("mappings", mappingsValueMap);

        LinkedHashMap<String, Object> allAndProperties = new LinkedHashMap<>();
        mappingsValueMap.put(mappingName, allAndProperties);
        allAndProperties.put("dynamic", "false");
        
        LinkedHashMap<String, Object> allMap = new LinkedHashMap<>();
        allAndProperties.put("_all", allMap);
        allMap.put("enabled", Boolean.FALSE);

        LinkedHashMap<String, Object> propertiesMap = new LinkedHashMap<>();
        allAndProperties.put("properties", propertiesMap);

        for (SqlRowValue srv : list) {
            String finalFieldType = extractFieldType(srv.fieldType);
            switch (finalFieldType) {
                case "boolean":
                case "integer":
                case "long":
                case "short":
                    createBasicTypeElement(srv, finalFieldType, propertiesMap);
                    break;
                case "date":
                    createDateElement(srv, finalFieldType, propertiesMap);
                    break;
                case "string":
                    if (srv.mustAnalyze) {
                        createAnalyzedStringElement(srv, finalFieldType, propertiesMap);
                    } else {
                        createNotAnalyzedStringElement(srv, finalFieldType, propertiesMap);
                    }
                    break;
                default:
                    break;
            }
        }
        return JSON.toJSONString(finalMap, SerializerFeature.PrettyFormat);
    }

    private static String extractFieldType(String rawFieldType) {
        String trimmed = trim2Empty(rawFieldType).toLowerCase();
        if (trimmed.equals("") || trimmed.equals("[]")) {
            throw new RuntimeException("Field type cannot be empty. rawFieldType=" + rawFieldType);
        }
        if (trimmed.endsWith("[]")) {
            String finalFieldType = trimmed.substring(0, trimmed.length() - 2);
            return finalFieldType;
        }
        return trimmed;
    }

    private static void createBasicTypeElement(SqlRowValue numRowValue, String finalFieldType, LinkedHashMap<String, Object> propertiesMap) {
        LinkedHashMap<String, Object> oneFieldValueMap = new LinkedHashMap<>();
        propertiesMap.put(numRowValue.fieldName, oneFieldValueMap);
        oneFieldValueMap.put("type", finalFieldType);

    }

    private static void createNotAnalyzedStringElement(SqlRowValue numRowValue, String finalFieldType, LinkedHashMap<String, Object> propertiesMap) {
        LinkedHashMap<String, Object> notAnalyzeStringFieldValueMap = new LinkedHashMap<>();
        propertiesMap.put(numRowValue.fieldName, notAnalyzeStringFieldValueMap);
        notAnalyzeStringFieldValueMap.put("type", finalFieldType);
        notAnalyzeStringFieldValueMap.put("index", "not_analyzed");
    }

    private static void createAnalyzedStringElement(SqlRowValue numRowValue, String finalFieldType, LinkedHashMap<String, Object> propertiesMap) {
        LinkedHashMap<String, Object> analyzeStringFieldValueMap = new LinkedHashMap<>();
        propertiesMap.put(numRowValue.fieldName, analyzeStringFieldValueMap);

        analyzeStringFieldValueMap.put("type", finalFieldType);
        analyzeStringFieldValueMap.put("index", "analyzed");
        analyzeStringFieldValueMap.put("analyzer", "ik_smart");
        analyzeStringFieldValueMap.put("search_analyzer", "ik_smart");
        analyzeStringFieldValueMap.put("store", "no");

        LinkedHashMap<String, Object> fieldsValueMap = new LinkedHashMap<>();
        analyzeStringFieldValueMap.put("fields", fieldsValueMap);

        LinkedHashMap<String, Object> rawValueMap = new LinkedHashMap<>();
        fieldsValueMap.put("raw", rawValueMap);

        rawValueMap.put("type", finalFieldType);
        rawValueMap.put("index", "not_analyzed");
        rawValueMap.put("store", "no");
    }

    private static void createDateElement(SqlRowValue numRowValue, String finalFieldType, LinkedHashMap<String, Object> propertiesMap) {
        Map<String, Object> dateFieldValueMap = new LinkedHashMap<>();
        propertiesMap.put(numRowValue.fieldName, dateFieldValueMap);
        dateFieldValueMap.put("type", finalFieldType);
        dateFieldValueMap.put("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis");
    }


    public static boolean str2boolean(String str) {
        String[] array = {"true", "yes", "y"};
        for (String aVal : array) {
            if (aVal.equalsIgnoreCase(trim2Empty(str))) {
                return true;
            }
        }
        return false;
    }

    public static String trim2Empty(String str) {
        return str == null ? "" : str.trim();
    }
}
