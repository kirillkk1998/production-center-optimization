package util;

import model.Detail;
import model.ProductionCenter;
import model.ProductionLine;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelReader {
    public static ProductionLine readFromExcel(String filePath) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(new File(filePath))) {
            // Читаем информацию о производственных центрах
            Sheet centersSheet = workbook.getSheetAt(0);
            Map<String, ProductionCenter> centers = readProductionCenters(centersSheet);
            
            // Читаем связи между центрами
            Sheet connectionsSheet = workbook.getSheetAt(1);
            readConnections(connectionsSheet, centers);
            
            // Читаем начальные данные
            Sheet initialDataSheet = workbook.getSheetAt(2);
            int totalWorkers = readInitialData(initialDataSheet, centers);
            
            validateCenters(centers);
            
            return new ProductionLine(centers.values(), totalWorkers);
        }
    }
    
    private static Map<String, ProductionCenter> readProductionCenters(Sheet sheet) {
        Map<String, ProductionCenter> centers = new HashMap<>();
        
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            String name = getStringCellValue(row.getCell(0));
            double processingTime = getNumericCellValue(row.getCell(1));
            int maxWorkers = (int) getNumericCellValue(row.getCell(2));
            
            validateInputData(processingTime, maxWorkers, 0, 0);
            
            centers.put(name, new ProductionCenter(name, processingTime, maxWorkers));
        }
        
        return centers;
    }
    
    private static void readConnections(Sheet sheet, Map<String, ProductionCenter> centers) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            String fromCenter = getStringCellValue(row.getCell(0));
            String toCenter = getStringCellValue(row.getCell(1));
            
            ProductionCenter from = centers.get(fromCenter);
            ProductionCenter to = centers.get(toCenter);
            
            if (from != null && to != null) {
                from.addNextCenter(to);
            }
        }
    }
    
    private static int readInitialData(Sheet sheet, Map<String, ProductionCenter> centers) {
        Row firstRow = sheet.getRow(1);
        if (firstRow == null) return 0;
        
        // Читаем общее количество рабочих
        int totalWorkers = (int) getNumericCellValue(firstRow.getCell(0));
        
        // Читаем начальный центр и количество деталей
        String initialCenterName = getStringCellValue(firstRow.getCell(1));
        int detailsCount = (int) getNumericCellValue(firstRow.getCell(2));
        
        validateInputData(0, 0, totalWorkers, detailsCount);
        
        ProductionCenter initialCenter = centers.get(initialCenterName);
        if (initialCenter != null) {
            initialCenter.setInitial(true);
            // Добавляем начальные детали
            for (int i = 0; i < detailsCount; i++) {
                initialCenter.addDetailToBuffer(new Detail(i));
            }
        }
        
        return totalWorkers;
    }
    
    private static void validateCenters(Map<String, ProductionCenter> centers) {
        // Проверяем наличие ровно одного начального и конечного центра
        long initialCount = centers.values().stream().filter(ProductionCenter::isInitial).count();
        
        centers.values().forEach(center -> {
            if (center.getNextCenters().isEmpty() && !center.isInitial()) {
                center.setFinal(true);
            }
        });
        
        long finalCount = centers.values().stream().filter(ProductionCenter::isFinal).count();
        
        if (initialCount != 1 || finalCount != 1) {
            throw new IllegalStateException(
                "Must have exactly one initial center and one final center. " +
                "Found: " + initialCount + " initial and " + finalCount + " final centers.");
        }
        
        // Проверяем отсутствие циклов
        checkForCycles(centers);
    }
    
    private static void checkForCycles(Map<String, ProductionCenter> centers) {
        Map<ProductionCenter, Boolean> visited = new HashMap<>();
        Map<ProductionCenter, Boolean> recursionStack = new HashMap<>();
        
        for (ProductionCenter center : centers.values()) {
            if (hasCycle(center, visited, recursionStack)) {
                throw new IllegalStateException("Cycle detected in production centers configuration");
            }
        }
    }
    
    private static boolean hasCycle(ProductionCenter center, 
                                  Map<ProductionCenter, Boolean> visited,
                                  Map<ProductionCenter, Boolean> recursionStack) {
        if (recursionStack.getOrDefault(center, false)) {
            return true;
        }
        
        if (visited.getOrDefault(center, false)) {
            return false;
        }
        
        visited.put(center, true);
        recursionStack.put(center, true);
        
        for (ProductionCenter next : center.getNextCenters()) {
            if (hasCycle(next, visited, recursionStack)) {
                return true;
            }
        }
        
        recursionStack.put(center, false);
        return false;
    }
    
    private static String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.getStringCellValue();
    }
    
    private static double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        return cell.getNumericCellValue();
    }
    
    private static void validateInputData(double processingTime, int maxWorkers, int totalWorkers, int detailsCount) {
        if (processingTime <= 0 || processingTime > 10) {
            throw new IllegalArgumentException("Processing time must be between 0 and 10 minutes");
        }
        if (maxWorkers <= 0) {
            throw new IllegalArgumentException("Max workers must be positive");
        }
        if (totalWorkers <= 0 || totalWorkers > 40) {
            throw new IllegalArgumentException("Total workers must be between 1 and 40");
        }
        if (detailsCount <= 0 || detailsCount > 2000) {
            throw new IllegalArgumentException("Details count must be between 1 and 2000");
        }
    }
    
    private static void validateCentersCount(int count) {
        if (count > 20) {
            throw new IllegalArgumentException("Maximum number of production centers is 20");
        }
    }
} 