import { useState, useEffect } from 'react'
import { useNavigate, useParams, Link } from 'react-router-dom'
import { buscarImovel } from '../services/api.js'
import Breadcrumb from '../components/Breadcrumb.jsx'

function ImovelDetalhePage() {
  const { id } = useParams()
  const [imovel, setImovel] = useState(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)
  const navegar = useNavigate()

  useEffect(() => {
    async function carregar() {
      try {
        const resposta = await buscarImovel(id)
        setImovel(resposta.data)
      } catch (err) {
        if (err.response?.status === 404) {
          setErro('Imóvel não encontrado.')
        } else {
          setErro('Erro ao carregar os dados do imóvel.')
        }
      } finally {
        setCarregando(false)
      }
    }

    carregar()
  }, [id])

  function formatarMoeda(valor) {
    if (valor == null) return 'Não informado'
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(valor)
  }

  function formatarData(dataIso) {
    if (!dataIso) return 'Não informada'
    return new Date(dataIso).toLocaleDateString('pt-BR')
  }

  if (carregando) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status"></div>
        <p className="mt-2 text-muted">Carregando imóvel...</p>
      </div>
    )
  }

  if (erro) {
    return (
      <div className="container py-4">
        <div className="alert alert-danger" role="alert">
          <i className="bi bi-exclamation-triangle me-2"></i>
          {erro}
        </div>
        <button className="btn btn-secondary" onClick={() => navegar(-1)}>
          <i className="bi bi-arrow-left me-1"></i>
          Voltar
        </button>
      </div>
    )
  }

  return (
    <div className="container py-4" style={{ maxWidth: '700px' }}>

      <Breadcrumb pagina="Imóveis" sub="Detalhe" />

      <div className="d-flex align-items-center mb-4">
        <button
          className="btn btn-outline-secondary me-3"
          onClick={() => navegar(-1)}
          title="Voltar"
        >
          <i className="bi bi-arrow-left"></i>
        </button>
        <h4 className="mb-0">
          <i className="bi bi-house-door me-2 text-primary"></i>
          Detalhe do Imóvel
        </h4>
      </div>

      <div className="card shadow-sm">

        <div className="card-header d-flex justify-content-between align-items-center">
          <h5 className="mb-0 fw-bold">{imovel.codigo}</h5>
          <span className={`badge fs-6 ${imovel.tipo === 'COMERCIAL' ? 'bg-warning text-dark' : 'badge-teal'}`}>
            {imovel.tipo}
          </span>
        </div>

        <div className="card-body">
          <dl className="row mb-0">

            <dt className="col-sm-4 text-muted">
              <i className="bi bi-geo-alt me-1"></i>
              CEP
            </dt>
            <dd className="col-sm-8">{imovel.cep || 'Não informado'}</dd>

            <dt className="col-sm-4 text-muted">
              <i className="bi bi-signpost me-1"></i>
              Logradouro
            </dt>
            <dd className="col-sm-8">
              {imovel.logradouro
                ? `${imovel.logradouro}, ${imovel.numero || 's/n'}${imovel.complemento ? ` — ${imovel.complemento}` : ''}`
                : 'Não informado'}
            </dd>

            <dt className="col-sm-4 text-muted">
              <i className="bi bi-map me-1"></i>
              Bairro
            </dt>
            <dd className="col-sm-8">{imovel.bairro || 'Não informado'}</dd>

            <dt className="col-sm-4 text-muted">
              <i className="bi bi-building me-1"></i>
              Cidade / UF
            </dt>
            <dd className="col-sm-8">
              {imovel.cidade ? `${imovel.cidade} / ${imovel.uf}` : 'Não informado'}
            </dd>

            <dt className="col-sm-4 text-muted">
              <i className="bi bi-currency-dollar me-1"></i>
              Valor de Compra
            </dt>
            <dd className="col-sm-8 fw-semibold text-primary">
              {formatarMoeda(imovel.valorCompra)}
            </dd>

            <dt className="col-sm-4 text-muted">
              <i className="bi bi-person me-1"></i>
              ID do Locador
            </dt>
            <dd className="col-sm-8 font-monospace small">
              {imovel.locadorId || 'Não informado'}
            </dd>

            {imovel.areaTotal != null && (
              <>
                <dt className="col-sm-4 text-muted">
                  <i className="bi bi-rulers me-1"></i>
                  Área Total
                </dt>
                <dd className="col-sm-8">{imovel.areaTotal} m²</dd>
              </>
            )}

            {imovel.quartos != null && (
              <>
                <dt className="col-sm-4 text-muted">
                  <i className="bi bi-door-closed me-1"></i>
                  Quartos
                </dt>
                <dd className="col-sm-8">{imovel.quartos}</dd>
              </>
            )}

            {imovel.vagas != null && (
              <>
                <dt className="col-sm-4 text-muted">
                  <i className="bi bi-p-square me-1"></i>
                  Vagas de Garagem
                </dt>
                <dd className="col-sm-8">{imovel.vagas}</dd>
              </>
            )}

            {imovel.dataCompra && (
              <>
                <dt className="col-sm-4 text-muted">
                  <i className="bi bi-calendar-check me-1"></i>
                  Data de Compra
                </dt>
                <dd className="col-sm-8">{formatarData(imovel.dataCompra)}</dd>
              </>
            )}

            {imovel.dataCadastro && (
              <>
                <dt className="col-sm-4 text-muted">
                  <i className="bi bi-calendar me-1"></i>
                  Cadastrado em
                </dt>
                <dd className="col-sm-8">{formatarData(imovel.dataCadastro)}</dd>
              </>
            )}

            {imovel.dataAtualizacao && (
              <>
                <dt className="col-sm-4 text-muted">
                  <i className="bi bi-clock-history me-1"></i>
                  Atualizado em
                </dt>
                <dd className="col-sm-8">{formatarData(imovel.dataAtualizacao)}</dd>
              </>
            )}

          </dl>
        </div>

        <div className="card-footer d-flex gap-2">
          <Link
            to={`/imoveis/${imovel.id}/editar`}
            className="btn btn-warning"
          >
            <i className="bi bi-pencil me-1"></i>
            Editar
          </Link>
          <button
            className="btn btn-outline-secondary"
            onClick={() => navegar(-1)}
          >
            <i className="bi bi-arrow-left me-1"></i>
            Voltar
          </button>
        </div>

      </div>
    </div>
  )
}

export default ImovelDetalhePage
