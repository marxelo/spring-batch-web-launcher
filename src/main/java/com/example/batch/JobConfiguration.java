package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

@Configuration
public class JobConfiguration {

	static Resource[] resources = new Resource[] { new ClassPathResource("data1.csv"),
			new ClassPathResource("data2.csv") };

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Bean
	public FlatFileItemReader<String> itemReader() {
		return new FlatFileItemReaderBuilder<String>().name("itemReader").lineMapper(new PassThroughLineMapper())
				.build();
	}

	@Bean
	public ItemReader<String> multiResourceItemReader() {
		final MultiResourceItemReader<String> reader = new MultiResourceItemReader<>();
		reader.setResources(resources);
		reader.setDelegate(itemReader());
		return reader;
	}

	@Bean
	public ItemProcessor<String, String> itemProcessor() {

		class MyItemProcessor implements ItemProcessor<String, String> {

			private StepExecution stepExecution;

			@BeforeStep
			public void saveStepExecution(final StepExecution stepExecution) {
				this.stepExecution = stepExecution;
				JobParameters jobParameters = stepExecution.getJobParameters();

				String processingDate = jobParameters.getString("processingDate");
				System.out.println(
					"processing date.....: " + processingDate);
			}

			@Nullable
			@Override
			public String process(final String item) {
				final ExecutionContext executionContext = stepExecution.getExecutionContext();
				final int resourceIndex = executionContext.getInt("MultiResourceItemReader.resourceIndex");
				System.out.println(
						"processing item = " + item + " coming from resource = " + resources[resourceIndex + 1]);
				return item;
			}
		}

		return new MyItemProcessor();
	}

	@Bean
	public ItemWriter<String> itemWriter() {
		return items -> {
			for (final String item : items) {
				System.out.println("writing item = " + item);
			}
		};
	}

	@Bean
	public Step step() {
		return steps.get("step").<String, String>chunk(1).reader(multiResourceItemReader()).processor(itemProcessor())
				.writer(itemWriter()).build();
	}

	@Bean
	public Job job() {
		return jobs.get("job").start(step()).build();
	}

}
