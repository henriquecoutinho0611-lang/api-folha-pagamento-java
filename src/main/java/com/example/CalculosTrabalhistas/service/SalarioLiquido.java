package com.example.CalculosTrabalhistas.service;

import com.example.CalculosTrabalhistas.DTO.EmpregadoRequest;
import com.example.CalculosTrabalhistas.DTO.EmpregadoResponse;
import com.example.CalculosTrabalhistas.entity.Empregado;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class SalarioLiquido {

    private final EmpregadoService calculos;

    public SalarioLiquido(EmpregadoService calculos) {
        this.calculos = calculos;
    }

    public EmpregadoResponse salarioLiquido(EmpregadoRequest requisicao) {
        Empregado empregado = calculos.toEntity(requisicao);
        int diasTrabalhados = empregado.getDiasTrabalhados();

        if (diasTrabalhados <= 0 || diasTrabalhados > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A quantidade de dias trabalhados ou de férias deve estar entre 1 e 30.");
        }

        BigDecimal salarioBruto = empregado.getSalario();
        BigDecimal salarioProporcional = calculos.salarioDeduzido(requisicao);
        BigDecimal salarioAposInss = calculos.faixaInss(salarioProporcional);
        BigDecimal descontoInss = salarioProporcional.subtract(salarioAposInss);
        BigDecimal descontoValeTransporte = salarioBruto.multiply(new BigDecimal("0.06"));
        BigDecimal descontoIrrf = calculos.faixaIRRF(salarioAposInss);

        if (empregado.isConducao() && descontoIrrf.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal salarioLiquido = salarioAposInss.subtract(descontoValeTransporte);
            BigDecimal descontoTotal = descontoInss.add(descontoValeTransporte);
            return new EmpregadoResponse(calculos.arredondar(salarioProporcional),
                    calculos.arredondar(salarioLiquido), calculos.arredondar(descontoInss),
                    calculos.arredondar(descontoValeTransporte), BigDecimal.ZERO, BigDecimal.ZERO,
                    calculos.arredondar(descontoTotal), BigDecimal.ZERO);
        }
        if (empregado.isConducao() && descontoIrrf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal salarioLiquido = salarioAposInss.subtract(descontoValeTransporte).subtract(descontoIrrf);
            BigDecimal descontoTotal = descontoInss.add(descontoValeTransporte).add(descontoIrrf);
            return new EmpregadoResponse(calculos.arredondar(salarioProporcional),
                    calculos.arredondar(salarioLiquido), calculos.arredondar(descontoInss),
                    calculos.arredondar(descontoValeTransporte), calculos.arredondar(descontoIrrf),
                    BigDecimal.ZERO, calculos.arredondar(descontoTotal), BigDecimal.ZERO);
        }
        if (descontoIrrf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal salarioLiquido = salarioAposInss.subtract(descontoIrrf);
            BigDecimal descontoTotal = descontoInss.add(descontoIrrf);
            return new EmpregadoResponse(calculos.arredondar(salarioProporcional),
                    calculos.arredondar(salarioLiquido), calculos.arredondar(descontoInss),
                    BigDecimal.ZERO, calculos.arredondar(descontoIrrf), BigDecimal.ZERO,
                    calculos.arredondar(descontoTotal), BigDecimal.ZERO);
        }
        if (descontoIrrf.compareTo(BigDecimal.ZERO) <= 0) {
            return new EmpregadoResponse(calculos.arredondar(salarioProporcional),
                    calculos.arredondar(salarioAposInss), calculos.arredondar(descontoInss),
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    calculos.arredondar(descontoInss), BigDecimal.ZERO);
        }
        return null;
    }
}