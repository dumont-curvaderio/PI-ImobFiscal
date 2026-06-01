// Página de Login
// Formulário simples com email e senha para entrar no sistema
import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'

function LoginPage() {
  // Estados dos campos do formulário
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')

  // Estado para mostrar mensagem de erro (null = sem erro)
  const [erro, setErro] = useState(null)

  // Estado para desabilitar o botão enquanto aguarda a resposta
  const [carregando, setCarregando] = useState(false)

  // Pega a função de login do contexto de autenticação
  const { fazerLogin } = useAuth()

  // Hook para navegar entre páginas programaticamente
  const navegar = useNavigate()

  // Função chamada quando o formulário é enviado
  async function handleSubmit(evento) {
    // Impede o comportamento padrão do form (recarregar a página)
    evento.preventDefault()

    setErro(null)       // Limpa erro anterior
    setCarregando(true) // Ativa estado de carregando

    try {
      // Tenta fazer o login chamando a API
      await fazerLogin(email, senha)

      // Se deu certo, redireciona para a lista de imóveis
      navegar('/imoveis')
    } catch (err) {
      // Se deu erro, mostra mensagem para o usuário
      if (err.response?.status === 401) {
        setErro('Email ou senha incorretos.')
      } else {
        setErro('Erro ao conectar com o servidor. Tente novamente.')
      }
    } finally {
      // Sempre desativa o carregando, deu certo ou não
      setCarregando(false)
    }
  }

  return (
    // Container centralizado verticalmente na tela
    <div className="container d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
      <div className="card shadow" style={{ width: '100%', maxWidth: '420px' }}>
        <div className="card-body p-4">

          {/* Cabeçalho do card */}
          <h2 className="card-title text-center mb-4">
            <i className="bi bi-building me-2 text-primary"></i>
            ImobFiscal
          </h2>
          <p className="text-center text-muted mb-4">Faça login para continuar</p>

          {/* Mensagem de erro (aparece só quando há erro) */}
          {erro && (
            <div className="alert alert-danger" role="alert">
              <i className="bi bi-exclamation-triangle me-2"></i>
              {erro}
            </div>
          )}

          {/* Formulário de login */}
          <form onSubmit={handleSubmit}>

            {/* Campo de email */}
            <div className="mb-3">
              <label htmlFor="email" className="form-label">Email</label>
              <input
                type="email"
                id="email"
                className="form-control"
                placeholder="seu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoFocus
              />
            </div>

            {/* Campo de senha */}
            <div className="mb-4">
              <label htmlFor="senha" className="form-label">Senha</label>
              <input
                type="password"
                id="senha"
                className="form-control"
                placeholder="Sua senha"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                required
              />
            </div>

            {/* Botão de envio */}
            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={carregando}
            >
              {/* Mostra spinner enquanto carrega */}
              {carregando ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                  Entrando...
                </>
              ) : (
                <>
                  <i className="bi bi-box-arrow-in-right me-2"></i>
                  Entrar
                </>
              )}
            </button>
          </form>

          {/* Link para cadastro */}
          <hr />
          <p className="text-center mb-0">
            Não tem conta?{' '}
            <Link to="/cadastro">Cadastre-se</Link>
          </p>

        </div>
      </div>
    </div>
  )
}

export default LoginPage
