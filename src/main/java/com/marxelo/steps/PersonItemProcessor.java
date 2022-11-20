package com.marxelo.steps;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.marxelo.models.dtos.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {


    @Nullable
    @Override
    public Person process(final @NonNull Person person) throws Exception {
        log.debug("Processing item: " + person.toString());
        // Simula tempo de processamento por item
        try {
            Thread.sleep(250L);
            // log.info("awake");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return person;
    }
}