package br.com.fiap.amparo.exception;

/** Regra de negocio violada: responde 422 Unprocessable Entity. */
public class BusinessException extends RuntimeException {
    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
