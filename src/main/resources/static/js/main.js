import {
    testarConexao,
    enviarSalario,
    enviarDecimo,
    enviarFerias
} from "./api.js";

// Elementos do HTML
const formulario = document.getElementById("formEmpregado");
const salarioInput = document.getElementById("salario");
const diasInput = document.getElementById("diasTrabalhados");
const mesesInput = document.getElementById("mesesTrabalhados");
const anosInput = document.getElementById("anosTrabalhados");
const conducaoInput = document.getElementById("conducao");
const resultado = document.getElementById("resultado");
const status = document.getElementById("status");

const btnSalario = document.getElementById("btnSalario");
const btnDecimo = document.getElementById("btnDecimo");
const btnFerias = document.getElementById("btnFerias");
const btnConexao = document.getElementById("btnConexao");

// Captura os dados do formulário
function obterDados() {
    if (!formulario.reportValidity()) {
        return null;
    }

    return {
        salario: Number(salarioInput.value),
        diasTrabalhados: Number(diasInput.value),
        mesesTrabalhados: Number(mesesInput.value),
        anosTrabalhados: Number(anosInput.value),
        conducao: conducaoInput.checked
    };
}

// Formata os valores em reais
function formatarMoeda(valor) {
    return Number(valor ?? 0).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

// Exibe os resultados do salário líquido
function mostrarSalarioLiquido(dados) {
    resultado.innerHTML = `
        <span class="resultado-titulo">Salário líquido</span>

        <strong class="resultado-valor">
            ${formatarMoeda(dados.salarioLiquido)}
        </strong>

        <p>Salário bruto: ${formatarMoeda(dados.salarioBruto)}</p>
        <p>Desconto INSS: ${formatarMoeda(dados.DescontoInss)}</p>
        <p>Desconto condução: ${formatarMoeda(dados.descontoConducao)}</p>
        <p>Desconto IRRF: ${formatarMoeda(dados.descontoIRRF)}</p>
        <p>Desconto pensão: ${formatarMoeda(dados.decontopensao)}</p>
        <p>Desconto total: ${formatarMoeda(dados.descontoTotal)}</p>
    `;
}

// Exibe os resultados do décimo terceiro
function mostrarDecimoTerceiro(dados) {
    resultado.innerHTML = `
        <span class="resultado-titulo">Décimo terceiro</span>

        <strong class="resultado-valor">
            ${formatarMoeda(dados.salarioLiquido)}
        </strong>

        <p>Salário bruto: ${formatarMoeda(dados.salarioBruto)}</p>
        <p>Desconto INSS: ${formatarMoeda(dados.DescontoInss)}</p>
        <p>Desconto IRRF: ${formatarMoeda(dados.descontoIRRF)}</p>
        <p>Desconto pensão: ${formatarMoeda(dados.decontopensao)}</p>
        <p>Desconto total: ${formatarMoeda(dados.descontoTotal)}</p>
    `;
}

// Exibe os resultados das férias
function mostrarFerias(dados) {
    resultado.innerHTML = `
        <span class="resultado-titulo">Férias</span>

        <strong class="resultado-valor">
            ${formatarMoeda(dados.salarioLiquido)}
        </strong>

        <p>Salário bruto: ${formatarMoeda(dados.salarioBruto)}</p>
        <p>Desconto INSS: ${formatarMoeda(dados.DescontoInss)}</p>
        <p>Desconto IRRF: ${formatarMoeda(dados.descontoIRRF)}</p>
        <p>Desconto pensão: ${formatarMoeda(dados.decontopensao)}</p>
        <p>Desconto total: ${formatarMoeda(dados.descontoTotal)}</p>
    `;
}

// Apresentação de erros
function mostrarErro(mensagem) {
    resultado.textContent = mensagem;
}

// Calcula salário líquido
async function calcularSalario() {
    const empregado = obterDados();

    if (!empregado) return;

    try {
        resultado.textContent = "Calculando salário...";

        const dados = await enviarSalario(empregado);

        mostrarSalarioLiquido(dados);

    } catch (erro) {
        mostrarErro(
            erro instanceof Error
                ? erro.message
                : "Erro ao calcular salário."
        );
    }
}

// Calcula décimo terceiro
async function calcularDecimo() {
    const empregado = obterDados();

    if (!empregado) return;

    try {
        resultado.textContent = "Calculando 13º salário...";

        const dados = await enviarDecimo(empregado);

        mostrarDecimoTerceiro(dados);

    } catch (erro) {
        mostrarErro(
            erro instanceof Error
                ? erro.message
                : "Erro ao calcular 13º salário."
        );
    }
}

// Calcula férias
async function calcularFerias() {
    const empregado = obterDados();

    if (!empregado) return;

    try {
        resultado.textContent = "Calculando férias...";

        const dados = await enviarFerias(empregado);

        mostrarFerias(dados);

    } catch (erro) {
        mostrarErro(
            erro instanceof Error
                ? erro.message
                : "Erro ao calcular férias."
        );
    }
}

// Testa conexão com a API
async function verificarConexao() {
    try {
        status.textContent = "Verificando...";

        const resposta = await testarConexao();

        status.textContent = resposta;

    } catch (erro) {
        status.textContent = "API offline";

        mostrarErro(
            erro instanceof Error
                ? erro.message
                : "Não foi possível conectar à API."
        );
    }
}

// Eventos dos botões
btnSalario.addEventListener("click", calcularSalario);
btnDecimo.addEventListener("click", calcularDecimo);
btnFerias.addEventListener("click", calcularFerias);
btnConexao.addEventListener("click", verificarConexao);

// Verifica conexão ao abrir a página
verificarConexao();