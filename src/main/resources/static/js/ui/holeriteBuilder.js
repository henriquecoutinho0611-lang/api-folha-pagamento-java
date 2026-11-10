import { formatarMoeda } from '../utils/formatters.js';

export function renderizarHolerite(dados, tipoCalculoNome, tipoCalculo) {
    const tbody = document.getElementById('holerite-body');
    tbody.innerHTML = ''; // Limpa resultados anteriores

    let totalVencimentos = 0;
    let totalDescontos = 0;

    // Função auxiliar para criar linhas da tabela
    const criarLinha = (descricao, provento, desconto, mostrarZeros = false) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${descricao}</td>
            <td class="text-right ${provento > 0 ? 'text-success' : ''}">${provento > 0 || mostrarZeros ? formatarMoeda(provento) : '-'}</td>
            <td class="text-right ${desconto > 0 ? 'text-danger' : ''}">${desconto > 0 || mostrarZeros ? formatarMoeda(desconto) : '-'}</td>
        `;
        tbody.appendChild(tr);
    };

    // 1. Processa Vencimentos
    if (dados.salarioBruto > 0) {
        criarLinha('Salário / Proventos Base', dados.salarioBruto, 0);
        totalVencimentos += dados.salarioBruto;
    }
    if (dados.acrecinoferias > 0) { // Nome conforme seu DTO
        criarLinha('Acréscimo 1/3 de Férias', dados.acrecinoferias, 0);
        totalVencimentos += dados.acrecinoferias;
    }

    // 2. Processa Descontos (Conforme nomes no seu DTO 'EmpregadoResponse')
    if (dados.DescontoInss > 0) {
        criarLinha('Contribuição INSS', 0, dados.DescontoInss);
    }
    if (dados.descontoConducao > 0) {
        criarLinha('Vale Transporte', 0, dados.descontoConducao);

    }
    const irrf = Number(dados.descontoIRRF ?? 0);
    const ehDecimoTerceiro = tipoCalculo === 'decinoTeceiro';
    if (irrf > 0 || ehDecimoTerceiro) {
        criarLinha('IRRF', 0, irrf, ehDecimoTerceiro);
    }

    // 3. Atualiza os Totais no Rodapé
    document.getElementById('total-vencimentos').innerText = formatarMoeda(totalVencimentos);
    document.getElementById('total-descontos').innerText = formatarMoeda(dados.descontoTotal);
    document.getElementById('valor-liquido').innerText = formatarMoeda(dados.salarioLiquido);

    // Atualiza o Título e mostra o componente
    document.getElementById('holerite-tipo').innerText = tipoCalculoNome;
    document.getElementById('holerite-container').style.display = 'block';
}
