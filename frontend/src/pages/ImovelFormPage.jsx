import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { buscarImovel, criarImovel, atualizarImovel, listarLocadores, criarLocador } from '../services/api.js'
import Breadcrumb from '../components/Breadcrumb.jsx'

function ImovelFormPage() {
  const { id } = useParams()
  const modoEdicao = Boolean(id)

  const [codigo, setCodigo] = useState('')
  const [tipo, setTipo] = useState('')
  const [cep, setCep] = useState('')
  const [logradouro, setLogradouro] = useState('')
  const [numero, setNumero] = useState('')
  const [complemento, setComplemento] = useState('')
  const [bairro, setBairro] = useState('')
  const [cidade, setCidade] = useState('')
  const [uf, setUf] = useState('')
  const [locadorId, setLocadorId] = useState('')

  const [areaTotal, setAreaTotal] = useState('')
  const [quartos, setQuartos] = useState('')
  const [vagas, setVagas] = useState('')
  const [valorCompra, setValorCompra] = useState('')
  const [dataCompra, setDataCompra] = useState('')

  const [carregando, setCarregando] = useState(false)
  const [carregandoDados, setCarregandoDados] = useState(modoEdicao)
  const [erro, setErro] = useState(null)
  const [buscandoCep, setBuscandoCep] = useState(false)

  const [locadores, setLocadores] = useState([])
  const [locadorBusca, setLocadorBusca] = useState('')
  const [locadoresFiltrados, setLocadoresFiltrados] = useState([])
  const [mostrarDropdown, setMostrarDropdown] = useState(false)
  const [locadorSelecionado, setLocadorSelecionado] = useState(null)

  const [mostrarModalNovoLocador, setMostrarModalNovoLocador] = useState(false)
  const [novoLocadorNome, setNovoLocadorNome] = useState('')
  const [novoLocadorCpfCnpj, setNovoLocadorCpfCnpj] = useState('')
  const [novoLocadorTipoPessoa, setNovoLocadorTipoPessoa] = useState('PF')
  const [salvandoLocador, setSalvandoLocador] = useState(false)
  const [erroModal, setErroModal] = useState(null)
  const [semNumero, setSemNumero] = useState(false)

  const navegar = useNavigate()

  async function buscarCep(valorCep) {
    const cepLimpo = valorCep.replace(/\D/g, '')
    if (cepLimpo.length !== 8) return
    setBuscandoCep(true)
    try {
      const resposta = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`)
      const dados = await resposta.json()
      if (!dados.erro) {
        setLogradouro(dados.logradouro || '')
        setBairro(dados.bairro || '')
        setCidade(dados.localidade || '')
        setUf(dados.uf || '')
      }
    } catch {
    } finally {
      setBuscandoCep(false)
    }
  }

  function handleSemNumero(marcado) {
    setSemNumero(marcado)
    setNumero(marcado ? 'S/N' : '')
  }

  function handleBuscaLocador(texto) {
    setLocadorBusca(texto)
    setLocadorSelecionado(null)
    setLocadorId('')

    if (texto.trim().length === 0) {
      setMostrarDropdown(false)
      return
    }
    const termo = texto.toLowerCase()
    const filtrados = locadores.filter(
      (loc) =>
        loc.nome.toLowerCase().includes(termo) ||
        (loc.cpfCnpj && loc.cpfCnpj.includes(texto))
    )
    setLocadoresFiltrados(filtrados)
    setMostrarDropdown(true)
  }

  function selecionarLocador(loc) {
    setLocadorSelecionado(loc)
    setLocadorId(loc.id)
    setLocadorBusca(loc.nome)
    setMostrarDropdown(false)
  }

  function abrirModalNovoLocador() {
    setNovoLocadorNome(locadorBusca)
    setNovoLocadorCpfCnpj('')
    setNovoLocadorTipoPessoa('PF')
    setErroModal(null)
    setMostrarDropdown(false)
    setMostrarModalNovoLocador(true)
  }

  async function handleSalvarNovoLocador() {
    if (!novoLocadorNome.trim()) {
      setErroModal('Nome é obrigatório.')
      return
    }
    setSalvandoLocador(true)
    setErroModal(null)
    try {
      const resposta = await criarLocador({
        tipoPessoa: novoLocadorTipoPessoa,
        nome: novoLocadorNome.trim(),
        cpfCnpj: novoLocadorCpfCnpj.trim() || null,
      })
      const novoLocador = resposta.data
      setLocadores((prev) => [...prev, novoLocador])
      selecionarLocador(novoLocador)
      setMostrarModalNovoLocador(false)
    } catch (err) {
      const msg = err.response?.data?.message || 'Erro ao criar locador.'
      setErroModal(msg)
    } finally {
      setSalvandoLocador(false)
    }
  }

  useEffect(() => {
    listarLocadores()
      .then((r) => setLocadores(r.data))
      .catch(() => setLocadores([]))

    if (modoEdicao) {
      carregarImovel()
    }
  }, [id])

  async function carregarImovel() {
    setCarregandoDados(true)
    try {
      const resposta = await buscarImovel(id)
      const imovel = resposta.data

      setCodigo(imovel.codigo || '')
      setTipo(imovel.tipo || 'RESIDENCIAL')
      setCep(imovel.cep || '')
      setLogradouro(imovel.logradouro || '')
      setNumero(imovel.numero || '')
      setSemNumero(imovel.numero === 'S/N')
      setComplemento(imovel.complemento || '')
      setBairro(imovel.bairro || '')
      setCidade(imovel.cidade || '')
      setUf(imovel.uf || '')
      setLocadorId(imovel.locadorId || '')
      if (imovel.locadorId) {
        const locadorEncontrado = locadores.find((l) => l.id === imovel.locadorId)
        if (locadorEncontrado) {
          setLocadorSelecionado(locadorEncontrado)
          setLocadorBusca(locadorEncontrado.nome)
        } else {
          setLocadorBusca(imovel.locadorId)
        }
      }
      setAreaTotal(imovel.areaTotal != null ? String(imovel.areaTotal) : '')
      setQuartos(imovel.quartos != null ? String(imovel.quartos) : '')
      setVagas(imovel.vagas != null ? String(imovel.vagas) : '')
      setValorCompra(imovel.valorCompra != null ? String(imovel.valorCompra) : '')
      setDataCompra(imovel.dataCompra || '')
    } catch (err) {
      setErro('Erro ao carregar os dados do imóvel.')
    } finally {
      setCarregandoDados(false)
    }
  }

  async function handleSubmit(evento) {
    evento.preventDefault()
    setErro(null)
    setCarregando(true)

    const dados = {
      codigo,
      tipo,
      cep: cep.replace(/\D/g, ''),
      logradouro,
      numero,
      complemento: complemento || null,
      bairro,
      cidade,
      uf: uf.toUpperCase(),
      locadorId: locadorId || null,
      areaTotal: areaTotal ? parseFloat(areaTotal) : null,
      quartos: quartos ? parseInt(quartos, 10) : null,
      vagas: vagas ? parseInt(vagas, 10) : null,
      valorCompra: valorCompra ? parseFloat(valorCompra) : null,
      dataCompra: dataCompra || null,
    }

    try {
      if (modoEdicao) {
        await atualizarImovel(id, dados)
      } else {
        await criarImovel(dados)
      }

      navegar('/imoveis')
    } catch (err) {
      const data = err.response?.data
      const mensagem = data?.message || data?.error || 'Erro ao salvar o imóvel.'
      const detalhesCampos = data?.errors
        ? Object.entries(data.errors).map(([campo, msg]) => `${campo}: ${msg}`).join(' | ')
        : null
      setErro(detalhesCampos ? `${mensagem} — ${detalhesCampos}` : mensagem)
    } finally {
      setCarregando(false)
    }
  }

  if (carregandoDados) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status"></div>
        <p className="mt-2 text-muted">Carregando dados do imóvel...</p>
      </div>
    )
  }

  return (
    <div className="container py-4" style={{ maxWidth: '800px' }}>

      <Breadcrumb pagina="Imóveis" sub={modoEdicao ? 'Editar' : 'Novo'} />

      <div className="d-flex align-items-center mb-4">
        <button
          className="btn btn-outline-secondary me-3"
          onClick={() => navegar(-1)}
          title="Voltar"
        >
          <i className="bi bi-arrow-left"></i>
        </button>
        <h4 className="mb-0">
          <i className={`bi ${modoEdicao ? 'bi-pencil-square' : 'bi-plus-circle'} me-2 text-primary`}></i>
          {modoEdicao ? 'Editar Imóvel' : 'Novo Imóvel'}
        </h4>
      </div>

      <div className="card shadow-sm">
        <div className="card-body p-4">

          {erro && (
            <div className="alert alert-danger" role="alert">
              <i className="bi bi-exclamation-triangle me-2"></i>
              {erro}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            <div className="row mb-3">
              <div className="col-md-8">
                <label htmlFor="codigo" className="form-label">
                  Código do Imóvel <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="codigo"
                  className="form-control"
                  value={codigo}
                  onChange={(e) => setCodigo(e.target.value)}
                  required
                />
              </div>
              <div className="col-md-4">
                <label htmlFor="tipo" className="form-label">
                  Tipo <span className="text-danger">*</span>
                </label>
                <select
                  id="tipo"
                  className="form-select"
                  value={tipo}
                  onChange={(e) => setTipo(e.target.value)}
                  required
                >
                  <option value="">Selecione o tipo...</option>
                  <option value="RESIDENCIAL">Residencial</option>
                  <option value="COMERCIAL">Comercial</option>
                </select>
              </div>
            </div>

            <div className="row mb-3">
              <div className="col-md-3">
                <label htmlFor="cep" className="form-label">
                  CEP <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="cep"
                  className="form-control"
                  value={cep}
                  onChange={(e) => setCep(e.target.value)}
                  onBlur={(e) => buscarCep(e.target.value)}
                  maxLength={9}
                  required
                />
                <div className="form-text">
                  {buscandoCep ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-1" role="status"></span>
                      Buscando endereço...
                    </>
                  ) : (
                    'Digite e saia do campo para preencher o endereço'
                  )}
                </div>
              </div>
              <div className="col-md-6">
                <label htmlFor="logradouro" className="form-label">
                  Logradouro <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="logradouro"
                  className="form-control"
                  value={logradouro}
                  onChange={(e) => setLogradouro(e.target.value)}
                  required
                />
              </div>
              <div className="col-md-3">
                <label htmlFor="numero" className="form-label d-flex align-items-center gap-2 flex-wrap">
                  Número <span className="text-danger">*</span>
                  <span className="form-check mb-0 ms-1">
                    <input
                      type="checkbox"
                      className="form-check-input"
                      id="semNumero"
                      checked={semNumero}
                      onChange={(e) => handleSemNumero(e.target.checked)}
                    />
                    <label className="form-check-label small text-muted" htmlFor="semNumero">
                      S/N
                    </label>
                  </span>
                </label>
                <input
                  type="text"
                  id="numero"
                  className="form-control"
                  value={numero}
                  onChange={(e) => { if (!semNumero) setNumero(e.target.value) }}
                  readOnly={semNumero}
                  required
                />
              </div>
            </div>

            <div className="row mb-3">
              <div className="col-md-4">
                <label htmlFor="complemento" className="form-label">
                  Complemento
                </label>
                <input
                  type="text"
                  id="complemento"
                  className="form-control"
                  value={complemento}
                  onChange={(e) => setComplemento(e.target.value)}
                />
              </div>
              <div className="col-md-4">
                <label htmlFor="bairro" className="form-label">
                  Bairro <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="bairro"
                  className="form-control"
                  value={bairro}
                  onChange={(e) => setBairro(e.target.value)}
                  required
                />
              </div>
              <div className="col-md-3">
                <label htmlFor="cidade" className="form-label">
                  Cidade <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="cidade"
                  className="form-control"
                  value={cidade}
                  onChange={(e) => setCidade(e.target.value)}
                  required
                />
              </div>
              <div className="col-md-1">
                <label htmlFor="uf" className="form-label">
                  UF <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="uf"
                  className="form-control text-uppercase"
                  value={uf}
                  onChange={(e) => setUf(e.target.value)}
                  maxLength={2}
                  required
                />
              </div>
            </div>

            <div className="mb-3">
              <label htmlFor="locadorBusca" className="form-label">
                Locador (Proprietário)
                <span className="text-muted small fw-normal ms-2">— opcional</span>
              </label>

              <div className="position-relative">
                <div className="input-group">
                  <span className="input-group-text">
                    <i className="bi bi-search"></i>
                  </span>
                  <input
                    type="text"
                    id="locadorBusca"
                    className={`form-control ${locadorSelecionado ? 'border-success' : ''}`}
                    placeholder="Digite o nome ou CPF/CNPJ para buscar..."
                    value={locadorBusca}
                    onChange={(e) => handleBuscaLocador(e.target.value)}
                    onFocus={() => {
                      if (locadorBusca.trim() && !locadorSelecionado) setMostrarDropdown(true)
                    }}
                    onBlur={() => setTimeout(() => setMostrarDropdown(false), 150)}
                    autoComplete="off"
                  />
                  {locadorSelecionado && (
                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      title="Limpar seleção"
                      onClick={() => {
                        setLocadorSelecionado(null)
                        setLocadorId('')
                        setLocadorBusca('')
                      }}
                    >
                      <i className="bi bi-x-lg"></i>
                    </button>
                  )}
                </div>

                {mostrarDropdown && (
                  <div
                    className="dropdown-menu show w-100 shadow-sm border mt-1"
                    style={{ maxHeight: '200px', overflowY: 'auto', zIndex: 1050 }}
                  >
                    {locadoresFiltrados.length > 0 ? (
                      locadoresFiltrados.map((loc) => (
                        <button
                          key={loc.id}
                          type="button"
                          className="dropdown-item py-2 d-flex align-items-center gap-2"
                          onMouseDown={() => selecionarLocador(loc)}
                        >
                          <i className="bi bi-person-circle text-primary"></i>
                          <span>
                            <strong>{loc.nome}</strong>
                            {loc.cpfCnpj && (
                              <span className="text-muted ms-2 small">{loc.cpfCnpj}</span>
                            )}
                          </span>
                        </button>
                      ))
                    ) : (
                      <span className="dropdown-item-text text-muted small py-2">
                        Nenhum locador encontrado para "{locadorBusca}"
                      </span>
                    )}
                    <div className="dropdown-divider my-1"></div>
                    <button
                      type="button"
                      className="dropdown-item text-primary py-2 d-flex align-items-center gap-2"
                      onMouseDown={abrirModalNovoLocador}
                    >
                      <i className="bi bi-plus-circle-fill"></i>
                      <span>
                        Criar{locadorBusca.trim() ? ` "${locadorBusca.trim()}"` : ' novo locador'}
                      </span>
                    </button>
                  </div>
                )}
              </div>

              {locadorSelecionado && (
                <div className="mt-2">
                  <span className="badge bg-success-subtle text-success border border-success px-3 py-2 fs-6">
                    <i className="bi bi-person-check-fill me-2"></i>
                    {locadorSelecionado.nome}
                    {locadorSelecionado.cpfCnpj && (
                      <span className="fw-normal ms-2 opacity-75">— {locadorSelecionado.cpfCnpj}</span>
                    )}
                  </span>
                </div>
              )}

              <div className="form-text">
                {locadorSelecionado
                  ? 'Locador selecionado. Clique no ✕ para trocar.'
                  : 'Busque pelo nome ou CPF/CNPJ. Se não existir, crie diretamente aqui.'}
              </div>
            </div>

            <div className="mb-4">
              <button
                type="button"
                className="btn btn-outline-secondary btn-sm w-100 d-flex justify-content-between align-items-center"
                data-bs-toggle="collapse"
                data-bs-target="#dadosAdicionais"
                aria-expanded="false"
                aria-controls="dadosAdicionais"
              >
                <span>
                  <i className="bi bi-chevron-down me-2"></i>
                  Dados adicionais (opcional)
                </span>
                <span className="text-muted small">Área, quartos, valor de compra...</span>
              </button>

              <div className="collapse" id="dadosAdicionais">
                <div className="border border-top-0 rounded-bottom p-3">

                  <div className="row mb-3">
                    <div className="col-md-4">
                      <label htmlFor="areaTotal" className="form-label">Área Total (m²)</label>
                      <input
                        type="number"
                        id="areaTotal"
                        className="form-control"
                        value={areaTotal}
                        onChange={(e) => setAreaTotal(e.target.value)}
                        min="0"
                        step="0.01"
                      />
                    </div>
                    <div className="col-md-4">
                      <label htmlFor="quartos" className="form-label">Quartos</label>
                      <input
                        type="number"
                        id="quartos"
                        className="form-control"
                        value={quartos}
                        onChange={(e) => setQuartos(e.target.value)}
                        min="0"
                        step="1"
                      />
                    </div>
                    <div className="col-md-4">
                      <label htmlFor="vagas" className="form-label">Vagas de Garagem</label>
                      <input
                        type="number"
                        id="vagas"
                        className="form-control"
                        value={vagas}
                        onChange={(e) => setVagas(e.target.value)}
                        min="0"
                        step="1"
                      />
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-md-6">
                      <label htmlFor="valorCompra" className="form-label">Valor de Compra (R$)</label>
                      <input
                        type="number"
                        id="valorCompra"
                        className="form-control"
                        value={valorCompra}
                        onChange={(e) => setValorCompra(e.target.value)}
                        min="0"
                        step="0.01"
                      />
                      <div className="form-text">Usado para cálculo de ganho de capital (GCAP)</div>
                    </div>
                    <div className="col-md-6">
                      <label htmlFor="dataCompra" className="form-label">Data de Compra</label>
                      <input
                        type="date"
                        id="dataCompra"
                        className="form-control"
                        value={dataCompra}
                        onChange={(e) => setDataCompra(e.target.value)}
                      />
                    </div>
                  </div>

                </div>
              </div>
            </div>

            {mostrarModalNovoLocador && (
              <div
                className="modal fade show d-block"
                style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
                onClick={(e) => {
                  if (e.target === e.currentTarget) setMostrarModalNovoLocador(false)
                }}
              >
                <div className="modal-dialog">
                  <div className="modal-content">
                    <div className="modal-header">
                      <h5 className="modal-title">
                        <i className="bi bi-person-plus me-2 text-primary"></i>
                        Novo Locador
                      </h5>
                      <button
                        type="button"
                        className="btn-close"
                        onClick={() => setMostrarModalNovoLocador(false)}
                      />
                    </div>

                    <div className="modal-body">
                      {erroModal && (
                        <div className="alert alert-danger py-2 mb-3">
                          <i className="bi bi-exclamation-triangle me-2"></i>
                          {erroModal}
                        </div>
                      )}

                      <div className="mb-3">
                        <label className="form-label fw-semibold">
                          Tipo de Pessoa <span className="text-danger">*</span>
                        </label>
                        <div className="d-flex gap-4">
                          <div className="form-check">
                            <input
                              type="radio" className="form-check-input" id="modalTipoPf"
                              value="PF" checked={novoLocadorTipoPessoa === 'PF'}
                              onChange={(e) => setNovoLocadorTipoPessoa(e.target.value)}
                            />
                            <label className="form-check-label" htmlFor="modalTipoPf">
                              Pessoa Física
                            </label>
                          </div>
                          <div className="form-check">
                            <input
                              type="radio" className="form-check-input" id="modalTipoPj"
                              value="PJ" checked={novoLocadorTipoPessoa === 'PJ'}
                              onChange={(e) => setNovoLocadorTipoPessoa(e.target.value)}
                            />
                            <label className="form-check-label" htmlFor="modalTipoPj">
                              Pessoa Jurídica
                            </label>
                          </div>
                        </div>
                      </div>

                      <div className="mb-3">
                        <label htmlFor="modalNome" className="form-label fw-semibold">
                          Nome completo <span className="text-danger">*</span>
                        </label>
                        <input
                          type="text" id="modalNome" className="form-control"
                          value={novoLocadorNome}
                          onChange={(e) => setNovoLocadorNome(e.target.value)}
                          autoFocus
                        />
                      </div>

                      <div className="mb-1">
                        <label htmlFor="modalCpfCnpj" className="form-label fw-semibold">
                          {novoLocadorTipoPessoa === 'PF' ? 'CPF' : 'CNPJ'}
                          <span className="text-muted small fw-normal ms-2">— opcional</span>
                        </label>
                        <input
                          type="text" id="modalCpfCnpj" className="form-control"
                          value={novoLocadorCpfCnpj}
                          onChange={(e) => setNovoLocadorCpfCnpj(e.target.value)}
                        />
                      </div>
                      <div className="form-text mb-0">
                        Você pode completar os dados restantes em <strong>Locadores &gt; Editar</strong> depois.
                      </div>
                    </div>

                    <div className="modal-footer">
                      <button
                        type="button" className="btn btn-secondary"
                        onClick={() => setMostrarModalNovoLocador(false)}
                      >
                        Cancelar
                      </button>
                      <button
                        type="button" className="btn btn-primary"
                        onClick={handleSalvarNovoLocador}
                        disabled={salvandoLocador}
                      >
                        {salvandoLocador ? (
                          <>
                            <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                            Salvando...
                          </>
                        ) : (
                          <>
                            <i className="bi bi-floppy me-2"></i>
                            Salvar e selecionar
                          </>
                        )}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            )}

            <div className="d-flex gap-2">
              <button
                type="submit"
                className="btn btn-primary"
                disabled={carregando}
              >
                {carregando ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Salvando...
                  </>
                ) : (
                  <>
                    <i className="bi bi-floppy me-2"></i>
                    Salvar
                  </>
                )}
              </button>

              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => navegar(-1)}
                disabled={carregando}
              >
                <i className="bi bi-x-circle me-1"></i>
                Cancelar
              </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  )
}

export default ImovelFormPage
