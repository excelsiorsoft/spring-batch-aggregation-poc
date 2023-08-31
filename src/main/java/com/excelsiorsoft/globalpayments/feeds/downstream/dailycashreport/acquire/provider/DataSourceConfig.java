/**
 * 
 */
package com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.acquire.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * @author x266345
 *
 */
//@Configuration
public class DataSourceConfig {
   @Bean
   public EmbeddedDatabase dataSource() {
      return new EmbeddedDatabaseBuilder().build();
   }
}
