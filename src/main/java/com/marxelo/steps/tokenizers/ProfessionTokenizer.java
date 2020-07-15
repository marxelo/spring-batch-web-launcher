package com.marxelo.steps.tokenizers;

import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;

public class ProfessionTokenizer extends FixedLengthTokenizer {
    public ProfessionTokenizer() {
        String[] names = new String[] { "lineType", "profession" };

        Range[] ranges = new Range[] { new Range(1, 1), new Range(2, 30) };

        this.setNames(names);
        this.setColumns(ranges);
        this.setStrict(false);
    }
}