import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { listarLocadores, excluirLocador } from '../services/api.js'
import Toast from '../components/Toast.jsx'
import Breadcrumb from '../components/Breadcrumb.jsx'

function LocadoresPage() {
  const [locadores, setLocadores] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)
  const [toast, setToast] = useState(null)

  useEffect(() => { carregarLocadores() }, [])

  async function carregarLocadores() {
    setCarregando(true)
    setErro(null)
    try {
      const resposta = await listarLocadores()
      setLocadores(resposta.data)
    } catch {
      setErro('Erro ao carregar locadores. Tente novamente.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleExcluir(id, nome) {
    if (!window.confirm(`Deseja excluir o locador "${nome}"?`)) return
    try {
      await excluirLocador(id)
      setLocadores(locadores.filter((l) => l.id !== id))
      setToast({ mensagem: `Locador "${nome}" excluído com sucesso.`, tipo: 'success' })
    } catch {
      setToast({ mensagem: 'Erro ao excluir locador. Tente novamente.', tipo: 'danger' })
    }
  }

  function badgeRegime(regime) {
    const mapa = {
      PF: 'bg-primary',
      SIMPLES_NACIONAL: 'bg-success',
      LUCRO_PRESUMIDO: 'bg-warning text-dark',
      LUCRO_REAL: 'bg-danger',
    }
    const labels = {
      PF: 'Pessoa Física',
      SIMPLES_NACIONAL: 'Simples Nacional',
      LUCRO_PRESUMIDO: 'Lucro Presumido',
      LUCRO_REAL: 'Lucro Real',
    }
    return (
      <span className={`badge ${mapa[regime] || 'bg-secondary'}`}>
        {labels[regime] || regime || '—'}
      </span>
    )
  }

  return (
    <div className="container">
      {toast && (
        <Toast mensagem={toast.mensagem} tipo={toast.tipo} onFechar={() => setToast(null)} />
      )}

      <Breadcrumb pagina="Locadores" />

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 className="mb-0">
          <i className="bi bi-person-lines-fill me-2 text-primary"></i>
          Locadores Cadastrados
        </h4>
        <Link to="/locadores/novo" className="btn btn-primary">
          <i className="bi bi-plus-circle me-1"></i>
          Novo Locador
        </Link>
      </div>

      {carregando && (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
          <p className="mt-2 text-muted">Carregando locadores...</p>
        </div>
      )}

      {erro && (
        <div className="alert alert-danger">
          <i className="bi bi-exclamation-triangle me-2"></i>
          {erro}
          <button className="btn btn-sm btn-outline-danger ms-3" onClick={carregarLocadores}>
            Tentar novamente
          </button>
        </div>
      )}

      {!carregando && !erro && (
        <>
          {locadores.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-person-x display-1 text-primary"></i>
              <p className="mt-3">Nenhum locador cadastrado ainda.</p>
              <Link to="/locadores/novo" className="btn btn-primary">
                Cadastrar primeiro locador
              </Link>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle bg-white rounded">
                <thead className="table-dark">
                  <tr>
                    <th>Nome</th>
                    <th>CPF/CNPJ</th>
                    <th>Tipo</th>
                    <th>Regime Tributário</th>
                    <th>Contato</th>
                    <th className="text-center">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {locadores.map((locador) => (
                    <tr key={locador.id}>
                      <td className="fw-semibold">{locador.nome}</td>
                      <td>{locador.cpfCnpj}</td>
                      <td>
                        <span className={`badge ${locador.tipoPessoa === 'PJ' ? 'bg-warning text-dark' : 'badge-teal'}`}>
                          {locador.tipoPessoa}
                        </span>
                      </td>
                      <td>{badgeRegime(locador.regimeTributario)}</td>
                      <td className="text-muted small">{locador.email || locador.telefone || '—'}</td>
                      <td>
                        <div className="d-flex gap-1 justify-content-center">
                          <Link to={`/locadores/${locador.id}/editar`} className="btn btn-sm btn-outline-warning" title="Editar">
                            <i className="bi bi-pencil"></i>
                          </Link>
                          <button className="btn btn-sm btn-outline-danger" title="Excluir" onClick={() => handleExcluir(locador.id, locador.nome)}>
                            <i className="bi bi-trash"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <p className="text-muted small">Total: {locadores.length} locador(es)</p>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default LocadoresPage
