package br.fatec.imobfiscal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Ponto de entrada da aplicação Spring Boot
// @SpringBootApplication ativa: auto-configuração, scan de componentes e configuração
@SpringBootApplication
public class ImobFiscalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImobFiscalApplication.class, args);
    }
}
