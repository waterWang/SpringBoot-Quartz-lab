package com.chinasofti.ark.bdadp.quartz.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : water
 * @Date : 2016年9月11日
 * @Desc : 运行结果网络传输对象
 * @version: V1.0
 */

@Data
public class ResultDTO {

	@NotNull
	@Size(min = 1, max = 255)
	@ApiModelProperty(value = "运行结果状态码", required = true)
	private int resultCode;

	@NotNull
	@ApiModelProperty(value = "运行结果信息", required = false)
	private String resultMessage;

}
