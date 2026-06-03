import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { criarLocador, atualizarLocador, buscarLocador } from '../services/api.js'
import Toast from '../components/Toast.jsx'
import Breadcrumb from '../components/Breadcrumb.jsx'

const REGIMES = [
  { valor: 'PF', label: 'Pessoa Física' },
  { valor: 'SIMPLES_NACIONAL', label: 'Simples Nacional' },
  { valor: 'LUCRO_PRESUMIDO', label: 'Lucro Presumido' },
  { valor: 'LUCRO_REAL', label: 'Lucro Real' },
]

function LocadorFormPage() {
  const { id } = useParams()
  const navegar = useNavigate()
  const isEdicao = Boolean(id)

  const [tipoPessoa, setTipoPessoa] = useState('PF')
  const [cpfCnpj, setCpfCnpj] = useState('')
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [telefone, setTelefone] = useState('')
  const [regimeTributario, setRegimeTributario] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [toast, setToast] = useState(null)

  useEffect(() => {
    if (isEdicao) {
      buscarLocador(id).then((r) => {
        const l = r.data
        setTipoPessoa(l.tipoPessoa || 'PF')
        setCpfCnpj(l.cpfCnpj || '')
        setNome(l.nome || '')
        setEmail(l.email || '')
        setTelefone(l.telefone || '')
        setRegimeTributario(l.regimeTributario || 'PF')
      }).catch(() => setToast({ mensagem: 'Erro ao carregar locador.', tipo: 'danger' }))
    }
  }, [id, isEdicao])

  async function handleSubmit(e) {
    e.preventDefault()
    setEnviando(true)
    const dados = { tipoPessoa, cpfCnpj, nome, email: email || null, telefone: telefone || null, regimeTributario }
    try {
      if (isEdicao) {
        await atualizarLocador(id, dados)
      } else {
        await criarLocador(dados)
      }
      navegar('/locadores')
    } catch (err) {
      setToast({ mensagem: err.response?.data?.message || 'Erro ao salvar locador.', tipo: 'danger' })
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="container">
      {toast && <Toast mensagem={toast.mensagem} tipo={toast.tipo} onFechar={() => setToast(null)} />}

      <Breadcrumb pagina="Locadores" sub={isEdicao ? 'Editar' : 'Novo'} />

      <div className="d-flex align-items-center mb-4">
        <button
          className="btn btn-outline-secondary me-3"
          onClick={() => navegar(-1)}
          title="Voltar"
        >
          <i className="bi bi-arrow-left"></i>
        </button>
        <h4 className="mb-0">
          <i className="bi bi-person-plus-fill me-2 text-primary"></i>
          {isEdicao ? 'Editar Locador' : 'Novo Locador'}
        </h4>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <form onSubmit={handleSubmit}>

            <div className="mb-3">
              <label className="form-label fw-semibold">Tipo de Pessoa</label>
              <div className="d-flex gap-3">
                {['PF', 'PJ'].map((tipo) => (
                  <div key={tipo} className="form-check">
                    <input
                      className="form-check-input"
                      type="radio"
                      id={`tipo-${tipo}`}
                      value={tipo}
                      checked={tipoPessoa === tipo}
                      onChange={(e) => setTipoPessoa(e.target.value)}
                    />
                    <label className="form-check-label" htmlFor={`tipo-${tipo}`}>
                      {tipo === 'PF' ? 'Pessoa Física' : 'Pessoa Jurídica'}
                    </label>
                  </div>
                ))}
              </div>
            </div>

            <div className="row g-3">
              <div className="col-md-4">
                <label className="form-label">CPF / CNPJ <span className="text-danger">*</span></label>
                <input
                  className="form-control"
                  value={cpfCnpj}
                  onChange={(e) => setCpfCnpj(e.target.value)}
                  placeholder={tipoPessoa === 'PF' ? '000.000.000-00' : '00.000.000/0000-00'}
                  required
                />
              </div>
              <div className="col-md-8">
                <label className="form-label">Nome Completo / Razão Social <span className="text-danger">*</span></label>
                <input
                  className="form-control"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  placeholder="Nome do locador"
                  required
                />
              </div>
              <div className="col-md-6">
                <label className="form-label">E-mail</label>
                <input
                  type="email"
                  className="form-control"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="email@exemplo.com"
                />
              </div>
              <div className="col-md-3">
                <label className="form-label">Telefone</label>
                <input
                  className="form-control"
                  value={telefone}
                  onChange={(e) => setTelefone(e.target.value)}
                  placeholder="(00) 00000-0000"
                />
              </div>
              <div className="col-md-3">
                <label className="form-label">Regime Tributário</label>
                <select
                  className="form-select"
                  value={regimeTributario}
                  onChange={(e) => setRegimeTributario(e.target.value)}
                  required
                >
                  <option value="">Selecione o regime...</option>
                  {REGIMES.map((r) => (
                    <option key={r.valor} value={r.valor}>{r.label}</option>
                  ))}
                </select>
                <div className="form-text">Usado para calcular IBS/CBS</div>
              </div>
            </div>

            <div className="d-flex gap-2 mt-4">
              <button type="submit" className="btn btn-primary" disabled={enviando}>
                {enviando ? (
                  <><span className="spinner-border spinner-border-sm me-2"></span>Salvando...</>
                ) : (
                  <><i className="bi bi-check-lg me-1"></i>{isEdicao ? 'Salvar alterações' : 'Cadastrar Locador'}</>
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
  )
}

export default LocadorFormPage
