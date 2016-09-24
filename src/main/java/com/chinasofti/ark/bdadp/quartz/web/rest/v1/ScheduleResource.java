package com.chinasofti.ark.bdadp.quartz.web.rest.v1;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chinasofti.ark.bdadp.quartz.dto.ResultDTO;
import com.chinasofti.ark.bdadp.quartz.dto.ScheduleDTO;
import com.chinasofti.ark.bdadp.quartz.dto.ScheduleJob;
import com.chinasofti.ark.bdadp.quartz.exeption.ScheduleException;
import com.chinasofti.ark.bdadp.quartz.service.ScheduleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @Author : water
 * @Date : 2016年9月11日
 * @Desc : TODO
 * @version: V1.0
 */

@RestController
@RequestMapping(value = "/api/web/v1/schedule")
@Api(value = "任务调度", description = "任务调度api")
public class ScheduleResource {

	private static final Logger logger = LoggerFactory.getLogger(ScheduleResource.class);

	@Resource
	private ScheduleService scheduleService;
	
	@Value("${factoryJobConfig.param}")
	private String factoryJobConfigParam;

	@Value("${factoryJobConfig.url}")
	private String factoryJobConfigUrl;
	/**
	 * 添加job
	 * 
	 * @param scheduleDTO
	 * @return
	 * @throws ScheduleException
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "添加调度任务",  notes = "给某个场景添加调度任务<br><br><b>@author 王伟伟</b>" )
	public ResponseEntity<ResultDTO> addSchedule(@ApiParam(value = "调度DTO<br><br> 必填字段：scenarioId(即jobName) ，jobGroup ，triggerType（触发器类型SimpleTrigger 还是 CronTrigger）<br><br> ") @RequestBody ScheduleDTO scheduleDTO)
			throws ScheduleException {

		if (logger.isDebugEnabled()) {
			logger.debug("add Job  with triggerTiype: {} and ScenarioId : {} and jobGroup : {}",
					scheduleDTO.getTriggerType(), scheduleDTO.getScenarioId(), scheduleDTO.getJobGroup());
		}
		
		System.err.println(factoryJobConfigUrl + "==============");
		return new ResponseEntity<ResultDTO>(scheduleService.addJob(scheduleDTO), HttpStatus.OK);
	}

	/**
	 * delete job
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 * @throws ScheduleException
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation( value = "删除job",  notes = "删除job<br>@author 王伟伟")
	public ResponseEntity<ResultDTO> deleteJob(
			@ApiParam(value = "job名称", required = true) @RequestParam(value = "jobName") String jobName,
			@ApiParam(value = "job组", required = true) @RequestParam(value = "jobGroup") String jobGroup)
			throws ScheduleException {

		if (logger.isDebugEnabled()) {
			logger.debug("delete Job  with  jobName : {} and jobGroup : {}", jobName, jobGroup);
		}
		Scheduler scheduler = null;
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<ResultDTO>(scheduleService.deleteJob(scheduler, jobKey), HttpStatus.OK);
	}

	/**
	 * 立即运行，只会运行一次，方便测试时用
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 * @throws ScheduleException
	 */
	@RequestMapping(value = "/runOnce", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "立即运行，只会运行一次，方便测试时用",  notes = "立即运行，只会运行一次，方便测试时用<br>@author 王伟伟")
	public ResponseEntity<?> runOnceJob(
			@ApiParam(value = "job名称", required = true) @RequestParam(value = "jobName", required = true) String jobName,
			@ApiParam(value = "job组", required = true) @RequestParam(value = "jobGroup", required = true) String jobGroup)
			throws ScheduleException {

		if (logger.isDebugEnabled()) {
			logger.debug("runOnce Job  with  jobName : {} and jobGroup : {}", jobName, jobGroup);
		}
		Scheduler scheduler = null;
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scheduleService.runOnce(scheduler, jobKey);
		return new ResponseEntity<Object>("success", HttpStatus.OK);
	}

