import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { listarImoveis, listarContratos, listarBoletos } from '../services/api.js'
import { useAuth } from '../contexts/AuthContext.jsx'

function DashboardPage() {
  const [imoveis, setImoveis] = useState([])
  const [contratos, setContratos] = useState([])
  const [boletos, setBoletos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const { usuario } = useAuth()

  useEffect(() => {
    async function carregar() {
      try {
        const [resImoveis, resContratos, resBoletos] = await Promise.all([
          listarImoveis(),
          listarContratos(),
          listarBoletos(),
        ])
        setImoveis(resImoveis.data)
        setContratos(resContratos.data)
        setBoletos(resBoletos.data)
      } catch {
        // Silencia: listas vazias não impedem o dashboard de funcionar
      } finally {
        setCarregando(false)
      }
    }
    carregar()
  }, [])

  const total = imoveis.length
  const residenciais = imoveis.filter((i) => i.tipo === 'RESIDENCIAL').length
  const comerciais = imoveis.filter((i) => i.tipo === 'COMERCIAL').length
  const valorTotal = imoveis.reduce((soma, i) => soma + (i.valorCompra || 0), 0)

  function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor)
  }

  return (
    <div className="container py-4">

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-0">
            <i className="bi bi-speedometer2 me-2 text-primary"></i>
            Dashboard
          </h4>
          <small className="text-muted">Bem-vindo, {usuario?.email}</small>
        </div>
        <Link to="/imoveis/novo" className="btn btn-primary">
          <i className="bi bi-plus-circle me-1"></i>
          Novo Imóvel
        </Link>
      </div>

      {carregando ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
          <p className="mt-2 text-muted">Carregando dados...</p>
        </div>
      ) : (
        <>
          <div className="row g-3 mb-4">

            <div className="col-sm-6 col-lg-3">
              <div className="card h-100 border-0 shadow-sm">
                <div className="card-body d-flex align-items-center gap-3">
                  <div className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
                    style={{ width: 48, height: 48, background: 'rgba(56,115,115,0.12)' }}>
                    <i className="bi bi-building fs-5 text-primary"></i>
                  </div>
                  <div>
                    <div className="fs-2 fw-bold lh-1" style={{ color: 'var(--cor1)' }}>{total}</div>
                    <div className="text-muted small mt-1">Total de Imóveis</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-sm-6 col-lg-3">
              <div className="card h-100 border-0 shadow-sm">
                <div className="card-body d-flex align-items-center gap-3">
                  <div className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
                    style={{ width: 48, height: 48, background: 'rgba(56,115,115,0.12)' }}>
                    <i className="bi bi-house-door fs-5 text-primary"></i>
                  </div>
                  <div>
                    <div className="fs-2 fw-bold lh-1" style={{ color: 'var(--cor1)' }}>{residenciais}</div>
                    <div className="text-muted small mt-1">Residenciais</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-sm-6 col-lg-3">
              <div className="card h-100 border-0 shadow-sm">
                <div className="card-body d-flex align-items-center gap-3">
                  <div className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
                    style={{ width: 48, height: 48, background: 'rgba(245,172,54,0.15)' }}>
                    <i className="bi bi-shop fs-5 text-warning"></i>
                  </div>
                  <div>
                    <div className="fs-2 fw-bold lh-1" style={{ color: 'var(--cor1)' }}>{comerciais}</div>
                    <div className="text-muted small mt-1">Comerciais</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-sm-6 col-lg-3">
              <div className="card h-100 border-0 shadow-sm">
                <div className="card-body d-flex align-items-center gap-3">
                  <div className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
                    style={{ width: 48, height: 48, background: 'rgba(56,115,115,0.12)' }}>
                    <i className="bi bi-cash-stack fs-5 text-primary"></i>
                  </div>
                  <div>
                    <div className="fw-bold lh-1" style={{ color: 'var(--cor1)', fontSize: '1.05rem' }}>
                      {formatarMoeda(valorTotal)}
                    </div>
                    <div className="text-muted small mt-1">Valor do Portfólio</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-sm-6 col-lg-3">
              <div className="card h-100 border-0 shadow-sm">
                <div className="card-body d-flex align-items-center gap-3">
                  <div className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
                    style={{ width: 48, height: 48, background: 'rgba(25,135,84,0.12)' }}>
                    <i className="bi bi-file-text fs-5 text-success"></i>
                  </div>
                  <div>
                    <div className="fs-2 fw-bold lh-1" style={{ color: 'var(--cor1)' }}>
                      {contratos.filter((c) => c.status === 'ATIVO').length}
                    </div>
                    <div className="text-muted small mt-1">Contratos Ativos</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-sm-6 col-lg-3">
              <div className="card h-100 border-0 shadow-sm">
                <div className="card-body d-flex align-items-center gap-3">
                  <div className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
                    style={{ width: 48, height: 48, background: 'rgba(13,110,253,0.12)' }}>
                    <i className="bi bi-receipt fs-5 text-primary"></i>
                  </div>
                  <div>
                    <div className="fs-2 fw-bold lh-1" style={{ color: 'var(--cor1)' }}>{boletos.length}</div>
                    <div className="text-muted small mt-1">Boletos Gerados</div>
                  </div>
                </div>
              </div>
            </div>

          </div>

          <div className="row g-3">

            <div className="col-md-4">
              <div className="card border-0 shadow-sm h-100">
                <div className="card-body">
                  <h6 className="text-muted mb-3">
                    <i className="bi bi-lightning-charge me-2"></i>Ações Rápidas
                  </h6>
                  <div className="d-flex flex-column gap-2">
                    <Link to="/imoveis" className="btn btn-outline-primary text-start">
                      <i className="bi bi-list-ul me-2"></i>Ver todos os imóveis
                    </Link>
                    <Link to="/imoveis/novo" className="btn btn-primary text-start">
                      <i className="bi bi-plus-circle me-2"></i>Cadastrar novo imóvel
                    </Link>
                    <Link to="/contratos" className="btn btn-outline-primary text-start">
                      <i className="bi bi-file-text me-2"></i>Gerenciar contratos
                    </Link>
                    <Link to="/simulador-fiscal" className="btn btn-outline-secondary text-start">
                      <i className="bi bi-calculator me-2"></i>Simulador fiscal IBS/CBS
                    </Link>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-md-8">
              <div className="card border-0 shadow-sm h-100">
                <div className="card-body">
                  <h6 className="text-muted mb-3">
                    <i className="bi bi-clock-history me-2"></i>Imóveis Cadastrados
                  </h6>

                  {imoveis.length === 0 ? (
                    <div className="text-center py-3 text-muted">
                      <i className="bi bi-house-x display-6"></i>
                      <p className="mt-2 mb-0 small">Nenhum imóvel cadastrado ainda.</p>
                    </div>
                  ) : (
                    <ul className="list-unstyled mb-0">
                      {imoveis.slice(0, 5).map((imovel) => (
                        <li key={imovel.id}
                          className="d-flex justify-content-between align-items-center py-2 border-bottom">
                          <Link to={`/imoveis/${imovel.id}`} className="text-decoration-none">
                            <span className="fw-semibold" style={{ color: 'var(--cor1)' }}>
                              {imovel.codigo}
                            </span>
                            <span className="text-muted ms-2 small">
                              {imovel.cidade}/{imovel.uf}
                            </span>
                          </Link>
                          <span className={`badge ${imovel.tipo === 'COMERCIAL' ? 'bg-warning text-dark' : 'badge-teal'}`}>
                            {imovel.tipo}
                          </span>
                        </li>
                      ))}
                      {imoveis.length > 5 && (
                        <li className="pt-2">
                          <Link to="/imoveis" className="small text-primary">
                            Ver todos os {imoveis.length} imóveis →
                          </Link>
                        </li>
                      )}
                    </ul>
                  )}
                </div>
              </div>
            </div>

          </div>
        </>
      )}
    </div>
  )
}

export default DashboardPage
