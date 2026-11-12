import { formatarMoeda } from '../utils/formatters.js';

export function renderizarHolerite(dadosCalculo, nomeTipoCalculo, tipoCalculo) {
    const corpoTabela = document.getElementById('holerite-body');
    corpoTabela.innerHTML = ''; // Limpa resultados anteriores.

    let totalVencimentos = 0;
    let totalDescontos = 0;

    // Adiciona uma linha ao holerite.
    const adicionarLinha = (descricao, provento, desconto, mostrarValoresZerados = false) => {
        const linha = document.createElement('tr');
        linha.innerHTML = `
            <td>${descricao}</td>
            <td class="text-right ${provento > 0 ? 'text-success' : ''}">${provento > 0 || mostrarValoresZerados ? formatarMoeda(provento) : '-'}</td>
            <td class="text-right ${desconto > 0 ? 'text-danger' : ''}">${desconto > 0 || mostrarValoresZerados ? formatarMoeda(desconto) : '-'}</td>
        `;
        corpoTabela.appendChild(linha);
    };

    // 1. Processa os vencimentos.
    if (dadosCalculo.salarioBruto > 0) {
        adicionarLinha('Salário / Proventos Base', dadosCalculo.salarioBruto, 0);
        totalVencimentos += dadosCalculo.salarioBruto;
    }
    if (dadosCalculo.acrecinoferias > 0) { // Nome conforme o DTO.
        adicionarLinha('Acréscimo 1/3 de férias', dadosCalculo.acrecinoferias, 0);
        totalVencimentos += dadosCalculo.acrecinoferias;
    }

    // 2. Processa os descontos conforme os campos de EmpregadoResponse.
    if (dadosCalculo.DescontoInss > 0) {
        adicionarLinha('Contribuição ao INSS', 0, dadosCalculo.DescontoInss);
    }
    if (dadosCalculo.descontoConducao > 0) {
        adicionarLinha('Vale-transporte', 0, dadosCalculo.descontoConducao);

    }
    const valorIrrf = Number(dadosCalculo.descontoIRRF ?? 0);
    const ehDecimoTerceiro = tipoCalculo === 'decinoTeceiro';
    if (valorIrrf > 0 || ehDecimoTerceiro) {
        adicionarLinha('IRRF', 0, valorIrrf, ehDecimoTerceiro);
    }

    // 3. Atualiza os totais do rodapé.
    document.getElementById('total-vencimentos').innerText = formatarMoeda(totalVencimentos);
    document.getElementById('total-descontos').innerText = formatarMoeda(dadosCalculo.descontoTotal);
    document.getElementById('valor-liquido').innerText = formatarMoeda(dadosCalculo.salarioLiquido);

    // Atualiza o título e exibe o holerite.
    document.getElementById('holerite-tipo').innerText = nomeTipoCalculo;
    document.getElementById('holerite-container').style.display = 'block';
}
