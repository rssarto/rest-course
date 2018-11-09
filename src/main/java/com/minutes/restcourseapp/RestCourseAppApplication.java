package com.minutes.restcourseapp;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@SpringBootApplication
public class RestCourseAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestCourseAppApplication.class, args);
	}
	
	@Bean
	public LocaleResolver localResolver() {
		AcceptHeaderLocaleResolver sessionLocalResolver = new AcceptHeaderLocaleResolver();
		sessionLocalResolver.setDefaultLocale(Locale.US);
		return sessionLocalResolver;
	}
	
	/*
	@Bean
	public ResourceBundleMessageSource resourceBundleMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}
	*/
}
