package com.akshay.QuartzScheduler.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TriggerInfo implements Serializable {
    private int triggerCount; // 5
    private boolean isRunForever; // true/false
    private Long timeInterval;
    private Long initialOffSet;
    private String info;
}
