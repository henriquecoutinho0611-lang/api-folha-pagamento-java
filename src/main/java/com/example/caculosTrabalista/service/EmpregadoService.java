package com.example.caculosTrabalista.service;

import com.example.caculosTrabalista.DTO.EmpregadoRequest;
import com.example.caculosTrabalista.DTO.EmpregadoResponse;
import com.example.caculosTrabalista.entity.Empregado;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.caculosTrabalista.mapper.CaculosMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EmpregadoService {

    private final CaculosMapper caculosMapper;

    public EmpregadoService(CaculosMapper caculosMapper) {

        this.caculosMapper = caculosMapper ;

    }

    private BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal salarioDeduzido(EmpregadoRequest request) {
        Empregado empregado = caculosMapper.toEntity(request);
        BigDecimal salario = empregado.getSalario() ;
        BigDecimal dias = BigDecimal.valueOf(empregado.getDiasTrabalhados());
        return salario.multiply(dias)
                .divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal faixaIRRF(BigDecimal valor) {

        BigDecimal imposto = BigDecimal.ZERO;

        BigDecimal faixa1 = new BigDecimal("2428.80");
        BigDecimal faixa2 = new BigDecimal("2826.65");
        BigDecimal faixa3 = new BigDecimal("3751.05");
        BigDecimal faixa4 = new BigDecimal("4664.68");

        if (valor.compareTo(faixa1) <= 0) {

            imposto = BigDecimal.ZERO;

        } else if (valor.compareTo(faixa2) <= 0) {

            imposto = valor.multiply(new BigDecimal("0.075"))
                    .subtract(new BigDecimal("182.16"));

        } else if (valor.compareTo(faixa3) <= 0) {

            imposto = valor.multiply(new BigDecimal("0.15"))
                    .subtract(new BigDecimal("394.16"));

        } else if (valor.compareTo(faixa4) <= 0) {

            imposto = valor.multiply(new BigDecimal("0.225"))
                    .subtract(new BigDecimal("675.49"));

        } else {

            imposto = valor.multiply(new BigDecimal("0.275"))
                    .subtract(new BigDecimal("908.73"));
        }

        if (valor.compareTo(new BigDecimal("5000.00")) <= 0) {

            imposto = BigDecimal.ZERO;

        } else if (valor.compareTo(new BigDecimal("7350.00")) <= 0) {

            BigDecimal reducao = new BigDecimal("978.62")
                    .subtract(
                            new BigDecimal("0.133145").multiply(valor)
                    );

            imposto = imposto.subtract(reducao);
        }

        return imposto.max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal faixaInss(EmpregadoRequest request) {
        return faixaInss(salarioDeduzido(request));
    }

    private BigDecimal faixaInss(BigDecimal salario) {
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
            return faixa1.multiply(aliquota1).add(salario.subtract(faixa1).multiply(aliquota2));


        } else if (salario.compareTo(faixa3) <= 0) {
            return faixa1.multiply(aliquota1).
                    add(faixa2.subtract(faixa1).multiply(aliquota2)).
                    add(salario.subtract(faixa2).multiply(aliquota3));

        } else if (salario.compareTo(faixa4) <= 0) {
            return faixa1.multiply(aliquota1).
                    add(faixa2.subtract(faixa1).multiply(aliquota2)).
                    add(faixa3.subtract(faixa2).multiply(aliquota3)).
                    add(salario.subtract(faixa3).multiply(aliquota4));
        } else {
            return salario.subtract(new BigDecimal("988.09"));
        }
    }

    public EmpregadoResponse salarioLiquido(EmpregadoRequest request) {
        Empregado empregado = caculosMapper.toEntity(request);

        int diasTrabalhados = empregado.getDiasTrabalhados();

        if (diasTrabalhados > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os dias de ferias devem estar entre 1 e 30");
        }

        BigDecimal salario = empregado.getSalario();
        BigDecimal salariodia = salarioDeduzido(request);
        BigDecimal salariodeduzido = faixaInss(salariodia);
        BigDecimal descontoInss = salariodia.subtract(salariodeduzido);
        BigDecimal descontoConducao = salario.multiply(new BigDecimal("0.06"));
        BigDecimal descontoIrrf = faixaIRRF(salariodeduzido);

        if (empregado.isConducao()) {
            if (descontoIrrf.compareTo(BigDecimal.ZERO) <= 0) {
                BigDecimal salarioliquido = salariodeduzido.subtract(descontoConducao);
                BigDecimal descontototal = descontoInss.add(descontoConducao);

                return new EmpregadoResponse(arredondar(salariodia),
                        arredondar(salarioliquido),
                        arredondar(descontoInss),
                        arredondar(descontoConducao),
                        new BigDecimal("0"),
                        new BigDecimal("0"),
                        arredondar(descontototal),
                        new BigDecimal("0"));
            } else {
                BigDecimal salarioliquido = salariodeduzido.subtract(descontoConducao).subtract(descontoIrrf);
                BigDecimal descontototal = descontoInss.add(descontoConducao).add(descontoIrrf);

                return  new EmpregadoResponse(arredondar(salariodia),
                        arredondar(salarioliquido),
                        arredondar(descontoInss),
                        arredondar(descontoConducao),
                        arredondar(descontoIrrf),
                        new BigDecimal("0"),
                        arredondar(descontototal),
                        new BigDecimal("0"));
            }
        }

        if (descontoIrrf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal salarioliquido = salariodeduzido.subtract(descontoIrrf);
            BigDecimal descontototal = descontoInss.add(descontoIrrf);

            return new EmpregadoResponse(arredondar(salariodia),
                    arredondar(salarioliquido),
                    arredondar(descontoInss),
                    BigDecimal.ZERO,
                    arredondar(descontoIrrf),
                    BigDecimal.ZERO,
                    arredondar(descontototal),
                    BigDecimal.ZERO);
        } else {
            return new EmpregadoResponse(arredondar(salariodia),
                    arredondar(salariodeduzido),
                    arredondar(descontoInss),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    arredondar(descontoInss),
                    BigDecimal.ZERO);
        }
    }

    public EmpregadoResponse decimoTerceiro(EmpregadoRequest request) {
        Empregado empregado = caculosMapper.toEntity(request);

        int mesesTrabalhados = empregado.getMesesTrabalhados();

        if (mesesTrabalhados < 1 || mesesTrabalhados > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os meses trabalhados devem estar entre 1 e 12.");
        }

        BigDecimal salario = empregado.getSalario();
        BigDecimal decimoTerceiro = salario.multiply(BigDecimal.valueOf(mesesTrabalhados))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        BigDecimal salariodeduzido = faixaInss(decimoTerceiro);
        BigDecimal descontoInss = decimoTerceiro.subtract(salariodeduzido);
        BigDecimal descontoIrrf = faixaIRRF(salariodeduzido);

        if (descontoIrrf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal salarioliquido = salariodeduzido.subtract(descontoIrrf);
            BigDecimal descontototal = descontoInss.add(descontoIrrf);

            return new EmpregadoResponse(arredondar(decimoTerceiro),
                    arredondar(salarioliquido),
                    arredondar(descontoInss),
                    BigDecimal.ZERO,
                    arredondar(descontoIrrf),
                    BigDecimal.ZERO,
                    arredondar(descontototal),
                    BigDecimal.ZERO);
        } else {
            return new EmpregadoResponse(arredondar(decimoTerceiro),
                    arredondar(salariodeduzido),
                    arredondar(descontoInss),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    arredondar(descontoInss),
                    BigDecimal.ZERO);
        }
    }

    public EmpregadoResponse ferias(EmpregadoRequest request) {
        Empregado empregado = caculosMapper.toEntity(request);
        int diasTrabalhados = empregado.getDiasTrabalhados();


        if (diasTrabalhados < 1 || diasTrabalhados > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os dias de ferias devem estar entre 1 e 30");
        }

        BigDecimal salario = salarioDeduzido(request);
        BigDecimal salarioferias = salario.multiply(new BigDecimal("1.33333"));
        BigDecimal salariodeduzido = faixaInss(salarioferias);
        BigDecimal descontoInss = salarioferias.subtract(salariodeduzido);
        BigDecimal acrecinoferias = salario.multiply(new BigDecimal("0.33333"));
        BigDecimal descontoIrrf = faixaIRRF(salariodeduzido);

        if (descontoIrrf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal salarioliquido = salariodeduzido.subtract(descontoIrrf);
            BigDecimal descontototal = descontoInss.add(descontoIrrf);

            return new EmpregadoResponse(arredondar(salario),
                    arredondar(salarioliquido),
                    arredondar(descontoInss),
                    BigDecimal.ZERO,
                    arredondar(descontoIrrf),
                    BigDecimal.ZERO,
                    arredondar(descontototal),
                    arredondar(acrecinoferias));
        } else {
            return new EmpregadoResponse(arredondar(salario),
                    arredondar(salariodeduzido),
                    arredondar(descontoInss),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    arredondar(descontoInss),
                    arredondar(acrecinoferias));
        }
    }
}
