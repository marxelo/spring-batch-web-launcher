package com.marxelo.steps;

import com.marxelo.models.dtos.Address;
import com.marxelo.models.dtos.Person;
import com.marxelo.models.dtos.Profession;
import com.marxelo.steps.mappers.AddressFieldSetMapper;
import com.marxelo.steps.mappers.PersonFieldSetMapper;
import com.marxelo.steps.mappers.ProfessionFieldSetMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

public class PersonItemReader implements ItemStreamReader<Person> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonItemReader.class);

    private Person person;

    private Person personCache;

    private boolean recordFinished;

    private SingleItemPeekableItemReader<FieldSet> fieldSetReader;

    @Nullable
    @Override
    public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        recordFinished = false;

        while (!recordFinished) {
            process(fieldSetReader.read());
        }

        LOGGER.debug("Mapped: " + person);

        Person result = person;
        person = null;

        return result;

    }

    private void process(FieldSet fieldSet) throws Exception {

        // finish processing if we hit the end of file
        // fieldSetReader.peek();
        System.out.println("Antes peek................::::::::");
        String line = fieldSetReader.peek().readString(0);
        System.out.println("Resultado peek................::::::::" + line);
        if (fieldSet == null) {
            LOGGER.debug("FINISHED");
            person = personCache;
            personCache = null;
            recordFinished = true;
            return;
        }

        String lineId = fieldSet.readString(0);

        if (Person.PERSON_LINE.equals(lineId)) {
            if (personCache == null) {
                LOGGER.debug("STARTING NEW RECORD");
                LOGGER.debug("MAPPING person");
                personCache = personMapper().mapFieldSet(fieldSet);
            } else if (personCache != null) {
                LOGGER.debug("FINISHED NEW RECORD");
                LOGGER.debug("MAPPING person");
                person = personCache;

                LOGGER.debug("STARTING NEW RECORD");
                LOGGER.debug("MAPPING person");
                personCache = null;
                personCache = personMapper().mapFieldSet(fieldSet);

                recordFinished = true;
            }
        } else if (Address.ADDRESS_LINE.equals(lineId)) {
            LOGGER.debug("MAPPING ADDRESS");
            personCache.setAddress(addressMapper().mapFieldSet(fieldSet));

        } else if (Profession.PROFESSION_LINE.equals(lineId)) {
            LOGGER.debug("MAPPING PROFESSION");
            personCache.setProfession(professionMapper().mapFieldSet(fieldSet));

        } else {

            LOGGER.debug("Could not map LINE_ID=" + lineId);

        }
    }

    public void setFieldSetReader(SingleItemPeekableItemReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }

    @Bean
    public PersonFieldSetMapper personMapper() {
        PersonFieldSetMapper mapper = new PersonFieldSetMapper();
        return mapper;
    }

    @Bean
    public AddressFieldSetMapper addressMapper() {
        AddressFieldSetMapper mapper = new AddressFieldSetMapper();
        return mapper;
    }

    @Bean
    public ProfessionFieldSetMapper professionMapper() {
        ProfessionFieldSetMapper mapper = new ProfessionFieldSetMapper();
        return mapper;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() throws ItemStreamException {
        // TODO Auto-generated method stub

    }

}