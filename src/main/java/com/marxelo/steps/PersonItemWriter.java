package com.marxelo.steps;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.marxelo.models.dtos.Person;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class PersonItemWriter implements ItemWriter<Person> {


    // private Person person;

    @Override
    public void write(List<? extends Person> items) throws Exception {
        if (!items.isEmpty()) {
            for (final Person person : items) {
                log.debug("writing item... " + person);
            }
        }

    }

}
