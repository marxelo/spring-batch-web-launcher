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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Person [");
        if (address != null) {
            builder.append("address=").append(address.getAddressNumber()).append(", ");
        }
        if (firstName != null) {
            builder.append("firstName=").append(firstName).append(", ");
        }
        if (lastName != null) {
            builder.append("lastName=").append(lastName).append(", ");
        }
        if (lineType != null) {
            builder.append("lineType=").append(lineType).append(", ");
        }
        if (profession != null) {
            builder.append("profession=").append(profession.getProfession());
        }
        builder.append("]");
        return builder.toString();
    }

}