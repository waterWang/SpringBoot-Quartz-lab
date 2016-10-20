package com.github.water.quartz.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.water.quartz.dto.ResultDTO;
import com.github.water.quartz.dto.ScheduleDTO;
import com.github.water.quartz.dto.ScheduleJob;
import com.github.water.quartz.exeption.ScheduleException;
import com.github.water.quartz.util.MyFactoryJob;
import com.github.water.quartz.util.ScheduleUtils;
import com.github.water.quartz.util.constant.Constants;

/**
 * @Author : water
 * @Date : 2016年9月19日
 * @Desc : TODO
 * @version: V1.0
 */

@Service
@Transactional
public class ScheduleService {

	private final Logger LOG = LoggerFactory.getLogger(ScheduleService.class);
	
	@Value("${factoryJobConfig.param}")
	private String factoryJobConfigParam;

	@Value("${factoryJobConfig.url}")
	private String factoryJobConfigUrl;

	/**
	 * @param scheduleDTO
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResultDTO addJob(ScheduleDTO scheduleDTO) {

		System.out.println("------------------" + factoryJobConfigUrl);
		ResultDTO resultDTO = new ResultDTO();
		String triggerType = scheduleDTO.getTriggerType();

		if (!StringUtils.isEmpty(triggerType)) {

			String jobName = scheduleDTO.getScenarioId();
			String jobGroup = scheduleDTO.getJobGroup();
			Map<String, Object> jobDataMap = scheduleDTO.getJobDataMap();

			TriggerBuilder triggerBuilder = ScheduleUtils.createTriggerBuilder(jobName, jobGroup);
			JobBuilder jobBuilder = ScheduleUtils.createJobBuilder(MyFactoryJob.class, jobName, jobGroup);
			JobDetail jobDetail = ScheduleUtils.createJobDetail(jobBuilder, jobDataMap);
			
			jobDetail.getJobDataMap().put("factoryJobConfigUrl", factoryJobConfigUrl);
			jobDetail.getJobDataMap().put("factoryJobConfigParam", factoryJobConfigParam);
			Scheduler scheduler;
			try {
				scheduler = StdSchedulerFactory.getDefaultScheduler();

				if (triggerType.equalsIgnoreCase(Constants.QZ_CRON_TRIGGER)) {

					String cronExpression = scheduleDTO.getCronExpression();
					CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
					CronTrigger cronTrigger = (CronTrigger) triggerBuilder
							.withSchedule(scheduleBuilder.withMisfireHandlingInstructionFireAndProceed()).build();

					scheduler.scheduleJob(jobDetail, cronTrigger);

				} else if (triggerType.equalsIgnoreCase(Constants.QZ_SIMPLE_TRIGGER)) {

					Date startTime = scheduleDTO.getStartTime();
					Date endTime = scheduleDTO.getEndTime();
					int repeatCount = scheduleDTO.getRepeatCount();
					int repeatInterval = scheduleDTO.getRepeatInterval();

					SimpleTrigger simpleTrigger = ScheduleUtils.createSimpleTrigger(triggerBuilder, startTime, endTime,
							repeatCount, repeatInterval);
					scheduler.scheduleJob(jobDetail, simpleTrigger);

				}
				scheduler.start();
				resultDTO.setResultMessage(Constants.RESULT_SUCCESS_MSG);
				resultDTO.setResultCode(Constants.RESULT_SUCCESS_CODE);
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			resultDTO.setResultMessage(Constants.RESULT_EXCEPTION_MSG);
			resultDTO.setResultCode(Constants.RESULT_EXCEPTION_CODE);
		}

		return resultDTO;
	}

	/**
	 * 计划中的任务
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleJob> jobInPlan(Scheduler scheduler) throws Exception {
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				ScheduleJob job = new ScheduleJob();
				job.setJobName(jobKey.getName());
				job.setJobGroup(jobKey.getGroup());
				job.setDescription("触发器:" + trigger.getKey());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				job.setStatus(triggerState.name());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					String cronExpression = cronTrigger.getCronExpression();
					job.setCronExpression(cronExpression);
				}
				jobList.add(job);
			}
		}
		return jobList;
	}

	public List<ScheduleDTO> getJobListByStatus(Scheduler scheduler, String jobStatus) throws Exception {
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
		List<ScheduleDTO> jobList = new ArrayList<ScheduleDTO>();
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				ScheduleDTO job = new ScheduleDTO();
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

				if (jobStatus.equalsIgnoreCase(triggerState.name())) {
					job.setScenarioId(jobKey.getName());
					job.setJobGroup(jobKey.getGroup());
					job.setDescription("触发器:" + trigger.getKey());
					job.setStatus(triggerState.name());
					if (trigger instanceof CronTrigger) {
						CronTrigger cronTrigger = (CronTrigger) trigger;
						String cronExpression = cronTrigger.getCronExpression();
						job.setCronExpression(cronExpression);
						job.setTriggerType(Constants.QZ_CRON_TRIGGER);
					} else if (trigger instanceof SimpleTrigger) {
						SimpleTrigger simpleTriggerTrigger = (SimpleTrigger) trigger;
						Date startTime = simpleTriggerTrigger.getStartTime();
						Date entTime = simpleTriggerTrigger.getEndTime();
						int repeatCount = simpleTriggerTrigger.getRepeatCount();
						int repeatInterval = (int) simpleTriggerTrigger.getRepeatInterval();
						job.setStartTime(startTime);
						job.setEndTime(entTime);
						job.setRepeatCount(repeatCount);
						job.setRepeatInterval(repeatInterval / 1000); 
						job.setTriggerType(Constants.QZ_SIMPLE_TRIGGER);
					}

				}
				jobList.add(job);
			}
		}
		return jobList;
	}

	/**
	 * 运行中的任务
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleJob> jobInRunning(Scheduler scheduler) throws Exception {
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
		for (JobExecutionContext executingJob : executingJobs) {
			ScheduleJob job = new ScheduleJob();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();
			job.setJobName(jobKey.getName());
			job.setJobGroup(jobKey.getGroup());
			job.setDescription("触发器:" + trigger.getKey());
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			job.setStatus(triggerState.name());
			if (trigger instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				String cronExpression = cronTrigger.getCronExpression();
				job.setCronExpression(cronExpression);
			}
			jobList.add(job);
		}
		return jobList;
	}

	/**
	 * 删除定时任务
	 *
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 * @throws ScheduleException
	 */
	public ResultDTO deleteJob(Scheduler scheduler, JobKey jobKey) throws ScheduleException {
		ResultDTO resultDTO = new ResultDTO();
		try {
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			LOG.error("删除定时任务失败", e);
			throw new ScheduleException("删除定时任务失败: " + e.getMessage());
		}
		resultDTO.setResultCode(Constants.RESULT_SUCCESS_CODE);
		resultDTO.setResultMessage(Constants.RESULT_SUCCESS_MSG);
		return resultDTO;
	}

