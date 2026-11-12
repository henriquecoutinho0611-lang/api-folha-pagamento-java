package com.example.CalculosTrabalhistas.contoller;

import com.example.CalculosTrabalhistas.DTO.EmpregadoRequest;
import com.example.CalculosTrabalhistas.DTO.EmpregadoResponse;
import com.example.CalculosTrabalhistas.service.DecimoTerceiro;
import com.example.CalculosTrabalhistas.service.Ferias;
import com.example.CalculosTrabalhistas.service.SalarioLiquido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculos")
public class Controller {

    private final SalarioLiquido salarioLiquido;
    private final Ferias ferias;
    private final DecimoTerceiro decimoTerceiro;

    public Controller(SalarioLiquido salarioLiquido, Ferias ferias, DecimoTerceiro decimoTerceiro) {
        this.salarioLiquido = salarioLiquido;
        this.ferias = ferias;
        this.decimoTerceiro = decimoTerceiro;
    }

    @GetMapping("/status")
    public String status() {
        return "API disponível";
    }

    @PostMapping
    public ResponseEntity<EmpregadoResponse> salarioLiquido(@RequestBody EmpregadoRequest requisicao) {
        return ResponseEntity.status(HttpStatus.OK).body(salarioLiquido.salarioLiquido(requisicao));
    }

    @PostMapping("/decinoTeceiro")
    public ResponseEntity<EmpregadoResponse> decimo(@RequestBody EmpregadoRequest requisicao) {
        return ResponseEntity.status(HttpStatus.OK).body(decimoTerceiro.decimoTerceiro(requisicao));
    }

    @PostMapping("/ferias")
    public ResponseEntity<EmpregadoResponse> ferias(@RequestBody EmpregadoRequest requisicao) {
        return ResponseEntity.status(HttpStatus.OK).body(ferias.ferias(requisicao));
    }
}