package com.akshay.QuartzScheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

public class SecondJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("Executing 2nd Job " + new Date(System.currentTimeMillis()));
    }
}