package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.model.MotorTributario;
import br.fatec.imobfiscal.view.motor.CalculoRequest;
import br.fatec.imobfiscal.view.motor.ResultadoCalculoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
