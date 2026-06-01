// Página de Listagem de Imóveis
// Exibe todos os imóveis em uma tabela com ações por linha
import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'
import { listarImoveis, excluirImovel } from '../services/api.js'

function ImoveisPage() {
  // Lista de imóveis carregados da API
  const [imoveis, setImoveis] = useState([])

  // Estado de carregamento (mostra spinner enquanto busca os dados)
  const [carregando, setCarregando] = useState(true)

  // Mensagem de erro, caso a busca falhe
  const [erro, setErro] = useState(null)

  // Pega o usuário logado e a função de logout do contexto
  const { usuario, fazerLogout } = useAuth()

  const navegar = useNavigate()

  // useEffect: executa uma vez quando a página carrega
  // Equivale ao "ao abrir a página, buscar os dados"
  useEffect(() => {
    carregarImoveis()
  }, []) // Array vazio = executa só na primeira renderização

  // Função que busca os imóveis do backend
  async function carregarImoveis() {
    setCarregando(true)
    setErro(null)

    try {
      const resposta = await listarImoveis()
      setImoveis(resposta.data)
    } catch (err) {
      if (err.response?.status === 401) {
        // Token expirado ou inválido — faz logout
        fazerLogout()
        navegar('/login')
      } else {
        setErro('Erro ao carregar imóveis. Tente novamente.')
      }
    } finally {
      setCarregando(false)
    }
  }

  // Função chamada ao clicar em "Excluir"
  async function handleExcluir(id, codigo) {
    // Pede confirmação antes de excluir
    const confirmar = window.confirm(`Deseja excluir o imóvel "${codigo}"?`)
    if (!confirmar) return

    try {
      await excluirImovel(id)
      // Atualiza a lista removendo o imóvel excluído (sem recarregar tudo)
      setImoveis(imoveis.filter((imovel) => imovel.id !== id))
    } catch (err) {
      alert('Erro ao excluir o imóvel. Tente novamente.')
    }
  }

  // Função para fazer logout e ir para login
  function handleLogout() {
    fazerLogout()
    navegar('/login')
  }

  // Formata valor monetário para exibição (ex: 125000 → R$ 125.000,00)
  function formatarMoeda(valor) {
    if (valor == null) return '-'
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(valor)
  }

  return (
    <div>
      {/* Barra de navegação */}
      <nav className="navbar navbar-dark bg-primary mb-4">
        <div className="container">
          {/* Logo/título do sistema */}
          <span className="navbar-brand fw-bold">
            <i className="bi bi-building me-2"></i>
            ImobFiscal
          </span>

          {/* Lado direito: email do usuário e botão sair */}
          <div className="d-flex align-items-center gap-3">
            <span className="text-white-50 small d-none d-md-inline">
              <i className="bi bi-person-circle me-1"></i>
              {usuario?.email}
            </span>
            <button
              className="btn btn-outline-light btn-sm"
              onClick={handleLogout}
            >
              <i className="bi bi-box-arrow-right me-1"></i>
              Sair
            </button>
          </div>
        </div>
      </nav>

      {/* Conteúdo principal */}
      <div className="container">

        {/* Cabeçalho da seção com botão novo imóvel */}
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h4 className="mb-0">
            <i className="bi bi-house-door me-2"></i>
            Imóveis Cadastrados
          </h4>
          <Link to="/imoveis/novo" className="btn btn-primary">
            <i className="bi bi-plus-circle me-1"></i>
            Novo Imóvel
          </Link>
        </div>

        {/* Spinner de carregamento */}
        {carregando && (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Carregando...</span>
            </div>
            <p className="mt-2 text-muted">Carregando imóveis...</p>
          </div>
        )}

        {/* Mensagem de erro */}
        {erro && (
          <div className="alert alert-danger" role="alert">
            <i className="bi bi-exclamation-triangle me-2"></i>
            {erro}
            <button
              className="btn btn-sm btn-outline-danger ms-3"
              onClick={carregarImoveis}
            >
              Tentar novamente
            </button>
          </div>
        )}

        {/* Tabela de imóveis (só aparece quando não está carregando) */}
        {!carregando && !erro && (
          <>
            {/* Mensagem quando não há imóveis */}
            {imoveis.length === 0 ? (
              <div className="text-center py-5 text-muted">
                <i className="bi bi-house-x display-1"></i>
                <p className="mt-3">Nenhum imóvel cadastrado ainda.</p>
                <Link to="/imoveis/novo" className="btn btn-primary">
                  Cadastrar primeiro imóvel
                </Link>
              </div>
            ) : (
              /* Tabela responsiva com os imóveis */
              <div className="table-responsive">
                <table className="table table-striped table-hover align-middle">
                  <thead className="table-dark">
                    <tr>
                      <th>Código</th>
                      <th>Endereço</th>
                      <th>Tipo</th>
                      <th>Valor de Compra</th>
                      <th className="text-center">Ações</th>
                    </tr>
                  </thead>
                  <tbody>
                    {/* Renderiza uma linha para cada imóvel */}
                    {imoveis.map((imovel) => (
                      <tr key={imovel.id}>
                        <td>
                          <span className="fw-semibold">{imovel.codigo}</span>
                        </td>
                        {/* Endereço montado a partir dos campos separados do backend */}
                        <td>
                          {`${imovel.logradouro || ''}, ${imovel.numero || ''} — ${imovel.cidade || ''}/${imovel.uf || ''}`}
                        </td>
                        <td>
                          {/* Badge colorido por tipo — campo "tipo" do ImovelResponse */}
                          <span className={`badge ${imovel.tipo === 'COMERCIAL' ? 'bg-warning text-dark' : 'bg-success'}`}>
                            {imovel.tipo}
                          </span>
                        </td>
                        <td>{formatarMoeda(imovel.valorCompra)}</td>
                        <td>
                          {/* Botões de ação: Ver, Editar, Excluir */}
                          <div className="d-flex gap-1 justify-content-center">
                            <Link
                              to={`/imoveis/${imovel.id}`}
                              className="btn btn-sm btn-outline-info"
                              title="Ver detalhes"
                            >
                              <i className="bi bi-eye"></i>
                            </Link>
                            <Link
                              to={`/imoveis/${imovel.id}/editar`}
                              className="btn btn-sm btn-outline-warning"
                              title="Editar"
                            >
                              <i className="bi bi-pencil"></i>
                            </Link>
                            <button
                              className="btn btn-sm btn-outline-danger"
                              title="Excluir"
                              onClick={() => handleExcluir(imovel.id, imovel.codigo)}
                            >
                              <i className="bi bi-trash"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                {/* Contador de registros */}
                <p className="text-muted small">
                  Total: {imoveis.length} imóvel(is) cadastrado(s)
                </p>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}

export default ImoveisPage
