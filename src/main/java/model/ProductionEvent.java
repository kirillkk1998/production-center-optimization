package model;

public class ProductionEvent {
    private final double time;
    private final String productionCenter;
    private final int workersCount;
    private final int bufferCount;
    
    public ProductionEvent(double time, String productionCenter, int workersCount, int bufferCount) {
        this.time = time;
        this.productionCenter = productionCenter;
        this.workersCount = workersCount;
        this.bufferCount = bufferCount;
    }
    
    public double getTime() {
        return time;
    }
    
    public String getProductionCenter() {
        return productionCenter;
    }
    
    public int getWorkersCount() {
        return workersCount;
    }
    
    public int getBufferCount() {
        return bufferCount;
    }
} 