package com.marxelo.configuration;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import com.marxelo.models.dtos.Person;
import com.marxelo.steps.CreditItemProcessor;
import com.marxelo.steps.MyStepExecutionListener;
import com.marxelo.steps.PersonItemProcessor;
import com.marxelo.steps.PersonItemReader;
import com.marxelo.steps.PersonItemWriter;
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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
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

    static Resource[] resourceData = new Resource[] { new ClassPathResource("data1.csv"),
            new ClassPathResource("data2.csv") };

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    // @Bean
    // public BatchConfigurer configurer(BatchProperties properties,
    //         @Qualifier("batchDataSource") DataSource dataSource) {
    //     return new BasicBatchConfigurer(properties, dataSource, null) {
    //         @Override
    //         public PlatformTransactionManager getTransactionManager() {
    //             return transactionManager;
    //         }
    //     };
    // }    

    // @Autowired
    // private Person person;

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
        return stepBuilderFactory.get("creditStep")
                .<String, String> chunk(1)
                .reader(multiResourceItemReader())
                .processor(creditItemProcessor())
                .writer(itemWriter())
                .faultTolerant()
                .skipPolicy(new MySkipPolicy())
                .listener(new MySkipListener())
                .listener(new MyStepExecutionListener())
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

    // <--------------------------- Person --------------------------------->

    @StepScope
    @Bean
    public PersonItemProcessor personItemProcessor() {
        System.out.println("::::::::::::::::::: Before server is loaded :::::::::::::::::::::::");
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

    // @Bean
    public Step personStep() {
        return stepBuilderFactory.get("personStep")
                .<Person, Person> chunk(1)
                .reader(itemStreamReader())
                .processor(personItemProcessor())
                .writer(personWriter())
                .faultTolerant()
                .skipPolicy(new MySkipPolicy())
                .listener(new PersonSkipListener())
                .listener(new MyStepExecutionListener())
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

    @Bean
    public Job jobStepJob() {
        return this.jobBuilderFactory
                .get("jobStepJob")
                .start(jobStepJobStep1(null))
                .next(jobStepJobStep2(null))
                .build();
    }

    @Bean
    public Step jobStepJobStep1(JobLauncher jobLauncher) {
        return this.stepBuilderFactory
                .get("jobStepJobStep1")
                .job(personJob())
                .launcher(jobLauncher)
                .parametersExtractor(jobParametersExtractor())
                .build();
    }

    @Bean
    public Step jobStepJobStep2(JobLauncher jobLauncher) {
        return this.stepBuilderFactory
                .get("jobStepJobStep2")
                .job(creditJob())
                .launcher(jobLauncher)
                .parametersExtractor(jobParametersExtractor())
                .build();
    }

    @Bean
    public DefaultJobParametersExtractor jobParametersExtractor() {
        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();

        // extractor.setKeys(new String[] { "input.file" });
        extractor.setKeys(new String[] {});

        return extractor;
    }

    // ------------------------------------------------

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
