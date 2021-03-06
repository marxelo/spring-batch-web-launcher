package com.marxelo.configuration;

import javax.annotation.PreDestroy;

import com.marxelo.models.dtos.Person;
import com.marxelo.steps.CreditItemProcessor;
import com.marxelo.steps.PersonItemProcessor;
import com.marxelo.steps.PersonItemReader;
import com.marxelo.steps.PersonItemWriter;
import com.marxelo.steps.personStepExecutionListener;
import com.marxelo.steps.skippers.MySkipListener;
import com.marxelo.steps.skippers.MySkipPolicy;
import com.marxelo.steps.skippers.PersonSkipListener;
import com.marxelo.steps.tasklets.DownloadFileTasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    static Resource[] resourceData = new Resource[] {
            new ClassPathResource("data1.csv"),
            new ClassPathResource("data2.csv") };

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // @Autowired
    // private Person person;

    // @Bean
    // public BatchConfigurer configurer(BatchProperties properties,
    //         @Qualifier("batchDataSource") DataSource dataSource) {
    //     return new BasicBatchConfigurer(properties, null, null) {
    //         @Override
    //         public PlatformTransactionManager getTransactionManager() {
    //             return transactionManager;
    //         }
    //     };
    // }

    @Bean
    public FlatFileItemReader<String> itemReader() {
        return new FlatFileItemReaderBuilder<String>()
                .name("creditItemReader")
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    public ItemReader<String> multiResourceItemReader() {
        final MultiResourceItemReader<String> reader = new MultiResourceItemReader<>();
        reader.setResources(resourceData);
        reader.setDelegate(itemReader());
        return reader;
    }

    @Bean
    public CreditItemProcessor creditItemProcessor() {

        CreditItemProcessor processor = new CreditItemProcessor();
        return processor;
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return items -> {
            for (final String item : items) {
                System.out.println("writing item..: " + item);
            }
        };
    }

    @Bean
    public Step creditStep() {
        return stepBuilderFactory.get("creditStep").<String, String> chunk(1)
                .reader(multiResourceItemReader())
                .processor(creditItemProcessor())
                .writer(itemWriter())
                .faultTolerant()
                .skipPolicy(new MySkipPolicy())
                .listener(new MySkipListener())
                .build();
    }

    @Bean
    public DownloadFileTasklet downloadFileTasklet() {
        return new DownloadFileTasklet();
    }

    @Bean
    public Step downloadFileStep() {
        return stepBuilderFactory.get("downloadFileStep")
                .tasklet(downloadFileTasklet())
                .build();
    }

    @Bean
    public Job creditJob() {
        return jobBuilderFactory.get("creditJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadFileStep())
                .next(creditStep())
                .build();
    }

    //  <--------------------------- Person --------------------------------->

    public PersonItemProcessor personItemProcessor() {
        PersonItemProcessor processor = new PersonItemProcessor();
        return processor;
    }

    @Bean
    public PersonItemWriter personWriter() {
        PersonItemWriter writer = new PersonItemWriter();
        return writer;
    }

    @Bean
    public ItemStreamReader<Person> itemStreamReader() {
        return new PersonItemReader();
    }

    @Bean
    public Step personStep() {
        return stepBuilderFactory.get("personStep").<Person, Person> chunk(1)
                .reader(itemStreamReader())
                .processor(personItemProcessor())
                .writer(personWriter())
                .faultTolerant()
                .skipPolicy(new MySkipPolicy())
                .listener(new PersonSkipListener())
                .listener(new personStepExecutionListener())
                .build();
    }

    @Bean
    public Job personJob() {
        return jobBuilderFactory.get("personJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadFileStep())
                .next(personStep())
                .listener(new JobResultListener())
                .build();
    }

    // <-------- Fim PersonStep com peek -------------->

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler(@Value("${thread.pool.size}") int threadPoolSize) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(threadPoolSize);
        return threadPoolTaskScheduler;
    }

    @PreDestroy
    private void preDestroy() {
        LOGGER.info("Called onApplicationEvent().");
    }

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(12);
        executor.setCorePoolSize(8);
        executor.setQueueCapacity(15);

        return executor;
    }

    @Bean
    public SimpleJobLauncher asyncJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();

        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(threadPoolTaskExecutor());
        return jobLauncher;
    }

}
