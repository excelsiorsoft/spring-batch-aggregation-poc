package com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.acquire.provider;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.acquire.provider.AggregateItemJobConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBatchTest
//@SpringBootTest
@SpringJUnitConfig(AggregateItemJobConfiguration.class)
class AggregateItemJobTest {

	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @AfterEach
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }
    
	@Autowired
	public JobLauncher jobLauncher ;

	@Test
	void testJob() throws Exception {
		log.info("In test");
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
	}

}
