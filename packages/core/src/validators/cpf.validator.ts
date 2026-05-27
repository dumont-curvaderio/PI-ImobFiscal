export function validarCPF(cpf: string): boolean {
  const n = cpf.replace(/\D/g, '');
  if (n.length !== 11 || /^(\d)\1{10}$/.test(n)) return false;

  const calc = (len: number) =>
    n.slice(0, len).split('').reduce((sum, d, i) => sum + parseInt(d) * (len + 1 - i), 0);

  const d1 = (calc(9) * 10) % 11 % 10;
  const d2 = (calc(10) * 10) % 11 % 10;
  return d1 === parseInt(n[9]!) && d2 === parseInt(n[10]!);
}
