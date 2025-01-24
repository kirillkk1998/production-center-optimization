package util;

import model.ProductionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter {
    public static void writeResults(String filePath, List<ProductionEvent> events) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Записываем заголовок
            writer.write("Time, ProductionCenter, WorkersCount, BufferCount\n");
            
            // Записываем данные
            for (ProductionEvent event : events) {
                writer.write(String.format("%.1f, %s, %d, %d\n",
                    event.getTime(),
                    event.getProductionCenter(),
                    event.getWorkersCount(),
                    event.getBufferCount()
                ));
            }
        }
    }
} 