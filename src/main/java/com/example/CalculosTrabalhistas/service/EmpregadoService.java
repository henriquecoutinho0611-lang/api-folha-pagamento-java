package com.example.CalculosTrabalhistas.service;

import com.example.CalculosTrabalhistas.DTO.EmpregadoRequest;
import com.example.CalculosTrabalhistas.entity.Empregado;
import com.example.CalculosTrabalhistas.mapper.CaculosMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EmpregadoService {

    private final CaculosMapper calculosMapper;

    public EmpregadoService(CaculosMapper calculosMapper) {
        this.calculosMapper = calculosMapper;
    }

    public Empregado toEntity(EmpregadoRequest requisicao) {
        return calculosMapper.toEntity(requisicao);
    }

    public BigDecimal arredondar(BigDecimal valorParaArredondar) {
        return valorParaArredondar.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal salarioDeduzido(EmpregadoRequest requisicao) {
        Empregado empregado = toEntity(requisicao);
        BigDecimal salarioBruto = empregado.getSalario();
        BigDecimal diasTrabalhados = BigDecimal.valueOf(empregado.getDiasTrabalhados());
        return salarioBruto.multiply(diasTrabalhados)
                .divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal faixaIRRF(BigDecimal baseCalculo) {
        BigDecimal valorImposto = BigDecimal.ZERO;

        BigDecimal limiteFaixa1 = new BigDecimal("2428.80");
        BigDecimal limiteFaixa2 = new BigDecimal("2826.65");
        BigDecimal limiteFaixa3 = new BigDecimal("3751.05");
        BigDecimal limiteFaixa4 = new BigDecimal("4664.68");

        if (baseCalculo.compareTo(limiteFaixa1) <= 0) {
            valorImposto = BigDecimal.ZERO;
        } else if (baseCalculo.compareTo(limiteFaixa2) <= 0) {
            valorImposto = baseCalculo.multiply(new BigDecimal("0.075"))
                    .subtract(new BigDecimal("182.16"));
        } else if (baseCalculo.compareTo(limiteFaixa3) <= 0) {
            valorImposto = baseCalculo.multiply(new BigDecimal("0.15"))
                    .subtract(new BigDecimal("394.16"));
        } else if (baseCalculo.compareTo(limiteFaixa4) <= 0) {
            valorImposto = baseCalculo.multiply(new BigDecimal("0.225"))
                    .subtract(new BigDecimal("675.49"));
        } else if (baseCalculo.compareTo(limiteFaixa4) > 0) {
            valorImposto = baseCalculo.multiply(new BigDecimal("0.275"))
                    .subtract(new BigDecimal("908.73"));
        }

        if (baseCalculo.compareTo(new BigDecimal("5000.00")) <= 0) {
            valorImposto = BigDecimal.ZERO;
        }
        if (baseCalculo.compareTo(new BigDecimal("7350.00")) <= 0) {
            BigDecimal reducaoIrrf = new BigDecimal("978.62")
                    .subtract(new BigDecimal("0.133145").multiply(baseCalculo));
            valorImposto = valorImposto.subtract(reducaoIrrf);
        }

        return valorImposto.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal faixaInss(EmpregadoRequest requisicao) {
        return faixaInss(salarioDeduzido(requisicao));
    }

    public BigDecimal faixaInss(BigDecimal salarioBase) {
        BigDecimal limiteFaixa1 = new BigDecimal("1621.00");
        BigDecimal limiteFaixa2 = new BigDecimal("2902.84");
        BigDecimal limiteFaixa3 = new BigDecimal("4354.27");
        BigDecimal limiteFaixa4 = new BigDecimal("8475.55");

        BigDecimal aliquotaFaixa1 = new BigDecimal("0.925");
        BigDecimal aliquotaFaixa2 = new BigDecimal("0.91");
        BigDecimal aliquotaFaixa3 = new BigDecimal("0.88");
        BigDecimal aliquotaFaixa4 = new BigDecimal("0.86");

        if (salarioBase.compareTo(limiteFaixa1) <= 0) {
            return salarioBase.multiply(aliquotaFaixa1);
        }
        if (salarioBase.compareTo(limiteFaixa2) <= 0) {
            return limiteFaixa1.multiply(aliquotaFaixa1).add(salarioBase.subtract(limiteFaixa1)
                    .multiply(aliquotaFaixa2));
        }
        if (salarioBase.compareTo(limiteFaixa3) <= 0) {
            return limiteFaixa1.multiply(aliquotaFaixa1)
                    .add(limiteFaixa2.subtract(limiteFaixa1).multiply(aliquotaFaixa2))
                    .add(salarioBase.subtract(limiteFaixa2).multiply(aliquotaFaixa3));
        }
        if (salarioBase.compareTo(limiteFaixa4) <= 0) {
            return limiteFaixa1.multiply(aliquotaFaixa1)
                    .add(limiteFaixa2.subtract(limiteFaixa1).multiply(aliquotaFaixa2))
                    .add(limiteFaixa3.subtract(limiteFaixa2).multiply(aliquotaFaixa3))
                    .add(salarioBase.subtract(limiteFaixa3).multiply(aliquotaFaixa4));
        }
        if (salarioBase.compareTo(limiteFaixa4) > 0) {
            return salarioBase.subtract(new BigDecimal("988.09"));
        }
        return null;
    }
}