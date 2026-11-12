package com.example.CalculosTrabalhistas.service;

import com.example.CalculosTrabalhistas.DTO.EmpregadoRequest;
import com.example.CalculosTrabalhistas.DTO.EmpregadoResponse;
import com.example.CalculosTrabalhistas.entity.Empregado;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class Ferias {

    private final EmpregadoService calculos;

    public Ferias(EmpregadoService calculos) {
        this.calculos = calculos;
    }

    public EmpregadoResponse ferias(EmpregadoRequest requisicao) {
        Empregado empregado = calculos.toEntity(requisicao);
        int diasTrabalhados = empregado.getDiasTrabalhados();

        if (diasTrabalhados < 1 || diasTrabalhados > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Os dias de férias devem estar entre 1 e 30.");
        }

        BigDecimal salarioProporcional = calculos.salarioDeduzido(requisicao);
        BigDecimal salarioFeriasBruto = salarioProporcional.multiply(new BigDecimal("1.33333"));
        BigDecimal salarioAposInss = calculos.faixaInss(salarioFeriasBruto);
        BigDecimal descontoInss = salarioFeriasBruto.subtract(salarioAposInss);
        BigDecimal adicionalFerias = salarioProporcional.multiply(new BigDecimal("0.33333"));
        BigDecimal descontoIrrf = calculos.faixaIRRF(salarioAposInss);

        if (descontoIrrf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal salarioLiquido = salarioAposInss.subtract(descontoIrrf);
            BigDecimal descontoTotal = descontoInss.add(descontoIrrf);
            return new EmpregadoResponse(calculos.arredondar(salarioProporcional),
                    calculos.arredondar(salarioLiquido), calculos.arredondar(descontoInss),
                    BigDecimal.ZERO, calculos.arredondar(descontoIrrf), BigDecimal.ZERO,
                    calculos.arredondar(descontoTotal), calculos.arredondar(adicionalFerias));
        }
        return new EmpregadoResponse(calculos.arredondar(salarioProporcional),
                calculos.arredondar(salarioAposInss), calculos.arredondar(descontoInss),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                calculos.arredondar(descontoInss), calculos.arredondar(adicionalFerias));
    }
}