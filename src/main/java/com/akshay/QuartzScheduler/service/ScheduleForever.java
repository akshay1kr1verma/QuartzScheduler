package com.akshay.QuartzScheduler.service;

import com.akshay.QuartzScheduler.CommonUtils.CommonUtils;
import com.akshay.QuartzScheduler.jobs.SecondJob;
import com.akshay.QuartzScheduler.model.TriggerInfo;
import com.akshay.QuartzScheduler.schedular.MainSchedular;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ScheduleForever {
    private final MainSchedular schedular;
    private final CommonUtils commonUtils;


    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        TriggerInfo info = commonUtils.getTriggerInfoObj(1,
                true, 1000L, 1000L, "schedule forever info");
        schedular.scheduleJobWithPriority(SecondJob.class, info);

    }
}
