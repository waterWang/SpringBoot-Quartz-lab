package com.github.water.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

/**
 * 任务工厂类,非同步
 * @author Beacher Han(dirk_han@126.com)
 * @since  2016/3/31
 */
public class JobFactory implements Job {

	
    /* 日志对象 */
    private static final Logger LOG = LoggerFactory.getLogger(JobFactory.class);

    public void execute(JobExecutionContext context) throws JobExecutionException 
    {
    	try
		{
//            LOG.info("JobFactory execute");
//            ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get(Constants.JOB_PARAM_KEY);
//            System.out.println("jobName:" + scheduleJob.getJobName() + "  " + scheduleJob);
//            ScenesService scenesService = ContextLoader.getCurrentWebApplicationContext().getBean(ScenesService.class);
//            SceneExecTraceEntity sceneExecTrace = new SceneExecTraceEntity();
//    		sceneExecTrace.setSceneId(scheduleJob.getJobId());
//    		sceneExecTrace.setExecBatchNo(UUID.getId());
//    		sceneExecTrace.setSceneExecStatus(Constants.EXEC_STATUS_RUNNING);
//            scenesService.runScene(sceneExecTrace);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
		catch (Exception e)
		{
			LOG.error("Exception when executing quartz job: ", e);
			throw new JobExecutionException(e);
		}
    }
}
