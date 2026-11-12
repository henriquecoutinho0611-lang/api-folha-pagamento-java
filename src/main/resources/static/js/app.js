// Como a interface e a API estão no mesmo projeto, usamos um caminho relativo.
const URL_BASE_API = '/calculos';

async function calcular(tipoCalculo) {
    // Coleta os dados do formulário.
    const salarioBruto = parseFloat(document.getElementById('salario').value);
    const descontaValeTransporte = document.getElementById('conducao').value === 'true';
    const diasTrabalhados = parseInt(document.getElementById('diasTrabalhados').value);
    const mesesTrabalhados = parseInt(document.getElementById('mesesTrabalhados').value);

    if (isNaN(salarioBruto) || salarioBruto <= 0) {
        alert('O salário deve ser maior que zero.');
        return;
    }

    if (isNaN(diasTrabalhados) || diasTrabalhados <= 0 || diasTrabalhados >= 30) {
        alert('Informe uma quantidade válida de dias trabalhados ou de férias.');
        return;
    }

    // Monta o objeto correspondente ao EmpregadoRequest do Java.
    const corpoRequisicao = {
        salario: salarioBruto,
        conducao: descontaValeTransporte,
        diasTrabalhados: diasTrabalhados,
        mesesTrabalhados: mesesTrabalhados,
        anosTrabalhados: 1
    };

    // Monta a URL de acordo com o cálculo selecionado.
    const urlRequisicao = tipoCalculo === 'salarioLiquido'
        ? URL_BASE_API
        : `${URL_BASE_API}/${tipoCalculo}`;

    // Define o título do resultado.
    const titulosPorCalculo = {
        'salarioLiquido': 'Extrato: Salário Líquido',
        'decinoTeceiro': 'Extrato: 13º Salário',
        'ferias': 'Extrato: Férias',
        'Irrf': 'Extrato: IRRF'
    };

    try {
        const resposta = await fetch(urlRequisicao, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(corpoRequisicao)
        });

        if (resposta.ok) {
            const dadosCalculados = await resposta.json();
            mostrarResultado(dadosCalculados, titulosPorCalculo[tipoCalculo]);
        } else {
            alert('Não foi possível realizar o cálculo pela API.');
        }
    } catch (erro) {
        console.error('Falha na requisição:', erro);
        alert('Falha na comunicação. Verifique se o Spring Boot está rodando.');
    }
}

function mostrarResultado(dadosCalculados, tituloResultado) {
    // Formata os valores como moeda brasileira.
    const formatarMoeda = (valorMonetario) => valorMonetario.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

    document.getElementById('titulo-resultado').innerText = tituloResultado;

    // Exibe os dados recebidos em EmpregadoResponse.
    document.getElementById('res-bruto').innerText = formatarMoeda(dadosCalculados.salarioBruto || 0);
    document.getElementById('res-inss').innerText = '- ' + formatarMoeda(dadosCalculados.DescontoInss || 0);
    document.getElementById('res-vt').innerText = '- ' + formatarMoeda(dadosCalculados.descontoConducao || 0);
    document.getElementById('res-ferias').innerText = '+ ' + formatarMoeda(dadosCalculados.acrecinoferias || 0);
    document.getElementById('res-irrf').innerText = '- ' + formatarMoeda(dadosCalculados.descontoIRRF || 0);
    document.getElementById('res-liquido').innerText = formatarMoeda(dadosCalculados.salarioLiquido || 0);
    document.getElementById('res-liquido').innerText = formatarMoeda(dadosCalculados.descontoTotal || 0);

    // Mostra a div de resultado na tela
    document.getElementById('resultado-section').style.display = 'block';
}
