import { Link } from 'react-router-dom'

// Breadcrumb de navegação: mostra "Início > NomeDaPagina"
// Uso: <Breadcrumb pagina="Imóveis" />
// Uso com sub-nível: <Breadcrumb pagina="Imóveis" sub="Novo Imóvel" />
function Breadcrumb({ pagina, sub }) {
  return (
    <nav aria-label="breadcrumb" className="mb-2">
      <ol className="breadcrumb mb-0">
        <li className="breadcrumb-item">
          <Link to="/dashboard" className="text-decoration-none">
            <i className="bi bi-house-fill me-1"></i>Início
          </Link>
        </li>
        {sub ? (
          <>
            <li className="breadcrumb-item">
              <span className="text-muted">{pagina}</span>
            </li>
            <li className="breadcrumb-item active">{sub}</li>
          </>
        ) : (
          <li className="breadcrumb-item active">{pagina}</li>
        )}
      </ol>
    </nav>
  )
}

export default Breadcrumb
