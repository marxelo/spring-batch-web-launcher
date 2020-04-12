package com.marxelo.steps.mappers;

import com.marxelo.models.dtos.Profession;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class ProfessionFieldSetMapper implements FieldSetMapper<Profession> {
    public static final String LINE_TYPE_COLUMN = "lineType";
    public static final String PROFESSION_COLUMN = "profession";

    @Override
    public Profession mapFieldSet(FieldSet fieldSet) {
        Profession profession = new Profession();
        profession.setLineType(
                fieldSet.readString(LINE_TYPE_COLUMN));
        profession.setProfession(
                fieldSet.readString(PROFESSION_COLUMN));

        return profession;

    }
}