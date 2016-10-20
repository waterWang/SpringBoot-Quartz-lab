package com.github.water.quartz.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author : water
 * @Date : 2016年9月11日
 * @Desc : 同步的任务工厂类
 * @version: V1.0
 */
@DisallowConcurrentExecution
public class JobSyncFactory implements Job {

	// @Resource
	// private ScenesService scenesService;

	/* 日志对象 */
	private static final Logger LOG = LoggerFactory.getLogger(JobSyncFactory.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			LOG.info("JobSyncFactory execute");
//			JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
//			ScheduleJob scheduleJob = (ScheduleJob) mergedJobDataMap.get(Constants.JOB_PARAM_KEY);
//			System.out.println("jobName:" + scheduleJob.getJobName() + "  " + scheduleJob);
//			ScenesService scenesService = ContextLoader.getCurrentWebApplicationContext().getBean(ScenesService.class);
//			SceneExecTraceEntity sceneExecTrace = new SceneExecTraceEntity();
//			sceneExecTrace.setSceneId(scheduleJob.getJobId());
//			sceneExecTrace.setExecBatchNo(UUID.getId());
//			sceneExecTrace.setSceneExecStatus(Constants.EXEC_STATUS_RUNNING);
//			ScheduleJobService scheService = ContextLoader.getCurrentWebApplicationContext()
//					.getBean(ScheduleJobService.class);
//			scenesService.runScene(sceneExecTrace, scheService);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOG.error("Exception when executing quartz job: ", e);
			throw new JobExecutionException(e);
		}
	}
}
