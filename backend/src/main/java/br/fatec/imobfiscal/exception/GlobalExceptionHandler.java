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

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errosPorCampo = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errosPorCampo.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        String primeiraMensagem = errosPorCampo.values().stream()
                .findFirst()
                .orElse("Dados inválidos");

        Map<String, Object> corpo = new HashMap<>();
        corpo.put("message", primeiraMensagem);
        corpo.put("errors", errosPorCampo);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("message", ex.getReason());

        return ResponseEntity.status(ex.getStatusCode()).body(corpo);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }
}
