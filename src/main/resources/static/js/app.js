// Como o Front e o Back estão no mesmo projeto, usamos caminho relativo:
const API_URL = '/calculos';

async function calcular(tipoCalculo) {
    // 1. Coleta os dados do formulário
    const salario = parseFloat(document.getElementById('salario').value);
    const conducao = document.getElementById('conducao').value === 'true';
    const diasTrabalhados = parseInt(document.getElementById('diasTrabalhados').value);
    const mesesTrabalhados = parseInt(document.getElementById('mesesTrabalhados').value);

    if (isNaN(salario) || salario <= 0) {
        alert("O salário não pode ser negativo .");
        return;
    }

    if (isNaN(diasTrabalhados) || diasTrabalhados <= 0 || diasTrabalhados >= 30) {
        alert("Os dias Trabalhado ou dias de farias , devem estar entre 1 e 30");
        return;
    }

    // Monta o objeto equivalente ao EmpregadoRequest do Java
    const requestBody = {
        salario: salario,
        conducao: conducao,
        diasTrabalhados: diasTrabalhados,
        mesesTrabalhados: mesesTrabalhados,
        anosTrabalhados: 1
    };

    // Monta a URL correta dependendo do botão clicado
    const url = tipoCalculo === 'salarioLiquido' ? API_URL : `${API_URL}/${tipoCalculo}`;

    // Define o título da caixa de resultado
    const titulos = {
        'salarioLiquido': 'Extrato: Salário Líquido',
        'decinoTeceiro': 'Extrato: 13º Salário',
        'ferias': 'Extrato: Férias',
        'Irrf': 'Extrato: irrf'
    };

    try {
        const resposta = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (resposta.ok) {
            const dados = await resposta.json();
            mostrarResultado(dados, titulos[tipoCalculo]);
        } else {
            alert('Erro ao realizar o cálculo na API.');
        }
    } catch (erro) {
        console.error('Erro na requisição:', erro);
        alert('Falha na comunicação. Verifique se o Spring Boot está rodando.');
    }
}

function mostrarResultado(dados, titulo) {
    // Função para formatar os números em Real (R$)
    const formatarMoeda = (valor) => valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

    document.getElementById('titulo-resultado').innerText = titulo;

    // Alimenta os dados com o retorno do EmpregadoResponse
    document.getElementById('res-bruto').innerText = formatarMoeda(dados.salarioBruto || 0);
    document.getElementById('res-inss').innerText = "- " + formatarMoeda(dados.DescontoInss || 0);
    document.getElementById('res-vt').innerText = "- " + formatarMoeda(dados.descontoConducao || 0);
    document.getElementById('res-ferias').innerText = "+ " + formatarMoeda(dados.acrecinoferias || 0);
    document.getElementById( 'res-irrf').innerText = "- " + formatarMoeda(dados.descontoIRRF || 0);
    // O valor líquido vem no campo salarioLiquido
    document.getElementById('res-liquido').innerText = formatarMoeda(dados.salarioLiquido || 0);
    document.getElementById('res-liquido').innerText = formatarMoeda(dados.descontoTotal || 0);

    // Mostra a div de resultado na tela
    document.getElementById('resultado-section').style.display = 'block';
}