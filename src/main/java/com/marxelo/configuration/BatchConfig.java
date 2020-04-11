package com.marxelo.configuration;

import javax.annotation.PreDestroy;

import com.marxelo.steps.CreditItemProcessor;
import com.marxelo.steps.DebitItemProcessor;
import com.marxelo.steps.skippers.MySkipListener;
import com.marxelo.steps.skippers.MySkipPolicy;
import com.marxelo.steps.tasklets.DownloadFileTasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    static Resource[] resources = new Resource[] { new ClassPathResource("data1.csv"), new ClassPathResource("data2.csv") };

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

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
        System.out.println(".........FlatFileItemReader.......... ");

        return new FlatFileItemReaderBuilder<String>()
                .name("itemReader")
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    public ItemReader<String> multiResourceItemReader() {
        System.out.println("........ItemReader............. ");

        final MultiResourceItemReader<String> reader = new MultiResourceItemReader<>();
        reader.setResources(resources);
        reader.setDelegate(itemReader());
        return reader;
    }

    @Bean
    public CreditItemProcessor creditItemProcessor() {
        System.out.println(".........CreditItemProcessor.............");

        CreditItemProcessor processor = new CreditItemProcessor();
        return processor;
    }

    @Bean
    public DebitItemProcessor debitItemProcessor() {
        DebitItemProcessor processor = new DebitItemProcessor();
        return processor;
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        System.out.println("I'm gonna write something ");
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
    public Step debitStep() {
        return stepBuilderFactory.get("debitStep").<String, String> chunk(1)
                .reader(multiResourceItemReader())
                .processor(debitItemProcessor())
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

    @Bean
    public Job debitJob() {
        return jobBuilderFactory.get("debitJob")
                .incrementer(new RunIdIncrementer())
                .start(downloadFileStep())
                .next(debitStep())
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

}
