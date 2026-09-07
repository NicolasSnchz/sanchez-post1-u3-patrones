package com.universidad.confudes.externo.qrcheck;

public class QRCheckRequest {
    private final String payload;
    private final long idEvento;

    public QRCheckRequest(String payload, long idEvento) {
        this.payload = payload;
        this.idEvento = idEvento;
    }
    public String getPayload() { return payload; }
    public long getIdEvento() { return idEvento; }
}
