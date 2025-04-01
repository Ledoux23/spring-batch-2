package com.spring_batch_2;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*
@SpringBootApplication
public class SpringBatch2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatch2Application.class, args);
	}

}
*/

import com.spring_batch_2.model.Employee;
import com.spring_batch_2.repository.EmployeeRepository;

@SpringBootApplication
public class SpringBatch2Application implements CommandLineRunner {

    private final EmployeeRepository repository;

    public SpringBatch2Application(EmployeeRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBatch2Application.class, args);
    }

    @Override
    public void run(String... args) {
        repository.saveAll(Arrays.asList(
                new Employee("Alice", "IT", 60000),
                new Employee("Bob", "HR", 50000),
                new Employee("Charlie", "Finance", 70000)
        ));
    }
}

/*
structure du projet :

│── config/
│   ├── BatchConfig.java         # Configuration du batch
│── model/
│   ├── Employee.java            # Entité JPA
│── processor/
│   ├── EmployeeProcessor.java   # Transformation des données
│── repository/
│   ├── EmployeeRepository.java  # Accès base de données
│── writer/
│   ├── CsvWriter.java           # Écriture dans un fichier CSV
│── SpringBatchApplication.java  # Classe principale
*/