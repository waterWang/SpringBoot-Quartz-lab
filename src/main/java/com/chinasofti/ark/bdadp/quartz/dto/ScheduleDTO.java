package com.chinasofti.ark.bdadp.quartz.dto;

import java.util.Date;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : water
 * @Date : 2016年9月11日
 * @Desc : 任务调度网络传输对象
 * @version: V1.0
 */
@Data
public class ScheduleDTO {

	@ApiModelProperty(position = 1 ,value = "触发器类型（SimpleTrigger 还是 CronTrigger）", required = true)
	private String triggerType;

//	@Size(min = 1, max = 255)
	@ApiModelProperty(position = 2 , value = "场景id（job名称）", required = true)
	private String scenarioId;

//	@Size(min = 1, max = 255 )
	@ApiModelProperty(position = 3 ,value = "job分组", required = true)
	private String jobGroup;

	@ApiModelProperty(position = 4 ,value = "是否异步")
	private Boolean isSync;

	@ApiModelProperty(position = 5 ,value = "修改时间")
	private Date modifyTime;

	@ApiModelProperty(position = 6 ,value = "任务属性或者配置")
	private Map<String, Object> jobDataMap;
	
//	@Size(min = 1, max = 255)
	@ApiModelProperty(position = 7 ,value = "job状态" )
	private String status;
	
//	@Size(min = 1, max = 255)
	@ApiModelProperty(position = 8 ,value = "job描述信息")
	private String description;

	/*
	 * cron trigger
	 * 
	 */

	@ApiModelProperty(position = 9 ,value = "任务运行时间表达式（CronTrigger类型触发器的 属性）")
	private String cronExpression;

	/*
	 * simple trigger
	 * 
	 */

	@ApiModelProperty(position = 10 ,value = "创建时间（SimpleTrigger类型触发器的 属性）")
	private Date startTime;

	@ApiModelProperty(position = 11 ,value = "结束时间（SimpleTrigger类型触发器的 属性）")
	private Date endTime;

	@ApiModelProperty(position = 12 ,value = "重复次数（SimpleTrigger类型触发器的 属性）")
	private int repeatCount;

	@ApiModelProperty(position = 13 ,value = "重复间隔，单位秒（SimpleTrigger类型触发器的 属性）")
	private int repeatInterval;

	
}
