# spring-boot-quartz
Sample Spring Boot application that uses the Quartz framework

Based on https://gist.github.com/jelies/5085593 with additional features (liquibase, misfire handling, configurable triggers, enable/disable quartz flag)

```
QRTZ_CALENDARS 以 Blob 类型存储 Quartz 的 Calendar 信息 
QRTZ_CRON_TRIGGERS 存储 Cron Trigger，包括Cron表达式和时区信息 
QRTZ_FIRED_TRIGGERS 存储与已触发的 Trigger 相关的状态信息，以及相联 Job的执行信息QRTZ_PAUSED_TRIGGER_GRPS 存储已暂停的 Trigger组的信息 
QRTZ_SCHEDULER_STATE 存储少量的有关 Scheduler 的状态信息，和别的Scheduler实例(假如是用于一个集群中) 
QRTZ_LOCKS 存储程序的悲观锁的信息(假如使用了悲观锁) 
QRTZ_JOB_DETAILS 存储每一个已配置的 Job 的详细信息 
QRTZ_JOB_LISTENERS 存储有关已配置的 JobListener的信息 
QRTZ_SIMPLE_TRIGGERS存储简单的Trigger，包括重复次数，间隔，以及已触的次数 
QRTZ_BLOG_TRIGGERS Trigger 作为 Blob 类型存储(用于 Quartz 用户用JDBC创建他们自己定制的 Trigger 类型，JobStore并不知道如何存储实例的时候) 
QRTZ_TRIGGER_LISTENERS 存储已配置的 TriggerListener的信息 
QRTZ_TRIGGERS 存储已配置的 Trigger 的信息 
```


```
#调度标识名 集群中每一个实例都必须使用相同的名称 org.quartz.scheduler.instanceName：scheduler
#ID设置为自动获取 每一个必须不同 org.quartz.scheduler.instanceId ：AUTO
#数据保存方式为持久化 org.quartz.jobStore.class ：org.quartz.impl.jdbcjobstore.JobStoreTX
#数据库平台 org.quartz.jobStore.driverDelegateClass：org.quartz.impl.jdbcjobstore.oracle.weblogic.WebLogicOracleDelegate#数据库别名 随便取org.quartz.jobStore.dataSource ： myXADS
#表的前缀 org.quartz.jobStore.tablePrefix ： QRTZ_
#设置为TRUE不会出现序列化非字符串类到 BLOB 时产生的类版本问题org.quartz.jobStore.useProperties ： true
#加入集群 org.quartz.jobStore.isClustered ： true
#调度实例失效的检查时间间隔 org.quartz.jobStore.clusterCheckinInterval：20000 
#容许的最大作业延长时间 org.quartz.jobStore.misfireThreshold ：60000
#ThreadPool 实现的类名 org.quartz.threadPool.class：org.quartz.simpl.SimpleThreadPool
#线程数量 org.quartz.threadPool.threadCount ： 10
#线程优先级 org.quartz.threadPool.threadPriority ： 5
#自创建父线程org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread： true 
#设置数据源org.quartz.dataSource.myXADS.jndiURL： CT
#jbdi类名 org.quartz.dataSource.myXADS.java.naming.factory.initial ：weblogic.jndi.WLInitialContextFactory#URLorg.quartz.dataSource.myXADS.java.naming.provider.url：=t3://localhost:7001
【注】：在J2EE工程中如果想用数据库管理Quartz的相关信息，就一定要配置数据源，这是Quartz的要求。
```


```
表qrtz_job_details:保存job详细信息,该表需要用户根据实际情况初始化 
      job_name:集群中job的名字,该名字用户自己可以随意定制,无强行要求 
      job_group:集群中job的所属组的名字,该名字用户自己随意定制,无强行要求 
      job_class_name:集群中个notejob实现类的完全包名,quartz就是根据这个路径到classpath找到该job类 
      is_durable:是否持久化,把该属性设置为1，quartz会把job持久化到数据库中 
      job_data:一个blob字段，存放持久化job对象 

      表qrtz_triggers: 保存trigger信息 
      trigger_name:trigger的名字,该名字用户自己可以随意定制,无强行要求 
      trigger_group:trigger所属组的名字,该名字用户自己随意定制,无强行要求 
      job_name:qrtz_job_details表job_name的外键 
      job_group:qrtz_job_details表job_group的外键 
      trigger_state:当前trigger状态，设置为ACQUIRED,如果设置为WAITING,则job不会触发 
      trigger_cron:触发器类型,使用cron表达式 

      表qrtz_cron_triggers:存储cron表达式表 
      trigger_name:qrtz_triggers表trigger_name的外键 
      trigger_group:qrtz_triggers表trigger_group的外键 
      cron_expression:cron表达式 
       
      表qrtz_scheduler_state:存储集群中note实例信息，quartz会定时读取该表的信息判断集群中每个实例的当前状态 
      instance_name:之前配置文件中org.quartz.scheduler.instanceId配置的名字，就会写入该字段，如果设置为AUTO,quartz会根据物理机名和当前时间产生一个名字 
      last_checkin_time:上次检查时间 
      checkin_interval:检查间隔时间 
      
 ```
 
 
 quartz框架中T_TASK_TRIGGERS表 TRIGGER_STATE 字段显示任务的属性大概状态有这几种： 
 
 WAITING, ACQUIRED, COMPLETE, PAUSED, BLOCKED, PAUSED_BLOCKED, ERROR;
 
https://github.com/quartz-scheduler/quartz/blob/fb14850e1133dd3d884f2b99748b12286df8afc6/quartz-core/src/main/java/org/quartz/impl/jdbcjobstore/Constants.java

http://127.0.0.1:8080/schedule/swagger-ui.html

NONE, NORMAL, PAUSED, COMPLETE, ERROR, BLOCKED
