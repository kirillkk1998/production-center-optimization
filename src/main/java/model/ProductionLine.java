package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProductionLine {
    private final List<ProductionCenter> centers;
    private final int totalWorkers;
    private double currentTime;
    private List<ProductionEvent> events;
    
    public ProductionLine(Collection<ProductionCenter> centers, int totalWorkers) {
        this.centers = new ArrayList<>(centers);
        this.totalWorkers = totalWorkers;
        this.currentTime = 0.0;
        this.events = new ArrayList<>();
    }
    
    public void simulate() {
        boolean isProcessing = true;
        
        while (isProcessing) {
            // Распределяем рабочих оптимально между центрами
            optimizeWorkerDistribution();
            
            // Обрабатываем детали в каждом центре
            processDetailsInCenters();
            
            // Проверяем завершение всех работ
            isProcessing = !isProductionComplete();
            
            // Увеличиваем время
            currentTime += 1.0;
            
            // Сохраняем состояние для отчета
            saveCurrentState();
        }
    }
    
    private void optimizeWorkerDistribution() {
        // Распределяем рабочих на основе размера буфера и времени обработки
        int remainingWorkers = totalWorkers;
        
        // Сначала определяем приоритеты центров
        List<ProductionCenter> prioritizedCenters = getPrioritizedCenters();
        
        // Распределяем рабочих согласно приоритетам
        for (ProductionCenter center : prioritizedCenters) {
            if (remainingWorkers <= 0) break;
            
            int optimalWorkers = calculateOptimalWorkers(center, remainingWorkers);
            center.setCurrentWorkers(optimalWorkers);
            remainingWorkers -= optimalWorkers;
        }
    }
    
    private List<ProductionCenter> getPrioritizedCenters() {
        List<ProductionCenter> prioritized = new ArrayList<>(centers);
        prioritized.sort((c1, c2) -> {
            // Приоритет на основе размера буфера и времени обработки
            double priority1 = c1.getBufferSize() * c1.getProcessingTime();
            double priority2 = c2.getBufferSize() * c2.getProcessingTime();
            return Double.compare(priority2, priority1);
        });
        return prioritized;
    }
    
    private int calculateOptimalWorkers(ProductionCenter center, int availableWorkers) {
        int maxNeeded = Math.min(center.getMaxWorkers(), center.getBufferSize());
        return Math.min(maxNeeded, availableWorkers);
    }
    
    private void processDetailsInCenters() {
        for (ProductionCenter center : centers) {
            center.processDetails(currentTime);
        }
    }
    
    private boolean isProductionComplete() {
        return centers.stream().allMatch(center -> 
            center.getBufferSize() == 0 && center.getDetailsInProcessCount() == 0);
    }
    
    private void saveCurrentState() {
        for (ProductionCenter center : centers) {
            events.add(new ProductionEvent(
                currentTime,
                center.getName(),
                center.getCurrentWorkers(),
                center.getBufferSize()
            ));
        }
    }
    
    public List<ProductionEvent> getEvents() {
        return events;
    }
    
    public double getTotalTime() {
        return currentTime;
    }
    
    public Collection<ProductionCenter> getCenters() {
        return Collections.unmodifiableList(centers);
    }
    
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append(String.format("Total simulation time: %.1f minutes\n", currentTime));
        stats.append(String.format("Total workers: %d\n", totalWorkers));
        
        int totalProcessed = centers.stream()
            .filter(ProductionCenter::isFinal)
            .mapToInt(ProductionCenter::getTotalDetailsProcessed)
            .sum();
        
        stats.append(String.format("Total details processed: %d\n", totalProcessed));
        return stats.toString();
    }
} 