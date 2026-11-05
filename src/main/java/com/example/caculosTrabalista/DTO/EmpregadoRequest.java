package com.example.caculosTrabalista.DTO;

import java.math.BigDecimal;

public record EmpregadoRequest(  BigDecimal salario,
         boolean conducao,
         int diasTrabalhados,
        int mesesTrabalhados,
        int anosTrabalhados
) {}
