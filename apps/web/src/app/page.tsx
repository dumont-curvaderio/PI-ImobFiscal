import Link from 'next/link';

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <h1 className="text-4xl font-bold text-gray-900">ImobFiscal</h1>
      <p className="mt-4 text-lg text-gray-600">
        Gestão imobiliária com compliance fiscal — LC 214/2025
      </p>
      <div className="mt-8 flex gap-4">
        <Link
          href="/login"
          className="rounded-md bg-blue-600 px-6 py-3 text-white hover:bg-blue-700"
        >
          Entrar
        </Link>
        <Link
          href="/cadastro"
          className="rounded-md border border-blue-600 px-6 py-3 text-blue-600 hover:bg-blue-50"
        >
          Criar conta
        </Link>
      </div>
    </main>
  );
}
