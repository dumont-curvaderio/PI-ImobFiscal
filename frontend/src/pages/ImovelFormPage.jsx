// Página de Formulário de Imóvel
// Funciona tanto para CRIAR um novo imóvel quanto para EDITAR um existente
// A diferença é detectada pela URL: se tem ":id" na URL, é edição; senão, é criação
import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { buscarImovel, criarImovel, atualizarImovel } from '../services/api.js'

function ImovelFormPage() {
  // useParams lê os parâmetros da URL (ex: /imoveis/5/editar → id = "5")
  const { id } = useParams()

  // Se tem "id" na URL, estamos editando. Se não tem, estamos criando.
  const modoEdicao = Boolean(id)

  // Estados dos campos do formulário
  const [codigo, setCodigo] = useState('')
  const [endereco, setEndereco] = useState('')
  const [tipoUso, setTipoUso] = useState('RESIDENCIAL')
  const [valorVenal, setValorVenal] = useState('')
  const [locadorId, setLocadorId] = useState('')

  // Estados de controle
  const [carregando, setCarregando] = useState(false)
  const [carregandoDados, setCarregandoDados] = useState(modoEdicao) // Só carrega dados no modo edição
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

      // Preenche os campos com os dados vindos do backend
      setCodigo(imovel.codigo || '')
      setEndereco(imovel.endereco || '')
      setTipoUso(imovel.tipoUso || 'RESIDENCIAL')
      setValorVenal(imovel.valorVenal || '')
      setLocadorId(imovel.locadorId || '')
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
    const dados = {
      codigo,
      endereco,
      tipoUso,
      // Converte valorVenal para número (vem como string do input)
      valorVenal: valorVenal ? parseFloat(valorVenal) : null,
      // Converte locadorId para número inteiro
      locadorId: locadorId ? parseInt(locadorId, 10) : null,
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
    <div className="container py-4" style={{ maxWidth: '700px' }}>

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

            {/* Linha 1: Código e Tipo de Uso lado a lado */}
            <div className="row mb-3">
              <div className="col-md-6">
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
              <div className="col-md-6">
                <label htmlFor="tipoUso" className="form-label">
                  Tipo de Uso <span className="text-danger">*</span>
                </label>
                {/* Select com as opções de tipo de uso */}
                <select
                  id="tipoUso"
                  className="form-select"
                  value={tipoUso}
                  onChange={(e) => setTipoUso(e.target.value)}
                  required
                >
                  <option value="RESIDENCIAL">Residencial</option>
                  <option value="COMERCIAL">Comercial</option>
                </select>
              </div>
            </div>

            {/* Endereço (ocupa linha inteira) */}
            <div className="mb-3">
              <label htmlFor="endereco" className="form-label">
                Endereço <span className="text-danger">*</span>
              </label>
              <input
                type="text"
                id="endereco"
                className="form-control"
                placeholder="Rua, número, bairro, cidade"
                value={endereco}
                onChange={(e) => setEndereco(e.target.value)}
                required
              />
            </div>

            {/* Linha 2: Valor Venal e Locador ID */}
            <div className="row mb-4">
              <div className="col-md-6">
                <label htmlFor="valorVenal" className="form-label">
                  Valor Venal (R$)
                </label>
                <input
                  type="number"
                  id="valorVenal"
                  className="form-control"
                  placeholder="0,00"
                  value={valorVenal}
                  onChange={(e) => setValorVenal(e.target.value)}
                  min="0"
                  step="0.01"
                />
                <div className="form-text">Valor para cálculo de IPTU</div>
              </div>
              <div className="col-md-6">
                <label htmlFor="locadorId" className="form-label">
                  ID do Locador
                </label>
                <input
                  type="number"
                  id="locadorId"
                  className="form-control"
                  placeholder="Número do locador"
                  value={locadorId}
                  onChange={(e) => setLocadorId(e.target.value)}
                  min="1"
                />
                <div className="form-text">Código do proprietário no sistema</div>
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
