import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { criarContrato, listarImoveis, calcularImposto } from '../services/api.js'
import Toast from '../components/Toast.jsx'
import Breadcrumb from '../components/Breadcrumb.jsx'

const TIPOS_LOCACAO = [
  { valor: 'RESIDENCIAL_LONGA', label: 'Residencial Longa Permanência' },
  { valor: 'COMERCIAL', label: 'Comercial' },
  { valor: 'SHORT_STAY', label: 'Short Stay (até 90 dias)' },
  { valor: 'RURAL', label: 'Rural' },
]

function ContratoFormPage() {
  const navegar = useNavigate()

  const [imovelId, setImovelId] = useState('')
  const [tipoLocacao, setTipoLocacao] = useState('')
  const [locatarioTipo, setLocatarioTipo] = useState('')
  const [locatarioCpfCnpj, setLocatarioCpfCnpj] = useState('')
  const [locatarioNome, setLocatarioNome] = useState('')
  const [valorAluguel, setValorAluguel] = useState('')
  const [diaVencimento, setDiaVencimento] = useState('')
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')

  const [imoveis, setImoveis] = useState([])
  const [previewFiscal, setPreviewFiscal] = useState(null)
  const [calculando, setCalculando] = useState(false)
  const [enviando, setEnviando] = useState(false)
  const [toast, setToast] = useState(null)

  useEffect(() => {
    listarImoveis()
      .then((r) => setImoveis(r.data))
      .catch(() => setToast({ mensagem: 'Erro ao carregar imóveis.', tipo: 'danger' }))
  }, [])

  const calcularPreview = useCallback(async (valor, imvId) => {
    if (!valor || !imvId || isNaN(parseFloat(valor)) || parseFloat(valor) <= 0) {
      setPreviewFiscal(null)
      return
    }
    const imovel = imoveis.find((i) => i.id === imvId)
    if (!imovel) return

    setCalculando(true)
    try {
      const resposta = await calcularImposto({
        valorBase: parseFloat(valor),
        regime: 'PF',
        tipoImovel: imovel.tipo,
      })
      setPreviewFiscal(resposta.data)
    } catch {
      setPreviewFiscal(null)
    } finally {
      setCalculando(false)
    }
  }, [imoveis])

  function handleValorBlur() {
    calcularPreview(valorAluguel, imovelId)
  }

  function handleImovelChange(e) {
    setImovelId(e.target.value)
    calcularPreview(valorAluguel, e.target.value)
  }

  function formatarMoeda(valor) {
    if (valor == null) return '—'
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor)
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setEnviando(true)
    const dados = {
      imovelId,
      tipoLocacao,
      locatarioTipo,
      locatarioCpfCnpj,
      locatarioNome,
      valorAluguel: parseFloat(valorAluguel),
      diaVencimento: parseInt(diaVencimento),
      dataInicio,
      dataFim: dataFim || null,
    }
    try {
      await criarContrato(dados)
      navegar('/contratos')
    } catch (err) {
      setToast({ mensagem: err.response?.data?.message || 'Erro ao salvar contrato.', tipo: 'danger' })
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="container">
      {toast && <Toast mensagem={toast.mensagem} tipo={toast.tipo} onFechar={() => setToast(null)} />}

      <Breadcrumb pagina="Contratos" sub="Novo" />

      <div className="d-flex align-items-center mb-4">
        <button
          className="btn btn-outline-secondary me-3"
          onClick={() => navegar(-1)}
          title="Voltar"
        >
          <i className="bi bi-arrow-left"></i>
        </button>
        <h4 className="mb-0">
          <i className="bi bi-file-earmark-plus me-2 text-primary"></i>
          Novo Contrato de Locação
        </h4>
      </div>

      <div className="row g-4">
        <div className="col-lg-8">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <form onSubmit={handleSubmit}>

                <div className="mb-3">
                  <label className="form-label fw-semibold">Imóvel <span className="text-danger">*</span></label>
                  <select className="form-select" value={imovelId} onChange={handleImovelChange} required>
                    <option value="">Selecione um imóvel...</option>
                    {imoveis.map((i) => (
                      <option key={i.id} value={i.id}>
                        {i.codigo} — {i.logradouro}, {i.numero} ({i.tipo})
                      </option>
                    ))}
                  </select>
                </div>

                <div className="mb-3">
                  <label className="form-label fw-semibold">Tipo de Locação</label>
                  <select className="form-select" value={tipoLocacao} onChange={(e) => setTipoLocacao(e.target.value)} required>
                    <option value="">Selecione o tipo...</option>
                    {TIPOS_LOCACAO.map((t) => (
                      <option key={t.valor} value={t.valor}>{t.label}</option>
                    ))}
                  </select>
                </div>

                <hr className="my-3" />
                <h6 className="text-muted mb-3"><i className="bi bi-person me-2"></i>Dados do Locatário</h6>

                <div className="row g-3">
                  <div className="col-md-3">
                    <label className="form-label">Tipo</label>
                    <select className="form-select" value={locatarioTipo} onChange={(e) => setLocatarioTipo(e.target.value)} required>
                      <option value="">Selecione...</option>
                      <option value="PF">Pessoa Física</option>
                      <option value="PJ">Pessoa Jurídica</option>
                    </select>
                  </div>
                  <div className="col-md-4">
                    <label className="form-label">CPF / CNPJ <span className="text-danger">*</span></label>
                    <input className="form-control" value={locatarioCpfCnpj} onChange={(e) => setLocatarioCpfCnpj(e.target.value)} required />
                  </div>
                  <div className="col-md-5">
                    <label className="form-label">Nome Completo <span className="text-danger">*</span></label>
                    <input className="form-control" value={locatarioNome} onChange={(e) => setLocatarioNome(e.target.value)} required />
                  </div>
                </div>

                <hr className="my-3" />
                <h6 className="text-muted mb-3"><i className="bi bi-currency-dollar me-2"></i>Dados Financeiros</h6>

                <div className="row g-3">
                  <div className="col-md-4">
                    <label className="form-label">Valor do Aluguel (R$) <span className="text-danger">*</span></label>
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      className="form-control"
                      value={valorAluguel}
                      onChange={(e) => setValorAluguel(e.target.value)}
                      onBlur={handleValorBlur}
                      required
                    />
                    <div className="form-text">Preencha para ver o cálculo fiscal</div>
                  </div>
                  <div className="col-md-2">
                    <label className="form-label">Dia Vencimento</label>
                    <input type="number" min="1" max="31" className="form-control" value={diaVencimento} onChange={(e) => setDiaVencimento(e.target.value)} required />
                  </div>
                  <div className="col-md-3">
                    <label className="form-label">Data Início <span className="text-danger">*</span></label>
                    <input type="date" className="form-control" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} required />
                  </div>
                  <div className="col-md-3">
                    <label className="form-label">Data Fim</label>
                    <input type="date" className="form-control" value={dataFim} onChange={(e) => setDataFim(e.target.value)} />
                  </div>
                </div>

                <div className="d-flex gap-2 mt-4">
                  <button type="submit" className="btn btn-primary" disabled={enviando}>
                    {enviando ? (
                      <><span className="spinner-border spinner-border-sm me-2"></span>Salvando...</>
                    ) : (
                      <><i className="bi bi-check-lg me-1"></i>Criar Contrato</>
                    )}
                  </button>
                  <button type="button" className="btn btn-outline-secondary" onClick={() => navegar(-1)}>
                    Cancelar
                  </button>
                </div>

              </form>
            </div>
          </div>
        </div>

        {/* Preview Fiscal */}
        <div className="col-lg-4">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="text-muted mb-3">
                <i className="bi bi-calculator me-2"></i>
                Preview Fiscal — IBS/CBS
              </h6>

              {calculando && (
                <div className="text-center py-3">
                  <div className="spinner-border spinner-border-sm text-primary" role="status"></div>
                  <p className="mt-2 small text-muted">Calculando...</p>
                </div>
              )}

              {!calculando && !previewFiscal && (
                <div className="text-center py-3 text-muted small">
                  <i className="bi bi-info-circle display-6 d-block mb-2"></i>
                  Preencha o valor do aluguel e selecione um imóvel para ver o cálculo fiscal.
                </div>
              )}

              {!calculando && previewFiscal && (
                <div>
                  <table className="table table-sm table-borderless">
                    <tbody>
                      <tr>
                        <td className="text-muted">Valor Base</td>
                        <td className="text-end fw-semibold">{formatarMoeda(previewFiscal.valorBase)}</td>
                      </tr>
                      <tr className="text-danger">
                        <td>IBS ({(previewFiscal.aliquotaIbs * 100).toFixed(2)}%)</td>
                        <td className="text-end">-{formatarMoeda(previewFiscal.valorIbs)}</td>
                      </tr>
                      <tr className="text-danger">
                        <td>CBS ({(previewFiscal.aliquotaCbs * 100).toFixed(2)}%)</td>
                        <td className="text-end">-{formatarMoeda(previewFiscal.valorCbs)}</td>
                      </tr>
                      <tr className="border-top">
                        <td className="fw-bold">Líquido ao Locador</td>
                        <td className="text-end fw-bold text-success">{formatarMoeda(previewFiscal.valorLiquido)}</td>
                      </tr>
                    </tbody>
                  </table>
                  <div className="alert alert-success py-2 mb-0 small">
                    <i className="bi bi-check-circle me-1"></i>
                    Split Payment <strong>ATIVO</strong> — IBS+CBS retidos automaticamente
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ContratoFormPage
