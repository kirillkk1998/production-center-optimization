package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class ProductionLineTest {
    
    @Test
    void testSimpleProduction() {
        // Создаем простую линию из двух центров
        ProductionCenter center1 = new ProductionCenter("Center 1", 2.0, 1);
        ProductionCenter center2 = new ProductionCenter("Center 2", 3.0, 1);
        
        center1.setInitial(true);
        center2.setFinal(true);
        center1.addNextCenter(center2);
        
        // Добавляем одну деталь в начальный центр
        center1.addDetailToBuffer(new Detail(1));
        
        ProductionLine line = new ProductionLine(Arrays.asList(center1, center2), 2);
        
        // Запускаем симуляцию
        line.simulate();
        
        // Проверяем результаты
        assertTrue(line.getEvents().size() > 0);
        assertEquals(0, center1.getBufferSize());
        assertEquals(0, center2.getBufferSize());
        assertEquals(0, center1.getDetailsInProcessCount());
        assertEquals(0, center2.getDetailsInProcessCount());
    }

    @Test
    void testComplexProduction() {
        // Создаем сложную производственную линию
        ProductionCenter center1 = new ProductionCenter("Center 1", 2.0, 2);
        ProductionCenter center2 = new ProductionCenter("Center 2", 1.5, 1);
        ProductionCenter center3 = new ProductionCenter("Center 3", 1.0, 2);
        ProductionCenter center4 = new ProductionCenter("Center 4", 2.0, 1);
        
        // Настраиваем связи
        center1.setInitial(true);
        center1.addNextCenter(center2);
        center1.addNextCenter(center3);
        center2.addNextCenter(center4);
        center3.addNextCenter(center4);
        center4.setFinal(true);
        
        // Добавляем начальные детали
        for (int i = 0; i < 10; i++) {
            center1.addDetailToBuffer(new Detail(i));
        }
        
        ProductionLine line = new ProductionLine(Arrays.asList(center1, center2, center3, center4), 6);
        line.simulate();
        
        // Проверяем результаты
        assertEquals(0, center1.getBufferSize());
        assertEquals(0, center2.getBufferSize());
        assertEquals(0, center3.getBufferSize());
        assertEquals(0, center4.getBufferSize());
        
        assertEquals(10, center1.getTotalDetailsProcessed());
        assertEquals(5, center2.getTotalDetailsProcessed()); // Примерно половина деталей
        assertEquals(5, center3.getTotalDetailsProcessed()); // Примерно половина деталей
        assertEquals(10, center4.getTotalDetailsProcessed());
    }
} 