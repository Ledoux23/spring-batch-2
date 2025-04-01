##Un exemple complet de traitement batch où :

✅ Les données sont extraites d'une base de données H2
✅ Les données sont transformées (exemple : mise en majuscules)
✅ Les résultats sont écrits dans un fichier CSV

📌 Scénario
Nous avons une base de données contenant une table employees avec des employés.
Le batch extrait ces données, met les noms en majuscules, et écrit le résultat dans un fichier CSV.

📂 1️⃣ Structure du projet

src/main/java/com/example/batch
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

📝 2️⃣ Dépendances pom.xml

Ajoute ces dépendances si elles ne sont pas déjà présentes :

<dependencies>
    <!-- Spring Boot & Batch -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-batch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Base de données H2 -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>

🛠️ 3️⃣ Configuration application.properties

Ajoute ces lignes pour configurer la base H2 et le batch :

# Base de données H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true

# Création automatique des tables
spring.jpa.hibernate.ddl-auto=update
spring.batch.jdbc.initialize-schema=always

🏷️ 4️⃣ Modèle Employee.java

Créer une entité Employee qui sera stockée en base.

package com.example.batch.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String department;
    private double salary;

    public Employee() {}

    public Employee(String name, String department, double salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}

🏛️ 5️⃣ Repository EmployeeRepository.java

Interface pour récupérer les employés depuis la base.

package com.example.batch.repository;

import com.example.batch.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

📥 6️⃣ Lecture depuis la base JdbcEmployeeReader.java

Lecture des employés depuis la base avec JdbcCursorItemReader.

package com.example.batch.config;

import com.example.batch.model.Employee;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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

🔄 7️⃣ Transformation des données EmployeeProcessor.java

Mettre les noms en majuscule.

package com.example.batch.processor;

import com.example.batch.model.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee employee) {
        employee.setName(employee.getName().toUpperCase());
        return employee;
    }
}

📝 8️⃣ Écriture dans un fichier CSV CsvWriter.java

Les données transformées seront enregistrées dans employees_output.csv.

package com.example.batch.writer;

import com.example.batch.model.Employee;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class CsvWriter {

    @Bean
    public FlatFileItemWriter<Employee> writer() {
        return new FlatFileItemWriterBuilder<Employee>()
                .name("employeeWriter")
                .resource(new FileSystemResource("employees_output.csv"))
                .delimited()
                .names("name", "department", "salary")
                .build();
    }
}

⚙ 9️⃣ Configuration Batch BatchConfig.java

Définition des étapes du batch.

package com.example.batch.config;

import com.example.batch.model.Employee;
import com.example.batch.processor.EmployeeProcessor;
import com.example.batch.writer.CsvWriter;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

🏁 🔟 Lancement du batch avec SpringBatchApplication.java

Ajoute quelques employés pour tester.

package com.example.batch;

import com.example.batch.model.Employee;
import com.example.batch.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class SpringBatchApplication implements CommandLineRunner {

    private final EmployeeRepository repository;

    public SpringBatchApplication(EmployeeRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
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

