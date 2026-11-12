package com.example.CalculosTrabalhistas.service;

import com.example.CalculosTrabalhistas.DTO.EmpregadoRequest;
import com.example.CalculosTrabalhistas.DTO.EmpregadoResponse;
import com.example.CalculosTrabalhistas.entity.Empregado;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DecimoTerceiro {

    private final EmpregadoService calculos;

    public DecimoTerceiro(EmpregadoService calculos) {
        this.calculos = calculos;
    }

    public EmpregadoResponse decimoTerceiro(EmpregadoRequest requisicao) {
        Empregado empregado = calculos.toEntity(requisicao);
        int mesesTrabalhados = empregado.getMesesTrabalhados();

        if (mesesTrabalhados < 1 || mesesTrabalhados > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Os meses trabalhados devem estar entre 1 e 12.");
        }

        BigDecimal salarioBruto = empregado.getSalario();
        BigDecimal valorDecimoTerceiro = salarioBruto.multiply(BigDecimal.valueOf(mesesTrabalhados))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        BigDecimal salarioAposInss = calculos.faixaInss(valorDecimoTerceiro);
        BigDecimal descontoInss = valorDecimoTerceiro.subtract(salarioAposInss);
        BigDecimal descontoIrrf = calculos.faixaIRRF(salarioAposInss);

        if (descontoIrrf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal salarioLiquido = salarioAposInss.subtract(descontoIrrf);
            BigDecimal descontoTotal = descontoInss.add(descontoIrrf);
            return new EmpregadoResponse(calculos.arredondar(valorDecimoTerceiro),
                    calculos.arredondar(salarioLiquido), calculos.arredondar(descontoInss),
                    BigDecimal.ZERO, calculos.arredondar(descontoIrrf), BigDecimal.ZERO,
                    calculos.arredondar(descontoTotal), BigDecimal.ZERO);
        }

        return new EmpregadoResponse(calculos.arredondar(valorDecimoTerceiro),
                calculos.arredondar(salarioAposInss), calculos.arredondar(descontoInss),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                calculos.arredondar(descontoInss), BigDecimal.ZERO);
    }
}