const API_URL = "/calculos";

// Verifica a conexão com a API.
export async function testarConexao() {
    const resposta = await fetch(`${API_URL}/status`);

    if (!resposta.ok) {
        throw new Error(`Erro ao conectar: HTTP ${resposta.status}`);
    }

    return await resposta.text();
}

// Função auxiliar para enviar os dados e receber o JSON.
async function enviarRequisicao(url, empregado) {
    const resposta = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(empregado)
    });

    if (!resposta.ok) {
        throw new Error(`Erro na requisição: HTTP ${resposta.status}`);
    }

    return await resposta.json();
}

// Envia os dados para calcular o salário líquido.
export async function enviarSalario(empregado) {
    return await enviarRequisicao(`${API_URL}`, empregado);
}

// Envia os dados para calcular o décimo terceiro.
export async function enviarDecimo(empregado) {
    return await enviarRequisicao(`${API_URL}/decinoTeceiro`, empregado);
}

// Envia os dados para calcular as férias.
export async function enviarFerias(empregado) {
    return await enviarRequisicao(`${API_URL}/ferias`, empregado);
}