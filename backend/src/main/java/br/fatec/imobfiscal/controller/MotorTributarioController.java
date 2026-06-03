package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.service.MotorTributarioService;
import br.fatec.imobfiscal.model.CalculoRequest;
import br.fatec.imobfiscal.model.ResultadoCalculoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/motor-tributario")
@RequiredArgsConstructor
public class MotorTributarioController {

    private final MotorTributarioService motorTributarioService;

    @PostMapping("/calcular")
    public ResponseEntity<ResultadoCalculoDTO> calcular(@Valid @RequestBody CalculoRequest request) {
        return ResponseEntity.ok(motorTributarioService.calcular(request));
    }
}
