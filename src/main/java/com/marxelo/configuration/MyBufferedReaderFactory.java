package com.marxelo.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

public class MyBufferedReaderFactory implements BufferedReaderFactory {

    private static final int BUFFER_SIZE = 8192;

    @Override
    public BufferedReader create(Resource resource, String encoding)
            throws UnsupportedEncodingException, IOException {
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding),
                BUFFER_SIZE);
    }

}
