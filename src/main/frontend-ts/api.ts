export interface Empregado {
    salario: number;
    conducao: boolean;
    diasTrabalhados: number;
    mesesTrabalhados: number;
    anosTrabalhados: number;
}

const API_URL = "/calculos";

// Verifica a conexão com a API.
export async function testarConexao(): Promise<string> {
    const resposta = await fetch(`${API_URL}/status`);

    if (!resposta.ok) {
        throw new Error(`Erro ao conectar: HTTP ${resposta.status}`);
    }

    return await resposta.text();
}

// Envia os dados para calcular o salário líquido.
export async function enviarSalario(
    empregado: Empregado
): Promise<number> {
    const resposta = await fetch(`${API_URL}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(empregado)
    });

    if (!resposta.ok) {
        throw new Error(`Erro no salário: HTTP ${resposta.status}`);
    }

    return Number(await resposta.text());
}

// Envia os dados para calcular o décimo terceiro.
export async function enviarDecimo(
    empregado: Empregado
): Promise<number> {
    const resposta = await fetch(`${API_URL}/decinoTeceiro`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(empregado)
    });

    if (!resposta.ok) {
        throw new Error(`Erro no décimo terceiro: HTTP ${resposta.status}`);
    }

    return Number(await resposta.text());
}

// Envia os dados para calcular as férias.
export async function enviarFerias(
    empregado: Empregado
): Promise<number> {
    const resposta = await fetch(`${API_URL}/ferias`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(empregado)
    });

    if (!resposta.ok) {
        throw new Error(`Erro nas férias: HTTP ${resposta.status}`);
    }

    return Number(await resposta.text());
}