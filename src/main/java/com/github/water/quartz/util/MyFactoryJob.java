package com.github.water.quartz.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 实现Job接口，定义具体运行的任务
 */
@Service
public class MyFactoryJob implements Job {
	
	private final Logger logger = LoggerFactory.getLogger(MyFactoryJob.class);
	
//	@Value("${factoryJobConfig.param}")
	private String factoryJobConfigParam;

//	@Value("${factoryJobConfig.url}")
	private String factoryJobConfigUrl;
	
	@Value("${factoryJobConfig.param}")
	public void setFactoryJobConfigParam(String factoryJobConfigParam) {
		this.factoryJobConfigParam = factoryJobConfigParam;
	}

	@Value("${factoryJobConfig.url}")
	public void setFactoryJobConfigUrl(String factoryJobConfigUrl) {
		this.factoryJobConfigUrl = factoryJobConfigUrl;
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("111111~~~~~~~~~~" + factoryJobConfigUrl);
		
		String jobName = jobExecutionContext.getJobDetail().getKey().getName();
		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		String postUrl = dataMap.getString("factoryJobConfigUrl") + jobName + dataMap.getString("factoryJobConfigParam");;
		
		System.out.println("2222~~~~~~~~~~" + postUrl);
		HttpUtil.get(postUrl);

		// 任务执行的时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy 年 MM 月 dd 日  HH 时 mm 分 ss 秒");
		String jobRunTime = dateFormat.format(Calendar.getInstance().getTime());

		// 输出任务执行情况
		System.err.println("任务 : " + jobName + " 在  " + jobRunTime + " 执行了 ");
		logger.info(" jobName : {} run  jobRunTime : {} ",jobName ,jobRunTime);
	}

	

}