// Configuração do Vite para o projeto ImobFiscal
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    // Porta padrão do servidor de desenvolvimento
    port: 5173,
    // Proxy para evitar problemas de CORS ao chamar o backend Spring Boot
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
