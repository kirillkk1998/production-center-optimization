package model;

import java.time.LocalDateTime;

public class Detail {
    private int id;
    private LocalDateTime processingStartTime;
    
    public Detail(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public LocalDateTime getProcessingStartTime() {
        return processingStartTime;
    }
    
    public void setProcessingStartTime(LocalDateTime time) {
        this.processingStartTime = time;
    }
    
    // Геттеры и сеттеры
    // ...
} 