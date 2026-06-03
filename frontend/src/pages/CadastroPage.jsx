import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { cadastrar } from '../services/api.js'

function CadastroPage() {
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState(null)
  const [sucesso, setSucesso] = useState(false)
  const [carregando, setCarregando] = useState(false)
  const navegar = useNavigate()

  async function handleSubmit(evento) {
    evento.preventDefault()

    setErro(null)
    setCarregando(true)

    try {
      await cadastrar(nome, email, senha)
      setSucesso(true)
      setTimeout(() => navegar('/login'), 2000)
    } catch (err) {
      if (err.response?.status === 409) {
        setErro('Este email já está cadastrado.')
      } else if (err.response?.data?.message) {
        setErro(err.response.data.message)
      } else {
        setErro('Erro ao cadastrar. Tente novamente.')
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
            <i className="bi bi-person-plus me-2 text-primary"></i>
            Criar Conta
          </h2>
          <p className="text-center text-muted mb-4">ImobFiscal — Sistema Imobiliário</p>

          {sucesso && (
            <div className="alert alert-success" role="alert">
              <i className="bi bi-check-circle me-2"></i>
              Conta criada com sucesso! Redirecionando para o login...
            </div>
          )}

          {erro && (
            <div className="alert alert-danger" role="alert">
              <i className="bi bi-exclamation-triangle me-2"></i>
              {erro}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            <div className="mb-3">
              <label htmlFor="nome" className="form-label">Nome completo</label>
              <input
                type="text"
                id="nome"
                className="form-control"
                placeholder="Seu nome"
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                required
                autoFocus
              />
            </div>

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
              />
            </div>

            <div className="mb-4">
              <label htmlFor="senha" className="form-label">Senha</label>
              <input
                type="password"
                id="senha"
                className="form-control"
                placeholder="Mínimo 6 caracteres"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                minLength={6}
                required
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={carregando || sucesso}
            >
              {carregando ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                  Cadastrando...
                </>
              ) : (
                <>
                  <i className="bi bi-person-check me-2"></i>
                  Cadastrar
                </>
              )}
            </button>
          </form>

          <hr />
          <p className="text-center mb-0">
            Já tem conta?{' '}
            <Link to="/login">Fazer login</Link>
          </p>

        </div>
      </div>
    </div>
  )
}

export default CadastroPage
