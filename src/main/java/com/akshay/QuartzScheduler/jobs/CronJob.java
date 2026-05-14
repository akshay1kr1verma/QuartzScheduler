package com.akshay.QuartzScheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

public class CronJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("Executing Cron Job " + new Date(System.currentTimeMillis()));
    }
}