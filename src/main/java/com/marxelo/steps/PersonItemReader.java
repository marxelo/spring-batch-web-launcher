package com.marxelo.steps;

import com.marxelo.models.dtos.Person;
import com.marxelo.steps.mappers.AddressFieldSetMapper;
import com.marxelo.steps.mappers.PersonFieldSetMapper;
import com.marxelo.steps.mappers.ProfessionFieldSetMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

public class PersonItemReader implements ItemStreamReader<Person> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonItemReader.class);
    private SingleItemPeekableItemReader<FieldSet> delegate;

    @SuppressWarnings("unused")
    private MultiResourceItemReader<FieldSet> resourceItemReader;

    public PersonItemReader(MultiResourceItemReader<FieldSet> multiResourceItemReader) {

        this.resourceItemReader = multiResourceItemReader;
    }

    @Nullable
    @Override
    public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        LOGGER.info("start read");

        Person record = null;

        FieldSet fieldSet;

        while ((fieldSet = delegate.read()) != null) {
            String prefix = fieldSet.readString(0);
            if (prefix.equals("N")) {
                record = new Person();
                record = personMapper().mapFieldSet(fieldSet);
            } else if (prefix.equals("A")) {
                record.setAddress(addressMapper().mapFieldSet(fieldSet));
            } else if (prefix.equals("P")) {
                record.setProfession(professionMapper().mapFieldSet(fieldSet));
            }

            FieldSet nextLine = delegate.peek();
            if (nextLine == null || nextLine.readString(0).equals("N")) {
                break;
            }

        }
        LOGGER.info("end read");
        return record;

    }

    public SingleItemPeekableItemReader<FieldSet> getSingalPeekable() {
        return delegate;
    }

    public void setSingalPeekable(SingleItemPeekableItemReader<FieldSet> singalPeekable) {
        this.delegate = singalPeekable;

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
        //  Auto-generated method stub

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        //  Auto-generated method stub

    }

    @Override
    public void close() throws ItemStreamException {
        //  Auto-generated method stub

    }

}