/**
 * 
 */
package com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.acquire.provider;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.domain.Trade;

import lombok.extern.slf4j.Slf4j;

/**
 * @author x266345
 *
 */
@Slf4j
@Configuration
@EnableBatchProcessing
//@Import(DataSourceConfig.class)
public class AggregateItemJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
/*
	@Bean
	   public EmbeddedDatabase dataSource() {
	      return new EmbeddedDatabaseBuilder().build();
	   }
	
	@Bean
	   public JobRepositoryFactoryBean myJobRepository(DataSource dataSource) {
	      JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
	      jobRepositoryFactoryBean.setDataSource(dataSource);
	       return jobRepositoryFactoryBean;
	  }
*/	
	
	@Bean
	public Job job() throws Exception {
		return this.jobBuilderFactory
				.get("multilineJob")
				.incrementer(new RunIdIncrementer())
				.flow(step())
				.end()
				.build();
	}

	@Bean
	public Step step() throws Exception {
		return this.stepBuilderFactory.get("step1")
				.<Trade, Trade>chunk(1)
				.reader(reader())
				.writer(writer())
				.processor(processor())
				.build();
	}
	
	@Bean
	public ItemProcessor<Trade, Trade> processor() {
		return new ItemProcessor<Trade, Trade>() {

			@Override
			public Trade process(Trade item) throws Exception {
				log.info("In processor: "+item.getCustomer());
				item.setProcessed(true);
				return item;
			}
		};
	}
	
	/*
	 * @Bean public FlatFileItemWriter<AggregateItem<List<?>>> writer() {
	 * FlatFileItemWriter<AggregateItem<List<?>>> writer = new
	 * FlatFileItemWriter<>(); writer.setResource(new FileSystemResource(
	 * "file:target/test-outputs/20070122.testStream.multilineStep.txt"));
	 * writer.setLineAggregator(new PassThroughLineAggregator<>()); return writer; }
	 */
	
	@Bean
	@StepScope
	public FlatFileItemWriter<Trade> writer(
			/*@Value("#{jobParameters['out.file.name']}") String fileName*/) {
		return new FlatFileItemWriterBuilder<Trade>()
				.name("tradeWriter")
				.resource(new FileSystemResource("target/test-outputs/20070122.testStream.multilineStep.txt"/*fileName*/))
				.lineAggregator(new PassThroughLineAggregator<Trade>())
				.build();
	}

	@Bean
	public ItemReader<Trade>/*AggregateItemReader*/ reader() {
		AggregateItemReader<Trade> aggregateItemReader = new AggregateItemReader<>();
		aggregateItemReader.setItemReader(fileItemReader());
		//return aggregateItemReader;
		return new DequeueItemReader<>(aggregateItemReader);
	}

	
	@Bean
	@StepScope
	public FlatFileItemReader<AggregateItem<Trade>> fileItemReader/*tradeFileItemReader*/(
	//@Value("#{jobParameters['in.file.name']}") String fileName 
			) {

		return new FlatFileItemReaderBuilder<AggregateItem<Trade>>()
				.name("tradeFileItemReader")
				.resource(new ClassPathResource("data/20070122.teststream.multilineStep.txt"/* fileName */))
				.linesToSkip(0)
				.lineTokenizer(fixedFileDescriptor())
				.fieldSetMapper(fieldSetMapper())
				.build();
	}
	
//	@Bean
//	public FlatFileItemReader<?> fileItemReader() {
//		// Create reader instance
//		FlatFileItemReader<?> reader = new FlatFileItemReader<>();
//
//		// Set input file location
//		reader.setResource(new FileSystemResource("classpath:data/20070122.teststream.multilineStep.txt"));
//
//		// Set number of lines to skips. Use it if file has header rows.
//		reader.setLinesToSkip(0);
//
//		reader.setLineMapper(lineMapper());
//
//		return reader;
//	}
	
//	@Bean
//	public DefaultLineMapper lineMapper() {
//		DefaultLineMapper lineMapper = new DefaultLineMapper();
//		lineMapper.setLineTokenizer(fixedFileDescriptor());
//		lineMapper.setFieldSetMapper(fieldSetMapper());
//		return lineMapper;
//	}
	
	@Bean
	public AggregateItemFieldSetMapper<Trade> fieldSetMapper() {
		AggregateItemFieldSetMapper<Trade> fieldSetMapper = new AggregateItemFieldSetMapper<>();
		fieldSetMapper.setDelegate(new TradeFieldSetMapper());
		return fieldSetMapper;
	}

	@SuppressWarnings("serial")
	@Bean
	public PatternMatchingCompositeLineTokenizer fixedFileDescriptor() {

		PatternMatchingCompositeLineTokenizer lineTokenizer = new PatternMatchingCompositeLineTokenizer();

		Map<String, LineTokenizer> tokenizers = new HashMap<String, LineTokenizer>() {
			
			{
				put("BEGIN*", beginRecordTokenizer());
				put("END*", endRecordTokenizer());
				put("*", tradeRecordTokenizer());
			}
		};

		lineTokenizer.setTokenizers(tokenizers);
		return lineTokenizer;

	}

	@Bean
	public FixedLengthTokenizer beginRecordTokenizer() {
		FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
		tokenizer.setColumns(new Range[] { new Range(1, 5) });
		return tokenizer;
	}

	@Bean
	public FixedLengthTokenizer endRecordTokenizer() {
		FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
		tokenizer.setColumns(new Range[] { new Range(1, 3) });
		return tokenizer;
	}

	@Bean
	public FixedLengthTokenizer tradeRecordTokenizer() {
		FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
		tokenizer.setNames(new String[] { "ISIN", "Quantity", "Price", "Customer" });
		tokenizer.setColumns(new Range[] { 
				new Range(1, 12), 
				new Range(13, 15), 
				new Range(16, 20), 
				new Range(21, 29) });
		return tokenizer;
	}

}
