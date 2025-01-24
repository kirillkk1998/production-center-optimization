package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Представляет производственный центр в системе.
 * Обрабатывает детали с помощью назначенных рабочих.
 */
public class ProductionCenter {
    private static final Logger logger = Logger.getLogger(ProductionCenter.class.getName());
    
    private String name;
    private double processingTime;
    private int maxWorkers;
    private Queue<Detail> buffer;
    private List<ProductionCenter> nextCenters;
    private int currentWorkers;
    private boolean isInitial;
    private boolean isFinal;
    
    // Храним информацию о деталях в обработке
    private Map<Detail, Double> detailsInProcess;
    private int nextCenterIndex; // Для равномерного распределения деталей
    
    // Статистика
    private int totalDetailsProcessed;
    private int maxBufferSize;
    private double totalWorkTime;
    private double simulationTime;
    
    public ProductionCenter(String name, double processingTime, int maxWorkers) {
        this.name = name;
        this.processingTime = processingTime;
        this.maxWorkers = maxWorkers;
        this.buffer = new LinkedList<>();
        this.nextCenters = new ArrayList<>();
        this.currentWorkers = 0;
        this.isInitial = false;
        this.isFinal = false;
        this.detailsInProcess = new HashMap<>();
        this.nextCenterIndex = 0;
        
        this.totalDetailsProcessed = 0;
        this.maxBufferSize = 0;
        this.totalWorkTime = 0;
        this.simulationTime = 0;
    }
    
    // Геттеры и сеттеры
    public String getName() {
        return name;
    }
    
    public double getProcessingTime() {
        return processingTime;
    }
    
    public int getMaxWorkers() {
        return maxWorkers;
    }
    
    public int getCurrentWorkers() {
        return currentWorkers;
    }
    
    public void setCurrentWorkers(int workers) {
        this.currentWorkers = workers;
    }
    
    public boolean isInitial() {
        return isInitial;
    }
    
    public void setInitial(boolean initial) {
        isInitial = initial;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
    
    public void addNextCenter(ProductionCenter center) {
        nextCenters.add(center);
    }
    
    public void addDetailToBuffer(Detail detail) {
        buffer.add(detail);
    }
    
    public int getBufferSize() {
        return buffer.size();
    }
    
    public void processDetails(double currentTime) {
        this.simulationTime = currentTime;
        maxBufferSize = Math.max(maxBufferSize, buffer.size());
        
        if (currentWorkers > 0) {
            totalWorkTime += 1.0;
        }
        
        // Завершаем обработку готовых деталей
        List<Detail> completedDetails = new ArrayList<>();
        detailsInProcess.forEach((detail, startTime) -> {
            if (currentTime - startTime >= processingTime) {
                completedDetails.add(detail);
                totalDetailsProcessed++;
                logger.fine(String.format("Center %s completed detail %d at time %.1f",
                    name, detail.getId(), currentTime));
            }
        });
        
        // Отправляем готовые детали в следующие центры
        for (Detail detail : completedDetails) {
            detailsInProcess.remove(detail);
            if (!isFinal) {
                sendDetailToNextCenter(detail);
            }
        }
        
        // Начинаем обработку новых деталей
        while (detailsInProcess.size() < currentWorkers && !buffer.isEmpty()) {
            Detail detail = buffer.poll();
            detailsInProcess.put(detail, currentTime);
        }
    }
    
    private void sendDetailToNextCenter(Detail detail) {
        if (nextCenters.isEmpty()) return;
        
        // Распределяем детали равномерно между следующими центрами
        ProductionCenter nextCenter = nextCenters.get(nextCenterIndex);
        nextCenter.addDetailToBuffer(detail);
        
        nextCenterIndex = (nextCenterIndex + 1) % nextCenters.size();
    }
    
    public int getDetailsInProcessCount() {
        return detailsInProcess.size();
    }
    
    public List<ProductionCenter> getNextCenters() {
        return nextCenters;
    }
    
    // Методы для получения статистики
    public double getAverageLoad() {
        return simulationTime > 0 ? totalWorkTime / simulationTime : 0;
    }
    
    public int getTotalDetailsProcessed() {
        return totalDetailsProcessed;
    }
    
    public int getMaxBufferSize() {
        return maxBufferSize;
    }
    
    public String getStatistics() {
        return String.format(
            "Center: %s\n" +
            "Total details processed: %d\n" +
            "Average load: %.2f%%\n" +
            "Max buffer size: %d\n" +
            "Total work time: %.1f minutes\n",
            name,
            totalDetailsProcessed,
            getAverageLoad() * 100,
            maxBufferSize,
            totalWorkTime
        );
    }

    /**
     * Вычисляет приоритет центра для распределения рабочих.
     * Приоритет основан на размере буфера и времени обработки.
     *
     * @return значение приоритета
     */
    public double calculatePriority() {
        return getBufferSize() * processingTime;
    }

    /**
     * Возвращает оптимальное количество рабочих для текущего состояния центра.
     * Учитывает размер буфера и максимальное количество рабочих.
     *
     * @param availableWorkers доступное количество рабочих
     * @return оптимальное количество рабочих
     */
    public int getOptimalWorkersCount(int availableWorkers) {
        return Math.min(Math.min(maxWorkers, buffer.size()), availableWorkers);
    }
} 