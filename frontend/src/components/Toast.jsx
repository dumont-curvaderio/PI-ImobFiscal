import { useEffect } from 'react'

function Toast({ mensagem, tipo = 'success', onFechar }) {
  useEffect(() => {
    const timer = setTimeout(onFechar, 3000)
    return () => clearTimeout(timer)
  }, [onFechar])

  const icone = tipo === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'

  return (
    <div className="position-fixed bottom-0 end-0 p-3" style={{ zIndex: 1055 }}>
      <div className={`alert alert-${tipo} d-flex align-items-center gap-2 shadow mb-0`} role="alert">
        <i className={`bi ${icone}`}></i>
        <span>{mensagem}</span>
        <button type="button" className="btn-close ms-2" onClick={onFechar}></button>
      </div>
    </div>
  )
}

export default Toast
