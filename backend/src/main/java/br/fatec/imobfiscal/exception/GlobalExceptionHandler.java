package br.fatec.imobfiscal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

// Captura exceções lançadas em qualquer Controller e retorna JSON padronizado
// Sem isso, o Spring retorna o formato padrão sem as mensagens de validação
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Erros de validação (@NotNull, @NotBlank, @Size etc. no DTO)
    // Ex: locadorId nulo → 400 com { "campo": "mensagem de erro" }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errosPorCampo = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errosPorCampo.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // Pega a primeira mensagem para exibição principal
        String primeiraMensagem = errosPorCampo.values().stream()
                .findFirst()
                .orElse("Dados inválidos");

        Map<String, Object> corpo = new HashMap<>();
        corpo.put("message", primeiraMensagem);
        corpo.put("errors", errosPorCampo);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    // Erros com status HTTP explícito (lançados com ResponseStatusException).
    // Ex: login inválido → 401; e-mail já cadastrado → 409.
    // Precisa vir ANTES do handler genérico de RuntimeException, senão tudo
    // viraria 400 (ResponseStatusException também é uma RuntimeException).
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("message", ex.getReason());

        return ResponseEntity.status(ex.getStatusCode()).body(corpo);
    }

    // Erros de negócio lançados manualmente no Model/DAO (RuntimeException)
    // Ex: "Locador não encontrado", "Imóvel não encontrado"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }
}
