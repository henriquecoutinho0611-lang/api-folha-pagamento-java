package com.example.caculosTrabalista.contoller;

import com.example.caculosTrabalista.entity.Empregado;
import com.example.caculosTrabalista.service.EmpregadoService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/calculos")

public class Controller {



    private final EmpregadoService empregadoService;

    public Controller(EmpregadoService empregadoService) {
        this.empregadoService = empregadoService;
    }

    @GetMapping("/status")
    public String status() {

        return "API disponível";
    }

    @PostMapping()
    public double salarioLiquido(@RequestBody Empregado empregado){
        return empregadoService.salarioLiquido(empregado);
    }

    @PostMapping("/decinoTeceiro")
    public double decimo(@RequestBody Empregado empregado){
        return empregadoService.decinoTeceiro(empregado);
    }
    @PostMapping("/ferias")
    public double ferias(@RequestBody Empregado empregado){
        return empregadoService.ferias(empregado);
    }


}
