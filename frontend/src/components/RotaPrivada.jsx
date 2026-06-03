import { Navigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'

function RotaPrivada({ children }) {
  const { usuario } = useAuth()

  if (!usuario) {
    return <Navigate to="/login" replace />
  }

  return children
}

export default RotaPrivada
