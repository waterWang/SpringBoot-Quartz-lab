package com.kaviddiss.bootquartz;
/** 
* @Author : water  
* @Date   : 2016年9月11日 
* @Desc   : TODO
* @version: V1.0
*/
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
/**
 * 实际执行任务的业务类,需要实现Job接口
 * @remark
 * @author feizi
 * @time 2015-3-23下午2:49:46
 */
public class TestJobTwo implements Job {

    /**
     * 执行任务的方法
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("================执行任务二....");

        //do more...这里可以执行其他需要执行的任务  
    }

}
