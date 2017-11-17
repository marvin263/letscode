package com.tntrip;

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
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by libing2 on 2016/12/13.
 */
public class ReadExcel {

    public static final String sql = "insert into mob_chat_serviceaccount_keyword_autoreply(serviceaccount_type_id, keyword_text, auto_reply_text, activate_online_consultation, create_time, del_flag) values\n" +
            "(${serviceaccount_type_id},'${keyword_text}','${auto_reply_text}',${activate_online_consultation}, CURRENT_TIMESTAMP, 0);\n";

    private static class SqlRowValue {
        private String serviceaccount_type_id;
        private String keyword_text;
        private String auto_reply_text;
        private String activate_online_consultation;
    }


    public static void main(String[] args) throws Exception {
        String fullPath = "C:/关键词配置模板-机票.xlsx";
        FileInputStream fis = new FileInputStream(fullPath);
        // Use an InputStream, needs more memory
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIter = sheet.rowIterator();
        List<SqlRowValue> list = new ArrayList<>();
        int rowIndex = 0;
        while (rowIter.hasNext()) {
            if (rowIndex == 0) {
                rowIter.next();
                rowIndex++;
                continue;
            }
            rowIndex++;
            SqlRowValue srv = new SqlRowValue();
            list.add(srv);

            Row next = rowIter.next();
            Iterator<Cell> cellIterator = next.cellIterator();
            int col = 0;
            while (cellIterator.hasNext()) {
                Cell aCell = cellIterator.next();
                String s = "";
                CellType cellTypeEnum = aCell.getCellTypeEnum();
                switch (cellTypeEnum) {
                    case NUMERIC:
                        double d = aCell.getNumericCellValue();
                        s = Double.valueOf(d).intValue() + "";
                        break;
                    case STRING:
                        s = aCell.getStringCellValue();
                        break;
                }

                switch (col) {
                    case 0:
                        srv.serviceaccount_type_id = s;
                        break;
                    case 1:
                        srv.keyword_text = s;
                        break;
                    case 2:
                        srv.auto_reply_text = replaceEnter(s);
                        break;
                    case 3:
                        srv.activate_online_consultation = s;
                        break;
                }
                col++;
            }
        }
        fis.close();

        String finalSQL = generateSQL(list);
        try (FileWriter fw = new FileWriter(new File(fullPath).getAbsolutePath()+".sql");
             BufferedWriter bw = new BufferedWriter(fw);
        ) {
            bw.write(finalSQL);
        }

    }

    private static String generateSQL(List<SqlRowValue> list) {
        StringBuilder sb = new StringBuilder();
        for (SqlRowValue srv : list) {
            String tmp = sql.replaceAll("\\Q" + "${serviceaccount_type_id}" + "\\E", srv.serviceaccount_type_id);
            tmp = tmp.replaceAll("\\Q" + "${keyword_text}" + "\\E", Matcher.quoteReplacement(srv.keyword_text));
            tmp = tmp.replaceAll("\\Q" + "${auto_reply_text}" + "\\E", Matcher.quoteReplacement(srv.auto_reply_text));
            tmp = tmp.replaceAll("\\Q" + "${activate_online_consultation}" + "\\E", srv.activate_online_consultation);
            sb.append(tmp);
        }
        return sb.toString();
    }


    private static String replaceEnter(String str) {
        if (str == null || str.equals("")) {
            return str;
        }
        str = str.trim();
        str = str.replaceAll("\r\n", "\n");
        str = str.replaceAll("\n", "\\\\n");
        System.out.println(str);
        return str;
    }
}
