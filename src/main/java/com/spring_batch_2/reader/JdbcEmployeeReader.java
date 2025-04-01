package com.spring_batch_2.reader;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring_batch_2.model.Employee;

@Configuration
public class JdbcEmployeeReader {

    @Bean
    public JdbcCursorItemReader<Employee> reader(DataSource dataSource) {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, name, department, salary FROM employees");
        reader.setRowMapper((rs, rowNum) -> new Employee(
                rs.getString("name"),
                rs.getString("department"),
                rs.getDouble("salary")
        ));
        return reader;
    }
}
