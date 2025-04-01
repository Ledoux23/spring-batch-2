package com.spring_batch_2.writer;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.spring_batch_2.model.Employee;

@Configuration
public class CsvWriter {

    @Bean
    public FlatFileItemWriter<Employee> writer() {
        return new FlatFileItemWriterBuilder<Employee>()
                .name("employeeWriter")
                .resource(new FileSystemResource("src/main/resources/employees_output.csv"))
                .delimited()
                .names("name", "department", "salary")
                .build();
    }
}
