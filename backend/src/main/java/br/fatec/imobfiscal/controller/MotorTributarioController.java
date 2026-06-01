package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.dto.motor.CalculoRequest;
import br.fatec.imobfiscal.dto.motor.ResultadoCalculoDTO;
import br.fatec.imobfiscal.service.MotorTributarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// UC-003 / UC-008: Motor Tributário — calcula IBS/CBS/Split Payment
@RestController
@RequestMapping("/api/motor-tributario")
@RequiredArgsConstructor
public class MotorTributarioController {

    private final MotorTributarioService motorTributarioService;

    @PostMapping("/calcular")
    public ResponseEntity<ResultadoCalculoDTO> calcular(
            @Valid @RequestBody CalculoRequest request) {
        return ResponseEntity.ok(motorTributarioService.calcular(request));
    }
}
