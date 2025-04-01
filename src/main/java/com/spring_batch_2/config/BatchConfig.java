package com.spring_batch_2.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.spring_batch_2.model.Employee;
import com.spring_batch_2.processor.EmployeeProcessor;
import com.spring_batch_2.reader.JdbcEmployeeReader;
import com.spring_batch_2.writer.CsvWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                     JdbcEmployeeReader reader, EmployeeProcessor processor, CsvWriter writer) {
        return new StepBuilder("step", jobRepository)
                .<Employee, Employee>chunk(5, transactionManager)
                .reader(reader.reader(null))
                .processor(processor)
                .writer(writer.writer())
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }
}

