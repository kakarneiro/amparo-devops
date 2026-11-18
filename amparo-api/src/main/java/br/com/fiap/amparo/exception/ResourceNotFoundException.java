package br.com.fiap.amparo.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String recurso, Object id) {
        super(recurso + " com id " + id + " nao encontrado(a)");
    }

    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }
}
