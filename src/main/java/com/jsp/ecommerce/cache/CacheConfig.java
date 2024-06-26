package com.jsp.ecommerce.cache;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jsp.ecommerce.entity.User;
@Configuration
public class CacheConfig {
	@Bean
	public Cache<String, String> otpCache(){
		return	CacheBuilder.newBuilder()
		.expireAfterWrite(Duration.ofMinutes(5))
		.concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();
		 
		
	}
	@Bean
	public Cache<String, User> userCache(){
		return	CacheBuilder.newBuilder()
		.expireAfterWrite(Duration.ofMinutes(15))
		.concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();
		 
		
	}

}
