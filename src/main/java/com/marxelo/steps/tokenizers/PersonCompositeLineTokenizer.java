package com.marxelo.steps.tokenizers;

import java.util.HashMap;
import java.util.Map;

import com.marxelo.models.dtos.Address;
import com.marxelo.models.dtos.Person;
import com.marxelo.models.dtos.Profession;

import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;

public class PersonCompositeLineTokenizer extends PatternMatchingCompositeLineTokenizer {

    public PersonCompositeLineTokenizer() {
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        tokenizers.put("*", defaulTokenizer());
        tokenizers.put(Person.PERSON_LINE + "*", personTokenizer());
        tokenizers.put(Address.ADDRESS_LINE + "*", addressTokenizer());
        tokenizers.put(Profession.PROFESSION_LINE + "*", professionTokenizer());
        this.setTokenizers(tokenizers);
    }

    @Bean
    public LineTokenizer defaulTokenizer() {
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        String[] names = new String[] { "lineType", "allColumns" };
        Range[] ranges = new Range[] { new Range(1, 2), new Range(5, 30) };
        tokenizer.setNames(names);
        tokenizer.setColumns(ranges);
        tokenizer.setStrict(false);
        return tokenizer;
    }

    @Bean
    public LineTokenizer personTokenizer() {
        PersonTokenizer tokenizer = new PersonTokenizer();
        return tokenizer;
    }

    @Bean
    public LineTokenizer addressTokenizer() {
        AddressTokenizer tokenizer = new AddressTokenizer();
        return tokenizer;
    }

    @Bean
    public LineTokenizer professionTokenizer() {
        ProfessionTokenizer tokenizer = new ProfessionTokenizer();
        return tokenizer;
    }
}