package com.akshay.QuartzScheduler.service;

import com.akshay.QuartzScheduler.CommonUtils.CommonUtils;
import com.akshay.QuartzScheduler.jobs.CronJob;
import com.akshay.QuartzScheduler.schedular.MainSchedular;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CronScheduler {
    private final MainSchedular schedular;
    private final CommonUtils commonUtils;


    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        schedular.scheduleCronJob(CronJob.class, "0/2 * * * * ?");

    }
}
