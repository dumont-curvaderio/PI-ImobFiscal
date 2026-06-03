import { useState } from 'react'
import { calcularImposto } from '../services/api.js'
import { Link } from 'react-router-dom'
import Breadcrumb from '../components/Breadcrumb.jsx'

const REGIMES = [
  { valor: 'PF', label: 'Pessoa Física' },
  { valor: 'SIMPLES_NACIONAL', label: 'Simples Nacional' },
  { valor: 'LUCRO_PRESUMIDO', label: 'Lucro Presumido' },
  { valor: 'LUCRO_REAL', label: 'Lucro Real' },
]

const TIPOS_IMOVEL = [
  { valor: 'RESIDENCIAL', label: 'Residencial' },
  { valor: 'COMERCIAL', label: 'Comercial' },
  { valor: 'RURAL', label: 'Rural' },
  { valor: 'MISTO', label: 'Misto' },
]

function SimuladorFiscalPage() {
  const [valorBase, setValorBase] = useState('')
  const [regime, setRegime] = useState('')
  const [tipoImovel, setTipoImovel] = useState('')
  const [resultado, setResultado] = useState(null)
  const [calculando, setCalculando] = useState(false)
  const [erro, setErro] = useState(null)

  function formatarMoeda(valor) {
    if (valor == null) return '—'
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor)
  }

  async function handleCalcular(e) {
    e.preventDefault()
    if (!valorBase || parseFloat(valorBase) <= 0 || !regime || !tipoImovel) return
    setCalculando(true)
    setErro(null)
    setResultado(null)
    try {
      const resposta = await calcularImposto({
        valorBase: parseFloat(valorBase),
        regime,
        tipoImovel,
      })
      setResultado(resposta.data)
    } catch {
      setErro('Erro ao calcular. Verifique se o backend está rodando.')
    } finally {
      setCalculando(false)
    }
  }

  return (
    <div className="container">
      <Breadcrumb pagina="Simulador Fiscal" />

      <div className="mb-4">
        <h4 className="mb-0">
          <i className="bi bi-calculator me-2 text-primary"></i>
          Simulador Fiscal — IBS/CBS
        </h4>
        <small className="text-muted">
          Reforma Tributária LC 214/2025 · Split Payment · Motor Tributário
        </small>
      </div>

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <form onSubmit={handleCalcular}>
                <div className="mb-3">
                  <label className="form-label fw-semibold">Valor do Aluguel (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0.01"
                    className="form-control form-control-lg"
                    value={valorBase}
                    onChange={(e) => setValorBase(e.target.value)}
                    placeholder="Ex: 1500.00"
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label fw-semibold">Regime Tributário do Locador</label>
                  <select className="form-select" value={regime} onChange={(e) => setRegime(e.target.value)} required>
                    <option value="">Selecione o regime...</option>
                    {REGIMES.map((r) => (
                      <option key={r.valor} value={r.valor}>{r.label}</option>
                    ))}
                  </select>
                </div>
                <div className="mb-4">
                  <label className="form-label fw-semibold">Tipo do Imóvel</label>
                  <select className="form-select" value={tipoImovel} onChange={(e) => setTipoImovel(e.target.value)} required>
                    <option value="">Selecione o tipo...</option>
                    {TIPOS_IMOVEL.map((t) => (
                      <option key={t.valor} value={t.valor}>{t.label}</option>
                    ))}
                  </select>
                </div>
                <button type="submit" className="btn btn-primary w-100" disabled={calculando}>
                  {calculando ? (
                    <><span className="spinner-border spinner-border-sm me-2"></span>Calculando...</>
                  ) : (
                    <><i className="bi bi-calculator me-2"></i>Calcular IBS/CBS</>
                  )}
                </button>
              </form>
            </div>
          </div>

          <div className="card border-0 shadow-sm mt-3">
            <div className="card-body">
              <h6 className="text-muted mb-2"><i className="bi bi-info-circle me-2"></i>O que é Split Payment?</h6>
              <p className="small text-muted mb-0">
                Mecanismo da Reforma Tributária (EC 132/2023) onde o IBS e CBS são retidos automaticamente no momento do pagamento, antes de creditar o valor líquido ao locador.
              </p>
            </div>
          </div>
        </div>

        <div className="col-lg-7">
          {erro && (
            <div className="alert alert-danger">
              <i className="bi bi-exclamation-triangle me-2"></i>{erro}
            </div>
          )}

          {!resultado && !erro && (
            <div className="card border-0 shadow-sm h-100">
              <div className="card-body d-flex align-items-center justify-content-center text-center text-muted py-5">
                <div>
                  <i className="bi bi-receipt display-1 d-block mb-3" style={{ color: 'var(--cor3)' }}></i>
                  <p>Preencha os dados e clique em <strong>Calcular</strong> para ver o detalhamento fiscal.</p>
                </div>
              </div>
            </div>
          )}

          {resultado && (
            <div className="card border-0 shadow-sm">
              <div className="card-header bg-white border-0 pb-0">
                <h5 className="mb-0" style={{ color: 'var(--cor1)' }}>
                  <i className="bi bi-receipt me-2"></i>Detalhamento Fiscal
                </h5>
                <small className="text-muted">
                  {REGIMES.find((r) => r.valor === resultado.regime)?.label} ·{' '}
                  {TIPOS_IMOVEL.find((t) => t.valor === resultado.tipoImovel)?.label}
                </small>
              </div>
              <div className="card-body">
                <table className="table table-borderless mb-3">
                  <tbody>
                    <tr className="border-bottom">
                      <td className="ps-0">Valor Total do Aluguel</td>
                      <td className="text-end fw-bold fs-5">{formatarMoeda(resultado.valorBase)}</td>
                    </tr>
                    <tr className="text-danger">
                      <td className="ps-0">
                        <i className="bi bi-arrow-right me-2"></i>
                        IBS — Imposto s/ Bens e Serviços ({(resultado.aliquotaIbs * 100).toFixed(2)}%)
                        <div className="text-muted small">Estadual/Municipal</div>
                      </td>
                      <td className="text-end fw-semibold">-{formatarMoeda(resultado.valorIbs)}</td>
                    </tr>
                    <tr className="text-danger border-bottom">
                      <td className="ps-0">
                        <i className="bi bi-arrow-right me-2"></i>
                        CBS — Contribuição s/ Bens e Serviços ({(resultado.aliquotaCbs * 100).toFixed(2)}%)
                        <div className="text-muted small">Federal</div>
                      </td>
                      <td className="text-end fw-semibold">-{formatarMoeda(resultado.valorCbs)}</td>
                    </tr>
                    <tr>
                      <td className="ps-0 fw-bold fs-5">Valor Líquido ao Locador</td>
                      <td className="text-end fw-bold fs-5 text-success">{formatarMoeda(resultado.valorLiquido)}</td>
                    </tr>
                  </tbody>
                </table>

                <div className="alert alert-success mb-0">
                  <i className="bi bi-check-circle-fill me-2"></i>
                  <strong>Split Payment ATIVO</strong> — IBS + CBS retidos automaticamente pelo gateway no momento do pagamento. O locador recebe o valor líquido em T+1 ou T+2.
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default SimuladorFiscalPage
