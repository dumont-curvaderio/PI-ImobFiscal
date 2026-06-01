// Componente raiz da aplicação
// Define todas as rotas usando React Router v6
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext.jsx'
import RotaPrivada from './components/RotaPrivada.jsx'
import Layout from './components/Layout.jsx'

// Importa todas as páginas
import LoginPage from './pages/LoginPage.jsx'
import CadastroPage from './pages/CadastroPage.jsx'
import DashboardPage from './pages/DashboardPage.jsx'
import ImoveisPage from './pages/ImoveisPage.jsx'
import ImovelFormPage from './pages/ImovelFormPage.jsx'
import ImovelDetalhePage from './pages/ImovelDetalhePage.jsx'
import LocadoresPage from './pages/LocadoresPage.jsx'
import LocadorFormPage from './pages/LocadorFormPage.jsx'
import ContratosPage from './pages/ContratosPage.jsx'
import ContratoFormPage from './pages/ContratoFormPage.jsx'
import SimuladorFiscalPage from './pages/SimuladorFiscalPage.jsx'
import BoletosPage from './pages/BoletosPage.jsx'

function App() {
  return (
    // BrowserRouter habilita a navegação por URLs normais (ex: /imoveis)
    <BrowserRouter>
      {/* AuthProvider fornece login/logout para toda a aplicação */}
      <AuthProvider>
        <Routes>
          {/* Rotas públicas — qualquer pessoa pode acessar */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/cadastro" element={<CadastroPage />} />

          {/* Rotas privadas — só acessíveis com login
              Layout envolve todas elas com a navbar do sistema */}
          <Route
            path="/dashboard"
            element={
              <RotaPrivada>
                <Layout>
                  <DashboardPage />
                </Layout>
              </RotaPrivada>
            }
          />
          <Route
            path="/imoveis"
            element={
              <RotaPrivada>
                <Layout>
                  <ImoveisPage />
                </Layout>
              </RotaPrivada>
            }
          />
          <Route
            path="/imoveis/novo"
            element={
              <RotaPrivada>
                <Layout>
                  <ImovelFormPage />
                </Layout>
              </RotaPrivada>
            }
          />
          <Route
            path="/imoveis/:id/editar"
            element={
              <RotaPrivada>
                <Layout>
                  <ImovelFormPage />
                </Layout>
              </RotaPrivada>
            }
          />
          <Route
            path="/imoveis/:id"
            element={
              <RotaPrivada>
                <Layout>
                  <ImovelDetalhePage />
                </Layout>
              </RotaPrivada>
            }
          />

          <Route path="/locadores" element={<RotaPrivada><Layout><LocadoresPage /></Layout></RotaPrivada>} />
          <Route path="/locadores/novo" element={<RotaPrivada><Layout><LocadorFormPage /></Layout></RotaPrivada>} />
          <Route path="/locadores/:id/editar" element={<RotaPrivada><Layout><LocadorFormPage /></Layout></RotaPrivada>} />
          <Route path="/contratos" element={<RotaPrivada><Layout><ContratosPage /></Layout></RotaPrivada>} />
          <Route path="/contratos/novo" element={<RotaPrivada><Layout><ContratoFormPage /></Layout></RotaPrivada>} />
          <Route path="/simulador-fiscal" element={<RotaPrivada><Layout><SimuladorFiscalPage /></Layout></RotaPrivada>} />
          <Route path="/boletos" element={<RotaPrivada><Layout><BoletosPage /></Layout></RotaPrivada>} />

          {/* Rota raiz redireciona para o dashboard */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />

          {/* Qualquer URL desconhecida vai para login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