	/**
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 * @throws ScheduleException
	 */
	public void runOnce(Scheduler scheduler, JobKey jobKey) throws ScheduleException {
		try {
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			LOG.error("运行一次定时任务失败: ", e);
			throw new ScheduleException("运行一次定时任务失败: " + e.getMessage());
		}
	}

	/**
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 * @throws ScheduleException
	 */
	public void pauseJob(Scheduler scheduler, JobKey jobKey) throws ScheduleException {

		try {
			scheduler.pauseJob(jobKey);
			LOG.info("Pause scene successful: " + jobKey.getName());
		} catch (SchedulerException e) {
			LOG.error("暂停定时任务失败", e);
			throw new ScheduleException("暂停定时任务失败: " + e.getMessage());
		}
	}

	/**
	 * 恢复任务
	 *
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 * @throws ScheduleException
	 */
	public void resumeJob(Scheduler scheduler, JobKey jobKey) throws ScheduleException {

		try {
			scheduler.resumeJob(jobKey);
			LOG.info("Resume scene successful: " + jobKey.getName());
		} catch (SchedulerException e) {
			LOG.error("恢复定时任务失败", e);
			throw new ScheduleException("恢复定时任务失败: " + e.getMessage());
		}
	}

	public void updateJob(ScheduleDTO scheduleDTO) throws ScheduleException {

		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(scheduleDTO.getScenarioId(), scheduleDTO.getJobGroup());
			// 获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			// 表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleDTO.getCronExpression());
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
