package br.com.fiap.amparo.model;

public enum TipoEvento {
    BOTAO_EMERGENCIA(true),
    QUEDA_DETECTADA(true),
    BATERIA_BAIXA(false),
    TESTE(false);

    private final boolean emergencia;

    TipoEvento(boolean emergencia) {
        this.emergencia = emergencia;
    }

    /** Eventos de emergencia acionam a cadeia de cuidadores automaticamente. */
    public boolean isEmergencia() {
        return emergencia;
    }
}
