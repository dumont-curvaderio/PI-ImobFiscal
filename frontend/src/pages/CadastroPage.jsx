// Página de Cadastro
// Formulário para criar uma nova conta no sistema
import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { cadastrar } from '../services/api.js'

function CadastroPage() {
  // Estados dos campos do formulário
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')

  // Estado para mensagem de erro ou sucesso
  const [erro, setErro] = useState(null)
  const [sucesso, setSucesso] = useState(false)

  // Estado para desabilitar o botão durante a requisição
  const [carregando, setCarregando] = useState(false)

  // Hook de navegação
  const navegar = useNavigate()

  // Função chamada ao enviar o formulário
  async function handleSubmit(evento) {
    evento.preventDefault()

    setErro(null)
    setCarregando(true)

    try {
      // Chama a API para criar o usuário
      await cadastrar(nome, email, senha)

      // Exibe mensagem de sucesso brevemente e redireciona para login
      setSucesso(true)
      setTimeout(() => navegar('/login'), 2000)
    } catch (err) {
      // Trata erros comuns do backend
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
    // Fundo com gradiente teal → escuro (igual ao hero do site de referência)
    <div className="auth-wrapper">
      <div className="card auth-card">
        <div className="card-body p-4">

          {/* Cabeçalho */}
          <h2 className="card-title text-center mb-1 auth-titulo">
            <i className="bi bi-person-plus me-2 text-primary"></i>
            Criar Conta
          </h2>
          <p className="text-center text-muted mb-4">ImobFiscal — Sistema Imobiliário</p>

          {/* Mensagem de sucesso */}
          {sucesso && (
            <div className="alert alert-success" role="alert">
              <i className="bi bi-check-circle me-2"></i>
              Conta criada com sucesso! Redirecionando para o login...
            </div>
          )}

          {/* Mensagem de erro */}
          {erro && (
            <div className="alert alert-danger" role="alert">
              <i className="bi bi-exclamation-triangle me-2"></i>
              {erro}
            </div>
          )}

          {/* Formulário de cadastro */}
          <form onSubmit={handleSubmit}>

            {/* Campo nome */}
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

            {/* Campo email */}
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

            {/* Campo senha */}
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

            {/* Botão de cadastro */}
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

          {/* Link para voltar ao login */}
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
