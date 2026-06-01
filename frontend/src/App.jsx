// Componente raiz da aplicação
// Define todas as rotas usando React Router v6
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext.jsx'
import RotaPrivada from './components/RotaPrivada.jsx'

// Importa todas as páginas
import LoginPage from './pages/LoginPage.jsx'
import CadastroPage from './pages/CadastroPage.jsx'
import ImoveisPage from './pages/ImoveisPage.jsx'
import ImovelFormPage from './pages/ImovelFormPage.jsx'
import ImovelDetalhePage from './pages/ImovelDetalhePage.jsx'

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

          {/* Rotas privadas — só acessíveis com login */}
          <Route
            path="/imoveis"
            element={
              <RotaPrivada>
                <ImoveisPage />
              </RotaPrivada>
            }
          />
          <Route
            path="/imoveis/novo"
            element={
              <RotaPrivada>
                <ImovelFormPage />
              </RotaPrivada>
            }
          />
          <Route
            path="/imoveis/:id/editar"
            element={
              <RotaPrivada>
                <ImovelFormPage />
              </RotaPrivada>
            }
          />
          <Route
            path="/imoveis/:id"
            element={
              <RotaPrivada>
                <ImovelDetalhePage />
              </RotaPrivada>
            }
          />

          {/* Rota raiz redireciona para a lista de imóveis */}
          <Route path="/" element={<Navigate to="/imoveis" replace />} />

          {/* Qualquer URL desconhecida vai para login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
