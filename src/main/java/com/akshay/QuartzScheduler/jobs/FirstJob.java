package com.akshay.QuartzScheduler.jobs;

import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class FirstJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("First Job in Quartz Scheduling...." + new Date(System.currentTimeMillis()));
    }
}
