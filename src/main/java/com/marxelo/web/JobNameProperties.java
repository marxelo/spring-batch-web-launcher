package com.marxelo.web;

import java.util.SortedMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:jobnamevalues.properties")
@ConfigurationProperties(prefix = "radiovalue")
public class JobNameProperties {
    private SortedMap<String, String> statusOptions;

    public SortedMap<String, String> getStatusOptions() {
        return statusOptions;
    }

    public void setStatusOptions(SortedMap<String, String> statusOptions) {
        this.statusOptions = statusOptions;
    }

}
