package com.github.water.quartz.util;

import java.util.Date;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.water.quartz.dto.ScheduleJob;
import com.github.water.quartz.exeption.ScheduleException;
import com.github.water.quartz.job.JobFactory;
import com.github.water.quartz.job.JobSyncFactory;
import com.github.water.quartz.util.constant.Constants;

/**
 * @Author : water
 * @Date : 2016年9月11日
 * @Desc : 任务调度辅助类
 * @version: V1.0
 */
public class ScheduleUtils {

	/** 日志对象 */
	private static final Logger LOG = LoggerFactory.getLogger(ScheduleUtils.class);

	/**
	 * 获取触发器key
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 */
	public static TriggerKey getTriggerKey(String jobName, String jobGroup) {

		return TriggerKey.triggerKey(jobName, jobGroup);
	}

	
	/**
	 * 获取表达式触发器
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 * @return
	 * @throws ScheduleException
	 */
	public static CronTrigger getCronTrigger(Scheduler scheduler, String jobName, String jobGroup)
			throws ScheduleException {

		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			return (CronTrigger) scheduler.getTrigger(triggerKey);
		} catch (SchedulerException e) {
			LOG.error("获取定时任务CronTrigger出现异常", e);
			throw new ScheduleException("获取定时任务CronTrigger出现异常: " + e.getMessage());
		}
	}


	/**
	 * 创建定时任务
	 *
	 * @param scheduler
	 *            the scheduler
	 * @param jobName
	 *            the job name
	 * @param jobGroup
	 *            the job group
	 * @param cronExpression
	 *            the cron expression
	 * @param isSync
	 *            the is sync
	 * @param param
	 *            the param
	 * @throws ScheduleException
	 */
	public static void createScheduleJobWithCronTriger(Scheduler scheduler, String jobName, String jobGroup,
			String cronExpression, boolean isSync, Object param) throws ScheduleException {
		// 同步或异步
		Class<? extends Job> jobClass = isSync ? JobSyncFactory.class : JobFactory.class;

		JobBuilder jobBuilder = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup);
		JobDetail jobDetail = jobBuilder.build();

		// 放入参数，运行时的方法可以获取
		jobDetail.getJobDataMap().put(Constants.JOB_PARAM_KEY, param);
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup);
		CronTrigger trigger = (CronTrigger) triggerBuilder
				.withSchedule(scheduleBuilder.withMisfireHandlingInstructionFireAndProceed()).build();

		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOG.error("创建定时任务失败", e);
			throw new ScheduleException("创建定时任务失败: " + e.getMessage());
		}
	}

	/**
	 * 创建定时任务
	 *
	 * @param scheduler
	 *            the scheduler
	 * @param jobName
	 *            the job name
	 * @param jobGroup
	 *            the job group
	 * @param cronExpression
	 *            the cron expression
	 * @param isSync
	 *            the is sync
	 * @param param
	 *            the param
	 * @throws ScheduleException
	 */
	/*
	 * public static void createScheduleJobWithSimpleTriger(Class<? extends Job>
	 * jobClass, String jobName, String jobGroup, String cronExpression, boolean
	 * isSync, Object... param) throws ScheduleException {
	 * 
	 * JobDetail jobDetail = createJobDetail(jobClass, isSync, jobName,
	 * jobGroup, param);
	 * 
	 * TriggerBuilder<Trigger> triggerBuilder =
	 * TriggerBuilder.newTrigger().withIdentity("trigger3", "group2");
	 * SimpleTrigger trigger = (SimpleTrigger) triggerBuilder.startAt(new
	 * Date()) .withSchedule(SimpleScheduleBuilder.simpleSchedule().
	 * withIntervalInSeconds(10).withRepeatCount(2)) .endAt(null).build(); try {
	 * scheduler.scheduleJob(jobDetail, trigger); } catch (SchedulerException e)
	 * { LOG.error("创建定时任务失败", e); throw new ScheduleException("创建定时任务失败: " +
	 * e.getMessage()); } }
	 */

	/**
	 * @param jobClass
	 * @param isSync
	 * @param jobName
	 * @param jobGroup
	 * @param param
	 * @return
	 */
	public static JobDetail createJobDetail(JobBuilder jobBuilder, Map<String,Object> dataMap) {
		JobDetail jobDetail = jobBuilder.build();
		for (String key : dataMap.keySet()) {
			jobDetail.getJobDataMap().put(key, dataMap.get(key));
		}
		return jobDetail;
	}
	
	/**
	 * @param jobClass
	 * @param jobName
	 * @param jobGroup
	 * @return
	 */
	public static JobBuilder createJobBuilder(Class<? extends Job> jobClass , String jobName,
			String jobGroup){
		return JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup);
	}

	/**
	 * @param jobName
	 * @param jobGroup
	 * @param startTime
	 * @param endTime
	 * @param repeatCount
	 * @param repeatInterval
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static SimpleTrigger createSimpleTrigger(TriggerBuilder triggerBuilder, Date startTime, Date endTime,
			int repeatCount, int repeatInterval) {

		SimpleTrigger simpleTrigger = (SimpleTrigger) triggerBuilder.startAt(startTime)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(repeatInterval)// 重复间隔
						.withRepeatCount(repeatCount))
				.endAt(endTime) // 重复次数
				.build();

		return simpleTrigger;
	}

	/**
	 * @param jobName
	 * @param jobGroup
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static TriggerBuilder createTriggerBuilder(String jobName, String jobGroup) {
		return TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup);
	}



	

	/**
	 * 获取jobKey
	 *
	 * @param jobName
	 *            the job name
	 * @param jobGroup
	 *            the job group
	 * @return the job key
	 */
	public static JobKey getJobKey(String jobName, String jobGroup) {

		return JobKey.jobKey(jobName, jobGroup);
	}

	/**
	 * 更新定时任务
	 *
	 * @param scheduler
	 *            the scheduler
	 * @param scheduleJob
	 *            the schedule job
	 * @throws ScheduleException
	 */
	public static void updateScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) throws ScheduleException {
		updateScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup(),
				scheduleJob.getCronExpression(), scheduleJob.getIsSync(), scheduleJob);
	}

	/**
	 * 更新定时任务
	 *
	 * @param scheduler
	 *            the scheduler
	 * @param jobName
	 *            the job name
	 * @param jobGroup
	 *            the job group
	 * @param cronExpression
	 *            the cron expression
	 * @param isSync
	 *            the is sync
	 * @param param
	 *            the param
	 * @throws ScheduleException
	 */
	public static void updateScheduleJob(Scheduler scheduler, String jobName, String jobGroup, String cronExpression,
			boolean isSync, Object param) throws ScheduleException {

		// 同步或异步
		// Class<? extends Job> jobClass = isSync ? JobSyncFactory.class :
		// JobFactory.class;

		try {
			// JobDetail jobDetail = scheduler.getJobDetail(getJobKey(jobName,
			// jobGroup));

			// jobDetail = jobDetail.getJobBuilder().ofType(jobClass).build();

			// 更新参数 实际测试中发现无法更新
			// JobDataMap jobDataMap = jobDetail.getJobDataMap();
			// jobDataMap.put(ScheduleJobVo.JOB_PARAM_KEY, param);
			// jobDetail.getJobBuilder().usingJobData(jobDataMap);

			TriggerKey triggerKey = ScheduleUtils.getTriggerKey(jobName, jobGroup);

			// 表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			LOG.error("更新定时任务失败", e);
			throw new ScheduleException("更新定时任务失败: " + e.getMessage());
		}
	}

	
}
