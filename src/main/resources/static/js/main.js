import { fetchCalculo } from './api/apiService.js';
import { renderizarHolerite } from './ui/holeriteBuilder.js';

// Controle das abas de cálculo.
const tabButtons = document.querySelectorAll('.tabs__btn');
const inputTipoCalculo = document.getElementById('tipo-calculo');

tabButtons.forEach((btn) => {
    btn.addEventListener('click', (event) => {
        document.querySelector('.tabs__btn--active')?.classList.remove('tabs__btn--active');
        event.currentTarget.classList.add('tabs__btn--active');
        inputTipoCalculo.value = event.currentTarget.getAttribute('data-calculo');
        document.getElementById('holerite-container').style.display = 'none';
    });
});

const form = document.getElementById('form-calculo');
const btnSubmit = document.getElementById('btn-submit');
const boxErro = document.getElementById('mensagem-erro');

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    boxErro.style.display = 'none';
    document.getElementById('holerite-container').style.display = 'none';

    const payload = {
        salario: parseFloat(document.getElementById('salario').value),
        conducao: document.getElementById('conducao').value === 'true',
        diasTrabalhados: parseInt(document.getElementById('diasTrabalhados').value, 10),
        mesesTrabalhados: parseInt(document.getElementById('mesesTrabalhados').value, 10),
        anosTrabalhados: 1
    };

    const nomeAba = document.querySelector('.tabs__btn--active').innerText;
    const textoOriginal = btnSubmit.innerText;
    btnSubmit.innerText = 'Processando...';
    btnSubmit.disabled = true;

    try {
        const dadosCalculados = await fetchCalculo(inputTipoCalculo.value, payload);
        renderizarHolerite(dadosCalculados, nomeAba, inputTipoCalculo.value);
    } catch (error) {
        boxErro.textContent = `⚠️ ${error.message}`;
        boxErro.style.display = 'block';
    } finally {
        btnSubmit.innerText = textoOriginal;
        btnSubmit.disabled = false;
    }
});
