package com.marxelo.steps.tokenizers;

import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;

public class PersonTokenizer extends FixedLengthTokenizer {
    public PersonTokenizer() {
        String[] names = new String[] { "lineType", "firstName", "lastName" };

        Range[] ranges = new Range[] { new Range(1, 1), new Range(2, 13), new Range(14, 30) };

        this.setNames(names);
        this.setColumns(ranges);
        this.setStrict(false);
    }
}