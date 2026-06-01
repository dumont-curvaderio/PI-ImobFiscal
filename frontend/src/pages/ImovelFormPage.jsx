// Página de Formulário de Imóvel
// Funciona tanto para CRIAR um novo imóvel quanto para EDITAR um existente
// A diferença é detectada pela URL: se tem ":id" na URL, é edição; senão, é criação
import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { buscarImovel, criarImovel, atualizarImovel } from '../services/api.js'

function ImovelFormPage() {
  // useParams lê os parâmetros da URL (ex: /imoveis/abc-uuid/editar → id = "abc-uuid")
  const { id } = useParams()

  // Se tem "id" na URL, estamos editando. Se não tem, estamos criando.
  const modoEdicao = Boolean(id)

  // -----------------------------------------------------------------------
  // Estados dos campos obrigatórios do formulário
  // Correspondem aos campos de ImovelRequest no backend
  // -----------------------------------------------------------------------
  const [codigo, setCodigo] = useState('')
  // Tipo do imóvel — enum no backend: RESIDENCIAL, COMERCIAL
  const [tipo, setTipo] = useState('RESIDENCIAL')
  // Endereço separado em partes (o backend retorna e espera campos individuais)
  const [cep, setCep] = useState('')
  const [logradouro, setLogradouro] = useState('')
  const [numero, setNumero] = useState('')
  const [complemento, setComplemento] = useState('')
  const [bairro, setBairro] = useState('')
  const [cidade, setCidade] = useState('')
  // UF: sigla do estado com 2 letras (ex: "SP", "RJ")
  const [uf, setUf] = useState('')
  // locadorId é UUID (string), não número inteiro
  const [locadorId, setLocadorId] = useState('')

  // -----------------------------------------------------------------------
  // Estados dos campos opcionais ("Dados adicionais")
  // -----------------------------------------------------------------------
  const [areaTotal, setAreaTotal] = useState('')
  const [quartos, setQuartos] = useState('')
  const [vagas, setVagas] = useState('')
  // valorCompra substitui o antigo "valorVenal"
  const [valorCompra, setValorCompra] = useState('')
  const [dataCompra, setDataCompra] = useState('')

  // -----------------------------------------------------------------------
  // Estados de controle da interface
  // -----------------------------------------------------------------------
  const [carregando, setCarregando] = useState(false)
  // No modo edição, aguarda os dados chegarem antes de mostrar o formulário
  const [carregandoDados, setCarregandoDados] = useState(modoEdicao)
  const [erro, setErro] = useState(null)

  const navegar = useNavigate()

  // Se for modo edição, busca os dados atuais do imóvel para preencher o formulário
  useEffect(() => {
    if (modoEdicao) {
      carregarImovel()
    }
  }, [id]) // Reexecuta se o id mudar

  // Busca os dados do imóvel pelo ID e preenche os campos
  async function carregarImovel() {
    setCarregandoDados(true)
    try {
      const resposta = await buscarImovel(id)
      const imovel = resposta.data

      // Preenche cada campo com o valor vindo do backend (ou string vazia como fallback)
      setCodigo(imovel.codigo || '')
      // Campo "tipo" do ImovelResponse (não "tipoUso")
      setTipo(imovel.tipo || 'RESIDENCIAL')
      setCep(imovel.cep || '')
      setLogradouro(imovel.logradouro || '')
      setNumero(imovel.numero || '')
      setComplemento(imovel.complemento || '')
      setBairro(imovel.bairro || '')
      setCidade(imovel.cidade || '')
      setUf(imovel.uf || '')
      // locadorId é UUID string — sem parseInt
      setLocadorId(imovel.locadorId || '')
      // Campos opcionais: converte null para string vazia para o input funcionar
      setAreaTotal(imovel.areaTotal != null ? String(imovel.areaTotal) : '')
      setQuartos(imovel.quartos != null ? String(imovel.quartos) : '')
      setVagas(imovel.vagas != null ? String(imovel.vagas) : '')
      // Campo "valorCompra" do ImovelResponse (não "valorVenal")
      setValorCompra(imovel.valorCompra != null ? String(imovel.valorCompra) : '')
      setDataCompra(imovel.dataCompra || '')
    } catch (err) {
      setErro('Erro ao carregar os dados do imóvel.')
    } finally {
      setCarregandoDados(false)
    }
  }

  // Função chamada ao enviar o formulário (criar ou editar)
  async function handleSubmit(evento) {
    evento.preventDefault()
    setErro(null)
    setCarregando(true)

    // Monta o objeto com os dados do formulário para enviar à API
    // Deve corresponder exatamente ao ImovelRequest do backend
    const dados = {
      codigo,
      tipo,
      // Remove tudo que não for dígito do CEP antes de enviar (ex: "13.000-000" → "13000000")
      cep: cep.replace(/\D/g, ''),
      logradouro,
      numero,
      // Complemento é opcional — envia null se estiver vazio
      complemento: complemento || null,
      bairro,
      cidade,
      // UF sempre em maiúsculas (ex: "sp" → "SP")
      uf: uf.toUpperCase(),
      // locadorId é UUID (string) — não usar parseInt
      locadorId: locadorId || null,
      // Campos numéricos opcionais: converte string para número, ou null se vazio
      areaTotal: areaTotal ? parseFloat(areaTotal) : null,
      quartos: quartos ? parseInt(quartos, 10) : null,
      vagas: vagas ? parseInt(vagas, 10) : null,
      // valorCompra substitui o antigo valorVenal
      valorCompra: valorCompra ? parseFloat(valorCompra) : null,
      // dataCompra no formato "YYYY-MM-DD" (input type="date" já retorna assim)
      dataCompra: dataCompra || null,
    }

    try {
      if (modoEdicao) {
        // Modo edição: chama PUT para atualizar
        await atualizarImovel(id, dados)
      } else {
        // Modo criação: chama POST para criar
        await criarImovel(dados)
      }

      // Após salvar com sucesso, volta para a lista
      navegar('/imoveis')
    } catch (err) {
      if (err.response?.data?.message) {
        setErro(err.response.data.message)
      } else {
        setErro('Erro ao salvar o imóvel. Verifique os dados e tente novamente.')
      }
    } finally {
      setCarregando(false)
    }
  }

  // Exibe spinner enquanto carrega os dados no modo edição
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

      {/* Cabeçalho com título dinâmico */}
      <div className="d-flex align-items-center mb-4">
        <button
          className="btn btn-outline-secondary me-3"
          onClick={() => navegar('/imoveis')}
          title="Voltar para a lista"
        >
          <i className="bi bi-arrow-left"></i>
        </button>
        <h4 className="mb-0">
          <i className={`bi ${modoEdicao ? 'bi-pencil-square' : 'bi-plus-circle'} me-2 text-primary`}></i>
          {/* Título muda conforme o modo */}
          {modoEdicao ? 'Editar Imóvel' : 'Novo Imóvel'}
        </h4>
      </div>

      {/* Card com o formulário */}
      <div className="card shadow-sm">
        <div className="card-body p-4">

          {/* Mensagem de erro */}
          {erro && (
            <div className="alert alert-danger" role="alert">
              <i className="bi bi-exclamation-triangle me-2"></i>
              {erro}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            {/* ============================================================
                Linha 1: Código (col-8) + Tipo (col-4)
                ============================================================ */}
            <div className="row mb-3">
              <div className="col-md-8">
                <label htmlFor="codigo" className="form-label">
                  Código do Imóvel <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="codigo"
                  className="form-control"
                  placeholder="Ex: IMV-001"
                  value={codigo}
                  onChange={(e) => setCodigo(e.target.value)}
                  required
                />
              </div>
              <div className="col-md-4">
                <label htmlFor="tipo" className="form-label">
                  Tipo <span className="text-danger">*</span>
                </label>
                {/* Select com os valores do enum Tipo no backend */}
                <select
                  id="tipo"
                  className="form-select"
                  value={tipo}
                  onChange={(e) => setTipo(e.target.value)}
                  required
                >
                  <option value="RESIDENCIAL">Residencial</option>
                  <option value="COMERCIAL">Comercial</option>
                </select>
              </div>
            </div>

            {/* ============================================================
                Linha 2: CEP (col-3) + Logradouro (col-6) + Número (col-3)
                ============================================================ */}
            <div className="row mb-3">
              <div className="col-md-3">
                <label htmlFor="cep" className="form-label">
                  CEP <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="cep"
                  className="form-control"
                  placeholder="00000000"
                  value={cep}
                  onChange={(e) => setCep(e.target.value)}
                  maxLength={9}
                  required
                />
                <div className="form-text">Somente dígitos ou com traço</div>
              </div>
              <div className="col-md-6">
                <label htmlFor="logradouro" className="form-label">
                  Logradouro <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="logradouro"
                  className="form-control"
                  placeholder="Ex: Rua das Flores"
                  value={logradouro}
                  onChange={(e) => setLogradouro(e.target.value)}
                  required
                />
              </div>
              <div className="col-md-3">
                <label htmlFor="numero" className="form-label">
                  Número <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  id="numero"
                  className="form-control"
                  placeholder="Ex: 100"
                  value={numero}
                  onChange={(e) => setNumero(e.target.value)}
                  required
                />
              </div>
            </div>

            {/* ============================================================
                Linha 3: Complemento (col-4) + Bairro (col-4) + Cidade (col-3) + UF (col-1)
                ============================================================ */}
            <div className="row mb-3">
              <div className="col-md-4">
                <label htmlFor="complemento" className="form-label">
                  Complemento
                </label>
                <input
                  type="text"
                  id="complemento"
                  className="form-control"
                  placeholder="Apto, sala, bloco..."
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
                  placeholder="Ex: Centro"
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
                  placeholder="Ex: Campinas"
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
                  placeholder="SP"
                  value={uf}
                  onChange={(e) => setUf(e.target.value)}
                  maxLength={2}
                  required
                />
              </div>
            </div>

            {/* ============================================================
                Linha 4: ID do Locador (linha inteira)
                locadorId é UUID (string de 36 chars), não número inteiro
                ============================================================ */}
            <div className="mb-3">
              <label htmlFor="locadorId" className="form-label">
                ID do Locador
              </label>
              <input
                type="text"
                id="locadorId"
                className="form-control"
                placeholder="Ex: 550e8400-e29b-41d4-a716-446655440000"
                value={locadorId}
                onChange={(e) => setLocadorId(e.target.value)}
              />
              <div className="form-text">UUID do proprietário cadastrado no sistema</div>
            </div>

            {/* ============================================================
                Seção colapsável: Dados adicionais (campos opcionais)
                Usa o componente Collapse nativo do Bootstrap 5
                ============================================================ */}
            <div className="mb-4">
              {/* Botão que abre/fecha a seção — data-bs-toggle="collapse" é Bootstrap puro */}
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

              {/* Conteúdo colapsável */}
              <div className="collapse" id="dadosAdicionais">
                <div className="border border-top-0 rounded-bottom p-3">

                  {/* Área total, Quartos, Vagas */}
                  <div className="row mb-3">
                    <div className="col-md-4">
                      <label htmlFor="areaTotal" className="form-label">Área Total (m²)</label>
                      <input
                        type="number"
                        id="areaTotal"
                        className="form-control"
                        placeholder="Ex: 85.5"
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
                        placeholder="Ex: 3"
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
                        placeholder="Ex: 2"
                        value={vagas}
                        onChange={(e) => setVagas(e.target.value)}
                        min="0"
                        step="1"
                      />
                    </div>
                  </div>

                  {/* Valor de Compra e Data de Compra */}
                  <div className="row">
                    <div className="col-md-6">
                      <label htmlFor="valorCompra" className="form-label">Valor de Compra (R$)</label>
                      <input
                        type="number"
                        id="valorCompra"
                        className="form-control"
                        placeholder="Ex: 250000.00"
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

            {/* Botões de ação */}
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

              {/* Botão cancelar volta para a lista sem salvar */}
              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => navegar('/imoveis')}
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
