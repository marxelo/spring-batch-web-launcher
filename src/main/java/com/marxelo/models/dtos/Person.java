package com.marxelo.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// @ToString
public class Person {

    public static final String PERSON_LINE = "N";

    private String lineType;
    private String firstName;
    private String lastName;
    private Address address;
    private Profession profession;
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        return "Person [firstName=" + firstName + ", lastName=" + lastName + ", address=" + address.getStreetName()
                + " " + address.getAddressNumber() + ", profession=" + profession.getProfession() + "]";
    }

}