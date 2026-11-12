const URL_BASE_API = '/calculos';

export async function fetchCalculo(tipoCalculo, dadosEnvio) {
    const urlRequisicao = tipoCalculo === 'salarioLiquido'
        ? URL_BASE_API
        : `${URL_BASE_API}/${tipoCalculo}`;

    try {
        const resposta = await fetch(urlRequisicao, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosEnvio)
        });

        if (!resposta.ok) {
            let mensagemErro = `Erro no servidor (status: ${resposta.status})`;
            try {
                // Tenta extrair o JSON de erro retornado pelo Spring Boot.
                const erroServidor = await resposta.json();
                // Usa a mensagem do Java ou, se ela não existir, o nome do erro.
                mensagemErro = erroServidor.message || erroServidor.error || mensagemErro;
            } catch (erroJson) {
                // Trata respostas de erro em HTML ou em outros formatos não JSON.
                console.error("O servidor não retornou um JSON de erro válido.");
            }
            // Exibe o erro na interface.
            throw new Error(mensagemErro);
        }

        return await resposta.json();

    } catch (erro) {
        // Trata falhas de conexão com a API.
        console.error("Falha na requisição:", erro);
        throw new Error(erro.message === "Failed to fetch"
            ? "O servidor Java está desligado ou o endereço da API está errado."
            : erro.message);
    }
}
