/**
 * 
 */
package com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.acquire.provider;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author x266345
 *
 */
//@EnableBatchProcessing
@SpringBootApplication
public class MainApplication {
	
	public static void main(String[] args) {
		//SpringApplication.run(MainApplication.class, args);
		
		new SpringApplicationBuilder(MainApplication.class).web(WebApplicationType.NONE).run(args);
	}

}
