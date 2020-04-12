package com.marxelo.steps;

import com.marxelo.models.dtos.Person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonItemProcessor.class);

    static Resource[] resources = new Resource[] { new ClassPathResource("pessoas.txt") };

    private StepExecution stepExecution;

    @Nullable
    @Override
    public Person process(final Person person) throws Exception {
        final ExecutionContext executionContext = stepExecution.getExecutionContext();
        final int resourceIndex = executionContext.getInt("MultiResourceItemReader.resourceIndex");
        LOGGER.info("PersonItemprocessor-processing item: " + person.toString() + " coming from resource = "
                + resources[resourceIndex + 1]);
        System.out.println("PersonItemprocessor-processing item: " + person.toString() + " coming from resource = "
                + resources[resourceIndex + 1]);
        return person;
    }
}