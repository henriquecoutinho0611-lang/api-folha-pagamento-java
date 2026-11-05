package com.example.caculosTrabalista.service;

import com.example.caculosTrabalista.DTO.EmpregadoRequest;
import com.example.caculosTrabalista.DTO.EmpregadoResponse;
import com.example.caculosTrabalista.entity.Empregado;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EmpregadoService {

    public BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal salarioDeduzido(EmpregadoRequest request) {

        BigDecimal salario = request.salario();

        BigDecimal dias = BigDecimal.valueOf(
                request.diasTrabalhados()
        );

        return salario
                .divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP)
                .multiply(dias);
    }

    public BigDecimal faixa(EmpregadoRequest request) {
        return faixa(salarioDeduzido(request));
    }

    public BigDecimal faixa(BigDecimal salario) {

        BigDecimal faixa1 = new BigDecimal("1621.00");
        BigDecimal faixa2 = new BigDecimal("2902.84");
        BigDecimal faixa3 = new BigDecimal("4354.27");
        BigDecimal faixa4 = new BigDecimal("8475.55");

        BigDecimal aliquota1 = new BigDecimal("0.925");
        BigDecimal aliquota2 = new BigDecimal("0.91");
        BigDecimal aliquota3 = new BigDecimal("0.88");
        BigDecimal aliquota4 = new BigDecimal("0.86");

        if (salario.compareTo(faixa1) <= 0) {

            return salario.multiply(aliquota1);

        } else if (salario.compareTo(faixa2) <= 0) {

            return faixa1.multiply(aliquota1)
                    .add(salario.subtract(faixa1).multiply(aliquota2));

        } else if (salario.compareTo(faixa3) <= 0) {

            return faixa1.multiply(aliquota1)
                    .add(faixa2.subtract(faixa1).multiply(aliquota2))
                    .add(salario.subtract(faixa2).multiply(aliquota3));

        } else if (salario.compareTo(faixa4) <= 0) {

            return faixa1.multiply(aliquota1)
                    .add(faixa2.subtract(faixa1).multiply(aliquota2))
                    .add(faixa3.subtract(faixa2).multiply(aliquota3))
                    .add(salario.subtract(faixa3).multiply(aliquota4));

        } else {

            return salario.subtract(new BigDecimal("988.09"));
        }
    }

    public EmpregadoResponse salarioLiquido( EmpregadoRequest request) {
            BigDecimal salario = request.salario();
            BigDecimal salariodeduzido = faixa(request);
            BigDecimal descontoInss = salario.subtract(salariodeduzido);
            BigDecimal descontoConducao = salario.multiply(new BigDecimal("0.06"));



        if (request.conducao()) {

             BigDecimal salarioliquido = salariodeduzido.multiply(new BigDecimal("0.94"));
            BigDecimal descontototal = descontoInss.add(descontoConducao);
             return  new EmpregadoResponse(arredondar(salario),arredondar(salarioliquido),arredondar(descontoInss),arredondar(descontoConducao),new BigDecimal("0"),new BigDecimal("0"),arredondar(descontototal));
        }

        return  new EmpregadoResponse(arredondar(salario),arredondar(salariodeduzido),arredondar(descontoInss),new BigDecimal("0"),new BigDecimal("0"),new BigDecimal("0"),new BigDecimal("0") );
    }

    public EmpregadoResponse decinoTeceiro(EmpregadoRequest request) {


        BigDecimal salario = request.salario();
        BigDecimal salariodeduzido = faixa(request);
        BigDecimal descontoInss = salario.subtract(salariodeduzido);


        int mesesTrabalhados = request.mesesTrabalhados();

        if (mesesTrabalhados <= 12) {

            BigDecimal valorMes = salariodeduzido.divide(
                    BigDecimal.valueOf(12),
                    2,
                    RoundingMode.HALF_UP
            );

            BigDecimal decimoTerceiro = valorMes.multiply(
                    BigDecimal.valueOf(mesesTrabalhados)
            );

            return new EmpregadoResponse(arredondar(salario),arredondar(salariodeduzido),arredondar(descontoInss),new BigDecimal("0"),new BigDecimal("0"),new BigDecimal("0"),new BigDecimal("0"));
        }

        return null;
    }

    public EmpregadoResponse ferias(EmpregadoRequest request) {

        BigDecimal salario = request.salario();
        BigDecimal salarioferias = salario.multiply(new BigDecimal("1.33333"));
        BigDecimal salariodeduzido = faixa(salarioferias);
        BigDecimal descontoInss = salarioferias.subtract(salariodeduzido);
        int mesesTrabalhados = request.mesesTrabalhados();

        if (mesesTrabalhados <= 12) {

            BigDecimal valorMes = salariodeduzido.divide(
                    BigDecimal.valueOf(12),
                    2,
                    RoundingMode.HALF_UP
            );

            BigDecimal valorDeduzido = valorMes.multiply(
                    BigDecimal.valueOf(mesesTrabalhados)
            );


            return new EmpregadoResponse(salario,salariodeduzido,descontoInss,new BigDecimal("0"),new BigDecimal("0"),new BigDecimal("0"), new BigDecimal("0"));
        }

        return null;
    }
}