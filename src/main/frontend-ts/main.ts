
import {
    testarConexao,
    enviarSalario,
    enviarDecimo,
    enviarFerias,
    type Empregado
} from "./api.js";

// Elementos do HTML
const formulario = document.getElementById("formEmpregado") as HTMLFormElement;
const salarioInput = document.getElementById("salario") as HTMLInputElement;
const diasInput = document.getElementById("diasTrabalhados") as HTMLInputElement;
const mesesInput = document.getElementById("mesesTrabalhados") as HTMLInputElement;
const anosInput = document.getElementById("anosTrabalhados") as HTMLInputElement;
const conducaoInput = document.getElementById("conducao") as HTMLInputElement;

const resultado = document.getElementById("resultado") as HTMLOutputElement;
const status = document.getElementById("status") as HTMLElement;

const btnSalario = document.getElementById("btnSalario") as HTMLButtonElement;
const btnDecimo = document.getElementById("btnDecimo") as HTMLButtonElement;
const btnFerias = document.getElementById("btnFerias") as HTMLButtonElement;
const btnConexao = document.getElementById("btnConexao") as HTMLButtonElement;

// Captura os dados do formulário
function obterDados(): Empregado | null {

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

// Apresentação padronizada dos resultados
function mostrarResultado(titulo: string, valor: number): void {

    resultado.innerHTML = `
        <span class="resultado-titulo">${titulo}</span>

        <strong class="resultado-valor">
            ${valor.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    })}
        </strong>
    `;
}

// Apresentação de erros
function mostrarErro(mensagem: string): void {
    resultado.textContent = mensagem;
}

// Calcula salário líquido
async function calcularSalario(): Promise<void> {

    const empregado = obterDados();

    if (!empregado) return;

    try {
        resultado.textContent = "Calculando salário...";

        const valor = await enviarSalario(empregado);

        mostrarResultado("Salário líquido", valor);

    } catch (erro) {
        mostrarErro(
            erro instanceof Error
                ? erro.message
                : "Erro ao calcular salário."
        );
    }
}

// Calcula décimo terceiro
async function calcularDecimo(): Promise<void> {

    const empregado = obterDados();

    if (!empregado) return;

    try {
        resultado.textContent = "Calculando 13º salário...";

        const valor = await enviarDecimo(empregado);

        mostrarResultado("13º salário", valor);

    } catch (erro) {
        mostrarErro(
            erro instanceof Error
                ? erro.message
                : "Erro ao calcular 13º salário."
        );
    }
}

// Calcula férias
async function calcularFerias(): Promise<void> {

    const empregado = obterDados();

    if (!empregado) return;

    try {
        resultado.textContent = "Calculando férias...";

        const valor = await enviarFerias(empregado);

        mostrarResultado("Férias", valor);

    } catch (erro) {
        mostrarErro(
            erro instanceof Error
                ? erro.message
                : "Erro ao calcular férias."
        );
    }
}

// Testa conexão com a API
async function verificarConexao(): Promise<void> {

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
