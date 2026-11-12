export function formatarMoeda(valorMonetario) {
    // Garante que o valor não seja nulo nem indefinido antes de formatar.
    const valorNumerico = Number(valorMonetario) || 0;
    return valorNumerico.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}
