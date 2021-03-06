package com.marxelo.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    public static final String ADDRESS_LINE = "A";

    private String lineType;
    private String streetName;
    private int addressNumber;

}