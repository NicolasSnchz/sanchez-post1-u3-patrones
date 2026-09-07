package com.universidad.confudes.certificados;

public class MejoraCodigoQR extends MejoraCertificado {

    private static final String URL_BASE = "https://confudes.udes.edu.co/verificar/";

    private final String urlBase;

    public MejoraCodigoQR(ServicioCertificados envuelto) {
        this(envuelto, URL_BASE);
    }

    public MejoraCodigoQR(ServicioCertificados envuelto, String urlBase) {
        super(envuelto);
        this.urlBase = urlBase;
    }

    @Override
    protected byte[] aplicarMejora(byte[] documento, SolicitudCertificado solicitud) {
        String url = urlBase + solicitud.getEventoId() + "/" + solicitud.getParticipanteId();
        return UtilidadesPDF.insertarCodigoQR(documento, url);
    }
}
