import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'ImobFiscal — Gestão Imobiliária com Compliance Fiscal',
  description:
    'SaaS imobiliário brasileiro com módulo fiscal completo conforme LC 214/2025',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR">
      <body>{children}</body>
    </html>
  );
}
