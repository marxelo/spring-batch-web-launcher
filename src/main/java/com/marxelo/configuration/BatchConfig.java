package com.marxelo.configuration;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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

import com.marxelo.listeners.JobResultListener;
import com.marxelo.listeners.MyStepExecutionListener;
import com.marxelo.listeners.DownloadCreditFileTaskletListener;
// import com.marxelo.listeners.DownloadPersonFileTaskletListener;
import com.marxelo.models.dtos.Person;
import com.marxelo.steps.CreditItemProcessor;
import com.marxelo.steps.PersonItemProcessor;
import com.marxelo.steps.PersonItemReader;
import com.marxelo.steps.PersonItemWriter;
import com.marxelo.steps.skippers.MySkipListener;
import com.marxelo.steps.skippers.MySkipPolicy;
import com.marxelo.steps.skippers.PersonSkipListener;
import com.marxelo.steps.tasklets.DownloadCreditFileTasklet;
import com.marxelo.steps.tasklets.DownloadPersonFileTasklet;
import com.marxelo.steps.tasklets.PersonJobWeekendTasklet;

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

    // @Autowired
    // private DataSource dataSource;

    // @Autowired
    // private Person person;

    // @Bean
    // public BatchConfigurer configurer(BatchProperties properties,
    // @Qualifier("batchDataSource") DataSource dataSource) {
    // return new BasicBatchConfigurer(properties, null, null) {
    // @Override
    // public PlatformTransactionManager getTransactionManager() {
    // return transactionManager;
    // }
    // };
    // }

    // <-------- Credit Job Configuration ----------------->

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
                .<String, String>chunk(1)
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
    public Step downloadCreditFileStep() {
        return stepBuilderFactory.get("downloadCreditFileStep")
                .listener(new DownloadCreditFileTaskletListener())
                .tasklet(new DownloadCreditFileTasklet())
                .build();
    }

    @Bean
    public Job creditJob() {
        return jobBuilderFactory.get("creditJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadCreditFileStep())
                .next(creditStep())
                .listener(new JobResultListener())
                .build();
    }
    @Bean
    public Job debitJob() {
        return jobBuilderFactory.get("debitJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadCreditFileStep())
                .next(creditStep())
                .listener(new JobResultListener())
                .build();
    }

    // <--------------------------- Person --------------------------------->



    @Bean
    public Step personJobWeekendStep() {
        return stepBuilderFactory.get("personJobWeekendStep")
                .tasklet(new PersonJobWeekendTasklet())
                .build();
    }

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
        return stepBuilderFactory.get("personStep")
                .<Person, Person>chunk(1)
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
    public Step downloadPersonFileStep() {
        return stepBuilderFactory.get("downloadPersonFileStep")
                .tasklet(new DownloadPersonFileTasklet())
                .build();
    }

    @Bean
    public Job personJob() {
        return jobBuilderFactory.get("personJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadPersonFileStep())
                .on("FILENOTFOUND").to(personJobWeekendStep())
                .from(downloadPersonFileStep())
                .on("COMPLETED").to(personStep())
                .end()
                .listener(new JobResultListener())                
                .build();
    }

    @Bean
    public Job slimPersonJob() {
        return jobBuilderFactory.get("slimPersonJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadPersonFileStep())
                   .on("COMPLETED").to(personStep())
                .from(downloadPersonFileStep())
                   .on("FILENOTFOUND")
                   .end()                
                .end()
                .listener(new JobResultListener())
                .build();
    }

    // <-------- Fim PersonStep com peek -------------->

    @Bean
    public Job principalJob() {
        return this.jobBuilderFactory
                .get("principalJob")
                .start(jobStepJobStep1(null))
                .next(jobStepJobStep2(null))
                .listener(new JobResultListener())
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
