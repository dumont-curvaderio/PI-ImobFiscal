import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { listarContratos, atualizarStatusContrato } from '../services/api.js'
import Toast from '../components/Toast.jsx'
import Breadcrumb from '../components/Breadcrumb.jsx'

function ContratosPage() {
  const [contratos, setContratos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)
  const [toast, setToast] = useState(null)

  useEffect(() => { carregarContratos() }, [])

  async function carregarContratos() {
    setCarregando(true)
    setErro(null)
    try {
      const resposta = await listarContratos()
      setContratos(resposta.data)
    } catch {
      setErro('Erro ao carregar contratos. Tente novamente.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleAlternarStatus(contrato) {
    const novoStatus = contrato.status === 'ATIVO' ? 'RESCINDIDO' : 'ATIVO'
    try {
      const resposta = await atualizarStatusContrato(contrato.id, novoStatus)
      setContratos(contratos.map((c) => c.id === contrato.id ? resposta.data : c))
      setToast({ mensagem: `Contrato atualizado para ${novoStatus}.`, tipo: 'success' })
    } catch {
      setToast({ mensagem: 'Erro ao atualizar status.', tipo: 'danger' })
    }
  }

  function formatarMoeda(valor) {
    if (valor == null) return '—'
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor)
  }

  function badgeStatus(status) {
    const mapa = {
      RASCUNHO: 'bg-secondary',
      ATIVO: 'bg-success',
      RESCINDIDO: 'bg-danger',
      ENCERRADO: 'bg-dark',
    }
    return <span className={`badge ${mapa[status] || 'bg-secondary'}`}>{status}</span>
  }

  const ativos = contratos.filter((c) => c.status === 'ATIVO').length

  return (
    <div className="container">
      {toast && <Toast mensagem={toast.mensagem} tipo={toast.tipo} onFechar={() => setToast(null)} />}

      <Breadcrumb pagina="Contratos" />

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 className="mb-0">
          <i className="bi bi-file-text me-2 text-primary"></i>
          Contratos de Locação
        </h4>
        <Link to="/contratos/novo" className="btn btn-primary">
          <i className="bi bi-plus-circle me-1"></i>
          Novo Contrato
        </Link>
      </div>

      {!carregando && !erro && contratos.length > 0 && (
        <div className="row g-2 mb-3">
          <div className="col-6 col-md-3">
            <div className="card border-0 bg-white shadow-sm text-center py-2">
              <div className="fs-4 fw-bold text-primary">{contratos.length}</div>
              <div className="text-muted small">Total</div>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className="card border-0 bg-white shadow-sm text-center py-2">
              <div className="fs-4 fw-bold text-success">{ativos}</div>
              <div className="text-muted small">Ativos</div>
            </div>
          </div>
        </div>
      )}

      {carregando && (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
          <p className="mt-2 text-muted">Carregando contratos...</p>
        </div>
      )}

      {erro && (
        <div className="alert alert-danger">
          <i className="bi bi-exclamation-triangle me-2"></i>{erro}
          <button className="btn btn-sm btn-outline-danger ms-3" onClick={carregarContratos}>Tentar novamente</button>
        </div>
      )}

      {!carregando && !erro && (
        <>
          {contratos.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-file-earmark-x display-1 text-primary"></i>
              <p className="mt-3">Nenhum contrato cadastrado ainda.</p>
              <Link to="/contratos/novo" className="btn btn-primary">Cadastrar primeiro contrato</Link>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle bg-white rounded">
                <thead className="table-dark">
                  <tr>
                    <th>Locatário</th>
                    <th>Tipo</th>
                    <th>Valor Aluguel</th>
                    <th>Vence dia</th>
                    <th>Início</th>
                    <th>Status</th>
                    <th className="text-center">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {contratos.map((contrato) => (
                    <tr key={contrato.id}>
                      <td>
                        <div className="fw-semibold">{contrato.locatarioNome}</div>
                        <div className="text-muted small">{contrato.locatarioCpfCnpj}</div>
                      </td>
                      <td>
                        <span className="badge badge-teal">{contrato.tipoLocacao?.replace('_', ' ')}</span>
                      </td>
                      <td className="fw-semibold">{formatarMoeda(contrato.valorAluguel)}</td>
                      <td>{contrato.diaVencimento}</td>
                      <td>{contrato.dataInicio}</td>
                      <td>{badgeStatus(contrato.status)}</td>
                      <td>
                        <div className="d-flex gap-1 justify-content-center">
                          {(contrato.status === 'ATIVO' || contrato.status === 'RASCUNHO') && (
                            <button
                              className={`btn btn-sm ${contrato.status === 'ATIVO' ? 'btn-outline-danger' : 'btn-outline-success'}`}
                              onClick={() => handleAlternarStatus(contrato)}
                              title={contrato.status === 'ATIVO' ? 'Rescindir' : 'Ativar'}
                            >
                              <i className={`bi ${contrato.status === 'ATIVO' ? 'bi-x-circle' : 'bi-check-circle'}`}></i>
                            </button>
                          )}
                          <Link to={`/boletos?contratoId=${contrato.id}`} className="btn btn-sm btn-outline-primary" title="Gerar Boleto">
                            <i className="bi bi-receipt"></i>
                          </Link>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <p className="text-muted small">Total: {contratos.length} contrato(s)</p>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default ContratosPage
