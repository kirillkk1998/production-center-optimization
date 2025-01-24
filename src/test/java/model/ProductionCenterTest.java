package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductionCenterTest {
    
    @Test
    void testInitialState() {
        ProductionCenter center = new ProductionCenter("Test Center", 5.0, 3);
        
        assertEquals("Test Center", center.getName());
        assertEquals(5.0, center.getProcessingTime());
        assertEquals(3, center.getMaxWorkers());
        assertEquals(0, center.getCurrentWorkers());
        assertEquals(0, center.getBufferSize());
        assertFalse(center.isInitial());
        assertFalse(center.isFinal());
    }
    
    @Test
    void testAddDetailToBuffer() {
        ProductionCenter center = new ProductionCenter("Test Center", 5.0, 3);
        Detail detail = new Detail(1);
        
        center.addDetailToBuffer(detail);
        
        assertEquals(1, center.getBufferSize());
    }
    
    @Test
    void testProcessDetails() {
        ProductionCenter center = new ProductionCenter("Test Center", 2.0, 2);
        center.setCurrentWorkers(1);
        
        // Добавляем деталь в буфер
        center.addDetailToBuffer(new Detail(1));
        
        // Начинаем обработку
        center.processDetails(0.0);
        assertEquals(0, center.getBufferSize());
        assertEquals(1, center.getDetailsInProcessCount());
        
        // Проверяем, что деталь еще не готова
        center.processDetails(1.0);
        assertEquals(1, center.getDetailsInProcessCount());
        
        // Проверяем, что деталь готова
        center.processDetails(2.0);
        assertEquals(0, center.getDetailsInProcessCount());
    }

    @Test
    void testMultipleWorkers() {
        ProductionCenter center = new ProductionCenter("Test Center", 2.0, 3);
        center.setCurrentWorkers(2);
        
        // Добавляем три детали в буфер
        center.addDetailToBuffer(new Detail(1));
        center.addDetailToBuffer(new Detail(2));
        center.addDetailToBuffer(new Detail(3));
        
        // Начинаем обработку
        center.processDetails(0.0);
        assertEquals(1, center.getBufferSize());
        assertEquals(2, center.getDetailsInProcessCount());
        
        // Проверяем статистику
        assertEquals(0.0, center.getAverageLoad());
        assertEquals(0, center.getTotalDetailsProcessed());
        
        // Завершаем обработку первых двух деталей
        center.processDetails(2.0);
        assertEquals(1, center.getBufferSize());
        assertEquals(0, center.getDetailsInProcessCount());
        assertEquals(2, center.getTotalDetailsProcessed());
    }

    @Test
    void testDetailDistribution() {
        ProductionCenter center1 = new ProductionCenter("Center 1", 1.0, 1);
        ProductionCenter center2 = new ProductionCenter("Center 2", 1.0, 1);
        ProductionCenter center3 = new ProductionCenter("Center 3", 1.0, 1);
        
        // Создаем разветвление
        center1.addNextCenter(center2);
        center1.addNextCenter(center3);
        
        center1.setCurrentWorkers(1);
        
        // Добавляем детали в первый центр
        for (int i = 0; i < 4; i++) {
            center1.addDetailToBuffer(new Detail(i));
        }
        
        // Обрабатываем детали
        center1.processDetails(0.0);
        center1.processDetails(1.0); // Первая деталь готова
        center1.processDetails(2.0); // Вторая деталь готова
        
        // Проверяем равномерное распределение
        assertEquals(1, center2.getBufferSize());
        assertEquals(1, center3.getBufferSize());
    }
} 