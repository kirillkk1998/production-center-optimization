package util;

import model.ProductionLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

class ExcelReaderTest {
    
    @BeforeEach
    void setUp() throws IOException {
        // Генерируем тестовый файл перед каждым тестом
        ExcelTestGenerator.generateTestFile("test_input.xlsx");
    }
    
    @Test
    void testReadFromExcel() throws IOException {
        ProductionLine line = ExcelReader.readFromExcel("test_input.xlsx");
        assertNotNull(line);
        
        // Проверяем, что все центры были созданы
        List<ProductionEvent> events = line.getEvents();
        assertTrue(events.isEmpty()); // События появятся только после симуляции
        
        // Запускаем симуляцию
        line.simulate();
        
        // Проверяем результаты
        assertFalse(events.isEmpty());
        assertEquals(10, events.get(events.size() - 1).getTime(), 0.1);
    }
    
    @Test
    void testInvalidExcelFile() {
        assertThrows(IOException.class, () -> {
            ExcelReader.readFromExcel("non_existent_file.xlsx");
        });
    }
} 