import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'

function Layout({ children }) {
  const { usuario, fazerLogout } = useAuth()
  const navegar = useNavigate()

  function handleLogout() {
    fazerLogout()
    navegar('/login')
  }

  return (
    <>
      <nav className="navbar navbar-dark navbar-imobfiscal">
        <div className="container">

          <Link to="/dashboard" className="navbar-brand fw-bold fs-5 text-decoration-none">
            <i className="bi bi-building me-2" style={{ color: 'var(--cor3)' }}></i>
            ImobFiscal
          </Link>

          <div className="d-flex align-items-center gap-3">
            <Link to="/imoveis" className="nav-link text-white d-none d-md-inline px-0" style={{ opacity: 0.85 }}>
              <i className="bi bi-house-door me-1"></i>Imóveis
            </Link>
            <Link to="/locadores" className="nav-link text-white d-none d-md-inline px-0" style={{ opacity: 0.85 }}>
              <i className="bi bi-person-lines-fill me-1"></i>Locadores
            </Link>
            <Link to="/contratos" className="nav-link text-white d-none d-md-inline px-0" style={{ opacity: 0.85 }}>
              <i className="bi bi-file-text me-1"></i>Contratos
            </Link>
            <Link to="/simulador-fiscal" className="nav-link text-white d-none d-md-inline px-0" style={{ opacity: 0.85 }}>
              <i className="bi bi-calculator me-1"></i>Simulador
            </Link>
            <Link to="/boletos" className="nav-link text-white d-none d-md-inline px-0" style={{ opacity: 0.85 }}>
              <i className="bi bi-receipt me-1"></i>Boletos
            </Link>
            <span className="navbar-usuario d-none d-md-inline">
              <i className="bi bi-person-circle me-1"></i>
              {usuario?.email}
            </span>
            <button
              className="btn btn-outline-light btn-sm"
              onClick={handleLogout}
            >
              <i className="bi bi-box-arrow-right me-1"></i>
              Sair
            </button>
          </div>

        </div>
      </nav>

      <main className="layout-main">
        {children}
      </main>
    </>
  )
}

export default Layout
