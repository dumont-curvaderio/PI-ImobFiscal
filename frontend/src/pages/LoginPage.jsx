import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'

function LoginPage() {
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState(null)
  const [carregando, setCarregando] = useState(false)
  const { fazerLogin } = useAuth()
  const navegar = useNavigate()

  async function handleSubmit(evento) {
    evento.preventDefault()

    setErro(null)
    setCarregando(true)

    try {
      await fazerLogin(email, senha)
      navegar('/imoveis')
    } catch (err) {
      if (err.response?.status === 401) {
        setErro('Email ou senha incorretos.')
      } else {
        setErro('Erro ao conectar com o servidor. Tente novamente.')
      }
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="auth-wrapper">
      <div className="card auth-card">
        <div className="card-body p-4">

          <h2 className="card-title text-center mb-1 auth-titulo">
            <i className="bi bi-building me-2 text-primary"></i>
            ImobFiscal
          </h2>
          <p className="text-center text-muted mb-4">Faça login para continuar</p>

          {erro && (
            <div className="alert alert-danger" role="alert">
              <i className="bi bi-exclamation-triangle me-2"></i>
              {erro}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            <div className="mb-3">
              <label htmlFor="email" className="form-label">Email</label>
              <input
                type="email"
                id="email"
                className="form-control"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoFocus
              />
            </div>

            <div className="mb-4">
              <label htmlFor="senha" className="form-label">Senha</label>
              <input
                type="password"
                id="senha"
                className="form-control"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                required
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={carregando}
            >
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
