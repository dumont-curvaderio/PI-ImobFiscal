// Componente de Rota Privada
// Protege páginas que só podem ser acessadas por usuários logados
// Se não estiver logado, redireciona automaticamente para /login
import { Navigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'

function RotaPrivada({ children }) {
  // Pega o usuário do contexto de autenticação
  const { usuario } = useAuth()

  // Se não há usuário logado, redireciona para a página de login
  // "replace" evita que a página protegida fique no histórico do navegador
  if (!usuario) {
    return <Navigate to="/login" replace />
  }

  // Se há usuário logado, renderiza o componente filho normalmente
  return children
}

export default RotaPrivada
