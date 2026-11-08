export function formatarMoeda(valor) {
    // Garante que o valor não seja nulo ou undefined antes de formatar
    const num = Number(valor) || 0;
    return num.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}