	/**
	 * 获取计划中的任务列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getJobListInPlan", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取计划中的任务列表", response = Void.class, notes = "获取计划中的任务列表<br>@author 王伟伟")
	public ResponseEntity<?> getJobListInPlan() throws Exception {

		Scheduler scheduler = null;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getJobListInPlan Job  with  jobName : {} and jobGroup : {}");
		}
		return new ResponseEntity<List<ScheduleJob>>(scheduleService.jobInPlan(scheduler), HttpStatus.OK);
	}

	/**
	 * 根据状态获取job列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getJobListByStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据状态获取job列表", response = Void.class, notes = "根据状态获取job列表<br>@author 王伟伟")
	public ResponseEntity<?> getJobListByStatus(
			@ApiParam(value = "job运行状态（NONE, NORMAL, PAUSED, COMPLETE, ERROR, BLOCKED）", required = true) @RequestParam(value = "jobStatus", required = true) String jobStatus)
			throws Exception {

		Scheduler scheduler = null;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getJobListInPlan Job  with  jobName : {} and jobGroup : {}");
		}
		return new ResponseEntity<List<ScheduleDTO>>(scheduleService.getJobListByStatus(scheduler,jobStatus ), HttpStatus.OK);
	}

	/**
	 * 获取运行中的任务列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getJobListInRunning", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取运行中的任务列表", response = Void.class, notes = "获取运行中的任务列表<br>@author 王伟伟")
	public ResponseEntity<?> getJobListInRunning() throws Exception {

		Scheduler scheduler = null;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getJobListInPlan Job  with  jobName : {} and jobGroup : {}");
		}
		return new ResponseEntity<List<ScheduleJob>>(scheduleService.jobInRunning(scheduler), HttpStatus.OK);
	}

	/**
	 * 暂停定时任务
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 * @throws ScheduleException
	 */
	@RequestMapping(value = "/pauseJob", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "暂停定时任务", response = Void.class, notes = "暂停定时任务<br>@author 王伟伟")
	public ResponseEntity<?> pauseJob(
			@ApiParam(value = "job名称", required = true) @RequestParam(value = "jobName", required = true) String jobName,
			@ApiParam(value = "job组", required = true) @RequestParam(value = "jobGroup", required = true) String jobGroup)
			throws ScheduleException {

		if (logger.isDebugEnabled()) {
			logger.debug("runOnce Job  with  jobName : {} and jobGroup : {}", jobName, jobGroup);
		}
		Scheduler scheduler = null;
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scheduleService.pauseJob(scheduler, jobKey);
		return new ResponseEntity<Object>("success", HttpStatus.OK);
	}

	/**
	 * 恢复定时任务
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 * @throws ScheduleException
	 */
	@RequestMapping(value = "/resumeJob", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "恢复定时任务", response = Void.class, notes = "恢复定时任务<br>@author 王伟伟")
	public ResponseEntity<?> resumeJob(
			@ApiParam(value = "job名称", required = true) @RequestParam(value = "jobName", required = true) String jobName,
			@ApiParam(value = "job组", required = true) @RequestParam(value = "jobGroup", required = true) String jobGroup)
			throws ScheduleException {

		if (logger.isDebugEnabled()) {
			logger.debug("runOnce Job  with  jobName : {} and jobGroup : {}", jobName, jobGroup);
		}
		Scheduler scheduler = null;
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scheduleService.resumeJob(scheduler, jobKey);
		return new ResponseEntity<Object>("success", HttpStatus.OK);
	}

	/**
	 * @param scheduleDTO
	 * @return
	 * @throws ScheduleException
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "更新job", response = Void.class, notes = "更新job<br>@author 王伟伟")
	public ResponseEntity<?> update(@ApiParam(value = "调度DTO") @RequestBody ScheduleDTO scheduleDTO)
			throws ScheduleException {

		scheduleService.updateJob(scheduleDTO);
		return new ResponseEntity<Object>("success", HttpStatus.OK);
	}

}