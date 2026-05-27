export function validarCNPJ(cnpj: string): boolean {
  const n = cnpj.replace(/\D/g, '');
  if (n.length !== 14 || /^(\d)\1{13}$/.test(n)) return false;

  const calcDigit = (s: string, weights: number[]) =>
    s.split('').reduce((sum, d, i) => sum + parseInt(d) * (weights[i] ?? 0), 0);

  const w1 = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
  const w2 = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];

  const r1 = calcDigit(n.slice(0, 12), w1) % 11;
  const d1 = r1 < 2 ? 0 : 11 - r1;

  const r2 = calcDigit(n.slice(0, 13), w2) % 11;
  const d2 = r2 < 2 ? 0 : 11 - r2;

  return d1 === parseInt(n[12]!) && d2 === parseInt(n[13]!);
}
