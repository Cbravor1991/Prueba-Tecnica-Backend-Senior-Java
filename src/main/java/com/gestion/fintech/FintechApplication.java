package com.gestion.fintech;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info= @Info(title="Library APIS", version = "1.0", description = "Endpoints"))
public class FintechApplication {

	public static void main(String[] args) {
		SpringApplication.run(FintechApplication.class, args);
		System.out.println("Hola, Mundo!");
	}

}
