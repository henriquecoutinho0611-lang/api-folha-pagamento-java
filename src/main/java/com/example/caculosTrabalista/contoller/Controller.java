package com.example.caculosTrabalista.contoller;

import com.example.caculosTrabalista.DTO.EmpregadoRequest;
import com.example.caculosTrabalista.DTO.EmpregadoResponse;
import com.example.caculosTrabalista.service.EmpregadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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
    public ResponseEntity<EmpregadoResponse> salarioLiquido(@RequestBody EmpregadoRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(empregadoService.salarioLiquido(request));
    }

    @PostMapping("/decinoTeceiro")
    public ResponseEntity<EmpregadoResponse> decimo(@RequestBody EmpregadoRequest request) {


            return ResponseEntity.status(HttpStatus.OK).body(empregadoService.decimoTerceiro(request));
    }


    @PostMapping("/ferias")
    public ResponseEntity<EmpregadoResponse> ferias(@RequestBody EmpregadoRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(empregadoService.ferias(request));
    }


}
