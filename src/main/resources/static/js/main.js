import { fetchCalculo } from './api/apiService.js';
import { renderizarHolerite } from './ui/holeriteBuilder.js';

// --- CONTROLE DE ABAS ---
const tabButtons = document.querySelectorAll('.tabs__btn');
const inputTipoCalculo = document.getElementById('tipo-calculo');

tabButtons.forEach(btn => {
    btn.addEventListener('click', (e) => {
        // Remove ativo de todos e adiciona no clicado
        document.querySelector('.tabs__btn--active').classList.remove('tabs__btn--active');
        e.target.classList.add('tabs__btn--active');

        // Salva a rota no input hidden
        inputTipoCalculo.value = e.target.getAttribute('data-calculo');

        // Esconde o holerite anterior ao trocar de aba
        document.getElementById('holerite-container').style.display = 'none';
    });
});

// --- CONTROLE DO FORMULÁRIO ---
const form = document.getElementById('form-calculo');
const btnSubmit = document.getElementById('btn-submit');

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Monta o Request para o Spring Boot
    const payload = {
        salario: parseFloat(document.getElementById('salario').value),
        conducao: document.getElementById('conducao').value === 'true',
        diasTrabalhados: parseInt(document.getElementById('diasTrabalhados').value),
        mesesTrabalhados: parseInt(document.getElementById('mesesTrabalhados').value),
        anosTrabalhados: 1
    };

    const endpoint = inputTipoCalculo.value;
    const nomeAba = document.querySelector('.tabs__btn--active').innerText;

    // Feedback Visual de Carregamento
    const textoOriginal = btnSubmit.innerText;
    btnSubmit.innerText = 'Processando...';
    btnSubmit.disabled = true;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Esconde a caixa de erro e o holerite sempre que tentar um novo cálculo
        const boxErro = document.getElementById('mensagem-erro');
        boxErro.style.display = 'none';
        document.getElementById('holerite-container').style.display = 'none';

        const payload = {
            salario: parseFloat(document.getElementById('salario').value),
            conducao: document.getElementById('conducao').value === 'true',
            diasTrabalhados: parseInt(document.getElementById('diasTrabalhados').value),
            mesesTrabalhados: parseInt(document.getElementById('mesesTrabalhados').value),
            anosTrabalhados: 1
        };

        const endpoint = inputTipoCalculo.value;
        const nomeAba = document.querySelector('.tabs__btn--active').innerText;

        const textoOriginal = btnSubmit.innerText;
        btnSubmit.innerText = 'Processando...';
        btnSubmit.disabled = true;

        try {
            const dadosCalculados = await fetchCalculo(endpoint, payload);
            renderizarHolerite(dadosCalculados, nomeAba);
        } catch (error) {
            // MOSTRA A MENSAGEM DO JAVA NA TELA
            boxErro.innerText = "⚠️ " + error.message;
            boxErro.style.display = 'block';
        } finally {
            btnSubmit.innerText = textoOriginal;
            btnSubmit.disabled = false;
        }
    });
});