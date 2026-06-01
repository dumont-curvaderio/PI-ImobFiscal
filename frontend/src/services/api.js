// Serviço de comunicação com o backend
// Centraliza todas as chamadas HTTP usando Axios
import axios from 'axios'

// UUID da imobiliária — placeholder para o MVP acadêmico
// Em produção, este valor viria do perfil do usuário logado (token JWT)
// Formato UUID: string de 36 caracteres (8-4-4-4-12 dígitos hexadecimais)
const IMOBILIARIA_ID = '11111111-1111-1111-1111-111111111111'

// Cria uma instância do Axios com configurações padrão
const api = axios.create({
  // Em dev: Vite faz proxy de /api para http://localhost:8080 (ver vite.config.js)
  // Em produção: usa a variável VITE_API_URL definida no Vercel/Render
  baseURL: import.meta.env.VITE_API_URL || '/api',
})

// Interceptor de requisição:
// Antes de cada chamada HTTP, adiciona o token JWT no cabeçalho Authorization
api.interceptors.request.use((config) => {
  // Busca o token salvo no localStorage
  const token = localStorage.getItem('imobfiscal_token')

  if (token) {
    // Formato padrão para autenticação JWT: "Bearer <token>"
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

// =============================================
// Funções de autenticação
// =============================================

// Faz login e retorna { token, email }
export function login(email, senha) {
  return api.post('/auth/login', { email, senha })
}

// Cria uma nova conta de usuário
export function cadastrar(nome, email, senha) {
  return api.post('/auth/cadastro', {
    nome,
    email,
    senha,
    imobiliariaId: IMOBILIARIA_ID,
  })
}

// =============================================
// Funções de imóveis
// =============================================

// Busca todos os imóveis da imobiliária
export function listarImoveis() {
  return api.get(`/imobiliarias/${IMOBILIARIA_ID}/imoveis`)
}

// Busca um imóvel pelo ID
export function buscarImovel(id) {
  return api.get(`/imobiliarias/${IMOBILIARIA_ID}/imoveis/${id}`)
}

// Cria um novo imóvel
export function criarImovel(dados) {
  return api.post(`/imobiliarias/${IMOBILIARIA_ID}/imoveis`, dados)
}

// Atualiza um imóvel existente
export function atualizarImovel(id, dados) {
  return api.put(`/imobiliarias/${IMOBILIARIA_ID}/imoveis/${id}`, dados)
}

// Remove um imóvel (soft delete no backend)
export function excluirImovel(id) {
  return api.delete(`/imobiliarias/${IMOBILIARIA_ID}/imoveis/${id}`)
}

// ─── Locadores ────────────────────────────────────────────────────────────────
export const listarLocadores = () =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/locadores`)

export const buscarLocador = (id) =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/locadores/${id}`)

export const criarLocador = (dados) =>
  api.post(`/imobiliarias/${IMOBILIARIA_ID}/locadores`, dados)

export const atualizarLocador = (id, dados) =>
  api.put(`/imobiliarias/${IMOBILIARIA_ID}/locadores/${id}`, dados)

export const excluirLocador = (id) =>
  api.delete(`/imobiliarias/${IMOBILIARIA_ID}/locadores/${id}`)

// ─── Contratos ────────────────────────────────────────────────────────────────
export const listarContratos = () =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/contratos`)

export const buscarContrato = (id) =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/contratos/${id}`)

export const criarContrato = (dados) =>
  api.post(`/imobiliarias/${IMOBILIARIA_ID}/contratos`, dados)

export const atualizarStatusContrato = (id, status) =>
  api.patch(`/imobiliarias/${IMOBILIARIA_ID}/contratos/${id}/status?status=${status}`)

// ─── Motor Tributário ─────────────────────────────────────────────────────────
export const calcularImposto = (dados) =>
  api.post('/motor-tributario/calcular', dados)

// ─── Boletos ──────────────────────────────────────────────────────────────────
export const listarBoletos = () =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/boletos`)

export const gerarBoleto = (dados) =>
  api.post(`/imobiliarias/${IMOBILIARIA_ID}/boletos/gerar`, dados)

export const buscarBoleto = (id) =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/boletos/${id}`)

export default api
