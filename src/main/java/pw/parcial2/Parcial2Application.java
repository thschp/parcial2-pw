package pw.parcial2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "pw.parcial2")
@EntityScan(basePackages = "pw.parcial2.model")
@EnableJpaRepositories(basePackages = "pw.parcial2.repository")
public class Parcial2Application {

	public static void main(String[] args) {
		SpringApplication.run(Parcial2Application.class, args);
	}

}
