package com.marxelo.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Profession {

    public static final String PROFESSION_LINE = "P";

    private String lineType;
    private String profession;

}