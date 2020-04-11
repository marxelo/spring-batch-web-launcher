package com.marxelo.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person {

    public static final String PERSON_LINE = "N";

    private String lineType;
    private String firstName;
    private String lastName;
    private Address address;
    private Profession profession;

}