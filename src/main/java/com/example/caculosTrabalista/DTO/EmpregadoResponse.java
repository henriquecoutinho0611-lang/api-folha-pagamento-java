package com.example.caculosTrabalista.DTO;

import java.math.BigDecimal;

public record EmpregadoResponse(
        BigDecimal salarioBruto,
        BigDecimal salarioLiquido,
        BigDecimal DescontoInss,
        BigDecimal descontoConducao,
        BigDecimal descontoIRRF,
        BigDecimal decontopensao,
        BigDecimal descontoTotal,
        BigDecimal acrecinoferias) {
}
