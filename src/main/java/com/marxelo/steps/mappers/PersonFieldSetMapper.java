package com.marxelo.steps.mappers;

import com.marxelo.models.dtos.Person;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class PersonFieldSetMapper implements FieldSetMapper<Person> {
    public static final String LINE_TYPE_COLUMN = "lineType";
    public static final String FIRST_NAME_COLUMN = "firstName";
    public static final String LAST_NAME_COLUMN = "lastName";

    @Override
    public Person mapFieldSet(FieldSet fieldSet) {
        Person person = new Person();
        person.setLineType(
                fieldSet.readString(LINE_TYPE_COLUMN));
        person.setFirstName(
                fieldSet.readString(FIRST_NAME_COLUMN));
        person.setLastName(fieldSet.readString(LAST_NAME_COLUMN));

        return person;

    }
}