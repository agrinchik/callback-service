package com.xyz.callbackservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The solution is implemented using Spring/Spring Boot frameworks
 * The Spring Batch Scheduling framework was not used, as it internally uses the JobRepository mechanism (in database) to store job execution state
 * Internal in-memory data storage implementation has synchronized access to underlying HashMap collection
 * A number of concurrent requests to the REST API  will depend on system resources as every request will be handled by a new thread in the web container
 */
@SpringBootApplication
public class CallbackServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallbackServiceApplication.class, args);
	}

}
