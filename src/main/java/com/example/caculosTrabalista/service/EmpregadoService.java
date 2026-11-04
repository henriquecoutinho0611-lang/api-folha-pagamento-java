package com.example.caculosTrabalista.service;

import com.example.caculosTrabalista.entity.Empregado;
import org.springframework.stereotype.Service;

@Service
public class EmpregadoService {

    public double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public double salarioDeduzido(Empregado empregado) {

        double salario = empregado.getSalario();

        int diasTrabalhados = empregado.getDiasTrabalhados();

        double salarioPorDia = salario / 30;

        return salarioPorDia * diasTrabalhados;
    }

    public double faixa(Empregado empregado) {
        return faixa(salarioDeduzido(empregado));
    }

    public double faixa(double salario) {

        double faixa1 = 1621.00;
        double faixa2 = 2902.84;
        double faixa3 = 4354.27;
        double faixa4 = 8475.55;

        if (salario <= faixa1) {

            return salario * 0.925;

        } else if (salario <= faixa2) {

            return faixa1 * 0.925 +
                    (salario - faixa1) * 0.91;

        } else if (salario <= faixa3) {

            return faixa1 * 0.925 +
                    (faixa2 - faixa1) * 0.91 +
                    (salario - faixa2) * 0.88;

        } else if (salario <= faixa4) {

            return faixa1 * 0.925 +
                    (faixa2 - faixa1) * 0.91 +
                    (faixa3 - faixa2) * 0.88 +
                    (salario - faixa3) * 0.86;

        } else {

            return salario - 988.09;
        }
    }

    public double salarioLiquido(Empregado empregado) {

        double resultado = faixa(empregado);

        if (empregado.isConducao()) {
            resultado = resultado * 0.94;
        }

        return arredondar(resultado);
    }

    public double decinoTeceiro(Empregado empregado) {

        double salario = faixa(empregado);

        int mesesTrabalhados = empregado.getMesesTrabalhados();

        if (mesesTrabalhados <= 12) {

            double valorMes = salario / 12;

            double decimoTerceiro =
                    valorMes * mesesTrabalhados;

            return arredondar(decimoTerceiro);
        }

        throw new IllegalArgumentException(
                "Quantidade de meses inválida."
        );
    }

    public double ferias(Empregado empregado) {

        double salario = empregado.getSalario();

        int mesesTrabalhados = empregado.getMesesTrabalhados();

        if (mesesTrabalhados <= 12) {

            double valorMes = salario / 12;

            double valorDeduzido =
                    valorMes * mesesTrabalhados;

            double ferias =
                    valorDeduzido * 1.3333;

            double feriasDeduzido = faixa(ferias);

            return arredondar(feriasDeduzido);
        }

        throw new IllegalArgumentException(
                "Quantidade de meses inválida."
        );
    }
}
