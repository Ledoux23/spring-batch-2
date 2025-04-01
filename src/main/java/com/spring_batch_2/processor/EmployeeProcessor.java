package com.spring_batch_2.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.spring_batch_2.model.Employee;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee employee) {
        employee.setName(employee.getName().toUpperCase());
        return employee;
    }
}
