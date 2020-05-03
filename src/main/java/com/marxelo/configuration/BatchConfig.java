package com.marxelo.configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
import com.marxelo.steps.tokenizers.PersonCompositeLineTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
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

    static Resource[] resourceData = new Resource[] { new ClassPathResource("data1.csv") };
    static Resource[] resourcePessoas = new Resource[] { new ClassPathResource("pessoas.txt"),
            new ClassPathResource("pessoas-20200606.txt") };

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

    String BASE_DIR = "src/main/resources/";

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
                .name("itemReader")
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    public ItemReader<String> myMultiResourceItemReader() {
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
                .reader(myMultiResourceItemReader())
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

    //  <--------------------------- PersonX --------------------------------->

    @Bean
    public FlatFileItemReader<FieldSet> reader() {
        FlatFileItemReader<FieldSet> reader = new FlatFileItemReader<>();
        final DefaultLineMapper<FieldSet> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(personTokenizers());
        defaultLineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());
        reader.setLineMapper(defaultLineMapper);
        // reader.setLineMapper(new DefaultLineMapper() {
        //     {
        //         setLineTokenizer(personTokenizers());
        //     }
        //     {
        //         setFieldSetMapper(new PassThroughFieldSetMapper());
        //     }
        // });
        reader.setBufferedReaderFactory(new MyBufferedReaderFactory());
        return reader;
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<FieldSet> multiResourceItemReader(@Value("#{jobParameters['fileDate']}") String fileDate) {
        System.out.println("FileDate in reader.....:" + fileDate);

        String formattedString = fileDate;
        if (Objects.isNull(formattedString)) {
            LocalDate localDate = LocalDate.now().minusDays(1L);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            formattedString = localDate.format(formatter);
        } else {
            LOGGER.info("Utilizando a data de processamento = " + formattedString);
        }

        Resource[] inputResources = {
                new FileSystemResource(
                        BASE_DIR + "pessoas-" + formattedString + ".S1.txt"),
                new FileSystemResource(
                        BASE_DIR + "pessoas-" + formattedString + ".S2.txt") };
        MultiResourceItemReader<FieldSet> resourceItemReader = new MultiResourceItemReader<FieldSet>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }

    @Bean
    public SingleItemPeekableItemReader<FieldSet> readerPeek() {
        SingleItemPeekableItemReader<FieldSet> reader = new SingleItemPeekableItemReader<>();
        reader.setDelegate(multiResourceItemReader(null));
        return reader;
    }

    @Bean
    @StepScope
    public PersonItemReader personItemReader() {
        PersonItemReader reader = new PersonItemReader(null);
        reader.setSingalPeekable(readerPeek());
        return reader;
    }

    @Bean
    public Step personStep() {
        return stepBuilderFactory.get("personStep").<Person, Person> chunk(1)
                .reader(personItemReader())
                .processor(personItemProcessor())
                .writer(personWriter())
                .faultTolerant()
                .skipPolicy(new MySkipPolicy())
                .listener(new PersonSkipListener())
                // .stream(personFileItemReader(WILL_BE_INJECTED))
                .listener(new personStepExecutionListener())
                .build();
    }

    //  <--------------------------- Person --------------------------------->
    @Bean
    public PersonCompositeLineTokenizer personTokenizers() {
        PersonCompositeLineTokenizer tokenizers = new PersonCompositeLineTokenizer();
        return tokenizers;
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
    public Job personJob() {
        return jobBuilderFactory.get("personJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadFileStep())
                .next(personStep())
                .listener(new JobResultListener())
                .build();
    }

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
