import { fetchCalculo } from './api/apiService.js';
import { renderizarHolerite } from './ui/holeriteBuilder.js';

// Controle das abas de cálculo.
const botoesAbas = document.querySelectorAll('.tabs__btn');
const campoTipoCalculo = document.getElementById('tipo-calculo');

botoesAbas.forEach((botaoAba) => {
    botaoAba.addEventListener('click', (evento) => {
        document.querySelector('.tabs__btn--active')?.classList.remove('tabs__btn--active');
        evento.currentTarget.classList.add('tabs__btn--active');
        campoTipoCalculo.value = evento.currentTarget.getAttribute('data-calculo');
        document.getElementById('holerite-container').style.display = 'none';
    });
});

const formulario = document.getElementById('form-calculo');
const botaoEnviar = document.getElementById('btn-submit');
const mensagemErro = document.getElementById('mensagem-erro');

formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();

    mensagemErro.style.display = 'none';
    document.getElementById('holerite-container').style.display = 'none';

    const dadosEnvio = {
        salario: parseFloat(document.getElementById('salario').value),
        conducao: document.getElementById('conducao').value === 'true',
        diasTrabalhados: parseInt(document.getElementById('diasTrabalhados').value, 10),
        mesesTrabalhados: parseInt(document.getElementById('mesesTrabalhados').value, 10),
        anosTrabalhados: 1
    };

    const nomeDaAba = document.querySelector('.tabs__btn--active').innerText;
    const textoOriginalDoBotao = botaoEnviar.innerText;
    botaoEnviar.innerText = 'Processando...';
    botaoEnviar.disabled = true;

    try {
        const dadosCalculados = await fetchCalculo(campoTipoCalculo.value, dadosEnvio);
        renderizarHolerite(dadosCalculados, nomeDaAba, campoTipoCalculo.value);
    } catch (erro) {
        mensagemErro.textContent = `⚠️ ${erro.message}`;
        mensagemErro.style.display = 'block';
    } finally {
        botaoEnviar.innerText = textoOriginalDoBotao;
        botaoEnviar.disabled = false;
    }
});
