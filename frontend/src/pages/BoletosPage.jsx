import { useState, useEffect } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { listarBoletos, gerarBoleto } from '../services/api.js'
import Toast from '../components/Toast.jsx'
import Breadcrumb from '../components/Breadcrumb.jsx'

function BoletosPage() {
  const [boletos, setBoletos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)
  const [toast, setToast] = useState(null)
  const [searchParams] = useSearchParams()
  const [contratoId, setContratoId] = useState(searchParams.get('contratoId') || '')
  const [dataVencimento, setDataVencimento] = useState('')
  const [gerando, setGerando] = useState(false)
  const [mostrarForm, setMostrarForm] = useState(Boolean(searchParams.get('contratoId')))

  useEffect(() => { carregarBoletos() }, [])

  async function carregarBoletos() {
    setCarregando(true)
    setErro(null)
    try {
      const resposta = await listarBoletos()
      setBoletos(resposta.data)
    } catch {
      setErro('Erro ao carregar boletos.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleGerarBoleto(e) {
    e.preventDefault()
    if (!contratoId || !dataVencimento) return
    setGerando(true)
    try {
      await gerarBoleto({ contratoId, dataVencimento })
      setMostrarForm(false)
      setContratoId('')
      setDataVencimento('')
      setToast({ mensagem: 'Boleto gerado com sucesso!', tipo: 'success' })
      await carregarBoletos()
    } catch (err) {
      setToast({ mensagem: err.response?.data?.message || 'Erro ao gerar boleto.', tipo: 'danger' })
    } finally {
      setGerando(false)
    }
  }

  function formatarMoeda(valor) {
    if (valor == null) return '—'
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor)
  }

  function badgeStatus(status) {
    const mapa = { GERADO: 'bg-primary', PAGO: 'bg-success', VENCIDO: 'bg-danger', CANCELADO: 'bg-secondary' }
    return <span className={`badge ${mapa[status] || 'bg-secondary'}`}>{status}</span>
  }

  return (
    <div className="container">
      {toast && <Toast mensagem={toast.mensagem} tipo={toast.tipo} onFechar={() => setToast(null)} />}

      <Breadcrumb pagina="Boletos" />

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 className="mb-0">
          <i className="bi bi-receipt me-2 text-primary"></i>
          Boletos de Aluguel
        </h4>
        <button className="btn btn-primary" onClick={() => setMostrarForm(!mostrarForm)}>
          <i className="bi bi-plus-circle me-1"></i>
          Gerar Boleto
        </button>
      </div>

      {mostrarForm && (
        <div className="card border-0 shadow-sm mb-4">
          <div className="card-body">
            <h6 className="text-muted mb-3"><i className="bi bi-receipt me-2"></i>Gerar Novo Boleto</h6>
            <form onSubmit={handleGerarBoleto} className="row g-3 align-items-end">
              <div className="col-md-5">
                <label className="form-label">ID do Contrato</label>
                <input className="form-control" value={contratoId} onChange={(e) => setContratoId(e.target.value)} required />
                <div className="form-text">
                  <Link to="/contratos">Ver contratos →</Link>
                </div>
              </div>
              <div className="col-md-3">
                <label className="form-label">Data de Vencimento</label>
                <input type="date" className="form-control" value={dataVencimento} onChange={(e) => setDataVencimento(e.target.value)} required />
              </div>
              <div className="col-md-2">
                <button type="submit" className="btn btn-primary w-100" disabled={gerando}>
                  {gerando ? <span className="spinner-border spinner-border-sm"></span> : 'Gerar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {carregando && (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
          <p className="mt-2 text-muted">Carregando boletos...</p>
        </div>
      )}

      {erro && <div className="alert alert-danger"><i className="bi bi-exclamation-triangle me-2"></i>{erro}</div>}

      {!carregando && !erro && (
        <>
          {boletos.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-receipt-cutoff display-1 text-primary"></i>
              <p className="mt-3">Nenhum boleto gerado ainda.</p>
              <button className="btn btn-primary" onClick={() => setMostrarForm(true)}>Gerar primeiro boleto</button>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle bg-white rounded">
                <thead className="table-dark">
                  <tr>
                    <th>Vencimento</th>
                    <th>Aluguel Bruto</th>
                    <th className="text-danger">IBS</th>
                    <th className="text-danger">CBS</th>
                    <th className="text-success">Líquido</th>
                    <th>Regime</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {boletos.map((boleto) => (
                    <tr key={boleto.id}>
                      <td>{boleto.dataVencimento}</td>
                      <td className="fw-semibold">{formatarMoeda(boleto.valorAluguel)}</td>
                      <td className="text-danger small">
                        -{formatarMoeda(boleto.valorIbs)}
                        <div className="text-muted" style={{ fontSize: '0.7rem' }}>
                          ({(boleto.aliquotaIbs * 100).toFixed(2)}%)
                        </div>
                      </td>
                      <td className="text-danger small">
                        -{formatarMoeda(boleto.valorCbs)}
                        <div className="text-muted" style={{ fontSize: '0.7rem' }}>
                          ({(boleto.aliquotaCbs * 100).toFixed(2)}%)
                        </div>
                      </td>
                      <td className="fw-bold text-success">{formatarMoeda(boleto.valorLiquido)}</td>
                      <td>
                        <span className="badge bg-secondary small">{boleto.regimeTributario?.replace('_', ' ')}</span>
                      </td>
                      <td>{badgeStatus(boleto.status)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <p className="text-muted small">Total: {boletos.length} boleto(s)</p>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default BoletosPage
