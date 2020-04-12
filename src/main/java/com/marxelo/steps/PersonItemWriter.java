package com.marxelo.steps;

import java.util.List;

import com.marxelo.models.dtos.Person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class PersonItemWriter implements ItemWriter<Person> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonItemWriter.class);

    // private Person person;

    @Override
    public void write(List<? extends Person> items) throws Exception {
        if (!items.isEmpty()) {
            for (final Person person : items) {
                System.out.println("writing item..: " + person);
                LOGGER.debug("writing item..: " + person);
            }
        }

    }

}
