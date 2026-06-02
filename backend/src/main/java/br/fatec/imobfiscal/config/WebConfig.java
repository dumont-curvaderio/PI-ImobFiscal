package br.fatec.imobfiscal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

// Configuração de CORS da API.
//
// CORS (Cross-Origin Resource Sharing) é a regra do navegador que decide se um
// site (ex.: o frontend rodando em http://localhost:5173 ou na Vercel) pode
// chamar esta API, que roda em outro endereço (outra "origem"). Sem liberar a
// origem do frontend aqui, o navegador bloqueia as requisições.
//
// Antes essa configuração ficava dentro do Spring Security. Como removemos o
// Spring Security (a API não usa mais JWT), o CORS passou a ser configurado
// diretamente pelo Spring MVC, através desta classe.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Lê as origens permitidas do application.properties (app.cors.allowed-origins).
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Separa a lista "url1,url2" e remove espaços em branco.
        String[] origens = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        registry.addMapping("/api/**")
                // allowedOriginPatterns aceita curinga: http://localhost:* cobre
                // qualquer porta do Vite (5173, 5174, ...).
                .allowedOriginPatterns(append(origens, "http://localhost:*"))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // Pequeno auxiliar para adicionar o padrão localhost à lista de origens.
    private String[] append(String[] base, String extra) {
        String[] novo = Arrays.copyOf(base, base.length + 1);
        novo[base.length] = extra;
        return novo;
    }
}
