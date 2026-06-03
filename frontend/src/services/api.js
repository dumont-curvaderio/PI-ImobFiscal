import axios from 'axios'

const IMOBILIARIA_ID = '11111111-1111-1111-1111-111111111111'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('imobfiscal_token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export function login(email, senha) {
  return api.post('/auth/login', { email, senha })
}

export function cadastrar(nome, email, senha) {
  return api.post('/auth/cadastro', {
    nome,
    email,
    senha,
    imobiliariaId: IMOBILIARIA_ID,
  })
}

export function listarImoveis() {
  return api.get(`/imobiliarias/${IMOBILIARIA_ID}/imoveis`)
}

export function buscarImovel(id) {
  return api.get(`/imobiliarias/${IMOBILIARIA_ID}/imoveis/${id}`)
}

export function criarImovel(dados) {
  return api.post(`/imobiliarias/${IMOBILIARIA_ID}/imoveis`, dados)
}

export function atualizarImovel(id, dados) {
  return api.put(`/imobiliarias/${IMOBILIARIA_ID}/imoveis/${id}`, dados)
}

export function excluirImovel(id) {
  return api.delete(`/imobiliarias/${IMOBILIARIA_ID}/imoveis/${id}`)
}

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

export const listarContratos = () =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/contratos`)

export const buscarContrato = (id) =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/contratos/${id}`)

export const criarContrato = (dados) =>
  api.post(`/imobiliarias/${IMOBILIARIA_ID}/contratos`, dados)

export const atualizarStatusContrato = (id, status) =>
  api.patch(`/imobiliarias/${IMOBILIARIA_ID}/contratos/${id}/status?status=${status}`)

export const calcularImposto = (dados) =>
  api.post('/motor-tributario/calcular', dados)

export const listarBoletos = () =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/boletos`)

export const gerarBoleto = (dados) =>
  api.post(`/imobiliarias/${IMOBILIARIA_ID}/boletos/gerar`, dados)

export const buscarBoleto = (id) =>
  api.get(`/imobiliarias/${IMOBILIARIA_ID}/boletos/${id}`)

export default api
