// Ponto de entrada da aplicação React
// Aqui conectamos o React à página HTML (div#root do index.html)
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'

// Cria a raiz do React e renderiza o componente principal
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
