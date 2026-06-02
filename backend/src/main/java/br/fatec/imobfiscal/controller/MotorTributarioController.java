package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.model.MotorTributario;
import br.fatec.imobfiscal.view.motor.CalculoRequest;
import br.fatec.imobfiscal.view.motor.ResultadoCalculoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// UC-003 / UC-008: Motor Tributário — calcula IBS/CBS/Split Payment.
// O controller injeta o componente de lógica do model diretamente.
@RestController
@RequestMapping("/api/motor-tributario")
@RequiredArgsConstructor
public class MotorTributarioController {

    private final MotorTributario motorTributario;

    @PostMapping("/calcular")
    public ResponseEntity<ResultadoCalculoDTO> calcular(
            @Valid @RequestBody CalculoRequest request) {
        return ResponseEntity.ok(motorTributario.calcular(request));
    }
}
