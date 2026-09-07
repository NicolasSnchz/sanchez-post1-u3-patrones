package com.universidad.confudes.certificados;

public interface ServicioCertificados {
    byte[] emitir(SolicitudCertificado solicitud);
}
