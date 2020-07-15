package com.marxelo.steps.tokenizers;

import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;

public class AddressTokenizer extends FixedLengthTokenizer {
    public AddressTokenizer() {
        String[] names = new String[] { "lineType", "streetName", "addressNumber" };

        Range[] ranges = new Range[] { new Range(1, 1), new Range(2, 25), new Range(26, 30) };

        this.setNames(names);
        this.setColumns(ranges);
        this.setStrict(false);
    }
}