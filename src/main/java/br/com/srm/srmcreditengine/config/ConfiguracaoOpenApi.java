package br.com.srm.srmcreditengine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoOpenApi {

    @Bean
    public OpenAPI openApiPersonalizada() {
        return new OpenAPI()
                .info(new Info()
                        .title("SRM Credit Engine API")
                        .description("Motor de credito para precificacao e liquidacao de recebiveis, com gestao de cambio e relatorios analiticos.")
                        .version("v1")
                        .contact(new Contact().name("SRM")));
    }
}
