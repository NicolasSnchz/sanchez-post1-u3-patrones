package com.universidad.confudes.externo.qrcheck;

// SDK del proveedor "QRCheckAPI" (JAR de terceros) - no puede modificarse.
public class QRCheckClient {
    public QRCheckResponse validar(QRCheckRequest request) {
        boolean valido = request.getPayload() != null && request.getPayload().startsWith("QR-");
        int codigo = valido ? 200 : 401;
        String detalle = valido ? "Credencial verificada por el proveedor"
                                 : "Credencial rechazada por el proveedor";
        return new QRCheckResponse(codigo, detalle);
    }
}
