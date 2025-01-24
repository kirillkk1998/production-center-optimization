package util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTestGenerator {
    public static void generateTestFile(String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Создаем лист с производственными центрами
            Sheet centersSheet = workbook.createSheet("Centers");
            createHeader(centersSheet, new String[]{"Название центра", "Время обработки", "Макс. кол-во рабочих"});
            
            // Добавляем тестовые центры
            addRow(centersSheet, 1, new Object[]{"Центр 1", 2.0, 2});
            addRow(centersSheet, 2, new Object[]{"Центр 2", 3.0, 1});
            addRow(centersSheet, 3, new Object[]{"Центр 3", 2.5, 2});
            
            // Создаем лист со связями
            Sheet connectionsSheet = workbook.createSheet("Connections");
            createHeader(connectionsSheet, new String[]{"От центра", "К центру"});
            
            // Добавляем связи
            addRow(connectionsSheet, 1, new Object[]{"Центр 1", "Центр 2"});
            addRow(connectionsSheet, 2, new Object[]{"Центр 2", "Центр 3"});
            
            // Создаем лист с начальными данными
            Sheet initialDataSheet = workbook.createSheet("Initial Data");
            createHeader(initialDataSheet, new String[]{"Кол-во рабочих", "Начальный центр", "Кол-во деталей"});
            addRow(initialDataSheet, 1, new Object[]{5, "Центр 1", 10});
            
            // Сохраняем файл
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
    
    private static void createHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }
    
    private static void addRow(Sheet sheet, int rowNum, Object[] values) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            if (values[i] instanceof String) {
                cell.setCellValue((String) values[i]);
            } else if (values[i] instanceof Double) {
                cell.setCellValue((Double) values[i]);
            } else if (values[i] instanceof Integer) {
                cell.setCellValue((Integer) values[i]);
            }
        }
    }
} 