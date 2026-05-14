package com.akshay.QuartzScheduler.CommonUtils;

import com.akshay.QuartzScheduler.model.TriggerInfo;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommonUtils {
    public JobDetail getJobDetail(Class className, TriggerInfo info) {
        JobDataMap jobData = new JobDataMap();
        jobData.put(className.getSimpleName(), info);
        return JobBuilder
                .newJob(className)
                .setJobData(jobData)
                .withIdentity(className.getSimpleName(), "grp1")
                .storeDurably(true)
                .requestRecovery(false)
                .build();
    }

    public JobDetail getJobDetail(Class className){
        return JobBuilder.newJob(className)
                .withIdentity(className.getSimpleName(),"grp2")
                .storeDurably(true)
                .build();
    }

    public Trigger getTriggerInfoOfJob(Class className, TriggerInfo info) {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMilliseconds(info.getTimeInterval());
        if (info.isRunForever()) {
            builder.repeatForever();
        } else {
            builder.withRepeatCount(info.getTriggerCount());
        }
        return TriggerBuilder
                .newTrigger()
                .withIdentity(className.getSimpleName(), "simpleTriggerGrp")
                .startAt(new Date(System.currentTimeMillis() + info.getInitialOffSet()))
                .withSchedule(builder)
                .build();
    }

    public Trigger getTriggerInfoOfJobWithPriority(Class className, TriggerInfo info) {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMilliseconds(info.getTimeInterval());
        if (info.isRunForever()) {
            builder.repeatForever();
        } else {
            builder.withRepeatCount(info.getTriggerCount());
        }
        return TriggerBuilder
                .newTrigger()
                .withIdentity(className.getSimpleName(), "simpleTriggerGrpWithPriority")
                .startAt(new Date(System.currentTimeMillis() + info.getInitialOffSet()))
                .withSchedule(builder)
                .withPriority(50)
                .build();
    }

    public Trigger getTriggerByCronExpression(Class className,String expression){
        return TriggerBuilder
                .newTrigger()
                .withIdentity(className.getSimpleName(), "cronTriggerGrp")
                .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                .build();
    }

    public TriggerInfo getTriggerInfoObj(int triggerCount, boolean runForever,
                                         Long repeatValue, Long initialOffSet, String information) {
        TriggerInfo info = new TriggerInfo();
        info.setRunForever(runForever);
        info.setTriggerCount(triggerCount);
        info.setInitialOffSet(initialOffSet);
        info.setTimeInterval(repeatValue);
        info.setInfo(information);
        return info;
    }
}
