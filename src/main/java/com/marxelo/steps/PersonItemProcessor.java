package com.marxelo.steps;

import com.marxelo.models.dtos.Person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Nullable
    @Override
    public Person process(final Person person) throws Exception {
        LOGGER.debug("Processing item: " + person.toString());
        System.out.println("Processing item: " + person.toString());
        return person;
    }
}