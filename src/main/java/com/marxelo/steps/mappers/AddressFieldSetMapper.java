package com.marxelo.steps.mappers;

import com.marxelo.models.dtos.Address;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class AddressFieldSetMapper implements FieldSetMapper<Address> {
    public static final String LINE_TYPE_COLUMN = "lineType";
    public static final String STREET_NAME_COLUMN = "streetName";
    public static final String ADDRESS_NUMBER_COLUMN = "addressNumber";

    @Override
    public Address mapFieldSet(FieldSet fieldSet) {
        Address address = new Address();
        address.setLineType(
                fieldSet.readString(LINE_TYPE_COLUMN));
        address.setStreetName(
                fieldSet.readString(STREET_NAME_COLUMN));
        address.setAddressNumber(fieldSet.readInt(ADDRESS_NUMBER_COLUMN, 0));

        return address;

    }
}