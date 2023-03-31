package africa.semicolon.uberdeluxe;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Uber Deluxe", version = "0.0.1"))
public class UberDeluxeApplication {

	public static void main(String[] args) {
		SpringApplication.run(UberDeluxeApplication.class, args);
	}

}
