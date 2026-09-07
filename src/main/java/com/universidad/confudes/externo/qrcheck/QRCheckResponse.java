package com.universidad.confudes.externo.qrcheck;

public class QRCheckResponse {
    private final int codigoRespuesta; // 200 = OK, 401 = credencial invalida
    private final String detalle;

    public QRCheckResponse(int codigoRespuesta, String detalle) {
        this.codigoRespuesta = codigoRespuesta;
        this.detalle = detalle;
    }
    public int getCodigoRespuesta() { return codigoRespuesta; }
    public String getDetalle() { return detalle; }
}
