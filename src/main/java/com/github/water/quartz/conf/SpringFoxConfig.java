package com.github.water.quartz.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;
import org.springframework.web.context.request.async.DeferredResult;

/** 
* @Author : water  
* @Date   : 2016年9月8日 
* @Desc   : TODO
* @version: V1.0
*/


@Configuration
@EnableSwagger2
public class SpringFoxConfig {
    @SuppressWarnings("unchecked")
	@Bean
    public Docket testApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("API")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/api/.*")))//过滤的接口
                .build()
                .apiInfo(webApiInfo());
    }

    @SuppressWarnings("unchecked")
	@Bean
    public Docket demoApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("TEST")
                .genericModelSubstitutes(DeferredResult.class)
//              .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(regex("/test/.*")))//过滤的接口
                .build()
                .apiInfo(testApiInfo());
    }

    private ApiInfo webApiInfo() {
        ApiInfo apiInfo = new ApiInfo("Bdadp  API",//大标题
                "大数据场景开发平台Ark",//小标题
                "v0.0.0.1",//版本
                "www.Ark.com",//服务条款网址
                "Ark Team ",//创建人
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }

    private ApiInfo testApiInfo() {
        ApiInfo apiInfo = new ApiInfo("Bdadp Test API",//大标题
        		 "大数据场景开发平台Ark",//小标题
                 "v0.0.0.1",//版本
                 "www.Ark.com",//服务条款网址
                 "Ark Team ",//创建人
                 "The Apache License, Version 2.0",//链接显示文字
                 "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }
}
