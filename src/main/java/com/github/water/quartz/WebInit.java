package com.github.water.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import com.github.water.quartz.util.constant.Constants;


@SuppressWarnings("deprecation")
@Configurable
public class WebInit extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(WebInit.class);

	private static Class<Application> applicationClass = Application.class;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.profiles(addDefaultProfile()).sources(applicationClass);
	}

	private String addDefaultProfile() {
		String profile = System.getProperty("spring.profiles.active");
		if (profile != null) {
			log.info("Running with Spring profile(s) : {}", profile);
			return profile;
		}
		log.warn("No Spring profile configured, running with default configuration");
		return Constants.SPRING_PROFILE_TEST;
	}
}