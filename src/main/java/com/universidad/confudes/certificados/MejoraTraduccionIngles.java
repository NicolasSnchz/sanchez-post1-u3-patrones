package com.universidad.confudes.certificados;

public class MejoraTraduccionIngles extends MejoraCertificado {

    public MejoraTraduccionIngles(ServicioCertificados envuelto) {
        super(envuelto);
    }

    @Override
    protected byte[] aplicarMejora(byte[] documento, SolicitudCertificado solicitud) {
        return UtilidadesPDF.traducirAIngles(documento);
    }
}
