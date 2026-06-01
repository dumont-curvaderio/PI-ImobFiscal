// Contexto de autenticação
// Fornece login, logout e o estado do usuário para toda a aplicação
import { createContext, useContext, useState } from 'react'
import { login as loginApi } from '../services/api.js'

// Cria o contexto — começa vazio, será preenchido pelo AuthProvider
const AuthContext = createContext(null)

// Hook customizado para usar o contexto em qualquer componente
// Uso: const { usuario, fazerLogin, fazerLogout } = useAuth()
export function useAuth() {
  return useContext(AuthContext)
}

// Provider: envolve a aplicação e disponibiliza os dados de autenticação
export function AuthProvider({ children }) {
  // Estado do usuário: guarda o email do usuário logado (ou null se deslogado)
  // Inicializa verificando se já existe um token salvo (para persistir o login ao recarregar)
  const [usuario, setUsuario] = useState(() => {
    const token = localStorage.getItem('imobfiscal_token')
    const email = localStorage.getItem('imobfiscal_email')
    // Se existir token E email salvos, considera o usuário como logado
    return token && email ? { email } : null
  })

  // Função de login: chama a API e salva o token no localStorage
  async function fazerLogin(email, senha) {
    // Chama o endpoint POST /api/auth/login
    const resposta = await loginApi(email, senha)
    const { token, email: emailRetornado } = resposta.data

    // Salva o token para que o interceptor do Axios possa usá-lo
    localStorage.setItem('imobfiscal_token', token)
    // Salva o email para mostrar na interface e restaurar sessão
    localStorage.setItem('imobfiscal_email', emailRetornado)

    // Atualiza o estado: a aplicação sabe que o usuário está logado
    setUsuario({ email: emailRetornado })
  }

  // Função de logout: limpa o token e o estado do usuário
  function fazerLogout() {
    localStorage.removeItem('imobfiscal_token')
    localStorage.removeItem('imobfiscal_email')
    setUsuario(null)
  }

  // Objeto com tudo que os componentes filhos podem acessar
  const valor = {
    usuario,       // { email } ou null
    fazerLogin,    // função assíncrona
    fazerLogout,   // função síncrona
  }

  return (
    <AuthContext.Provider value={valor}>
      {children}
    </AuthContext.Provider>
  )
}
