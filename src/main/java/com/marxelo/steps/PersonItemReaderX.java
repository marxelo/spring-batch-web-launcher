package com.marxelo.steps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.marxelo.configuration.MyBufferedReaderFactory;
import com.marxelo.models.dtos.Person;
import com.marxelo.steps.mappers.AddressFieldSetMapper;
import com.marxelo.steps.mappers.PersonFieldSetMapper;
import com.marxelo.steps.mappers.ProfessionFieldSetMapper;
import com.marxelo.steps.tokenizers.PersonCompositeLineTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;

public class PersonItemReaderX implements ItemStreamReader<Person> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonItemReaderX.class);
    private SingleItemPeekableItemReader<FieldSet> delegate;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        String formattedString = stepExecution.getJobParameters().getString("fileDate");
        System.out.println("hahahahah fileDate" + formattedString);

        if (Objects.isNull(formattedString)) {
            LocalDate localDate = LocalDate.now().minusDays(1L);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            formattedString = localDate.format(formatter);
        } else {
            LOGGER.info("Utilizando a data de processamento = " + formattedString);
        }

        FlatFileItemReader<FieldSet> reader = new FlatFileItemReader<>();

        // reader.setResource(new ClassPathResource("pessoas-20200606.txt"));
        reader.setResource(new FileSystemResource("src/main/resources/pessoas-" + formattedString + ".txt"));
        final DefaultLineMapper<FieldSet> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(personTokenizers());
        defaultLineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());
        reader.setLineMapper(defaultLineMapper);
        reader.setBufferedReaderFactory(new MyBufferedReaderFactory());
        delegate = new SingleItemPeekableItemReader<>();
        delegate.setDelegate(reader);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        delegate.open(ec);
    }

    @Override
    public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        LOGGER.info("start read");
        System.out.println("start read");

        Person record = null;

        FieldSet fieldSet;

        while ((fieldSet = delegate.read()) != null) {
            String prefix = fieldSet.readString(0);
            // String prefix = fieldSet.readString("lineType");
            if (prefix.equals("N")) {
                record = new Person();
                record = personMapper().mapFieldSet(fieldSet);
            } else if (prefix.equals("A")) {
                record.setAddress(addressMapper().mapFieldSet(fieldSet));
            } else if (prefix.equals("P")) {
                record.setProfession(professionMapper().mapFieldSet(fieldSet));
            }

            FieldSet nextLine = delegate.peek();
            if (nextLine == null || nextLine.readString(0).equals("N")) {
                break;
            }

        }
        LOGGER.info("end read");
        System.out.println("end read");
        return record;
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {
        delegate.update(ec);
    }

    @Bean
    public PersonFieldSetMapper personMapper() {
        PersonFieldSetMapper mapper = new PersonFieldSetMapper();
        return mapper;
    }

    @Bean
    public AddressFieldSetMapper addressMapper() {
        AddressFieldSetMapper mapper = new AddressFieldSetMapper();
        return mapper;
    }

    @Bean
    public ProfessionFieldSetMapper professionMapper() {
        ProfessionFieldSetMapper mapper = new ProfessionFieldSetMapper();
        return mapper;
    }

    @Bean
    public PersonCompositeLineTokenizer personTokenizers() {
        PersonCompositeLineTokenizer tokenizers = new PersonCompositeLineTokenizer();
        return tokenizers;
    }
}