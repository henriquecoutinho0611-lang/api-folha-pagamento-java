const BASE_URL = '/calculos';

export async function fetchCalculo(endpoint, payload) {
    const url = endpoint === 'salarioLiquido' ? BASE_URL : `${BASE_URL}/${endpoint}`;

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            let mensagemErro = `Erro no servidor (Status: ${response.status})`;
            try {
                // Tenta extrair o JSON de erro que o Spring Boot manda
                const erroBackend = await response.json();
                // Pega a mensagem customizada do Java, se não achar pega o nome do erro
                mensagemErro = erroBackend.message || erroBackend.error || mensagemErro;
            } catch (jsonError) {
                // Se o Java mandou uma página de erro (HTML) que não é JSON, cai aqui
                console.error("O servidor não retornou um JSON de erro válido.");
            }
            // Dispara o erro para a tela vermelha do Front-end
            throw new Error(mensagemErro);
        }

        return await response.json();

    } catch (error) {
        // Se a API estiver fora do ar (desligada) ou o navegador bloquear, cai aqui
        console.error("Falha fatal na requisição:", error);
        throw new Error(error.message === "Failed to fetch"
            ? "O servidor Java está desligado ou o endereço da API está errado."
            : error.message);
    }
}