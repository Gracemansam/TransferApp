package com.patientRecTransferApp.serviceImpl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ExcelService {

  public  String readExcelFile(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    content.append(getCellValueAsString(cell)).append("\t");
                }
                content.append("\n");
            }
        }
        return content.toString();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }


   public byte[] writeExcelFile(String data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("DecryptedData");
            String[] rows = data.split("\n");
            for (int i = 0; i < rows.length; i++) {
                Row row = sheet.createRow(i);
                String[] cells = rows[i].split("\t");
                for (int j = 0; j < cells.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(cells[j]);
                }
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }
}